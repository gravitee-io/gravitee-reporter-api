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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import io.gravitee.reporter.api.configuration.Rules;
import io.gravitee.reporter.api.health.EndpointStatus;
import io.gravitee.reporter.api.health.Step;
import io.gravitee.reporter.api.v4.metric.AdditionalMetric;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@UtilityClass
public final class JacksonUtils {

    private static final char JSON_NESTED_SEPARATOR = '.';

    public static ObjectMapper mapper(@Nullable Rules rules) {
        var mapper = new ObjectMapper();

        if (rules != null && rules.containsRules()) {
            mapper
                .addMixIn(Reportable.class, FieldFilterMixin.class)
                .addMixIn(Request.class, FieldFilterMixin.class)
                .addMixIn(Response.class, FieldFilterMixin.class)
                .addMixIn(EndpointStatus.class, FieldFilterMixin.class)
                .addMixIn(Step.class, FieldFilterMixin.class)
                .setFilterProvider(new FieldFilterProvider(rules));
        }

        var module = new SimpleModule()
            .addSerializer(HttpHeaders.class, new HttpHeadersSerializer(rules))
            .addDeserializer(AdditionalMetric.class, new AdditionalMetricDeserialization())
            .addDeserializer(HttpHeaders.class, new HttpHeadersDeserializer());
        return mapper.registerModule(module).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String resolveJsonPath(JsonStreamContext context, PropertyWriter writer) {
        StringBuilder sb = new StringBuilder(writer != null ? writer.getName() : "");
        JsonStreamContext parent = getRealParent(context);

        while (parent != null) {
            if (parent.hasCurrentName()) {
                sb.insert(0, JSON_NESTED_SEPARATOR).insert(0, parent.getCurrentName());
            }

            parent = getRealParent(parent);
        }

        return sb.toString();
    }

    private static JsonStreamContext getRealParent(JsonStreamContext context) {
        if (context == null || context.inRoot()) {
            // No parent when current context is root.
            return null;
        }

        JsonStreamContext parent = context.getParent();

        if (parent == null) {
            return null;
        }

        if (parent.inArray()) {
            // Try to find the enclosing parent when direct parent is an array.
            return getRealParent(parent);
        } else {
            return parent;
        }
    }
}
