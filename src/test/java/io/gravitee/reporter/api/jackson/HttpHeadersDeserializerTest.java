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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.gateway.api.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
class HttpHeadersDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initializeObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(HttpHeaders.class, new HttpHeadersDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void shouldSerializeHttpHeaders() throws JsonProcessingException {
        Map<String, List<String>> header = Map.of("header1", List.of("value1"), "header2", List.of("value2"), "header3", List.of("value3"));

        HttpHeaders httpHeaders = objectMapper.readValue(objectMapper.writeValueAsString(header), HttpHeaders.class);

        assertThat(httpHeaders.getAll("header1")).first().isEqualTo("value1");
        assertThat(httpHeaders.getAll("header2")).first().isEqualTo("value2");
        assertThat(httpHeaders.getAll("header3")).first().isEqualTo("value3");
    }
}
