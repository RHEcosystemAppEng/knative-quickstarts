package com.redhat.knative.demo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
public class LoadedRecord extends PanacheEntity {

    @Column(length = 256, unique = false)
    public String type;
    @Column(length = 256, unique = false)
    public String message;

    public LoadedRecord() {
    }

    public LoadedRecord(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoadedRecord [message=" + message + ", type=" + type + "]";
    }
}