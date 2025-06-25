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
    private String planId;
    private String apiId;
    private String applicationId;
    private String gatewayId;
    private String environmentId;
    private String organizationId;
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
        @NonNull Long timestamp,
        @NonNull String planId,
        @NonNull String apiId,
        @NonNull String applicationId,
        @NonNull String gatewayId,
        @NonNull String environmentId,
        @NonNull String organizationId,
        @Nullable String topic
    ) {
        super(timestamp);
        log.trace(
            "Creating event native metrics at timestamp:{} for plan:{} api:{} application:{} gateway:{} environment:{} organization:{}",
            timestamp,
            planId,
            apiId,
            applicationId,
            gatewayId,
            environmentId,
            organizationId
        );
        this.planId = planId;
        this.apiId = apiId;
        this.applicationId = applicationId;
        this.gatewayId = gatewayId;
        this.environmentId = environmentId;
        this.organizationId = organizationId;
        this.topic = topic;
    }
}
