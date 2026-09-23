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
package io.gravitee.reporter.api.v4.report;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.gravitee.reporter.api.jackson.JacksonUtils;
import io.gravitee.reporter.api.v4.metric.event.BaseEventMetrics;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DecisionReportTest {

    static DecisionReport.DecisionReportBuilder<?, ?> minimalDecision() {
        return DecisionReport.builder()
            .timestamp(1L)
            .gatewayId("gw")
            .organizationId("o")
            .environmentId("e")
            .apiId("a")
            .eventId("evt-1")
            .phase(DecisionReport.Phase.RESOLVED)
            .decisionPointType(DecisionReport.DECISION_POINT_AUTHZ)
            .decisionPointId("default")
            .outcome(DecisionReport.Outcome.ALLOW)
            .enforced(DecisionReport.Enforced.ALLOW)
            .status(DecisionReport.Status.SUCCESS);
    }

    @Test
    void should_not_be_event_metrics() {
        assertThat(BaseEventMetrics.class.isAssignableFrom(DecisionReport.class)).isFalse();
    }

    @Test
    void should_carry_the_conclusion_and_the_effect_as_separate_values() {
        var indeterminate = minimalDecision()
            .outcome(DecisionReport.Outcome.INDETERMINATE)
            .indeterminateCause(DecisionReport.IndeterminateCause.NOT_APPLICABLE)
            .enforced(DecisionReport.Enforced.DENY)
            .build();

        assertThat(indeterminate.getOutcome()).isEqualTo(DecisionReport.Outcome.INDETERMINATE);
        assertThat(indeterminate.getIndeterminateCause()).isEqualTo(DecisionReport.IndeterminateCause.NOT_APPLICABLE);
        assertThat(indeterminate.getEnforced()).isEqualTo(DecisionReport.Enforced.DENY);
    }

    @Test
    void should_leave_case_id_unset_for_a_synchronous_decision() {
        assertThat(minimalDecision().build().getCaseId()).isNull();
    }

    @Test
    void should_treat_a_null_confidence_as_deterministic() {
        assertThat(minimalDecision().build().getConfidence()).isNull();
        assertThat(minimalDecision().confidence(0.94).build().getConfidence()).isEqualTo(0.94);
    }

    @Test
    void should_accept_point_specific_detail_through_the_additional_metrics_mixin() {
        var decision = minimalDecision()
            .build()
            .putAdditionalKeywordMetric("keyword_agent_type", "USER_EMBEDDED")
            .putAdditionalMetric("int_batch_index", 2);

        assertThat(decision.keywordAdditionalMetrics()).containsEntry("keyword_agent_type", "USER_EMBEDDED");
        assertThat(decision.intAdditionalMetrics()).containsEntry("int_batch_index", 2);
    }

    @Test
    void should_reject_an_additional_metric_whose_key_lacks_its_type_prefix() {
        var decision = minimalDecision().build();

        assertThatThrownBy(() -> decision.putAdditionalKeywordMetric("agent_type", "USER_EMBEDDED"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("keyword_");
    }

    @Test
    void should_keep_matched_rules_with_their_version_and_effect() {
        var decision = minimalDecision()
            .matchedRules(List.of(new DecisionReport.MatchedRule("policy#8", "github-writes-forbidden", "v3", "FORBID", null)))
            .build();

        assertThat(decision.getMatchedRules())
            .singleElement()
            .satisfies(rule -> {
                assertThat(rule.name()).isEqualTo("github-writes-forbidden");
                assertThat(rule.version()).isEqualTo("v3");
                assertThat(rule.effect()).isEqualTo("FORBID");
            });
    }

    @Test
    void should_throw_from_build_when_a_nonnull_field_is_missing() {
        assertThatThrownBy(() -> minimalDecision().gatewayId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("gatewayId");
        assertThatThrownBy(() -> minimalDecision().organizationId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("organizationId");
        assertThatThrownBy(() -> minimalDecision().environmentId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("environmentId");
        assertThatThrownBy(() -> minimalDecision().apiId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("apiId");
        assertThatThrownBy(() -> minimalDecision().eventId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("eventId");
        assertThatThrownBy(() -> minimalDecision().phase(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("phase");
        assertThatThrownBy(() -> minimalDecision().decisionPointType(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("decisionPointType");
        assertThatThrownBy(() -> minimalDecision().decisionPointId(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("decisionPointId");
        assertThatThrownBy(() -> minimalDecision().outcome(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("outcome");
        assertThatThrownBy(() -> minimalDecision().enforced(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("enforced");
        assertThatThrownBy(() -> minimalDecision().status(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("status");
    }

    @Test
    void should_round_trip_every_phase_through_its_label() {
        for (var value : DecisionReport.Phase.values()) {
            assertThat(DecisionReport.Phase.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_round_trip_every_outcome_through_its_label() {
        for (var value : DecisionReport.Outcome.values()) {
            assertThat(DecisionReport.Outcome.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_round_trip_every_enforced_through_its_label() {
        for (var value : DecisionReport.Enforced.values()) {
            assertThat(DecisionReport.Enforced.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_round_trip_every_indeterminate_cause_through_its_label() {
        for (var value : DecisionReport.IndeterminateCause.values()) {
            assertThat(DecisionReport.IndeterminateCause.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_round_trip_every_status_through_its_label() {
        for (var value : DecisionReport.Status.values()) {
            assertThat(DecisionReport.Status.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_round_trip_every_transformation_type_through_its_label() {
        for (var value : DecisionReport.TransformationType.values()) {
            assertThat(DecisionReport.TransformationType.fromLabel(value.getLabel())).isEqualTo(value);
        }
    }

    @Test
    void should_name_the_accepted_labels_for_an_unknown_one() {
        assertThatThrownBy(() -> DecisionReport.Outcome.fromLabel("bogus"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ALLOW")
            .hasMessageContaining("DENY")
            .hasMessageContaining("TRANSFORM")
            .hasMessageContaining("INDETERMINATE")
            .hasMessageContaining("PENDING");
    }

    @Test
    void should_serialize_enums_as_labels_and_a_matched_rule_version() {
        var mapper = JacksonUtils.mapper(null);
        var decision = minimalDecision()
            .matchedRules(List.of(new DecisionReport.MatchedRule("p1", "orders", "v3", "PERMIT", null)))
            .build();

        JsonNode json = mapper.valueToTree(decision);

        assertThat(json.get("phase").asText()).isEqualTo("RESOLVED");
        assertThat(json.get("outcome").asText()).isEqualTo("ALLOW");
        assertThat(json.get("enforced").asText()).isEqualTo("ALLOW");
        assertThat(json.get("status").asText()).isEqualTo("success");
        assertThat(json.get("matchedRules").get(0).get("version").asText()).isEqualTo("v3");
    }
}
