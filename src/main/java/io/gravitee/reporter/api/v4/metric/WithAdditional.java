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

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface WithAdditional<T extends WithAdditional<T>> {
    /**
     * Add a new additional metric of type {@link AdditionalMetric.LongMetric}.
     * @param key   the metric key must start with 'long_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalMetric(String key, Long value) {
        return addAdditionalMetric(new AdditionalMetric.LongMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, Long> longAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.LongMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'double_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalMetric(String key, Double value) {
        return addAdditionalMetric(new AdditionalMetric.DoubleMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, Double> doubleAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.DoubleMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'keyword_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalKeywordMetric(String key, String value) {
        return addAdditionalMetric(new AdditionalMetric.KeywordMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, String> keywordAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.KeywordMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'json_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalJSONMetric(String key, String value) {
        return addAdditionalMetric(new AdditionalMetric.JSONMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, String> jsonAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.JSONMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'bool_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalMetric(String key, Boolean value) {
        return addAdditionalMetric(new AdditionalMetric.BooleanMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, Boolean> boolAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.BooleanMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'int_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalMetric(String key, Integer value) {
        return addAdditionalMetric(new AdditionalMetric.IntegerMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, Integer> intAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.IntegerMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * @param key   the metric key must start with 'string_'
     * @param value the metric value
     * @return the current instance
     */
    default T putAdditionalMetric(String key, String value) {
        return addAdditionalMetric(new AdditionalMetric.StringMetric(key, value));
    }

    /**
     * Returns a map of additional metrics where the key is a metric name and the value is a {@code Long}.
     * Only metrics of type {@link AdditionalMetric.LongMetric} are included in the returned map.
     *
     * @return a map containing the long additional metrics, or {@code null} if no such metrics exist
     */
    @Nullable
    default Map<String, String> stringAdditionalMetrics() {
        return additionalMetrics(entry ->
            entry instanceof AdditionalMetric.StringMetric d ? Stream.of(Map.entry(d.name(), d.value())) : Stream.empty()
        );
    }

    /**
     * Adds a new additional metric to the collection.
     *
     * @param metric the additional metric to be added; must be an implementation of {@link AdditionalMetric}
     *               and correctly follow the naming conventions defined for its type.
     */
    private T addAdditionalMetric(AdditionalMetric metric) {
        getAdditionalMetrics().removeIf(m -> m.name().equals(metric.name()));
        getAdditionalMetrics().add(metric);
        return (T) this;
    }

    /**
     * Retrieves a collection of additional metrics associated with the instance.
     *
     * @return a collection of {@link AdditionalMetric} representing various additional metrics;
     *         may be an empty collection if no metrics are defined but will never be null
     */
    Collection<AdditionalMetric> getAdditionalMetrics();

    /**
     * Sets the collection of additional metrics associated with the instance.
     *
     * @param additionalMetrics the collection of {@link AdditionalMetric} objects to be associated with
     *                          the instance; this collection replaces any previously defined metrics and
     *                          must not contain null elements.
     */
    void setAdditionalMetrics(Collection<AdditionalMetric> additionalMetrics);

    private <T> Map<String, T> additionalMetrics(Function<AdditionalMetric, Stream<Map.Entry<String, T>>> typeGard) {
        Map<String, T> collect = getAdditionalMetrics()
            .stream()
            .flatMap(typeGard)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return !collect.isEmpty() ? collect : null;
    }
}
