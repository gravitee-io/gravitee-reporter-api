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
package io.gravitee.reporter.api.v4.metric;

import io.gravitee.reporter.api.v4.common.MetricsDimensions;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@SuperBuilder
@Jacksonized
@ToString(callSuper = true)
public class NativeMessageMetrics extends MetricsDimensions {

    private Long clientThroughputProducedBytes;
    private Long clientThroughputConsumedBytes;
    private Long gatewayThroughputProducedBytes;
    private Long gatewayThroughputConsumedBytes;
    private Long clientMessagesProduced;
    private Long clientMessagesConsumed;
    private Long gatewayMessagesProduced;
    private Long gatewayMessagesConsumed;
    private Integer produceThrottleTimePerTopic;
    private Integer consumeThrottleTimePerTopic;
    private Integer lagPerTopic;
}
