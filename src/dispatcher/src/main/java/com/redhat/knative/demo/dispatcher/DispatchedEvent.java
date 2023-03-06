package com.redhat.knative.demo.dispatcher;

public class DispatchedEvent {
    private String msg;

    public DispatchedEvent(){}
    public DispatchedEvent(String msg) {
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
        return "DispatchedEvent [msg=" + msg + "]";
    }
}
