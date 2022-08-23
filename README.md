# knative-quickstarts
Generate, display and store recurring events (polyglot)

## Sample architecture
```mermaid
graph TD
   poller(<b>sources.eventing.knative.dev.CronJobSource</b><br/>poller)
   producer(<b>serving.knative.dev.Service</b><br/>&ltPython&gt producer)
   producer-binding(<b>SinkBinding</b><br/>producer-binding)
   broker[<b>eventing.knative.dev.Broker</b><br/>default]
   poller --sink.ref--> producer
   producer-binding --subject<br/>env.K_SINK--> producer
   producer-binding --sink.ref--> broker

   dispatcher(<b>serving.knative.dev.Service</b><br/>&ltGo&gt dispatcher)
   dispatcher-binding(<b>SinkBinding</b><br/>dispatcher-binding)
   dispatcher-trigger(<b>Trigger</b><br/>dispatcher-trigger)
   dispatcher-binding --subject<br/>env.K_SINK--> dispatcher
   dispatcher-binding --sink.uri--> event-channel
   dispatcher-trigger --subscriber--> dispatcher
   dispatcher-trigger --broker--> broker

   event-channel(<b>Channel</b><br/>event-channel)

   loader(<b>serving.knative.dev.Service</b><br/>&ltQuarkus&gt loader)
   loader-subscription(<b>messaging.knative.dev.Subscription</b><br/>loader-subscription)
   loader-subscription --channel--> event-channel
   loader-subscription --subscriber--> loader

   event-display(<b>serving.knative.dev.Service</b><br/>&ltImage&gt<br/>&ltmin-scale=1&gt<br/>event-display)
   event-display-trigger(<b>Trigger</b><br/>event-display-trigger)
   event-display-subscription(<b>messaging.knative.dev.Subscription</b><br/>loader-subscription)
   event-display-trigger --subscriber--> event-display
   event-display-trigger --broker--> broker
   event-display-subscription --channel--> event-channel
   event-display-subscription --subscriber--> event-display

   event-store[(<b>v1.Service</b><br/>&ltPostgreSQL&gt<br/>event-store)]
   loader --env.POSTGRES_HOST-->event-store
```

## Installing the application

```bash
oc process -p=APP_NAMESPACE=knative-quickstarts -f config/infra | oc apply -f -
oc process -p=IMAGE_NAMESPACE=knative-quickstarts -f config/build | oc apply -f -
oc process -p=APP_NAMESPACE=knative-quickstarts -f config/app | oc apply -f -
```

```bash
oc logs -f -l app=knative-quickstarts-demo -c knative-quickstarts-demo-app
```

```bash
oc process -p=APP_NAMESPACE=knative-quickstarts -f config/infra | oc delete -f -
oc process -p=IMAGE_NAMESPACE=knative-quickstarts -f config/build | oc delete -f -
oc process -p=APP_NAMESPACE=knative-quickstarts -f config/app | oc delete -f -
```

## Uninstalling the application
```bash
oc process -p=APP_NAMESPACE=knative-quickstarts -f config/app | oc delete -f -
```

kn service update producer-python --scale-min=2
kn service update producer-python --traffic @latest=100
