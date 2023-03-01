package com.redhat.knative.demo.producer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.quarkus.funqy.Funq;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;


public class ControllerResource {

    Logger logger = Logger.getLogger(ControllerResource.class);

    @ConfigProperty(name = "app.service.name", defaultValue = "app-service")
    String serviceName;
    @ConfigProperty(name = "app.revision.name", defaultValue = "app-revision")
    String revisionName;

    @Inject
    @RestClient
    EventNotifier eventNotifier;

    @Funq
    public String produceEvent() {
        logger.info("Quarkus Producer, serviceName: " + serviceName + ", revision: " + revisionName);

        JsonObject jsonObject = new JsonObject()
                .put("message", "A new message from " + revisionName + " at " + LocalDateTime.now());

        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withId(UUID.randomUUID().toString())
                .withType("com.redhat.knative.demo.Produced")
                .withSource(URI.create(serviceName))
                .withData(jsonObject.toString().getBytes())
                .build();

        return eventNotifier.emit(cloudEvent);
    }
}