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
public final class MessageMetrics extends AbstractReportable implements WithAdditional<MessageMetrics> {

    /**
     * Identifiers
     */
    private String requestId;
    private String apiId;
    private String apiName;
    private String clientIdentifier;
    private String correlationId;
    private String parentCorrelationId;
    private String organizationId;
    private String environmentId;

    /**
     * Dimensions of the connection that carried this message.
     *
     * <p>Named and typed exactly as on the connection metrics, because they are the same concepts.
     * They are duplicated onto every message on purpose: message analytics filtering on a plan, an
     * application or an entrypoint otherwise has to resolve the matching connections first and narrow
     * the message query by their ids — a join that is expensive, bounded by
     * {@code index.max_terms_count}, and that silently drops the messages of any stream opened before
     * the query window. Carrying the dimension here removes the need for it.
     *
     * <p>Nullable: a message reported by a gateway that predates this carries none of them, which is
     * what {@link #schemaVersion} is for.
     */
    private String planId;

    private String applicationId;

    private String entrypointId;

    /**
     * Version of the message document's shape, stamped by the writer.
     *
     * <p>Its presence is what tells a reader that this document was written by a gateway carrying the
     * connection dimensions above — a question the dimensions themselves cannot answer, since a
     * message may legitimately have no plan and an absent field is indistinguishable from an absent
     * writer. Analytics uses it to decide, per query window, whether the connection join is still
     * required for the data in that window.
     */
    private Integer schemaVersion;

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
        this.additionalMetrics = new HashSet<>(additionalMetrics.size());
        this.additionalMetrics.addAll(additionalMetrics);
    }
}
