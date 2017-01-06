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

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public final class RequestMetrics extends AbstractMetrics {

    private long proxyResponseTimeMs = -1;

    private long proxyLatencyMs = -1;

    private long apiResponseTimeMs = -1;

    private String api;

    private String application;

    private String apiKey;

    private String subscription;

    private String plan;

    private String requestId;

    private HttpMethod requestHttpMethod;

    private String requestPath;

    private String requestUri;

    private String requestLocalAddress;

    private String requestRemoteAddress;

    private String requestContentType;

    private long requestContentLength = 0;

    private int responseHttpStatus;

    private String responseContentType;

    private long responseContentLength = 0;

    private String endpoint;

    private String transactionId;

    private RequestMetrics(long timestamp) {
        super("unknown", timestamp);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public long getApiResponseTimeMs() {
        return apiResponseTimeMs;
    }

    public void setApiResponseTimeMs(long apiResponseTimeMs) {
        this.apiResponseTimeMs = apiResponseTimeMs;
    }

    public HttpMethod getRequestHttpMethod() {
        return requestHttpMethod;
    }

    public void setRequestHttpMethod(HttpMethod requestHttpMethod) {
        this.requestHttpMethod = requestHttpMethod;
    }

    public int getResponseHttpStatus() {
        return responseHttpStatus;
    }

    public void setResponseHttpStatus(int responseHttpStatus) {
        this.responseHttpStatus = responseHttpStatus;
    }

    public String getRequestLocalAddress() {
        return requestLocalAddress;
    }

    public void setRequestLocalAddress(String requestLocalAddress) {
        this.requestLocalAddress = requestLocalAddress;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
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

    public String getRequestRemoteAddress() {
        return requestRemoteAddress;
    }

    public void setRequestRemoteAddress(String requestRemoteAddress) {
        this.requestRemoteAddress = requestRemoteAddress;
    }

    public long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public long getRequestContentLength() {
        return requestContentLength;
    }

    public void setRequestContentLength(long requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public static Builder on(long timestamp) {
        return new Builder(timestamp);
    }

    public static class Builder {

        private final long timestamp;

        private Builder(long timestamp) {
            this.timestamp = timestamp;
        }

        public RequestMetrics build() {
            return new RequestMetrics(timestamp);
        }
    }
}
