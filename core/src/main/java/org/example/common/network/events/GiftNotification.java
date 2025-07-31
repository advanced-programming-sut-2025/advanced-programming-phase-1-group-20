package org.example.common.network.events;


public class GiftNotification extends Notification {
    private final String senderName;
    private final String itemName;
    private final int quantity;
    private final long timestamp;

    public GiftNotification(String senderName, String itemName, int quantity, String sourceId, String targetId) {
        super(NotificationType.GIFT_RECEIVED, sourceId, targetId);
        this.senderName = senderName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderName() {
        return senderName;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getGiftTimestamp() {
        return timestamp;
    }

    public String getDisplayMessage() {
        return senderName + " sent you " + itemName + " x" + quantity + "!";
    }
}
