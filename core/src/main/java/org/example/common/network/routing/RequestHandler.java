package org.example.common.network.routing;

import org.example.common.network.requests.Request;
import org.example.common.network.responses.Response;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for handling network requests.
 * All request handlers must implement this interface.
 */
@FunctionalInterface
public interface RequestHandler {
    /**
     * Handle a network request and return a response.
     * @param request The request to handle
     * @return A CompletableFuture that will complete with the response
     */
    CompletableFuture<Response> handle(Request request);
} 