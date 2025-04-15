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
import io.gravitee.reporter.api.http.SecurityType;
import io.gravitee.reporter.api.v4.log.Log;
import java.util.Map;
import lombok.experimental.SuperBuilder;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class NoopMetrics extends Metrics {

    public NoopMetrics() {
        super.setEnabled(false);
    }

    @Override
    public void setEnabled(final boolean enabled) {}

    @Override
    public void setRequestId(final String requestId) {}

    @Override
    public void setTransactionId(final String transactionId) {}

    @Override
    public void setApiId(final String apiId) {}

    @Override
    public void setPlanId(final String planId) {}

    @Override
    public void setApplicationId(final String applicationId) {}

    @Override
    public void setSubscriptionId(final String subscriptionId) {}

    @Override
    public void setClientIdentifier(final String clientIdentifier) {}

    @Override
    public void setTenant(final String tenant) {}

    @Override
    public void setZone(final String zone) {}

    @Override
    public void setHttpMethod(final HttpMethod httpMethod) {}

    @Override
    public void setLocalAddress(final String localAddress) {}

    @Override
    public void setRemoteAddress(final String remoteAddress) {}

    @Override
    public void setHost(final String host) {}

    @Override
    public void setUri(final String uri) {}

    @Override
    public void setPathInfo(final String pathInfo) {}

    @Override
    public void setMappedPath(final String mappedPath) {}

    @Override
    public void setUserAgent(final String userAgent) {}

    @Override
    public void setRequestContentLength(final long requestContentLength) {}

    @Override
    public void setEndpoint(final String endpoint) {}

    @Override
    public void setEndpointResponseTimeMs(final long endpointResponseTimeMs) {}

    @Override
    public void setStatus(final int status) {}

    @Override
    public void setResponseContentLength(final long responseContentLength) {}

    @Override
    public void setGatewayResponseTimeMs(final long gatewayResponseTimeMs) {}

    @Override
    public void setGatewayLatencyMs(final long gatewayLatencyMs) {}

    @Override
    public void setUser(final String user) {}

    @Override
    public void setSecurityType(final SecurityType securityType) {}

    @Override
    public void setSecurityToken(final String securityToken) {}

    @Override
    public void setErrorMessage(final String errorMessage) {}

    @Override
    public void setErrorKey(final String errorKey) {}

    @Override
    public void setCustomMetrics(final Map<String, String> customMetrics) {}

    @Override
    public void setLog(final Log log) {}

    @Override
    public void addCustomMetric(final String key, final String value) {}
}
