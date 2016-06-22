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
package io.gravitee.reporter.api.health;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.AbstractMetrics;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public class HealthStatus extends AbstractMetrics {

    private final String api;

    // HTTP Request URL
    private String url;

    // HTTP Request method
    private HttpMethod method;

    // Health was a success or not
    private boolean success;

    // Defect message in case of fail
    private String message;

    // HTTP Response Status code
    private int status;

    // Endpoint state
    private int state;

    private HealthStatus(long timestamp,
                         String api,
                         String url,
                         HttpMethod httpMethod,
                         int status,
                         boolean success,
                         int state,
                         String message) {
        super("unknown", timestamp);
        this.api = api;
        this.url = url;
        this.method = httpMethod;
        this.status = status;
        this.success = success;
        this.state = state;
        this.message = message;
    }

    public String getApi() {
        return api;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUrl() {
        return url;
    }

    public int getState() {
        return state;
    }

    public static Builder forApi(String api) {
        return new Builder(api);
    }

    public static class Builder {

        private final String api;
        private long timestamp;

        private String url;
        private HttpMethod method;
        private String message;
        private int status;
        private int state;
        private boolean success = true;

        private Builder(String api) {
            this.api = api;
        }

        public Builder on(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder success() {
            return success(true);
        }

        public Builder fail() {
            return success(false);
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public HealthStatus build() {
            return new HealthStatus(
                    timestamp, api, url, method, status, success, state, message);
        }
    }
}
