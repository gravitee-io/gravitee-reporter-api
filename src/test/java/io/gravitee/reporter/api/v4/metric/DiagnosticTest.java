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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DiagnosticTest {

    @Test
    void should_build_diagnostic_from_error_with_root_cause_message_and_class_name_as_key() {
        Throwable error = new RuntimeException("wrapper", new IllegalStateException("root cause"));

        Diagnostic diagnostic = Diagnostic.fromError(error, "kafka-reactor", "kafka-reactor");

        assertThat(diagnostic.getKey()).isEqualTo(IllegalStateException.class.getName());
        assertThat(diagnostic.getMessage()).isEqualTo("root cause");
        assertThat(diagnostic.getComponentType()).isEqualTo("kafka-reactor");
        assertThat(diagnostic.getComponentName()).isEqualTo("kafka-reactor");
    }

    @Test
    void should_fall_back_to_class_name_when_no_message_in_chain() {
        Diagnostic diagnostic = Diagnostic.fromError(new NullPointerException(), "k", "k");

        assertThat(diagnostic.getKey()).isEqualTo(NullPointerException.class.getName());
        assertThat(diagnostic.getMessage()).isEqualTo(NullPointerException.class.getName());
    }

    @Test
    void should_return_null_when_error_is_null() {
        assertThat(Diagnostic.fromError(null, "k", "k")).isNull();
    }

    @Test
    void should_return_class_name_on_cycle_with_null_messages() {
        Throwable a = new RuntimeException();
        Throwable b = new RuntimeException();
        a.initCause(b);
        b.initCause(a);

        assertThat(Diagnostic.fromError(a, "k", "k").getMessage()).isEqualTo(RuntimeException.class.getName());
    }

    @Test
    void should_skip_blank_messages_and_return_next_non_blank_in_chain() {
        Throwable error = new RuntimeException("wrapper", new IllegalStateException("   ", new NullPointerException("")));

        assertThat(Diagnostic.fromError(error, "k", "k").getMessage()).isEqualTo("wrapper");
    }

    @Test
    void should_return_middle_message_when_only_middle_exception_has_one() {
        Throwable root = new NullPointerException();
        Throwable middle = new IllegalStateException("middle cause", root);
        Throwable outer = new RuntimeException((String) null, middle);

        assertThat(Diagnostic.fromError(outer, "k", "k").getMessage()).isEqualTo("middle cause");
    }
}
