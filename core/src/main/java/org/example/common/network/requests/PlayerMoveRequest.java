package org.example.common.network.requests;

import org.example.common.network.responses.PlayerMoveResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routes.Route;

/**
 * Request for player movement operations.
 */
public class PlayerMoveRequest extends Request {
    private final String username;
    private final float x;
    private final float y;
    private final long moveTimestamp;

    public PlayerMoveRequest(String username, float x, float y, String sourceId) {
        super(new Route("/player/move", Route.RouteType.PLAYER_ACTIONS, "PlayerActionHandler"), sourceId);
        this.username = username;
        this.x = x;
        this.y = y;
        this.moveTimestamp = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getMoveTimestamp() {
        return moveTimestamp;
    }

    @Override
    public Class<? extends Response> getExpectedResponseType() {
        return PlayerMoveResponse.class;
    }
}
