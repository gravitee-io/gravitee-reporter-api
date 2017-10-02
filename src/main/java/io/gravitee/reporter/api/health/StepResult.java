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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StepResult {

    /**
     * Step name
     */
    private String step;

    /**
     * HTTP Request URL
     */
    private String url;

    /**
     * HTTP Request method
     */
    private HttpMethod method;

    /**
     * HTTP Response Status code
     */
    private int status;

    /**
     * Was a success or not
     */
    private boolean success;

    /**
     * Defect message in case of health-check failure
     */
    private String failMessage;

    /**
     * Response time in ms
     */
    private long responseTime;
    
    StepResult(final String step) {
        this.step = step;
    }
    
    public void setFailMessage(final String message) {
    	this.failMessage = message;
    	this.success = false;
    }
}
