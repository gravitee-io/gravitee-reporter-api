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
package io.gravitee.reporter.api.jackson;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.gateway.api.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;

public class HttpHeadersSerializerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void initializeObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(HttpHeaders.class, new HttpHeadersSerializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void shouldSerializeHttpHeaders() throws JsonProcessingException {
        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.add("header1", "value1");
        httpHeaders.add("header2", "value2");
        httpHeaders.add("header1", "value3");

        String json = objectMapper.writeValueAsString(httpHeaders);

        assertEquals("{\"header1\":[\"value1\",\"value3\"],\"header2\":[\"value2\"]}", json);
    }
}