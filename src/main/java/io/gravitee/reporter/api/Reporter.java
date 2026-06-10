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

import io.gravitee.common.service.Service;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface Reporter extends Service<Reporter> {
    void report(Reportable reportable);

    default boolean canHandle(Reportable reportable) {
        return true;
    }

    /**
     * Returns the set of {@link ReportTarget}s this reporter is able to handle.
     * The dispatch layer only forwards a {@link Reportable} to this reporter when
     * at least one of the reportable's targets matches one of the reporter's
     * supported targets.
     *
     * <p>Defaults to {@link ReportTarget#DEFAULT} ({@code ANALYTICS}) — the historical
     * behavior before the OTel reporter was introduced. Only reporters that explicitly
     * opt in to {@code TRACING} receive tracing-targeted reportables.
     */
    default Set<ReportTarget> supportedTargets() {
        return ReportTarget.DEFAULT;
    }
}
