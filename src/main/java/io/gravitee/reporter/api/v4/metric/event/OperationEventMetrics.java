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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Anthony CALLAERT (anthony.callaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@ToString(callSuper = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED, force = true)
public class OperationEventMetrics extends BaseEventMetrics {

    private static final String DOCUMENT_TYPE = "operation";

    @NonNull
    private final String operation;

    private Long upstreamDurationsNanos;
    private Long downstreamDurationsNanos;
    private Long endpointDurationsNanos;
    private Integer upstreamCountIncrement; // Number of upstream requests received between two reports
    private Integer downstreamCountIncrement; // Number of downstream requests sent between two reports
    private Integer endpointUpstreamCountIncrement; // Number of upstream requests sent to then endpoint(broker) between two reports
    private Integer endpointDownstreamCountIncrement; // Number of downstream requests received from the endpoint(broker) between two reports

    @Override
    public String getDocumentType() {
        return DOCUMENT_TYPE;
    }

    @Override
    public String dimensionsKey() {
        return String.format("%s:%s", super.dimensionsKey(), getOperation());
    }
}
