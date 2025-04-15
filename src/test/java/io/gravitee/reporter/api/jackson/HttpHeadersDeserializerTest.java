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

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.reporter.api.configuration.Rules;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpHeadersDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void initializeObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(HttpHeaders.class, new HttpHeadersDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void shouldSerializeHttpHeaders() throws JsonProcessingException {
        Map<String, List<String>> header = new HashMap<>();
        header.put("header1", List.of("value1"));
        header.put("header2", List.of("value2"));
        header.put("header3", List.of("value3"));

        HttpHeaders httpHeaders = objectMapper.readValue(objectMapper.writeValueAsString(header), HttpHeaders.class);

        assertEquals("value1", httpHeaders.getAll("header1").get(0));
        assertEquals("value2", httpHeaders.getAll("header2").get(0));
        assertEquals("value3", httpHeaders.getAll("header3").get(0));
    }
}
