package com.redhat.knative.demo.producer;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/")
@RegisterRestClient
public interface EventNotifier {

    @POST
    @Produces(JsonFormat.CONTENT_TYPE)
    String emit(CloudEvent event);
}
