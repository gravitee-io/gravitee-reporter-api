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
package io.gravitee.reporter.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.reporter.api.v4.log.Log;
import io.gravitee.reporter.api.v4.log.MessageLog;
import java.util.EnumSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReportTargetTest {

    @Test
    void all_should_contain_both_targets() {
        assertThat(ReportTarget.ALL).containsExactlyInAnyOrder(ReportTarget.ANALYTICS, ReportTarget.TRACING);
    }

    @Test
    void all_should_be_unmodifiable() {
        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> ReportTarget.ALL.add(ReportTarget.ANALYTICS)
        );
    }

    @Test
    void default_should_contain_analytics_only() {
        assertThat(ReportTarget.DEFAULT).containsExactly(ReportTarget.ANALYTICS);
    }

    @Test
    void default_should_be_unmodifiable() {
        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> ReportTarget.DEFAULT.add(ReportTarget.TRACING)
        );
    }

    @Nested
    class LogTargets {

        @Test
        void should_default_to_all_targets() {
            var log = Log.builder().build();
            assertThat(log.getTargets()).isEqualTo(ReportTarget.DEFAULT);
        }

        @Test
        void should_accept_specific_targets_via_builder() {
            var log = Log.builder().targets(EnumSet.of(ReportTarget.TRACING)).build();
            assertThat(log.getTargets()).containsExactly(ReportTarget.TRACING);
        }

        @Test
        void should_accept_specific_targets_via_setter() {
            var log = Log.builder().build();
            log.setTargets(EnumSet.of(ReportTarget.ANALYTICS));
            assertThat(log.getTargets()).containsExactly(ReportTarget.ANALYTICS);
        }

        @Test
        void should_return_all_when_targets_set_to_null() {
            var log = Log.builder().build();
            log.setTargets(null);
            assertThat(log.getTargets()).isEqualTo(ReportTarget.DEFAULT);
        }
    }

    @Nested
    class MessageLogTargets {

        @Test
        void should_default_to_all_targets() {
            var messageLog = MessageLog.builder().build();
            assertThat(messageLog.getTargets()).isEqualTo(ReportTarget.DEFAULT);
        }

        @Test
        void should_accept_specific_targets_via_builder() {
            var messageLog = MessageLog.builder().targets(EnumSet.of(ReportTarget.TRACING)).build();
            assertThat(messageLog.getTargets()).containsExactly(ReportTarget.TRACING);
        }
    }

    @Nested
    class ReportableInterfaceDefault {

        @Test
        void should_default_to_all_targets_for_anonymous_reportable() {
            Reportable reportable = java.time.Instant::now;
            assertThat(reportable.getTargets()).isEqualTo(ReportTarget.DEFAULT);
        }
    }
}
