package org.example.common.network.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Dispatches network events to registered listeners.
 */
public class EventDispatcher {
    private static EventDispatcher instance;
    private final Map<String, List<Consumer<Notification>>> listeners;
    private final List<Consumer<Notification>> broadcastListeners;
    
    private EventDispatcher() {
        this.listeners = new ConcurrentHashMap<>();
        this.broadcastListeners = new CopyOnWriteArrayList<>();
    }
    
    public static EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }
        return instance;
    }
    
    /**
     * Register a listener for a specific target.
     */
    public void addListener(String targetId, Consumer<Notification> listener) {
        listeners.computeIfAbsent(targetId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    /**
     * Register a listener for broadcast notifications.
     */
    public void addBroadcastListener(Consumer<Notification> listener) {
        broadcastListeners.add(listener);
    }
    
    /**
     * Remove a listener for a specific target.
     */
    public void removeListener(String targetId, Consumer<Notification> listener) {
        List<Consumer<Notification>> targetListeners = listeners.get(targetId);
        if (targetListeners != null) {
            targetListeners.remove(listener);
        }
    }
    
    /**
     * Remove a broadcast listener.
     */
    public void removeBroadcastListener(Consumer<Notification> listener) {
        broadcastListeners.remove(listener);
    }
    
    /**
     * Dispatch a notification to appropriate listeners.
     */
    public void dispatch(Notification notification) {
        // Dispatch to broadcast listeners
        for (Consumer<Notification> listener : broadcastListeners) {
            try {
                listener.accept(notification);
            } catch (Exception e) {
                System.err.println("Error in broadcast listener: " + e.getMessage());
            }
        }
        
        // Dispatch to specific target listeners
        String targetId = notification.getTargetId();
        if (targetId != null) {
            List<Consumer<Notification>> targetListeners = listeners.get(targetId);
            if (targetListeners != null) {
                for (Consumer<Notification> listener : targetListeners) {
                    try {
                        listener.accept(notification);
                    } catch (Exception e) {
                        System.err.println("Error in target listener for " + targetId + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Clear all listeners.
     */
    public void clear() {
        listeners.clear();
        broadcastListeners.clear();
    }
} 