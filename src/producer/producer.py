from flask import Flask, request, make_response
from cloudevents.http import CloudEvent, to_structured
import requests
import uuid
import os
from datetime import datetime

app = Flask(__name__)

@app.route('/', methods=['POST'])
def produce_new_event():
    serviceName = os.environ['K_SERVICE']
    revisionName = os.environ['K_REVISION']
    app.logger.info(f'[{revisionName}] - Received CloudEvent with type {request.type} and data: {request.data}')

    # Create a CloudEvent
    # - The CloudEvent "id" is generated if omitted. "specversion" defaults to "1.0".
    attributes = {
        "type": "com.redhat.knative.demo.Produced",
        "source": serviceName
    }
    now = datetime.now().strftime("%m/%d/%Y, %H:%M:%S")
    data = {"message": f"A new message from {revisionName} at {now}"}
    event = CloudEvent(attributes, data)

    # Creates the HTTP request representation of the CloudEvent in structured content mode
    headers, body = to_structured(event)

    sinkUrl = os.environ['K_SINK']
    app.logger.info(f'[{revisionName}] - Sending CloudEvent of type {event._attributes["type"]} with data {event.data} to {sinkUrl}')

    # POST
    response = requests.post(sinkUrl, data=body, headers=headers)
    return response.reason

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8080)

