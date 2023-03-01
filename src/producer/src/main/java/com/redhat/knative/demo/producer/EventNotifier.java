package com.redhat.knative.demo.producer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;


@Path("/")
@RegisterRestClient
public interface EventNotifier {

    @POST
    @Produces(JsonFormat.CONTENT_TYPE)
    String emit(CloudEvent event);
}
