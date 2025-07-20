package org.example.common.network.responses;

import org.example.common.network.events.NetworkEvent;

/**
 * Base class for all server-to-client responses.
 * Provides common functionality for response handling and status tracking.
 */
public abstract class Response extends NetworkEvent {
    private final String requestId;
    private final ResponseStatus status;
    private final String message;
    
    protected Response(String requestId, String sourceId, ResponseStatus status, String message) {
        super(EventType.RESPONSE, sourceId);
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public ResponseStatus getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return status == ResponseStatus.SUCCESS;
    }
    
    public boolean isError() {
        return status == ResponseStatus.ERROR;
    }
    
    public enum ResponseStatus {
        SUCCESS,
        ERROR,
        PENDING,
        CANCELLED
    }
} 