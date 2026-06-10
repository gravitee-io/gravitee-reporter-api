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
package io.gravitee.reporter.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Identifies the intended destination of a {@link Reportable}.
 *
 * <p>A reportable may target one or more destinations (e.g. analytics backends
 * like Elasticsearch, or tracing backends like an OTLP collector). Reporters
 * declare which targets they support via {@link Reporter#supportedTargets()},
 * and the dispatch layer only forwards a reportable to reporters whose
 * supported targets intersect with the reportable's targets.
 *
 * @author Ayoub RAFFASS (ayoub.raffass at graviteesource.com)
 * @author GraviteeSource Team
 */
public enum ReportTarget {
    /** Analytics reporters: Elasticsearch, file, TCP, Datadog, etc. */
    ANALYTICS,

    /** Tracing reporters: OpenTelemetry log exporter (Loki, OTLP). */
    TRACING;

    /** Shared immutable set containing all targets. */
    public static final Set<ReportTarget> ALL = Collections.unmodifiableSet(EnumSet.allOf(ReportTarget.class));

    /** Default target for reportables and reporters — matches the pre-OTel historical behavior. */
    public static final Set<ReportTarget> DEFAULT = Collections.unmodifiableSet(EnumSet.of(ANALYTICS));
}
