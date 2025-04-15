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

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.gravitee.gateway.api.http.HttpHeaders;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpHeadersDeserializer extends StdDeserializer<HttpHeaders> {

    public HttpHeadersDeserializer() {
        super(HttpHeaders.class);
    }

    @Override
    public HttpHeaders deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jn = jp.getCodec().readTree(jp);
        HttpHeaders httpHeaders = HttpHeaders.create();
        jp.getCodec().treeToValue(jn, Map.class).forEach((k, v) -> httpHeaders.add((CharSequence) k, (List<CharSequence>) v));
        return httpHeaders;
    }
}
