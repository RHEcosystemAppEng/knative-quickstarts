package main

import (
	"context"
	"log"
	"os"

	cloudevents "github.com/cloudevents/sdk-go/v2"
	"github.com/dmartinol/knative-quickstarts/src/dispatcher/pkg/eventschema"
	"github.com/google/uuid"
)

func receive(ctx context.Context, event cloudevents.Event) (*cloudevents.Event, cloudevents.Result) {
	log.Printf("Event received. \n%s\n", event)
	data := &eventschema.DemoEvent{}
	if err := event.DataAs(data); err != nil {
		log.Printf("Error while extracting cloudevent Data: %s\n", err.Error())
		return nil, cloudevents.NewHTTPResult(400, "failed to convert data: %s", err)
	}
	log.Printf("Hello World Message from received event %q", data.Msg)

	// Respond with another event (optional)
	// This is optional and is intended to show how to respond back with another event after processing.
	// The response will go back into the knative eventing system just like any other event
	newEvent := cloudevents.NewEvent()
	// Setting the ID here is not necessary. When using NewDefaultClient the ID is set
	// automatically. We set the ID anyway so it appears in the log.
	newEvent.SetID(uuid.New().String())
	newEvent.SetSource("knative/eventing/samples/hello-world")
	newEvent.SetType("dev.knative.samples.hifromknative")
	if err := newEvent.SetData(cloudevents.ApplicationJSON, eventschema.HiFromKnative{Msg: "Hi from helloworld-go app!"}); err != nil {
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
		log.Printf("Responding with event\n%s\n", newEvent)
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
