package com.redhat.knative.demo.loader;

import com.redhat.knative.demo.LoadedRecord;
import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.transaction.Transactional;

public class LoaderFunction {
    private static final Logger log = Logger.getLogger(LoaderFunction.class);

    @ConfigProperty(name = "k-revision")
    private String revisionName;

    @Funq("com.redhat.knative.demo.dispatcher.Dispatched")
    @Transactional
    public void load(CloudEvent<DispatchedEvent> event) {
        DispatchedEvent dispatchedEvent = event.data();
        log.infof("[%s] - Event received with type %s and data: %s\n", revisionName, event.type(),
                dispatchedEvent);

        LoadedRecord record = new LoadedRecord(event.type(),dispatchedEvent.getMsg());
        record.persist();
        log.infof("[%s] - Record persisted as: %s\n", revisionName, record);
    }
}