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
package io.gravitee.reporter.api.v4.log;

import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.v4.common.Message;
import io.gravitee.reporter.api.v4.common.MessageConnectorType;
import io.gravitee.reporter.api.v4.common.MessageOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString(callSuper = true)
public class MessageLog extends AbstractReportable {

    private String apiId;
    private String apiName;
    private String requestId;
    private String clientIdentifier;
    private String correlationId;
    private String parentCorrelationId;
    private MessageOperation operation;
    private MessageConnectorType connectorType;
    private String connectorId;
    private Message message;
}
