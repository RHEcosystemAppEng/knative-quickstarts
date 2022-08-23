package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	cloudevents "github.com/cloudevents/sdk-go/v2"
	"github.com/dmartinol/knative-quickstarts/src/dispatcher/pkg/eventschema"
	"github.com/google/uuid"
)

func receive(ctx context.Context, event cloudevents.Event) (*cloudevents.Event, cloudevents.Result) {
	serviceName, isSet := os.LookupEnv("K_SERVICE")
	if !isSet {
		serviceName = "service-NA"
	}
	revisionName, isSet := os.LookupEnv("K_REVISION")
	if !isSet {
		revisionName = "revision-NA"
	}
	data := &eventschema.ProducedEvent{}
	if err := event.DataAs(data); err != nil {
		log.Printf("[%s] - Error while extracting cloudevent Data: %s\n", revisionName, err.Error())
		return nil, cloudevents.NewHTTPResult(400, "failed to convert data: %s", err)
	}
	log.Printf("[%s] - Event received with type %s and data: %s\n", revisionName, event.Type(), data)

	newEvent := cloudevents.NewEvent()
	// Setting the ID here is not necessary. When using NewDefaultClient the ID is set
	// automatically. We set the ID anyway so it appears in the log.
	newEvent.SetID(uuid.New().String())
	newEvent.SetSource(serviceName)
	newEvent.SetType("com.redhat.knative.demo.Dispatched")
	now := time.Now().Format("2006-01-02 15:04:05")

	dispatchedEvent := eventschema.DispatchedEvent{Msg: fmt.Sprintf("Dispatched from %s at %s", revisionName, now)}
	if err := newEvent.SetData(cloudevents.ApplicationJSON, dispatchedEvent); err != nil {
		return nil, cloudevents.NewHTTPResult(500, "failed to set response data: %s", err)
	}

	destination, isSet := os.LookupEnv("K_SINK")
	if !isSet {
		destination = "http://localhost:8080"
	}
	c, err := cloudevents.NewClientHTTP()
	if err != nil {
		log.Fatalf("Failed to create client, %v", err)
	}
	dispatchCtx := cloudevents.ContextWithTarget(context.Background(), destination)
	if result := c.Send(dispatchCtx, newEvent); cloudevents.IsUndelivered(result) {
		log.Printf("[%s] - Sending CloudEvent of type %s with data %s to %s\n", revisionName, newEvent.Type(), dispatchedEvent, destination)
		return &newEvent, nil
	}
	return nil, nil
}

func main() {
	log.Print("Dispatcher app started.")
	c, err := cloudevents.NewDefaultClient()
	if err != nil {
		log.Fatalf("failed to create client, %v", err)
	}
	log.Fatal(c.StartReceiver(context.Background(), receive))
}
