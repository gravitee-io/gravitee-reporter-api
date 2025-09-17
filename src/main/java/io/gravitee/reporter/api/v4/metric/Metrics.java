/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.v4.metric;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.http.SecurityType;
import io.gravitee.reporter.api.v4.log.Log;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Metrics extends AbstractReportable {

    @Builder.Default
    private boolean enabled = true;

    /**
     * Identifiers
     */
    private String requestId;
    private String transactionId;
    private String apiId;
    private String apiName;
    private String apiType;
    private String planId;
    private String applicationId;
    private String subscriptionId;
    private String clientIdentifier;
    /**
     * Tenant
     */
    private String tenant;
    private String zone;
    /**
     * Request metrics
     */
    private HttpMethod httpMethod;
    private String localAddress;
    private String remoteAddress;
    private String host;
    private String uri;
    private String pathInfo;
    private String mappedPath;
    private String userAgent;

    @Builder.Default
    private long requestContentLength = 0;

    private boolean requestEnded;
    /**
     * Entrypoint metrics
     */
    private String entrypointId;
    /**
     * Endpoint metrics
     */
    private String endpoint;

    @Builder.Default
    private long endpointResponseTimeMs = 0;

    /**
     * Response metrics
     */
    private int status;

    @Builder.Default
    private long responseContentLength = 0;

    @Builder.Default
    private long gatewayResponseTimeMs = 0;

    @Builder.Default
    private long gatewayLatencyMs = 0;

    private final Collection<AdditionalMetric> additionalMetrics = new HashSet<>();

    /**
     * Security metrics
     */
    private String user;
    private SecurityType securityType;
    private String securityToken;

    /**
     * Error metrics
     */
    private String errorMessage;
    private String errorKey;

    /**
     * Error metrics
     */
    private Diagnostic failure;

    @Builder.Default
    private Collection<Diagnostic> warnings = null;

    /**
     * Custom metrics
     */
    @Builder.Default
    private Map<String, String> customMetrics = new HashMap<>();

    /**
     * Log
     */
    private Log log;

    public void addCustomMetric(String key, String value) {
        if (this.customMetrics == null) {
            this.customMetrics = new HashMap<>();
        }
        this.customMetrics.put(key, value);
    }

    /**
     * Add a warning diagnostic to the metrics
     */
    public void addWarning(Diagnostic executionWarn) {
        if (this.warnings == null) {
            this.warnings = new LinkedList<>();
        }
        this.warnings.add(executionWarn);
    }

    public io.gravitee.reporter.api.http.Metrics toV2() {
        io.gravitee.reporter.api.http.Metrics metricsV2 = io.gravitee.reporter.api.http.Metrics.on(getTimestamp()).build();
        metricsV2.setProxyResponseTimeMs(gatewayResponseTimeMs);
        metricsV2.setProxyLatencyMs(gatewayLatencyMs);
        metricsV2.setApiResponseTimeMs(endpointResponseTimeMs);
        metricsV2.setRequestId(requestId);
        metricsV2.setApi(apiId);
        metricsV2.setApiName(apiName);
        metricsV2.setApplication(applicationId);
        metricsV2.setTransactionId(transactionId);
        metricsV2.setClientIdentifier(clientIdentifier);
        metricsV2.setTenant(tenant);
        metricsV2.setMessage(errorMessage);
        metricsV2.setPlan(planId);
        metricsV2.setLocalAddress(localAddress);
        metricsV2.setRemoteAddress(remoteAddress);
        metricsV2.setHttpMethod(httpMethod);
        metricsV2.setHost(host);
        metricsV2.setUri(uri);
        metricsV2.setRequestContentLength(requestContentLength);
        metricsV2.setResponseContentLength(responseContentLength);
        metricsV2.setStatus(status);
        metricsV2.setEndpoint(endpoint);
        if (log != null) {
            io.gravitee.reporter.api.log.Log logV2 = new io.gravitee.reporter.api.log.Log(log.getTimestamp());
            logV2.setRequestId(log.getRequestId());
            logV2.setApi(log.getApiId());
            logV2.setApiName(log.getApiName());
            logV2.setClientRequest(log.getEntrypointRequest());
            logV2.setClientResponse(log.getEntrypointResponse());
            logV2.setProxyRequest(log.getEndpointRequest());
            logV2.setProxyResponse(log.getEndpointResponse());
            metricsV2.setLog(logV2);
        }
        metricsV2.setPath(pathInfo);
        metricsV2.setMappedPath(mappedPath);
        metricsV2.setUserAgent(userAgent);
        metricsV2.setUser(user);
        metricsV2.setSecurityType(securityType);
        metricsV2.setSecurityToken(securityToken);
        metricsV2.setErrorKey(errorKey);
        metricsV2.setSubscription(subscriptionId);
        metricsV2.setZone(zone);
        metricsV2.setCustomMetrics(customMetrics);
        metricsV2.setFailure(failure);
        metricsV2.setWarnings(warnings);
        return metricsV2;
    }

    /**
     * @param key the metric key, MUST starts with 'long_'
     * @param value the metric value
     * @return updated Metrics object
     */
    public Metrics putAdditionalMetric(String key, Long value) {
        addAdditionalMetric(new AdditionalMetric.LongMetric(key, value));
        return this;
    }

    @Nullable
    public Map<String, Long> longAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.LongMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key the metric key, MUST starts with 'double_'
     * @param value the metric value
     * @return updated Metrics object
     */
    public Metrics putAdditionalMetric(String key, Double value) {
        addAdditionalMetric(new AdditionalMetric.DoubleMetric(key, value));
        return this;
    }

    @Nullable
    public Map<String, Double> doubleAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.DoubleMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key the metric key, MUST starts with 'keyword_'
     * @param value the metric value
     * @return updated Metrics object
     */
    public Metrics putAdditionalKeywordMetric(String key, String value) {
        addAdditionalMetric(new AdditionalMetric.KeywordMetric(key, value));
        return this;
    }

    @Nullable
    public Map<String, String> keywordAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.KeywordMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key the metric key, MUST starts with 'bool_'
     * @param value the metric value
     * @return updated Metrics object
     */
    public Metrics putAdditionalMetric(String key, Boolean value) {
        addAdditionalMetric(new AdditionalMetric.BooleanMetric(key, value));
        return this;
    }

    @Nullable
    public Map<String, Boolean> boolAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.BooleanMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    private void addAdditionalMetric(AdditionalMetric metric) {
        additionalMetrics.removeIf(m -> m.name().equals(metric.name()));
        additionalMetrics.add(metric);
    }

    private <T> Map<String, T> additionalMetrics(Function<AdditionalMetric, Stream<Map.Entry<String, T>>> typeGard) {
        Map<String, T> collect = additionalMetrics
            .stream()
            .flatMap(typeGard)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return !collect.isEmpty() ? collect : null;
    }
}
