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
import java.time.Instant;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

/**
 * @author Anthony CALLAERT (anthony.callaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
abstract class OperationEventMetricsDecorator extends AbstractReportable {

    @NonNull
    private final AbstractReportable wrapper;

    @NonNull
    private final String documentType;

    @NonNull
    private final String operation;

    OperationEventMetricsDecorator(@NonNull AbstractReportable wrapper, @NonNull String documentType, @NonNull String operation) {
        this.wrapper = wrapper;
        this.documentType = documentType;
        this.operation = operation;
    }

    @Override
    public long getTimestamp() {
        return wrapper.getTimestamp();
    }

    @Override
    public void setTimestamp(long timestamp) {
        wrapper.setTimestamp(timestamp);
    }

    @Override
    public Instant timestamp() {
        return wrapper.timestamp();
    }
}
