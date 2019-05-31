package org.adragomir.backend.app.model;

import java.util.Map;

public class Event {
    public String type;
    public long timestampMs;
    public Map<String, String> metadata;

    public Event() {

    }

    public Event(String type, Map<String, String> metadata) {
        this.type = type;
        this.metadata = metadata;
        this.timestampMs = System.currentTimeMillis();

    }
}
