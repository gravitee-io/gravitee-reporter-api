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
package io.gravitee.reporter.api.v4.log;

import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.common.ReportAction;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import io.gravitee.reporter.api.v4.common.Message;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
public class MessagesLog extends Log {

    private List<ReportAction<Message>> onEntrypointMessageRequest;
    private List<ReportAction<Message>> onEndpointMessageRequest;
    private List<ReportAction<Message>> onEntrypointMessageResponse;
    private List<ReportAction<Message>> onEndpointMessageResponse;

    public MessagesLog doOnEntrypointMessageRequest(ReportAction<Message> action) {
        if (onEntrypointMessageRequest == null) {
            onEntrypointMessageRequest = new ArrayList<>();
        }

        onEntrypointMessageRequest.add(action);

        return this;
    }

    public MessagesLog doOnEndpointMessageRequest(ReportAction<Message> action) {
        if (onEndpointMessageRequest == null) {
            onEndpointMessageRequest = new ArrayList<>();
        }

        onEndpointMessageRequest.add(action);

        return this;
    }

    public MessagesLog doOnEntrypointMessageResponse(ReportAction<Message> action) {
        if (onEntrypointMessageResponse == null) {
            onEntrypointMessageResponse = new ArrayList<>();
        }

        onEntrypointMessageResponse.add(action);

        return this;
    }

    public MessagesLog doOnEndpointMessageResponse(ReportAction<Message> action) {
        if (onEndpointMessageResponse == null) {
            onEndpointMessageResponse = new ArrayList<>();
        }

        onEndpointMessageResponse.add(action);

        return this;
    }
}
