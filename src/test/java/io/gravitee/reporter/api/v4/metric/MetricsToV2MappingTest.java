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

import io.gravitee.common.http.HttpMethod;
import io.gravitee.reporter.api.http.SecurityType;
import io.gravitee.reporter.api.v4.log.Log;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MetricsToV2MappingTest {

    @Test
    void toV2_should_map_all_fields_correctly() {
        // GIVEN
        var failure = new Diagnostic();
        failure.setKey("fail");
        failure.setMessage("failure");

        Metrics v4 = Metrics
            .builder()
            .enabled(true)
            .requestId("req-123")
            .transactionId("tx-999")
            .apiId("api-1")
            .apiName("My API")
            .apiType("async")
            .planId("plan-x")
            .applicationId("app-id")
            .subscriptionId("sub-1")
            .clientIdentifier("client-xyz")
            .organizationId("org-7")
            .environmentId("env-6")
            .tenant("tenant-a")
            .zone("zone-b")
            .httpMethod(HttpMethod.GET)
            .localAddress("127.0.0.1")
            .remoteAddress("10.0.0.2")
            .host("example.com")
            .uri("/test/resource")
            .pathInfo("/test")
            .mappedPath("/mapped")
            .userAgent("JUnit")
            .requestContentLength(1234)
            .requestEnded(true)
            .entrypointId("entry-1")
            .endpoint("endpoint-x")
            .endpointResponseTimeMs(42)
            .status(200)
            .responseContentLength(4321)
            .gatewayResponseTimeMs(80)
            .gatewayLatencyMs(40)
            .user("bob")
            .securityType(SecurityType.API_KEY)
            .securityToken("token-xyz")
            .errorMessage("error-msg")
            .errorKey("error-key")
            .failure(failure)
            .customMetrics(Map.of("k1", "v1"))
            .build();

        // warnings
        var warning = new Diagnostic();
        warning.setKey("warn");
        warning.setMessage("warning");
        v4.addWarning(warning);

        // additional metrics
        v4.putAdditionalKeywordMetric("keyword_b", "keyword");
        v4.putAdditionalJSONMetric("json_c", "{\"hello\":\"world\"}");
        v4.putAdditionalMetric("int_d", 123);
        v4.putAdditionalMetric("double_e", 456.789);
        v4.putAdditionalMetric("bool_f", true);
        v4.putAdditionalMetric("bool_g", false);
        v4.putAdditionalMetric("long_l", 10L);
        v4.putAdditionalMetric("string_m", "foobar");

        // optional: log
        Log log = Log.builder().timestamp(System.currentTimeMillis()).build();
        log.setRequestId("req-123");
        log.setApiId("api-1");
        log.setApiName("My API");
        v4.setLog(log);

        // WHEN
        var v2 = v4.toV2();

        // THEN — Identifiers
        assertThat(v2.getRequestId()).isEqualTo("req-123");
        assertThat(v2.getTransactionId()).isEqualTo("tx-999");
        assertThat(v2.getApi()).isEqualTo("api-1");
        assertThat(v2.getApiName()).isEqualTo("My API");
        assertThat(v2.getApplication()).isEqualTo("app-id");
        assertThat(v2.getPlan()).isEqualTo("plan-x");
        assertThat(v2.getSubscription()).isEqualTo("sub-1");
        assertThat(v2.getClientIdentifier()).isEqualTo("client-xyz");
        assertThat(v2.getOrganizationId()).isEqualTo("org-7");
        assertThat(v2.getEnvironmentId()).isEqualTo("env-6");

        // Tenant / zone
        assertThat(v2.getTenant()).isEqualTo("tenant-a");
        assertThat(v2.getZone()).isEqualTo("zone-b");

        // Request
        assertThat(v2.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(v2.getLocalAddress()).isEqualTo("127.0.0.1");
        assertThat(v2.getRemoteAddress()).isEqualTo("10.0.0.2");
        assertThat(v2.getHost()).isEqualTo("example.com");
        assertThat(v2.getUri()).isEqualTo("/test/resource");
        assertThat(v2.getPath()).isEqualTo("/test");
        assertThat(v2.getMappedPath()).isEqualTo("/mapped");
        assertThat(v2.getUserAgent()).isEqualTo("JUnit");
        assertThat(v2.getRequestContentLength()).isEqualTo(1234);

        // Endpoint / gateway
        assertThat(v2.getEndpoint()).isEqualTo("endpoint-x");
        assertThat(v2.getApiResponseTimeMs()).isEqualTo(42);
        assertThat(v2.getStatus()).isEqualTo(200);
        assertThat(v2.getResponseContentLength()).isEqualTo(4321);
        assertThat(v2.getProxyResponseTimeMs()).isEqualTo(80);
        assertThat(v2.getProxyLatencyMs()).isEqualTo(40);

        // Security
        assertThat(v2.getUser()).isEqualTo("bob");
        assertThat(v2.getSecurityType()).isEqualTo(SecurityType.API_KEY);
        assertThat(v2.getSecurityToken()).isEqualTo("token-xyz");

        // Errors
        assertThat(v2.getMessage()).isEqualTo("error-msg");
        assertThat(v2.getErrorKey()).isEqualTo("error-key");
        assertThat(v2.getFailure().getMessage()).isEqualTo("failure");
        assertThat(v2.getWarnings()).hasSize(1);
        assertThat(v2.getWarnings().iterator().next().getMessage()).isEqualTo("warning");

        // Custom metrics
        assertThat(v2.getCustomMetrics()).containsEntry("k1", "v1");

        // Additional metrics
        assertThat(v2.getAdditionalMetrics()).hasSize(8);

        // Log
        assertThat(v2.getLog()).isNotNull();
        assertThat(v2.getLog().getRequestId()).isEqualTo("req-123");
        assertThat(v2.getLog().getApi()).isEqualTo("api-1");
        assertThat(v2.getLog().getApiName()).isEqualTo("My API");
    }
}
