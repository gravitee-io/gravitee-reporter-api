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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.gravitee.reporter.api.v4.metric.AdditionalMetric;
import io.gravitee.reporter.api.v4.metric.WithAdditional;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * One record per point in a flow that says yes or no — a policy engine, a guardian agent, a human
 * approver, an external authority.
 *
 * <p>Two fields carry the result and they are not interchangeable. {@code outcome} is what the
 * decision point <em>concluded</em>, and it may conclude nothing ({@code INDETERMINATE},
 * {@code PENDING}). {@code enforced} is what the system <em>did</em>, and it is never unknown —
 * something always happened. A guardian that fails open concludes {@code INDETERMINATE} while the
 * call is enforced as {@code ALLOW}; recording only the effect would make that indistinguishable
 * from a control that ran and permitted.
 *
 * <p>{@code verdict} keeps the point's own word for its conclusion, verbatim, so a native
 * vocabulary survives alongside the closed axis that makes cross-point queries possible.
 *
 * <p><strong>What a producer must set.</strong> The four inherited from {@link BaseEventMetrics}
 * ({@code gatewayId}, {@code organizationId}, {@code environmentId}, {@code apiId}) plus the fields
 * marked {@code @NonNull} below; omitting any of them throws from {@code build()}. Set
 * {@code timestamp} too — nothing downstream backfills it, and an unset one is a primitive
 * {@code 0}, so the record is indexed at the epoch instead of being rejected.
 *
 * <p>Fields with a closed vocabulary name their accepted values below and declare them as
 * constants on this class. Prefer the constants over string literals.
 *
 * @author GraviteeSource Team
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@ToString(callSuper = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED, force = true)
public class DecisionEventMetrics extends BaseEventMetrics implements WithAdditional<DecisionEventMetrics> {

    private static final String DOCUMENT_TYPE = "decision";

    /** Lets one lookup serve every vocabulary below; the label is the wire value, pinned here. */
    private interface Labelled {
        String getLabel();
    }

    public static final String DECISION_POINT_AUTHZ = "authz";
    public static final String DECISION_POINT_GUARDIAN = "guardian";
    public static final String DECISION_POINT_HUMAN_APPROVAL = "human-approval";
    public static final String DECISION_POINT_EXTERNAL_APPROVAL = "external-approval";

    /**
     * Where the record sits in the decision's own lifecycle — not where it sits in the request flow,
     * which is {@code checkpoint}. A point that answers immediately emits one {@link #RESOLVED}
     * record and never a {@link #REQUESTED} one.
     */
    public enum Phase implements Labelled {
        /** The decision was asked for and has not come back. Pairs with {@link Outcome#PENDING}. */
        REQUESTED("REQUESTED"),
        /** The decision came back, whatever it concluded. The only phase a synchronous point uses. */
        RESOLVED("RESOLVED");

        private final String label;

        Phase(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static Phase fromLabel(String label) {
            return lookup(Phase.class, values(), label);
        }
    }

    /**
     * What the decision point <em>concluded</em> — its judgement, which may be no judgement at all.
     *
     * <p>Deliberately not the same set as {@link Enforced}, and the difference is the point of both
     * existing. Only an outcome can be {@link #INDETERMINATE} or {@link #PENDING}, because only a
     * judgement can be absent; only an effect can be {@link Enforced#SUSPEND}, because holding a
     * call is something the system does, not something a point concludes. Recording one and
     * inferring the other loses exactly the cases worth auditing — a guardian that timed out and was
     * allowed through reads as a clean approval.
     */
    public enum Outcome implements Labelled {
        /** The point judged the call acceptable as it stands. */
        ALLOW("ALLOW"),
        /** The point judged the call unacceptable. */
        DENY("DENY"),
        /** Acceptable only in altered form; {@link TransformationType} says how. */
        TRANSFORM("TRANSFORM"),
        /** No judgement reached — it errored, timed out, or had no rule to apply. Set {@code indeterminateCause}. */
        INDETERMINATE("INDETERMINATE"),
        /** No judgement yet; one is still expected. Pairs with {@link Phase#REQUESTED}. */
        PENDING("PENDING");

        private final String label;

        Outcome(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static Outcome fromLabel(String label) {
            return lookup(Outcome.class, values(), label);
        }
    }

    /**
     * What the system actually <em>did</em> with the call. Never unknown: whatever the point
     * concluded, something happened next, and that something is always one of these.
     *
     * <p>This is the field to trust when asking what reached the backend. {@link Outcome} says what
     * was decided; a fail-open pairs {@link Outcome#INDETERMINATE} with {@link #ALLOW}, a fail-closed
     * pairs the same outcome with {@link #DENY}.
     */
    public enum Enforced implements Labelled {
        /** The call proceeded unaltered. */
        ALLOW("ALLOW"),
        /** The call was stopped. */
        DENY("DENY"),
        /** The call proceeded, altered; {@link TransformationType} says how. */
        TRANSFORM("TRANSFORM"),
        /** The call was held pending a decision that had not arrived — neither let through nor refused. */
        SUSPEND("SUSPEND");

        private final String label;

        Enforced(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static Enforced fromLabel(String label) {
            return lookup(Enforced.class, values(), label);
        }
    }

    /** Why no judgement was reached. Set only alongside {@link Outcome#INDETERMINATE}. */
    public enum IndeterminateCause implements Labelled {
        /** The point ran and found nothing that applied to this call. */
        NOT_APPLICABLE("NOT_APPLICABLE"),
        /** The point did not answer in time. */
        TIMEOUT("TIMEOUT"),
        /** The point failed. {@code errorType} carries the detail. */
        ERROR("ERROR"),
        /** The point was not yet able to serve decisions — starting up, or missing its ruleset. */
        NOT_READY("NOT_READY");

        private final String label;

        IndeterminateCause(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static IndeterminateCause fromLabel(String label) {
            return lookup(IndeterminateCause.class, values(), label);
        }
    }

    /**
     * Whether the evaluation itself ran, which is not whether it approved. A point that runs
     * perfectly and denies the call is {@link #SUCCESS} with {@link Outcome#DENY}; {@link #ERROR} is
     * for the evaluation breaking, and then the outcome is {@link Outcome#INDETERMINATE}.
     */
    public enum Status implements Labelled {
        SUCCESS("success"),
        ERROR("error");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static Status fromLabel(String label) {
            return lookup(Status.class, values(), label);
        }
    }

    /**
     * How the content was altered. All three describe what was done to the payload, never what it
     * contained — the distinction between them is what happens to the original value.
     */
    public enum TransformationType implements Labelled {
        /** The value was removed outright and nothing stands in its place. Length is not preserved. */
        REDACT("REDACT"),
        /** The value was replaced by a placeholder of the same shape, so the payload stays well formed. */
        MASK("MASK"),
        /** The value was replaced by a different meaningful value — a rephrasing, not a removal. */
        REWRITE("REWRITE");

        private final String label;

        TransformationType(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static TransformationType fromLabel(String label) {
            return lookup(TransformationType.class, values(), label);
        }
    }

    private static <E extends Enum<E> & Labelled> E lookup(Class<E> type, E[] values, String label) {
        return Arrays
            .stream(values)
            .filter(v -> v.getLabel().equals(label))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Unknown " +
                    type.getSimpleName() +
                    ": '" +
                    label +
                    "'. Accepted: " +
                    Arrays.stream(values).map(Labelled::getLabel).toList()
                )
            );
    }

    /** A policy, a detector, or whatever else the point matched on. */
    public record MatchedRule(String id, String name, String effect, Map<String, String> annotations) {}

    /* ── identity and lifecycle ─────────────────────────────────────────── */

    /** Required. Identifies this record. */
    @NonNull
    private String eventId;

    /**
     * Groups the records of one decision taken over time. Null unless the decision actually spans
     * records — a synchronous decision has a single record and needs no grouping.
     */
    @Nullable
    private String caseId;

    /** Groups decisions taken together in one call. Null unless the call decided several things. */
    @Nullable
    private String batchId;

    /**
     * Required. One of {@link #PHASE_REQUESTED}, {@link #PHASE_RESOLVED}. A synchronous point emits a
     * single {@code RESOLVED} record; only an asynchronous one emits {@code REQUESTED} first.
     */
    @NonNull
    private Phase phase;

    /* ── which decision point ───────────────────────────────────────────── */

    /**
     * Required. What kind of point decided: {@link #DECISION_POINT_AUTHZ},
     * {@link #DECISION_POINT_GUARDIAN}, {@link #DECISION_POINT_HUMAN_APPROVAL},
     * {@link #DECISION_POINT_EXTERNAL_APPROVAL}.
     */
    @NonNull
    private String decisionPointType;

    /** Required. Which instance of that kind decided, for example {@code guardian-agent}. */
    @NonNull
    private String decisionPointId;

    /**
     * What ruleset was in force: a policy generation, a model version, a workflow version. A string
     * rather than a number, because the same slot has to hold {@code gpt-4o-2024-11}.
     */
    @Nullable
    private String decisionPointVersion;

    /** Where in the flow the point sat, as {@code <flow phase>.<what was inspected>}. */
    @Nullable
    private String checkpoint;

    @Nullable
    private String caller;

    /* ── what was being decided ─────────────────────────────────────────── */

    /** RFC 8693 {@code sub} — the principal the decision is about. */
    @Nullable
    private String subjectType;

    @Nullable
    private String subjectId;

    /** RFC 8693 {@code act} — the delegate. Present only under delegation. */
    @Nullable
    private String actorType;

    @Nullable
    private String actorId;

    @Nullable
    private String action;

    @Nullable
    private String resourceType;

    @Nullable
    private String resourceId;

    /**
     * Canonical hash of the arguments the point actually judged — never the arguments themselves.
     * Carried on both records of an asynchronous decision, so an auditor can prove the thing
     * approved is the thing executed.
     */
    @Nullable
    private String argsHash;

    /* ── the conclusion, and the effect ─────────────────────────────────── */

    /**
     * Required. What the point <em>concluded</em>: {@link #OUTCOME_ALLOW}, {@link #OUTCOME_DENY},
     * {@link #OUTCOME_TRANSFORM}, {@link #OUTCOME_INDETERMINATE}, {@link #OUTCOME_PENDING}. May
     * conclude nothing; pair with {@link #enforced}, which never can.
     */
    @NonNull
    private Outcome outcome;

    /**
     * Required. What the system <em>did</em>: {@link #ENFORCED_ALLOW}, {@link #ENFORCED_DENY},
     * {@link #ENFORCED_TRANSFORM}, {@link #ENFORCED_SUSPEND}. Never unknown — a point that failed
     * open still records the effect it had.
     */
    @NonNull
    private Enforced enforced;

    /** The point's own word for its conclusion, verbatim. */
    @Nullable
    private String verdict;

    /**
     * Why no conclusion was reached: {@link #INDETERMINATE_NOT_APPLICABLE},
     * {@link #INDETERMINATE_TIMEOUT}, {@link #INDETERMINATE_ERROR}, {@link #INDETERMINATE_NOT_READY}.
     * Set only alongside {@link #OUTCOME_INDETERMINATE}.
     */
    @Nullable
    private IndeterminateCause indeterminateCause;

    /** Null means deterministic. That is a statement, not a gap. */
    @Nullable
    private Double confidence;

    @Nullable
    private List<String> reasons;

    @Nullable
    private List<MatchedRule> matchedRules;

    /* ── transformative decisions ───────────────────────────────────────── */

    @Nullable
    private Boolean transformed;

    /**
     * What was done: {@link #TRANSFORMATION_REDACT}, {@link #TRANSFORMATION_REWRITE},
     * {@link #TRANSFORMATION_MASK}. Never what it was done to.
     */
    @Nullable
    private TransformationType transformationType;

    /* ── who decided, for human and external points ─────────────────────── */

    /** Who was required to approve — a specification, not an identity. */
    @Nullable
    private String requiredApprover;

    @Nullable
    private String deciderType;

    @Nullable
    private String deciderId;

    @Nullable
    private String channel;

    /* ── correlation ────────────────────────────────────────────────────── */

    @Nullable
    private String requestId;

    @Nullable
    private String traceId;

    @Nullable
    private String conversationId;

    /** Reserved. Opaque, and never interpreted here. */
    @Nullable
    private String missionId;

    /* ── execution ──────────────────────────────────────────────────────── */

    /**
     * Required. Whether the evaluation itself ran: {@link #STATUS_SUCCESS}, {@link #STATUS_ERROR}.
     * Distinct from {@link #outcome} — a point runs successfully and still concludes {@code DENY}.
     */
    @NonNull
    private Status status;

    @Nullable
    private String errorType;

    @Nullable
    private Long durationNanos;

    /** Asynchronous points only: {@code REQUESTED} to {@code RESOLVED}. */
    @Nullable
    private Long waitedNanos;

    /* ── point-specific detail ──────────────────────────────────────────── */

    @Builder.Default
    private Collection<AdditionalMetric> additionalMetrics = new HashSet<>();

    @Override
    public void setAdditionalMetrics(Collection<AdditionalMetric> additionalMetrics) {
        Objects.requireNonNull(additionalMetrics, "Additional metrics must not be null");
        this.additionalMetrics = new HashSet<>(additionalMetrics.size());
        this.additionalMetrics.addAll(additionalMetrics);
    }

    @Override
    public String getDocumentType() {
        return DOCUMENT_TYPE;
    }
}
