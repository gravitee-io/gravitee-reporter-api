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
package io.gravitee.reporter.api.http;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.log.Log;
import io.gravitee.reporter.api.v4.metric.Diagnostic;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@Jacksonized
@SuperBuilder
public class Metrics extends AbstractReportable {

    private long proxyResponseTimeMs = 0;
    private long proxyLatencyMs = 0;
    private long apiResponseTimeMs = 0;
    private String requestId;
    private String api;
    private String apiName;
    private String application;
    private String transactionId;
    private String clientIdentifier;
    private String organizationId;
    private String environmentId;
    private String tenant;
    private String message;
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
    private SecurityType securityType;
    private String securityToken;
    private String errorKey;
    private String subscription;
    private String zone;
    private Map<String, String> customMetrics = new HashMap<>();
    private Diagnostic failure;
    private Collection<Diagnostic> warnings = null;

    protected Metrics(long timestamp) {
        super(timestamp);
    }

    public static Builder on(long timestamp) {
        return new Builder(timestamp);
    }

    public void addCustomMetric(String key, String value) {
        this.customMetrics.put(key, value);
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
