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

import java.time.Instant;
import java.util.List;

import io.gravitee.common.utils.UUID;
import io.gravitee.reporter.api.Reportable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Data
@AllArgsConstructor
public class EndpointHealthStatus implements Reportable {

	private long timestamp;
	
    /**
     * Health-check identifier
     */
    private final String id = UUID.random().toString();

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
    private final List<StepResult> steps;


    @Override
    public Instant getTimestamp() {
        return Instant.ofEpochMilli(timestamp);
    }
    
    public boolean isSuccess() {
    	return steps.stream().allMatch(StepResult::isSuccess);
    }
}
