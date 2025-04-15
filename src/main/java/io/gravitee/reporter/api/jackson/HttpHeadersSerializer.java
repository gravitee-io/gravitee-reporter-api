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
package io.gravitee.reporter.api.jackson;

import static io.gravitee.reporter.api.configuration.Rules.FIELD_WILDCARD;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.reporter.api.configuration.Rules;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpHeadersSerializer extends StdSerializer<HttpHeaders> {

    private final transient Rules rules;

    /**
     * Deprecated constructor including all headers whatever the include/exclude rules defined
     * Use {@link HttpHeadersSerializer#HttpHeadersSerializer(Rules)} instead.
     *
     * @deprecated Kept only for backward compatibility
     */
    @Deprecated(since = "1.23", forRemoval = true)
    public HttpHeadersSerializer() {
        super(HttpHeaders.class);
        this.rules = new Rules();
    }

    public HttpHeadersSerializer(Rules rules) {
        super(HttpHeaders.class);
        this.rules = Objects.requireNonNullElseGet(rules, Rules::new);
    }

    @Override
    public void serialize(HttpHeaders httpHeaders, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        // Compute the base path of these headers to be able to filter properly on `log.clientRequest.headers` or `log.clientResponse.headers` etc
        String baseJsonPath =
            JacksonUtils.resolveJsonPath(jsonGenerator.getOutputContext(), null) + jsonGenerator.getOutputContext().getCurrentName();

        jsonGenerator.writeStartObject();

        for (String httpHeaderName : getHeadersToIncludeAccordingToRules(baseJsonPath, httpHeaders)) {
            jsonGenerator.writeFieldName(httpHeaderName);
            jsonGenerator.writeStartArray();
            for (String headerValue : httpHeaders.getAll(httpHeaderName)) {
                jsonGenerator.writeString(headerValue);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }

    private Set<String> getHeadersToIncludeAccordingToRules(String baseJsonPath, HttpHeaders httpHeaders) {
        // If the base path (e.g. `log.clientRequest.headers`) is in the inclusion list then we want all the headers
        if (rules.getIncludeFields().contains(baseJsonPath)) {
            return httpHeaders.names();
        }

        return httpHeaders
            .names()
            .stream()
            .filter(header -> {
                String headerPath = baseJsonPath + "." + header;
                return (
                    // Include it if exclusion list doesn't contain "*" and if it is not in the exclusion list
                    (!rules.getExcludeFields().contains(FIELD_WILDCARD) && !rules.getExcludeFields().contains(headerPath)) ||
                    // OR if it is in the inclusion list
                    rules.getIncludeFields().contains(headerPath)
                );
            })
            .collect(Collectors.toSet());
    }
}
