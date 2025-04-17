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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.reporter.api.configuration.Rules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpHeadersSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initializeObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(HttpHeaders.class, new HttpHeadersSerializer(new Rules()));
        objectMapper.registerModule(module);
    }

    @Test
    void shouldSerializeHttpHeaders() throws JsonProcessingException {
        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.add("header1", "value1");
        httpHeaders.add("header2", "value2");
        httpHeaders.add("header1", "value3");

        String output = objectMapper.writeValueAsString(httpHeaders);

        JsonNode node = new ObjectMapper().readTree(output);

        assertThat(node.get("header1").get(0).asText()).isEqualTo("value1");
        assertThat(node.get("header1").get(1).asText()).isEqualTo("value3");
        assertThat(node.get("header2").get(0).asText()).isEqualTo("value2");
    }
}
