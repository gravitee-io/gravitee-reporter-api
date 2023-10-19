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
package io.gravitee.reporter.api.common;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.gateway.api.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
public class Request {

    private List<ReportAction<Request>> onReportActions;

    private HttpMethod method;

    private HttpHeaders headers;

    private String uri;

    private String body;

    public Request doOnReport(ReportAction<Request> action) {
        if (onReportActions == null) {
            onReportActions = new ArrayList<>();
        }

        onReportActions.add(action);

        return this;
    }
}
