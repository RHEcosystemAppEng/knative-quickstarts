package com.redhat.knative.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redhat.knative.demo.loader.DispatchedEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
public class LoaderFunctionTest {

    @Test
    public void testDispatchedEvent() throws JsonProcessingException {
        DispatchedEvent event = new DispatchedEvent("Test message");
        RestAssured.given().contentType("application/json")
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", "com.redhat.knative.demo.dispatcher.Dispatched")
                .header("ce-source", "test")
                .body(event)
                .post("/")
                .then().statusCode(204);
    }

    @Test
    public void testUnexpectedEvent() throws JsonProcessingException {
        String event = "Test message";
        RestAssured.given().contentType("application/json")
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", "com.redhat.knative.demo.dispatcher.Dispatched")
                .header("ce-source", "test")
                .body(event)
                .post("/")
                .then().statusCode(500)
                .header("ce-id", nullValue())
                .header("ce-type", nullValue())
                .header("ce-source", nullValue())
                .body(Matchers.notNullValue());
    }
}