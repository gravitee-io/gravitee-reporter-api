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

import io.gravitee.gateway.api.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@NoArgsConstructor
public class Response {

    private List<ReportAction<Response>> onReportActions;

    private int status;

    private HttpHeaders headers;

    private String body;

    public Response(final int status) {
        this.status = status;
    }

    public Response doOnReport(ReportAction<Response> action) {
        if (onReportActions == null) {
            onReportActions = new ArrayList<>();
        }

        onReportActions.add(action);

        return this;
    }
}
