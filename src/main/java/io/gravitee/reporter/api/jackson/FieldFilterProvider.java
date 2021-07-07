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

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.gravitee.reporter.api.configuration.Rules;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class FieldFilterProvider extends SimpleFilterProvider {

    static final String JACKSON_JSON_FILTER_NAME = "fieldFilter";

    /**
     * Creates a jackson filter provider that aims to serialize only fields corresponding to the field expression passed in parameter.
     */
    public FieldFilterProvider(Rules rules) {
        this.addFilter(JACKSON_JSON_FILTER_NAME, new FieldPropertyFilter(
                rules.getRenameFields(), rules.getIncludeFields(), rules.getExcludeFields()
        ));
    }
}
