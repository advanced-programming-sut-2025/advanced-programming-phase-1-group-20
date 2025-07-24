package org.example.common.network.handlers;

import org.example.common.network.requests.PlayerMoveRequest;
import org.example.common.network.requests.Request;
import org.example.common.network.responses.PlayerMoveResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routing.RequestHandler;

import java.util.concurrent.CompletableFuture;


public class PlayerActionHandler implements RequestHandler {

    @Override
    public CompletableFuture<Response> handle(Request request) {
        if (request instanceof PlayerMoveRequest) {
            return handlePlayerMove((PlayerMoveRequest) request);
        }

        CompletableFuture<Response> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalArgumentException("Unsupported request type"));
        return future;
    }

    private CompletableFuture<Response> handlePlayerMove(PlayerMoveRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return processPlayerMove(request);
            } catch (Exception e) {
                return PlayerMoveResponse.error(request.getRequestId(),
                                              request.getSourceId(), e.getMessage());
            }
        });
    }

    private PlayerMoveResponse processPlayerMove(PlayerMoveRequest request) {
        String username = request.getUsername();
        float x = request.getX();
        float y = request.getY();

        // Validate movement (check bounds, collision, etc.)
        if (!isValidMovement(x, y)) {
            return PlayerMoveResponse.error(request.getRequestId(),
                                          request.getSourceId(), "Invalid movement");
        }

        // Update player position in game state
        updatePlayerPosition(username, x, y);

        return PlayerMoveResponse.success(request.getRequestId(),
                                        request.getSourceId(), username, x, y);
    }

    private boolean isValidMovement(float x, float y) {
        // Implement movement validation logic
        // Check bounds, collision with objects, etc.
        return x >= 0 && y >= 0 && x < 1000 && y < 1000; // Example bounds
    }

    private void updatePlayerPosition(String username, float x, float y) {
        // Update player position in the game state
        // This would integrate with your existing game state management
        System.out.println("Player " + username + " moved to (" + x + ", " + y + ")");
    }
}
