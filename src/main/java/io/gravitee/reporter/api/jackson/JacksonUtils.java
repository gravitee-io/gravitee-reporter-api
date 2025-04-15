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

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public final class JacksonUtils {

    private static final char JSON_NESTED_SEPARATOR = '.';

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
