package org.acme;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.common.collect.Lists;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventBuilder;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.OpenShiftTestServer;
import io.quarkus.test.kubernetes.client.WithOpenShiftTestServer;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithOpenShiftTestServer
@QuarkusTest
class ControllerResourceTest {
    private static final Logger log = Logger.getLogger(ControllerResourceTest.class);
    private static WireMockServer sink;

    @OpenShiftTestServer
    private OpenShiftServer mockOpenShiftServer;

    @BeforeAll
    public static void startSink() {
        sink = new WireMockServer(options().port(8181));
        sink.start();
        sink.stubFor(post("/").willReturn(aResponse().withBody("ok").withStatus(200)));
    }

    @AfterAll
    public static void stopSink() {
        if (sink != null) {
            sink.stop();
        }
    }

    @Test
    public void dispatchEventTest() {
        JsonObject jsonObject = new JsonObject()
                .put("message", "Test data");
        ValidatableResponse ok = RestAssured.given().contentType("application/json")
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", "com.redhat.knative.demo.Produced")
                .header("ce-source", "test-service")
                .body(jsonObject)
                .post("/")
                .then().statusCode(200);

        log.info(ok.extract().asString());
        sink.verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("ce-source", WireMock.equalTo("test-service")));

        List<ServeEvent> allServeEvents = sink.getAllServeEvents();
        allServeEvents = Lists.reverse(allServeEvents);
        assertThat(allServeEvents, hasSize(1));

        ServeEvent event = allServeEvents.get(0);
        assertThat(event.getRequest().header("ce-type").values().get(0), containsString("com.redhat.knative.demo.Dispatched"));
        assertTrue(event.getRequest().getBodyAsString().contains("Dispatched from "));
    }
}