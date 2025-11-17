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

import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.v4.common.MessageConnectorType;
import io.gravitee.reporter.api.v4.common.MessageOperation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@SuperBuilder
@Jacksonized
@NoArgsConstructor
@ToString(callSuper = true)
public final class MessageMetrics extends AbstractReportable implements WithAdditional {

    /**
     * Identifiers
     */
    private String requestId;
    private String apiId;
    private String apiName;
    private String clientIdentifier;
    private String correlationId;
    private String parentCorrelationId;

    /**
     * Metrics
     */
    private MessageOperation operation;

    private MessageConnectorType connectorType;
    private String connectorId;

    @Builder.Default
    private long contentLength = -1;

    @Builder.Default
    private long count = -1;

    @Builder.Default
    private long errorCount = -1;

    @Builder.Default
    private long countIncrement = -1;

    @Builder.Default
    private long errorCountIncrement = -1;

    @Builder.Default
    private boolean error = false;

    @Builder.Default
    private long gatewayLatencyMs = -1;

    /**
     * Custom metrics
     */
    private Map<String, String> customMetrics;

    @Builder.Default
    private Collection<AdditionalMetric> additionalMetrics = new HashSet<>();

    @Override
    public void setAdditionalMetrics(Collection<AdditionalMetric> additionalMetrics) {
        Objects.requireNonNull(additionalMetrics, "Additional metrics must not be null");
        this.additionalMetrics = additionalMetrics;
    }
}
