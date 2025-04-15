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
package io.gravitee.reporter.api.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.utils.UUID;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.common.Request;
import io.gravitee.reporter.api.common.Response;
import io.gravitee.reporter.api.configuration.Rules;
import io.gravitee.reporter.api.health.EndpointStatus;
import io.gravitee.reporter.api.health.Step;
import io.gravitee.reporter.api.http.Metrics;
import io.gravitee.reporter.api.log.Log;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonFormatterTest {

    @Test
    public void shouldRenameField() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setRenameFields(Map.of("application", "app"));

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
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("application"));

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
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("*"));

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
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("api"));
        rules.setIncludeFields(Set.of("api"));

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
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("*"));
        rules.setIncludeFields(Set.of("api"));

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
    public void shouldExcludeAllFields_butIncludeAHeaderField() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("*"));
        rules.setIncludeFields(
            Set.of(
                "log.clientRequest.method",
                "log.clientRequest.headers.host",
                "log.clientRequest.headers.Cache-Control",
                "log.clientResponse.status",
                "log.clientResponse.headers.length"
            )
        );

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        long timestamp = System.currentTimeMillis();
        Request clientRequest = new Request();
        clientRequest.setHeaders(
            HttpHeaders.create().set("host", "localhost").set("content-type", "application/json").set("Cache-Control", "no-cache")
        );
        clientRequest.setMethod(HttpMethod.GET);

        Request proxyRequest = new Request();
        proxyRequest.setHeaders(HttpHeaders.create().set("host", "localhost-2"));
        proxyRequest.setMethod(HttpMethod.GET);

        Response clientResponse = new Response();
        clientResponse.setHeaders(HttpHeaders.create().set("length", "10"));
        clientResponse.setBody("{\"hello\":\"world\"}");
        clientResponse.setStatus(200);

        Log log = new Log(timestamp);
        log.setClientRequest(clientRequest);
        log.setProxyRequest(proxyRequest);
        log.setClientResponse(clientResponse);
        Metrics metrics = Metrics.on(timestamp).build();
        metrics.setLog(log);

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals("GET", node.get("log").get("clientRequest").get("method").asText());
        Assert.assertEquals("localhost", node.get("log").get("clientRequest").get("headers").get("host").get(0).asText());
        Assert.assertEquals("no-cache", node.get("log").get("clientRequest").get("headers").get("Cache-Control").get(0).asText());
        Assert.assertFalse(node.get("log").get("clientRequest").get("headers").has("content-type"));

        Assert.assertFalse(node.get("log").has("proxyRequest"));

        Assert.assertEquals("200", node.get("log").get("clientResponse").get("status").asText());
        Assert.assertEquals("10", node.get("log").get("clientResponse").get("headers").get("length").get(0).asText());
    }

    @Test
    public void shouldExcludeAllFields_butIncludeHeaders() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("*"));
        rules.setIncludeFields(Set.of("log.clientRequest.headers"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        long timestamp = System.currentTimeMillis();
        Request clientRequest = new Request();
        clientRequest.setHeaders(HttpHeaders.create().set("host", "localhost").set("content-type", "application/json"));
        clientRequest.setMethod(HttpMethod.GET);

        Log log = new Log(timestamp);
        log.setClientRequest(clientRequest);

        Metrics metrics = Metrics.on(timestamp).build();
        metrics.setLog(log);

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertEquals("localhost", node.get("log").get("clientRequest").get("headers").get("host").get(0).asText());
        Assert.assertEquals("application/json", node.get("log").get("clientRequest").get("headers").get("content-type").get(0).asText());
    }

    @Test
    public void shouldExcludeASingleHeader() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("log.clientRequest.headers.host"));

        // Once rules are defined, init the object mapper
        ObjectMapper mapper = initObjectMapper(rules);

        long timestamp = System.currentTimeMillis();
        Request clientRequest = new Request();
        clientRequest.setHeaders(HttpHeaders.create().set("host", "localhost").set("content-type", "application/json"));
        clientRequest.setMethod(HttpMethod.GET);

        Request proxyRequest = new Request();
        proxyRequest.setHeaders(HttpHeaders.create().set("host", "localhost-2"));
        proxyRequest.setMethod(HttpMethod.GET);

        Log log = new Log(timestamp);
        log.setClientRequest(clientRequest);
        log.setProxyRequest(proxyRequest);

        Metrics metrics = Metrics.on(timestamp).build();
        metrics.setLog(log);

        String output = mapper.writeValueAsString(metrics);

        JsonNode node = new ObjectMapper().readTree(output);
        Assert.assertFalse("localhost", node.get("log").get("clientRequest").get("headers").has("host"));
        Assert.assertEquals("application/json", node.get("log").get("clientRequest").get("headers").get("content-type").get(0).asText());

        Assert.assertEquals("localhost-2", node.get("log").get("proxyRequest").get("headers").get("host").get(0).asText());
    }

    @Test
    public void shouldExcludeNestedFields() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("clientRequest"));

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
        Rules rules = new Rules();
        rules.setExcludeFields(Set.of("clientRequest.uri"));

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
    public void shouldRenameNestedProperty() throws JsonProcessingException {
        Rules rules = new Rules();
        rules.setRenameFields(Map.of("clientRequest.uri", "path"));

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
        SimpleModule module = new SimpleModule();
        module.addSerializer(HttpHeaders.class, new HttpHeadersSerializer(rules));
        mapper.registerModule(module);

        return mapper;
    }
}
