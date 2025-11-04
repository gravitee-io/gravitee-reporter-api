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

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.reporter.api.v4.metric.AdditionalMetric;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdditionalMetricDeserializationTest {

    private final ObjectMapper objectMapper = JacksonUtils.mapper(null);

    @Test
    void should_serialize() throws JsonProcessingException {
        var input = List.of(
            new AdditionalMetric.LongMetric("long_value", 12L),
            new AdditionalMetric.BooleanMetric("bool_value", true),
            new AdditionalMetric.KeywordMetric("keyword_value", "foo"),
            new AdditionalMetric.DoubleMetric("double_value", 3.14),
            new AdditionalMetric.IntegerMetric("int_value", 42),
            new AdditionalMetric.StringMetric("string_value", "bar"),
            new AdditionalMetric.JSONMetric("json_value", "{\"hello\":\"world\"}")
        );

        var result = objectMapper.readValue(objectMapper.writeValueAsString(input), new TypeReference<List<AdditionalMetric>>() {});

        assertThat(result)
            .containsExactly(
                new AdditionalMetric.LongMetric("long_value", 12L),
                new AdditionalMetric.BooleanMetric("bool_value", true),
                new AdditionalMetric.KeywordMetric("keyword_value", "foo"),
                new AdditionalMetric.DoubleMetric("double_value", 3.14),
                new AdditionalMetric.IntegerMetric("int_value", 42),
                new AdditionalMetric.StringMetric("string_value", "bar"),
                new AdditionalMetric.JSONMetric("json_value", "{\"hello\":\"world\"}")
            );
    }

    @Test
    void should_fail_on_type() {
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

    @Test
    void should_fail_on_wrong_prefix() {
        assertThatCode(() -> new AdditionalMetric.LongMetric("foo", 12L)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.BooleanMetric("foo", true)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.KeywordMetric("foo", "foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.DoubleMetric("foo", 3.14)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.IntegerMetric("foo", 42)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.StringMetric("foo", "bar")).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.JSONMetric("foo", "{\"hello\":\"world\"}")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_fail_on_null_value() {
        assertThatCode(() -> new AdditionalMetric.LongMetric("long_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.BooleanMetric("bool_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.KeywordMetric("keyword_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.DoubleMetric("double_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.IntegerMetric("int_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.StringMetric("string_value", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> new AdditionalMetric.JSONMetric("json_value", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
