package org.example.common.network.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all network events in the system.
 * Provides common functionality for event identification, timestamps, and metadata.
 */
public abstract class NetworkEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String sourceId;
    private final EventType eventType;
    
    protected NetworkEvent(EventType eventType, String sourceId) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.sourceId = sourceId;
        this.eventType = eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public enum EventType {
        REQUEST,
        RESPONSE,
        NOTIFICATION,
        ERROR
    }
} 