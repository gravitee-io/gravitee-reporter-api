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
import static org.assertj.core.api.Assertions.catchException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.reporter.api.v4.metric.AdditionalMetric;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdditionalMetricDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initializeObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AdditionalMetric.class, new AdditionalMetricDeserialization());
        objectMapper.registerModule(module);
    }

    @Test
    void shouldSerialize() throws JsonProcessingException {
        var input = List.of(
            new AdditionalMetric.LongMetric("long_value", 12L),
            new AdditionalMetric.BooleanMetric("bool_value", true),
            new AdditionalMetric.KeywordMetric("keyword_value", "foo"),
            new AdditionalMetric.DoubleMetric("double_value", 3.14)
        );

        var result = objectMapper.readValue(objectMapper.writeValueAsString(input), new TypeReference<List<AdditionalMetric>>() {});

        assertThat(result)
            .containsExactlyInAnyOrder(
                new AdditionalMetric.LongMetric("long_value", 12L),
                new AdditionalMetric.BooleanMetric("bool_value", true),
                new AdditionalMetric.KeywordMetric("keyword_value", "foo"),
                new AdditionalMetric.DoubleMetric("double_value", 3.14)
            );
    }

    @Test
    void shouldSendError() {
        var input =
            """
                [{
                "name": "badtype_value",
                "value": 12
                }]
                """;

        Exception exception = catchException(() -> objectMapper.readValue(input, new TypeReference<List<AdditionalMetric>>() {}));

        assertThat(exception).isInstanceOf(JsonMappingException.class).hasMessageContaining("badtype_value");
    }
}
