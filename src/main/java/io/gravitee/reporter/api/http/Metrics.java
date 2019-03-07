/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.http;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.AbstractMetrics;
import io.gravitee.reporter.api.log.Log;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
public final class Metrics extends AbstractMetrics {

    private long proxyResponseTimeMs = -1;
    private long proxyLatencyMs = -1;
    private long apiResponseTimeMs = -1;
    private String requestId;
    private String api;
    private String application;
    private String transactionId;
    private String tenant;
    private String message;
    private String apiKey;
    private String plan;
    private String localAddress;
    private String remoteAddress;
    private HttpMethod httpMethod;
    private String host;
    private String uri;
    private long requestContentLength = 0;
    private long responseContentLength = 0;
    private int status;
    private String endpoint;
    private Log log;
    private String path;
    private String mappedPath;
    private String userAgent;
    private String user;

    private Metrics(long timestamp) {
        super(timestamp);
    }

    public long getProxyResponseTimeMs() {
        return proxyResponseTimeMs;
    }

    public void setProxyResponseTimeMs(long proxyResponseTimeMs) {
        this.proxyResponseTimeMs = proxyResponseTimeMs;
    }

    public long getProxyLatencyMs() {
        return proxyLatencyMs;
    }

    public void setProxyLatencyMs(long proxyLatencyMs) {
        this.proxyLatencyMs = proxyLatencyMs;
    }

    public long getApiResponseTimeMs() {
        return apiResponseTimeMs;
    }

    public void setApiResponseTimeMs(long apiResponseTimeMs) {
        this.apiResponseTimeMs = apiResponseTimeMs;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public long getRequestContentLength() {
        return requestContentLength;
    }

    public void setRequestContentLength(long requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMappedPath() {
        return mappedPath;
    }

    public void setMappedPath(String mappedPath) {
        this.mappedPath = mappedPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static Builder on(long timestamp) {
        return new Builder(timestamp);
    }

    public static class Builder {

        private final long timestamp;

        private Builder(long timestamp) {
            this.timestamp = timestamp;
        }

        public Metrics build() {
            return new Metrics(timestamp);
        }
    }
}
