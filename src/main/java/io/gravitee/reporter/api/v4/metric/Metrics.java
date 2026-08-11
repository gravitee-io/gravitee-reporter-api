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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.http.SecurityType;
import io.gravitee.reporter.api.v4.log.Log;
import java.util.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class Metrics extends AbstractReportable implements WithAdditional<Metrics> {

    @Builder.Default
    private boolean enabled = true;

    /**
     * Identifiers
     */
    private String requestId;
    private String transactionId;
    private String apiId;
    private String apiName;
    private String apiProductId;
    private String apiType;
    private String planId;
    private String applicationId;
    private String applicationName;
    private String subscriptionId;
    private String clientIdentifier;
    private String organizationId;
    private String environmentId;
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

    /**
     * Time elapsed until the endpoint response has been fully received, last byte of the body included, in
     * milliseconds. Counterpart of nginx's {@code $upstream_response_time}.
     * <p>
     * Connectors that do not stream a response body leave it equal to {@link #endpointResponseTtfbMs}.
     */
    @Builder.Default
    private long endpointResponseTimeMs = 0;

    /**
     * Time elapsed until the first byte of the endpoint response, i.e. up to its headers, in milliseconds.
     * Counterpart of nginx's {@code $upstream_header_time}. {@code -1} when the endpoint was never invoked.
     */
    @Builder.Default
    private long endpointResponseTtfbMs = -1;

    /**
     * Time spent acquiring a connection to the endpoint, in milliseconds. Counterpart of nginx's
     * {@code $upstream_connect_time}: it tells a wait on a saturated connection pool apart from a slow backend,
     * both of which are otherwise indistinguishable inside {@link #endpointResponseTtfbMs}. {@code -1} when no
     * connection was acquired.
     */
    @Builder.Default
    private long endpointConnectTimeMs = -1;

    /**
     * The three durations above, in nanoseconds. The gateway adds a fraction of a millisecond to a couple of
     * milliseconds to a request, so the millisecond is too coarse to measure its own overhead; nanoseconds also come
     * from a monotonic clock, which is the correct primitive for a duration.
     * <p>
     * They are the measured values: the millisecond counterparts are derived from them (see {@link #toMillis(long)}),
     * so both sets always agree. {@code -1} when nothing was measured.
     */
    @Builder.Default
    private long endpointResponseTimeNs = -1;

    @Builder.Default
    private long endpointResponseTtfbNs = -1;

    @Builder.Default
    private long endpointConnectTimeNs = -1;

    /**
     * Monotonic start of the endpoint invocation, the common origin the endpoint durations are derived from. Internal
     * bookkeeping: it is never reported and is meaningless once the request has been processed.
     */
    @JsonIgnore
    @Builder.Default
    private long endpointRequestStartNs = -1;

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

    /**
     * The two durations above, in nanoseconds — measured values, the milliseconds being derived from them.
     * {@code -1} when nothing was measured.
     */
    @Builder.Default
    private long gatewayResponseTimeNs = -1;

    @Builder.Default
    private long gatewayLatencyNs = -1;

    /**
     * Monotonic start of the request, the origin {@link #gatewayResponseTimeNs} is derived from. Internal bookkeeping:
     * never reported. {@link #getTimestamp()} stays a wall clock — it is what the request is indexed on — and cannot
     * serve here, an adjustable clock being unfit to measure a duration.
     */
    @JsonIgnore
    @Builder.Default
    private long requestStartNs = -1;

    @Builder.Default
    private Collection<AdditionalMetric> additionalMetrics = new HashSet<>();

    @Override
    public void setAdditionalMetrics(Collection<AdditionalMetric> additionalMetrics) {
        Objects.requireNonNull(additionalMetrics, "Additional metrics must not be null");
        this.additionalMetrics = new HashSet<>(additionalMetrics.size());
        this.additionalMetrics.addAll(additionalMetrics);
    }

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

    /**
     * The nanosecond durations are the measured ones; setting any of them derives its millisecond counterpart, so the
     * two sets cannot drift apart. The millisecond setters remain writable on their own for callers that only have
     * milliseconds to offer (the v2 policy adapter, mainly).
     */
    public void setEndpointResponseTimeNs(final long endpointResponseTimeNs) {
        this.endpointResponseTimeNs = endpointResponseTimeNs;
        this.endpointResponseTimeMs = toMillis(endpointResponseTimeNs);
    }

    public void setEndpointResponseTtfbNs(final long endpointResponseTtfbNs) {
        this.endpointResponseTtfbNs = endpointResponseTtfbNs;
        this.endpointResponseTtfbMs = toMillis(endpointResponseTtfbNs);
    }

    public void setEndpointConnectTimeNs(final long endpointConnectTimeNs) {
        this.endpointConnectTimeNs = endpointConnectTimeNs;
        this.endpointConnectTimeMs = toMillis(endpointConnectTimeNs);
    }

    public void setGatewayResponseTimeNs(final long gatewayResponseTimeNs) {
        this.gatewayResponseTimeNs = gatewayResponseTimeNs;
        this.gatewayResponseTimeMs = toMillis(gatewayResponseTimeNs);
    }

    public void setGatewayLatencyNs(final long gatewayLatencyNs) {
        this.gatewayLatencyNs = gatewayLatencyNs;
        this.gatewayLatencyMs = toMillis(gatewayLatencyNs);
    }

    /**
     * Rounds a duration to the nearest millisecond, rather than truncating it.
     * <p>
     * A difference of two {@code currentTimeMillis()} readings — how these durations used to be obtained — counts the
     * millisecond boundaries crossed, so a 0.75 ms duration reads 1 ms three times out of four and averages out to the
     * real duration. Truncating would instead bias every value down by half a millisecond, which is enough to report a
     * sub-millisecond gateway latency as a flat zero. Negative values mean "not measured" and are left alone.
     */
    private static long toMillis(final long nanos) {
        return nanos < 0 ? nanos : (nanos + 500_000L) / 1_000_000L;
    }

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
        metricsV2.setOrganizationId(organizationId);
        metricsV2.setEnvironmentId(environmentId);
        metricsV2.setAdditionalMetrics(additionalMetrics);
        return metricsV2;
    }
}
