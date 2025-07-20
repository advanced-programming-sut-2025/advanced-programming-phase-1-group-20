package org.example.common.network.requests;

import org.example.common.network.events.NetworkEvent;
import org.example.common.network.responses.Response;
import org.example.common.network.routes.Route;

/**
 * Base class for all client-to-server requests.
 * Provides common functionality for request handling and routing.
 */
public abstract class Request extends NetworkEvent {
    private final Route route;
    private final String requestId;

    protected Request(Route route, String sourceId) {
        super(EventType.REQUEST, sourceId);
        this.route = route;
        this.requestId = generateRequestId();
    }

    public Route getRoute() {
        return route;
    }

    public String getRequestId() {
        return requestId;
    }

    private String generateRequestId() {
        return "req_" + getEventId();
    }

    /**
     * Get the response type that this request expects.
     * @return The class of the expected response
     */
    public abstract Class<? extends Response> getExpectedResponseType();
}
