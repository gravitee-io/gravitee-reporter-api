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
package io.gravitee.reporter.api.v4.metric.eventnative;

import io.gravitee.reporter.api.AbstractReportable;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class EventNativeMetrics extends AbstractReportable {

    /**
     * Base dimensions
     */
    private String gatewayId;
    private String organizationId;
    private String environmentId;
    private String apiId;
    private String planId;
    private String applicationId;
    private String topic;
    /**
     * Metrics
     */
    private Number downstreamPublishMessagesTotal;
    private Number downstreamPublishMessageBytes;
    private Number upstreamPublishMessagesTotal;
    private Number upstreamPublishMessageBytes;
    private Number downstreamSubscribeMessagesTotal;
    private Number downstreamSubscribeMessageBytes;
    private Number upstreamSubscribeMessagesTotal;
    private Number upstreamSubscribeMessageBytes;
    private Number downstreamActiveConnections;
    private Number upstreamActiveConnections;

    public EventNativeMetrics() {}

    public EventNativeMetrics(
        @NonNull String gatewayId,
        @NonNull String organizationId,
        @NonNull String environmentId,
        @NonNull String apiId,
        @NonNull String planId,
        @NonNull String applicationId,
        @Nullable String topic
    ) {
        log.trace(
            "Creating event native metrics for gateway:{} organization:{} environment:{} api:{} plan:{} application:{} topic:{}",
            gatewayId,
            organizationId,
            environmentId,
            apiId,
            planId,
            applicationId,
            topic
        );
        this.gatewayId = gatewayId;
        this.organizationId = organizationId;
        this.environmentId = environmentId;
        this.apiId = apiId;
        this.planId = planId;
        this.applicationId = applicationId;
        this.topic = topic;
    }
}
