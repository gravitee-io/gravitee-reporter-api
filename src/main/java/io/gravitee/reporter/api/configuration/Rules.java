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
package io.gravitee.reporter.api.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Rules {

    public static final String FIELD_WILDCARD = "*";

    private Map<String, String> renameFields = Collections.emptyMap();
    private Set<String> excludeFields = Collections.emptySet();
    private Set<String> includeFields = Collections.emptySet();

    public boolean containsRules() {
        return (
            (getRenameFields() != null && !getRenameFields().isEmpty()) ||
            (getIncludeFields() != null && !getIncludeFields().isEmpty()) ||
            (getExcludeFields() != null && !getExcludeFields().isEmpty())
        );
    }

    public void setRenameFields(Map<String, String> renameFields) {
        this.renameFields = renameFields;
    }

    public void setExcludeFields(Set<String> excludeFields) {
        this.excludeFields = excludeFields;
    }

    public void setIncludeFields(Set<String> includeFields) {
        this.includeFields = includeFields;
    }

    public Map<String, String> getRenameFields() {
        return renameFields;
    }

    public Set<String> getExcludeFields() {
        return excludeFields;
    }

    public Set<String> getIncludeFields() {
        return includeFields;
    }
}
