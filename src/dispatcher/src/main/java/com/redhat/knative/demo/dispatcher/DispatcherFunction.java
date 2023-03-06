package com.redhat.knative.demo.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public class DispatcherFunction {

    Logger logger = Logger.getLogger(DispatcherFunction.class);

    @ConfigProperty(name = "app.service.name", defaultValue = "app-service")
    String serviceName;
    @ConfigProperty(name = "app.revision.name", defaultValue = "app-revision")
    String revisionName;

    @Inject
    @RestClient
    EventNotifier eventNotifier;

    @Inject
    ObjectMapper mapper;

    @Funq
    public String dispatchEvent(CloudEvent<ProducedEvent> cloudEvent) {
        logger.info(revisionName + " - Event received with type " + cloudEvent.type() + " and data: " + cloudEvent.data());

        DispatchedEvent dispatchedEvent = new
                DispatchedEvent("Dispatched from " + revisionName + " at " + LocalDateTime.now());

        io.cloudevents.CloudEvent newCloudEvent = CloudEventBuilder.v1()
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withId(UUID.randomUUID().toString())
                .withType("com.redhat.knative.demo.dispatcher.Dispatched")
                .withSource(URI.create(serviceName))
                .withData(PojoCloudEventData.wrap(dispatchedEvent, mapper::writeValueAsBytes))
                .build();

        return eventNotifier.emit(newCloudEvent);
    }
}