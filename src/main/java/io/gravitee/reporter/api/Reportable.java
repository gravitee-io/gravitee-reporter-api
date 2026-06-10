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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface Reportable {
    Instant timestamp();

    /**
     * Returns the set of {@link ReportTarget}s this reportable is intended for.
     * The dispatch layer uses this to route the reportable only to reporters
     * whose {@link Reporter#supportedTargets()} intersect with this set.
     *
     * <p>Defaults to {@link ReportTarget#DEFAULT} ({@code ANALYTICS}) — the historical
     * behavior before the OTel reporter was introduced. Only reportables that explicitly
     * opt in to {@code TRACING} are dispatched to tracing reporters.
     */
    @JsonIgnore
    default Set<ReportTarget> getTargets() {
        return ReportTarget.DEFAULT;
    }
}
