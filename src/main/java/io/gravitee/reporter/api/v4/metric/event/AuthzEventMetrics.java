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

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@ToString(callSuper = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED, force = true)
public class AuthzEventMetrics extends BaseEventMetrics {

    private static final String DOCUMENT_TYPE = "authz";

    public static final String OPERATION_EVALUATE = "evaluate";
    public static final String OPERATION_SEARCH = "search";

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_NOT_READY = "not-ready";

    public static final String DECISION_PERMIT = "PERMIT";
    public static final String DECISION_FORBID = "FORBID";
    public static final String DECISION_NOT_APPLICABLE = "NOT_APPLICABLE";

    public static final String EFFECT_PERMIT = "PERMIT";
    public static final String EFFECT_FORBID = "FORBID";

    public static final String CALLER_PEP = "pep";
    public static final String CALLER_GATEWAY = "gateway";
    public static final String CALLER_AUTHZEN = "authzen";
    public static final String CALLER_UNKNOWN = "unknown";

    public static final String SEARCH_TYPE_SUBJECT = "subject";
    public static final String SEARCH_TYPE_RESOURCE = "resource";
    public static final String SEARCH_TYPE_ACTION = "action";

    public record MatchedPolicy(String id, String name, String effect, Map<String, String> annotations) {}

    @NonNull
    private String operation;

    @NonNull
    private String eventId;

    @NonNull
    private String status;

    /** Correlates with the gateway request document that triggered this event. */
    @Nullable
    private String requestId;

    @Nullable
    private String caller;

    @Nullable
    private String targetPdpId;

    @Nullable
    private Long policyGeneration;

    @Nullable
    private String batchId;

    @Nullable
    private Integer batchIndex;

    @Nullable
    private Integer batchSize;

    @Nullable
    private String subjectType;

    @Nullable
    private String subjectId;

    @Nullable
    private String action;

    @Nullable
    private String resourceType;

    @Nullable
    private String resourceId;

    /** Evaluate only. */
    @Nullable
    private String decision;

    @Nullable
    private List<MatchedPolicy> matchedPolicies;

    @Nullable
    private List<String> reasons;

    /** Search only. */
    @Nullable
    private String searchType;

    @Nullable
    private Integer resultCount;

    @Nullable
    private Integer pageSize;

    @Nullable
    private Boolean hasMore;

    @Nullable
    private String errorType;

    @Nullable
    private Long durationNanos;

    @Override
    public String getDocumentType() {
        return DOCUMENT_TYPE;
    }
}
