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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Diagnostic {

    private String key;
    private String message;
    private String componentType;
    private String componentName;

    /**
     * Builds a diagnostic from a throwable. The {@code key} is the root cause's fully qualified
     * class name; the {@code message} is resolved via {@link #resolveErrorMessage(Throwable)}.
     * Returns {@code null} when {@code error} is {@code null} — callers can skip reporting.
     */
    public static Diagnostic fromError(Throwable error, String componentType, String componentName) {
        if (error == null) {
            return null;
        }
        Throwable root = ExceptionUtils.getRootCause(error);
        return new Diagnostic(root.getClass().getName(), resolveErrorMessage(error), componentType, componentName);
    }

    /**
     * Resolves a non-null throwable to its most informative message. Priority: non-blank message
     * of the root cause (last distinct node, cycle-safe via identity tracking in
     * {@link ExceptionUtils}) → first non-blank message walking outer-to-inner → root's fully
     * qualified class name.
     */
    private static String resolveErrorMessage(Throwable error) {
        Throwable root = ExceptionUtils.getRootCause(error);
        if (StringUtils.isNotBlank(root.getMessage())) {
            return root.getMessage();
        }
        for (Throwable t : ExceptionUtils.getThrowableList(error)) {
            if (StringUtils.isNotBlank(t.getMessage())) {
                return t.getMessage();
            }
        }
        return root.getClass().getName();
    }
}
