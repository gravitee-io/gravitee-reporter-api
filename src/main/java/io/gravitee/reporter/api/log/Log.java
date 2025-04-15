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
package io.gravitee.reporter.api.log;

import io.gravitee.reporter.api.AbstractReportable;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Jacksonized
@SuperBuilder
public class Log extends AbstractReportable {

    private String api;

    private String apiName;

    /**
     * Log identifier is equivalent to the request identifier
     */
    private String requestId;

    private Request clientRequest, proxyRequest;

    private Response clientResponse, proxyResponse;

    public Log(long timestamp) {
        super(timestamp);
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Request getClientRequest() {
        return clientRequest;
    }

    public void setClientRequest(Request clientRequest) {
        this.clientRequest = clientRequest;
    }

    public Request getProxyRequest() {
        return proxyRequest;
    }

    public void setProxyRequest(Request proxyRequest) {
        this.proxyRequest = proxyRequest;
    }

    public Response getClientResponse() {
        return clientResponse;
    }

    public void setClientResponse(Response clientResponse) {
        this.clientResponse = clientResponse;
    }

    public Response getProxyResponse() {
        return proxyResponse;
    }

    public void setProxyResponse(Response proxyResponse) {
        this.proxyResponse = proxyResponse;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
}
