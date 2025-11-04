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
package io.gravitee.reporter.api.v4.metric;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.gravitee.reporter.api.jackson.AdditionalMetricDeserialization;

@JsonDeserialize(using = AdditionalMetricDeserialization.class)
public sealed interface AdditionalMetric {
    String VALUE_FIELD = "value";

    String name();

    record LongMetric(String name, Long value) implements AdditionalMetric {
        public LongMetric {
            if (!name.startsWith("long_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of long metrics must start with 'long_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record IntegerMetric(String name, Integer value) implements AdditionalMetric {
        public IntegerMetric {
            if (!name.startsWith("int_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of integer metrics must start with 'int_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record KeywordMetric(String name, String value) implements AdditionalMetric {
        public KeywordMetric {
            if (!name.startsWith("keyword_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of keywords must start with 'keyword_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record BooleanMetric(String name, Boolean value) implements AdditionalMetric {
        public BooleanMetric {
            if (!name.startsWith("bool_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of booleans must start with 'bool_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record DoubleMetric(String name, Double value) implements AdditionalMetric {
        public DoubleMetric {
            if (!name.startsWith("double_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of double metrics must start with 'double_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record StringMetric(String name, String value) implements AdditionalMetric {
        public StringMetric {
            if (!name.startsWith("string_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of string metrics must start with 'string_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    record JSONMetric(String name, String value) implements AdditionalMetric {
        public JSONMetric {
            if (!name.startsWith("json_")) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Key of string metrics must start with 'json_'.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Invalid key: " + name + ". Value must not be null.");
            }
        }
    }

    static AdditionalMetric deserialize(JsonNode json) throws JsonParseException {
        String name = json.get("name").asText();
        return switch (name.split("_")[0]) {
            case "long" -> new AdditionalMetric.LongMetric(name, json.get(VALUE_FIELD).asLong());
            case "int" -> new AdditionalMetric.IntegerMetric(name, json.get(VALUE_FIELD).asInt());
            case "bool" -> new AdditionalMetric.BooleanMetric(name, json.get(VALUE_FIELD).asBoolean());
            case "double" -> new AdditionalMetric.DoubleMetric(name, json.get(VALUE_FIELD).asDouble());
            case "keyword" -> new AdditionalMetric.KeywordMetric(name, json.get(VALUE_FIELD).asText());
            case "string" -> new AdditionalMetric.StringMetric(name, json.get(VALUE_FIELD).asText());
            case "json" -> new AdditionalMetric.JSONMetric(name, json.get(VALUE_FIELD).asText());
            default -> throw new JsonParseException("Impossible to deserialize metric: " + name);
        };
    }
}
