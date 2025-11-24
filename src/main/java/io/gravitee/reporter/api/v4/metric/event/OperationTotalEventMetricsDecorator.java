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
package io.gravitee.reporter.api.v4.metric.event;

import io.gravitee.reporter.api.AbstractReportable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NonNull;

/**
 * @author Anthony CALLAERT (anthony.callaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@Accessors(chain = true)
public class OperationTotalEventMetricsDecorator extends OperationEventMetricsDecorator {

    private static final String DOCUMENT_TYPE = "operation_total";
    private Integer requestsTotal;
    private Integer responsesTotal;
    private Integer endpointRequestsTotal;
    private Integer endpointResponsesTotal;

    public OperationTotalEventMetricsDecorator(@NonNull AbstractReportable wrapper, @NonNull String operation) {
        super(wrapper, DOCUMENT_TYPE, operation);
    }
}
