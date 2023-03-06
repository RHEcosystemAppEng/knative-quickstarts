package com.redhat.knative.demo.producer;

public class ProducedEvent {
    private String msg;

    public ProducedEvent() {
    }

    public ProducedEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ProducedEvent{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
