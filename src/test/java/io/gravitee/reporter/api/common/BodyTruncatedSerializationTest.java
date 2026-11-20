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
package io.gravitee.reporter.api.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.reporter.api.jackson.JacksonUtils;
import org.junit.jupiter.api.Test;

class BodyTruncatedSerializationTest {

    private final ObjectMapper mapper = JacksonUtils.mapper(null);

    @Test
    void shouldSerializeBodyTruncatedWhenRequestBodyIsTruncated() throws JsonProcessingException {
        Request request = new Request();
        request.setBody("{\"hel");
        request.setBodyTruncated(true);

        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        assertThat(node.get("bodyTruncated").asBoolean()).isTrue();
    }

    @Test
    void shouldSerializeBodyTruncatedWhenResponseBodyIsTruncated() throws JsonProcessingException {
        Response response = new Response(200);
        response.setBody("{\"hel");
        response.setBodyTruncated(true);

        JsonNode node = mapper.readTree(mapper.writeValueAsString(response));

        assertThat(node.get("bodyTruncated").asBoolean()).isTrue();
    }

    @Test
    void shouldOmitBodyTruncatedWhenRequestBodyIsComplete() throws JsonProcessingException {
        Request request = new Request();
        request.setBody("{\"hello\":\"world\"}");

        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        assertThat(node.has("bodyTruncated")).isFalse();
    }

    @Test
    void shouldOmitBodyTruncatedWhenResponseBodyIsComplete() throws JsonProcessingException {
        Response response = new Response(200);
        response.setBody("{\"hello\":\"world\"}");

        JsonNode node = mapper.readTree(mapper.writeValueAsString(response));

        assertThat(node.has("bodyTruncated")).isFalse();
    }

    @Test
    void shouldReadBodyTruncatedBack() throws JsonProcessingException {
        Response response = new Response(200);
        response.setBody("{\"hel");
        response.setBodyTruncated(true);

        Response read = mapper.readValue(mapper.writeValueAsString(response), Response.class);

        assertThat(read.isBodyTruncated()).isTrue();
        assertThat(read.getBody()).isEqualTo("{\"hel");
    }
}
