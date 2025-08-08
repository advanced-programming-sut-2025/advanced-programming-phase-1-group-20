package org.example.common.models;

import java.util.HashMap;

public class Message {
    private Type type;
    private HashMap<String, Object> body;

    public Message() {
        this.body = new HashMap<>();
    }

    public Message(HashMap<String, Object> body, Type type) {
        this.body = body;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public HashMap<String, Object> getBody() {
        return body;
    }

    public void setBody(HashMap<String, Object> body) {
        this.body = body;
    }

    public <T> T getFromBody(String fieldName) {
        return (T) body.get(fieldName);
    }

    public int getIntFromBody(String fieldName) {
        return (int) ((double) ((Double) body.get(fieldName)));
    }

    public float getFloatFromBody(String fieldName) {
        Object value = body.get(fieldName);
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        } else if (value instanceof Float) {
            return (Float) value;
        } else {
            return Float.parseFloat(value.toString());
        }
    }

    public void putInBody(String key, Object value) {
        body.put(key, value);
    }

    public enum Type {
        // Authentication
        AUTH_LOGIN,
        AUTH_LOGOUT,

        // Player actions
        PLAYER_MOVE,
        PLAYER_ACTION,
        USE_TOOL,
        PLANT_SEED,
        HARVEST_CROP,
        FEED_ANIMAL,
        MILK_ANIMAL,

        // Game management
        CREATE_GAME,
        JOIN_GAME,
        LEAVE_GAME,
        START_GAME,
        REJOIN_GAME,

        // Farm selection
        SELECT_FARM,
        FARM_SELECTION_UPDATE,
        FARM_SELECTION_COMPLETE,

        // Lobby management
        CREATE_LOBBY,
        JOIN_LOBBY,
        LEAVE_LOBBY,
        LIST_LOBBIES,
        SEARCH_LOBBY,
        START_LOBBY_GAME,
        PLAYER_READY,

        // Game state synchronization
        GAME_STATE_FULL,
        GAME_STATE_UPDATE,
        PLAYER_UPDATE,
        PLAYER_DATA_UPDATE,
        WORLD_UPDATE,
        WEATHER_UPDATE,

        // Trading and market
        TRADE_REQUEST,
        TRADE_RESPONSE,
        TRADE_ACCEPT,
        TRADE_DECLINE,
        MARKET_BUY,
        MARKET_SELL,

        // Communication
        CHAT,
        TALK_TO_NPC,
        TALK_TO_PLAYER,

        // Friends and Gifts
        SEND_GIFT,
        GIFT_RECEIVED,
        GIFT_NOTIFICATION,

        // Inventory and items
        INVENTORY_UPDATE,
        ITEM_DROP,
        ITEM_PICKUP,

        // System messages
        PING,
        PONG,
        ERROR,
        SUCCESS,
        HEARTBEAT,

        // Online players
        PLAYER_STATUS_UPDATE,
        ONLINE_PLAYERS_LIST,
        REQUEST_PLAYERS_LIST
    }
}
