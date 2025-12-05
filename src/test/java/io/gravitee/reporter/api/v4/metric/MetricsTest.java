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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MetricsTest {

    Metrics underTest;

    @Test
    void should_extract_additional_metrics_by_type() {
        underTest = Metrics.builder().build();
        underTest.putAdditionalMetric("long_name", 42L);
        underTest.putAdditionalMetric("int_name", 12);
        underTest.putAdditionalMetric("double_name", 3.14);
        underTest.putAdditionalMetric("string_name", "foo");
        underTest.putAdditionalMetric("bool_name", true);
        underTest.putAdditionalKeywordMetric("keyword_name", "bar");
        underTest.putAdditionalJSONMetric("json_name", "{\"hello\":\"world\"}");

        assertThat(underTest.longAdditionalMetrics()).containsEntry("long_name", 42L);
        assertThat(underTest.intAdditionalMetrics()).containsEntry("int_name", 12);
        assertThat(underTest.doubleAdditionalMetrics()).containsEntry("double_name", 3.14);
        assertThat(underTest.stringAdditionalMetrics()).containsEntry("string_name", "foo");
        assertThat(underTest.boolAdditionalMetrics()).containsEntry("bool_name", true);
        assertThat(underTest.keywordAdditionalMetrics()).containsEntry("keyword_name", "bar");
        assertThat(underTest.jsonAdditionalMetrics()).containsEntry("json_name", "{\"hello\":\"world\"}");
    }

    @Test
    void should_extract_additional_metrics_by_type_from_collection() {
        underTest = Metrics.builder().build();
        underTest.setAdditionalMetrics(
            Set.of(
                new AdditionalMetric.LongMetric("long_name", 42L),
                new AdditionalMetric.IntegerMetric("int_name", 12),
                new AdditionalMetric.DoubleMetric("double_name", 3.14),
                new AdditionalMetric.StringMetric("string_name", "foo"),
                new AdditionalMetric.BooleanMetric("bool_name", true),
                new AdditionalMetric.KeywordMetric("keyword_name", "bar"),
                new AdditionalMetric.JSONMetric("json_name", "{\"hello\":\"world\"}")
            )
        );

        assertThat(underTest.longAdditionalMetrics()).containsEntry("long_name", 42L);
        assertThat(underTest.intAdditionalMetrics()).containsEntry("int_name", 12);
        assertThat(underTest.doubleAdditionalMetrics()).containsEntry("double_name", 3.14);
        assertThat(underTest.stringAdditionalMetrics()).containsEntry("string_name", "foo");
        assertThat(underTest.boolAdditionalMetrics()).containsEntry("bool_name", true);
        assertThat(underTest.keywordAdditionalMetrics()).containsEntry("keyword_name", "bar");
        assertThat(underTest.jsonAdditionalMetrics()).containsEntry("json_name", "{\"hello\":\"world\"}");
    }
}
