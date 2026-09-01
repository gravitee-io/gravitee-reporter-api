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
package io.gravitee.reporter.api.v4.metric.event;

import static org.assertj.core.api.Assertions.*;

import io.gravitee.reporter.api.v4.metric.AdditionalMetric;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DecisionEventMetricsTest {

    private static DecisionEventMetrics.DecisionEventMetricsBuilder<?, ?> aDecision() {
        return DecisionEventMetrics
            .builder()
            .gatewayId("gw")
            .organizationId("org")
            .environmentId("env")
            .apiId("api")
            .eventId("evt")
            .phase(DecisionEventMetrics.Phase.RESOLVED)
            .decisionPointType(DecisionEventMetrics.DECISION_POINT_AUTHZ)
            .decisionPointId("default")
            .outcome(DecisionEventMetrics.Outcome.ALLOW)
            .enforced(DecisionEventMetrics.Enforced.ALLOW)
            .status(DecisionEventMetrics.Status.SUCCESS);
    }

    @Test
    void should_report_decision_as_its_document_type() {
        assertThat(aDecision().build().getDocumentType()).isEqualTo("decision");
    }

    @Test
    void should_carry_the_conclusion_and_the_effect_as_separate_values() {
        // The pair that AuthzEventMetrics cannot express: no conclusion was reached,
        // yet the enforcement point still did something.
        var indeterminate = aDecision()
            .outcome(DecisionEventMetrics.Outcome.INDETERMINATE)
            .indeterminateCause(DecisionEventMetrics.IndeterminateCause.NOT_APPLICABLE)
            .enforced(DecisionEventMetrics.Enforced.DENY)
            .build();

        assertThat(indeterminate.getOutcome()).isEqualTo(DecisionEventMetrics.Outcome.INDETERMINATE);
        assertThat(indeterminate.getIndeterminateCause()).isEqualTo(DecisionEventMetrics.IndeterminateCause.NOT_APPLICABLE);
        assertThat(indeterminate.getEnforced()).isEqualTo(DecisionEventMetrics.Enforced.DENY);
    }

    @Test
    void should_leave_case_id_unset_for_a_synchronous_decision() {
        // caseId groups records of one decision taken over time. A synchronous decision
        // has a single record, so an absent caseId is the statement that none is needed.
        assertThat(aDecision().build().getCaseId()).isNull();
    }

    @Test
    void should_treat_a_null_confidence_as_deterministic() {
        assertThat(aDecision().build().getConfidence()).isNull();
        assertThat(aDecision().confidence(0.94).build().getConfidence()).isEqualTo(0.94);
    }

    @Test
    void should_accept_point_specific_detail_through_the_additional_metrics_mixin() {
        var decision = aDecision()
            .build()
            .putAdditionalKeywordMetric("keyword_agent_type", "USER_EMBEDDED")
            .putAdditionalMetric("int_batch_index", 2);

        assertThat(decision.keywordAdditionalMetrics()).containsEntry("keyword_agent_type", "USER_EMBEDDED");
        assertThat(decision.intAdditionalMetrics()).containsEntry("int_batch_index", 2);
    }

    @Test
    void should_reject_an_additional_metric_whose_key_lacks_its_type_prefix() {
        var decision = aDecision().build();

        assertThatThrownBy(() -> decision.putAdditionalKeywordMetric("agent_type", "USER_EMBEDDED"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("keyword_");
    }

    @Test
    void should_keep_matched_rules_with_their_effect() {
        var decision = aDecision()
            .matchedRules(List.of(new DecisionEventMetrics.MatchedRule("policy#8", "github-writes-forbidden", "FORBID", null)))
            .build();

        assertThat(decision.getMatchedRules())
            .singleElement()
            .satisfies(rule -> {
                assertThat(rule.name()).isEqualTo("github-writes-forbidden");
                assertThat(rule.effect()).isEqualTo("FORBID");
            });
    }

    @Test
    void should_expose_the_base_dimensions_key() {
        assertThat(aDecision().planId("plan").applicationId("app").build().dimensionsKey()).isEqualTo("gw:org:env:api:plan:app");
    }
}
