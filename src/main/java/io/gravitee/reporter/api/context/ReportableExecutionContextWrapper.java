/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.context;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.reporter.api.Reportable;
import java.time.Instant;

/**
 * @author Ovcharov Ilya (ovcharov.ilya@gmail.com)
 */
public class ReportableExecutionContextWrapper implements Reportable {

    private final long timestamp;
    private final ExecutionContext context;

    public ReportableExecutionContextWrapper(long timestamp, ExecutionContext context) {
        this.timestamp = timestamp;
        this.context = context;

        removeGraviteeAtrtibutes(context);
    }

    @Override
    public Instant timestamp() {
        return Instant.ofEpochMilli(timestamp);
    }

    public ExecutionContext getContext() {
        return context;
    }

    private void removeGraviteeAtrtibutes(ExecutionContext context){
        context.getAttributes().entrySet().removeIf(entry -> entry.getKey().startsWith("gravitee.attribute."));
    }
}

