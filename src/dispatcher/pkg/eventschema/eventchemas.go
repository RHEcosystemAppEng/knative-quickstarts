package eventschema

// HelloWorld defines the Data of CloudEvent with type=dev.knative.samples.helloworld
type DemoEvent struct {
	// Msg holds the message from the event
	Msg string `json:"msg,omitempty"`
}

// HiFromKnative defines the Data of CloudEvent with type=dev.knative.samples.hifromknative
type HiFromKnative struct {
	// Msg holds the message from the event
	Msg string `json:"msg,omitempty"`
}
