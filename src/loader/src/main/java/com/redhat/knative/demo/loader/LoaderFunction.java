package com.redhat.knative.demo.loader;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;

public class LoaderFunction {
    private static final Logger log = Logger.getLogger(LoaderFunction.class);

    @ConfigProperty(name = "k-revision")
    private String revisionName;

    @Funq("com.redhat.knative.demo.Dispatched")
    public String load(CloudEvent<DispatchedEvent> event) {
        log.infof("[%s] - Event received with type %s and data: %s\n", revisionName, event.type(),
                event.data());
        return "Dispatched";
    }
}