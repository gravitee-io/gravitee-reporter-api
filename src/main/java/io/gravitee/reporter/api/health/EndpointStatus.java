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

import io.gravitee.common.utils.UUID;
import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EndpointStatus extends AbstractReportable {

    /**
     * Health-check identifier
     */
    private final String id;

    /**
     * API identifier.
     */
    private final String api;

    /**
     * Endpoint name.
     */
    private final String endpoint;

    /**
     * Endpoint state
     */
    private int state;

    /**
     * Is the endpoint available ?
     */
    private boolean available = true;

    /**
     * Last health-check is a success or not
     */
    private final boolean success;

    /**
     * Average response time in ms
     */
    private long responseTime;

    /**
     * Health-check steps result.
     */
    private final List<Step> steps;

    /**
     * True if state changed, false otherwise
     */
    private boolean transition = false;

    private EndpointStatus(long timestamp, String api, String endpoint, List<Step> steps) {
        super(timestamp);
        this.id = UUID.toString(UUID.random());
        this.api = api;
        this.endpoint = endpoint;
        this.steps = steps;
        this.success = steps.stream().allMatch(Step::isSuccess);
    }

    public String getId() {
        return id;
    }

    public String getApi() {
        return api;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getState() {
        return state;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isTransition() {
        return transition;
    }

    public void setTransition(boolean transition) {
        this.transition = transition;
    }

    public static Builder forEndpoint(String api, String endpoint) {
        return new Builder(api, endpoint);
    }

    public static StepBuilder forStep(String step) {
        return new StepBuilder(step);
    }

    public static class Builder {

        private final String api;
        private final String endpoint;

        private long timestamp;

        private List<Step> steps = new ArrayList<>();

        private Builder(String api, String endpoint) {
            this.api = api;
            this.endpoint = endpoint;
        }

        public Builder on(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder step(Step step) {
            this.steps.add(step);
            return this;
        }

        public EndpointStatus build() {
            return new EndpointStatus(timestamp, api, endpoint, steps);
        }
    }

    public static class StepBuilder {

        private final String step;
        private boolean success = true;
        private String message;
        private long responseTime;
        private Request request;
        private Response response;

        private StepBuilder(String step) {
            this.step = step;
        }

        public StepBuilder success() {
            this.success = true;
            return this;
        }

        public StepBuilder fail(String message) {
            this.success = false;
            this.message = message;
            return this;
        }

        public StepBuilder responseTime(long responseTime) {
            this.responseTime = responseTime;
            return this;
        }

        public StepBuilder request(Request request) {
            this.request = request;
            return this;
        }

        public StepBuilder response(Response response) {
            this.response = response;
            return this;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public Step build() {
            Step result = new Step(step);
            result.setSuccess(success);
            result.setRequest(request);
            result.setResponse(response);
            result.setMessage(message);
            result.setResponseTime(responseTime);
            return result;
        }
    }
}
