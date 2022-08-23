package eventschema

// ProducedEvent defines the Data of CloudEvent with type=com.redhat.knative.demo.Produced
type ProducedEvent struct {
	// Msg holds the message from the event
	Msg string `json:"msg,omitempty"`
}

// DispatchedEvent defines the Data of CloudEvent with type=com.redhat.knative.demo.Dispatched
type DispatchedEvent struct {
	// Msg holds the message from the event
	Msg string `json:"msg,omitempty"`
}
