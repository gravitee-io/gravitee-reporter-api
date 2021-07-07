/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.util.Maps;
import io.gravitee.common.utils.UUID;
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import io.gravitee.reporter.api.configuration.Rules;
import io.gravitee.reporter.api.health.EndpointStatus;
import io.gravitee.reporter.api.health.Step;
import io.gravitee.reporter.api.http.Metrics;
import io.gravitee.reporter.api.log.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonFormatterTest {

    @Test
    public void shouldRenameField() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getRenameFields()).thenReturn(Maps.<String, String>builder().put("application", "app").build());

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Metrics metrics = Metrics.on(System.currentTimeMillis()).build();
        metrics.setApi("my-api");
        metrics.setApplication("my-application");
        metrics.setRemoteAddress("0.0.0.0");

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertFalse(node.has("application"));
        Assert.assertTrue(node.has("app"));
    }

    @Test
    public void shouldFilterField() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("application"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Metrics metrics = Metrics.on(System.currentTimeMillis()).build();
        metrics.setApi("my-api");
        metrics.setApplication("my-application");
        metrics.setRemoteAddress("0.0.0.0");

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertFalse(node.has("application"));
    }

    @Test
    public void shouldExcludeAllFields() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("*"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Metrics metrics = Metrics.on(System.currentTimeMillis()).build();
        metrics.setApi("my-api");
        metrics.setApplication("my-application");
        metrics.setRemoteAddress("0.0.0.0");

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals(0, node.size());
    }

    @Test
    public void shouldExcludeAndIncludeSameField() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("api"));
        Mockito.when(rules.getIncludeFields()).thenReturn(Collections.singleton("api"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Metrics metrics = Metrics.on(System.currentTimeMillis()).build();
        metrics.setApi("my-api");
        metrics.setApplication("my-application");
        metrics.setRemoteAddress("0.0.0.0");

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertTrue(node.has("api"));
    }

    @Test
    public void shouldExcludeAllFields_butIncludeOneField() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("*"));
        Mockito.when(rules.getIncludeFields()).thenReturn(Collections.singleton("api"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Metrics metrics = Metrics.on(System.currentTimeMillis()).build();
        metrics.setApi("my-api");
        metrics.setApplication("my-application");
        metrics.setRemoteAddress("0.0.0.0");

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals(1, node.size());
        Assert.assertTrue(node.has("api"));
    }

    @Test
    public void shouldExcludeNestedFields() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("clientRequest"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Log log = new Log(System.currentTimeMillis());
        log.setApi("my-api");
        log.setRequestId(UUID.random().toString());

        Request request = new Request();
        request.setUri("http://gravitee.io");
        log.setClientRequest(request);

        Response response = new Response();
        response.setStatus(HttpStatusCode.OK_200);

        log.setClientResponse(response);

        String output = mapper.writeValueAsString(log);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals(4, node.size());
        Assert.assertFalse(node.has("clientRequest"));
    }

    @Test
    public void shouldExcludeNestedProperty() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getExcludeFields()).thenReturn(Collections.singleton("clientRequest.uri"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Log log = new Log(System.currentTimeMillis());
        log.setApi("my-api");
        log.setRequestId(UUID.random().toString());

        Request request = new Request();
        request.setUri("http://gravitee.io");
        log.setClientRequest(request);

        Response response = new Response();
        response.setStatus(HttpStatusCode.OK_200);

        log.setClientResponse(response);

        String output = mapper.writeValueAsString(log);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals(5, node.size());
        Assert.assertTrue(node.has("clientRequest"));
        Assert.assertEquals(0, node.get("clientRequest").size());
    }

    @Test
    public void shouldRenameNestedPrgetExcludeFieldsoperty() throws JsonProcessingException {
        Rules rules = Mockito.mock(Rules.class);
        Mockito.when(rules.getRenameFields()).thenReturn(Maps.<String, String>builder().put("clientRequest.uri", "path").build());

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        Log log = new Log(System.currentTimeMillis());
        log.setApi("my-api");
        log.setRequestId(UUID.random().toString());

        Request request = new Request();
        request.setUri("http://gravitee.io");
        log.setClientRequest(request);

        Response response = new Response();
        response.setStatus(HttpStatusCode.OK_200);

        log.setClientResponse(response);

        String output = mapper.writeValueAsString(log);

        JsonNode node = new ObjectMapper().readTree(output);

        Assert.assertEquals(5, node.size());
        Assert.assertTrue(node.has("clientRequest"));
        Assert.assertEquals(1, node.get("clientRequest").size());
        Assert.assertFalse(node.get("clientRequest").has("uri"));
        Assert.assertTrue(node.get("clientRequest").has("path"));
    }

    private ObjectMapper initObjectMapper(Rules rules) {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(Reportable.class, FieldFilterMixin.class);
        mapper.addMixIn(Request.class, FieldFilterMixin.class);
        mapper.addMixIn(Response.class, FieldFilterMixin.class);
        mapper.addMixIn(EndpointStatus.class, FieldFilterMixin.class);
        mapper.addMixIn(Step.class, FieldFilterMixin.class);
        mapper.setFilterProvider(new FieldFilterProvider(rules));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
