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

import java.time.Instant;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.Reportable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class RequestMetrics implements Reportable {

	private long timestamp;
    private long proxyResponseTimeMs = -1;
    private long proxyLatencyMs = -1;
    private long apiResponseTimeMs = -1;
    private String api;
    private String application;

    /**
     * SubscriptionId (in case of api-key) or remote IP (key-less)
     */
    private String userId;
    private String apiKey;
    private String plan;
    private String requestId;
    private HttpMethod requestHttpMethod;
    private String requestPath;
    private String requestUri;
    private String requestLocalAddress;
    private String requestRemoteAddress;
    private long requestContentLength = 0;
    private int responseHttpStatus;
    private long responseContentLength = 0;
    private String endpoint;
    private String transactionId;
    private String tenant;
    private HttpHeaders clientRequestHeaders;
    private HttpHeaders clientResponseHeaders;
    private HttpHeaders proxyRequestHeaders;
    private HttpHeaders proxyResponseHeaders;
    private String message;
    
    
    @Override
    public Instant getTimestamp() {
        return Instant.ofEpochMilli(timestamp);
    }
}
