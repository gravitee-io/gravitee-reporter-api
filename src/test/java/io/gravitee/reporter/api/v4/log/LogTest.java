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
package io.gravitee.reporter.api.v4.log;

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.reporter.api.ReportTarget;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import java.util.Set;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LogTest {

    @Test
    void should_copy_every_field_with_to_builder() {
        Log log = Log.builder()
            .timestamp(1L)
            .targets(Set.of(ReportTarget.TRACING))
            .apiId("api-id")
            .apiName("api-name")
            .apiProductId("api-product-id")
            .requestId("request-id")
            .clientIdentifier("client-identifier")
            .organizationId("organization-id")
            .environmentId("environment-id")
            .applicationId("application-id")
            .applicationName("application-name")
            .planId("plan-id")
            .subscriptionId("subscription-id")
            .errorKey("error-key")
            .errorMessage("error-message")
            .requestEnded(true)
            .entrypointRequest(new Request())
            .endpointRequest(new Request())
            .entrypointResponse(new Response())
            .endpointResponse(new Response())
            .build();

        assertThat(log.toBuilder().build()).usingRecursiveComparison().isEqualTo(log);
    }

    @Test
    void should_not_change_a_copy_when_the_original_is_completed() {
        Log log = Log.builder().requestId("request-id").build();

        Log copy = log.toBuilder().build();
        log.setRequestEnded(true);
        log.setErrorKey("CONNECTION_CLOSED");

        assertThat(copy.isRequestEnded()).isFalse();
        assertThat(copy.getErrorKey()).isNull();
        assertThat(copy.getRequestId()).isEqualTo("request-id");
    }
}
