package org.example.models.entities;

import org.example.models.Items.Item;
import org.example.models.Player.Player;

/**
 * Represents a trade request between two players.
 * A trade request can be either a request for items or an offer of items.
 */
public class TradeRequest {
    private static int nextId = 1;

    private int id;
    private Player sender;
    private Player receiver;
    private Item item;
    private int amount;
    private int price; // Money amount if trading for money
    private Item targetItem; // Item requested in return (if not money)
    private int targetAmount; // Amount of target item
    private boolean isRequest; // true if request, false if offer
    private boolean isAccepted;
    private boolean isRejected;
    private boolean isViewed;


    public TradeRequest(Player sender, Player receiver, Item item, int amount, int price, boolean isRequest) {
        this.id = nextId++;
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.isRequest = isRequest;
        this.isAccepted = false;
        this.isRejected = false;
        this.isViewed = false;
    }


    public TradeRequest(Player sender, Player receiver, Item item, int amount,
                        Item targetItem, int targetAmount, boolean isRequest) {
        this.id = nextId++;
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.amount = amount;
        this.targetItem = targetItem;
        this.targetAmount = targetAmount;
        this.isRequest = isRequest;
        this.isAccepted = false;
        this.isRejected = false;
        this.isViewed = false;
    }

    public boolean accept() {
        if (isAccepted || isRejected) {
            return false;
        }

        // Check if the sender still has the items they're offering
        if (isRequest) {
            // For requests, check if receiver has the requested items
            if (targetItem != null) {
                if (receiver.getBackpack().getInventory().getOrDefault(targetItem, 0) < targetAmount) {
                    return false;
                }
            } else {
                if (receiver.getMoney() < price) {
                    return false;
                }
            }
        } else {
            // For offers, check if sender has the offered items
            if (sender.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
                return false;
            }
        }

        // Perform the trade
        if (isRequest) {
            // Handle request: receiver gives items/money to sender
            if (targetItem != null) {
                // Item for item trade
                receiver.getBackpack().remove(targetItem, targetAmount);
                sender.getBackpack().add(targetItem, targetAmount);
                sender.getBackpack().remove(item, amount);
                receiver.getBackpack().add(item, amount);
            } else {
                // Item for money trade
                receiver.decreaseMoney(price);
                sender.increaseMoney(price);
                sender.getBackpack().remove(item, amount);
                receiver.getBackpack().add(item, amount);
            }
        } else {
            // Handle offer: sender gives items to receiver
            if (targetItem != null) {
                // Item for item trade
                sender.getBackpack().remove(item, amount);
                receiver.getBackpack().add(item, amount);
                receiver.getBackpack().remove(targetItem, targetAmount);
                sender.getBackpack().add(targetItem, targetAmount);
            } else {
                // Item for money trade
                sender.getBackpack().remove(item, amount);
                receiver.getBackpack().add(item, amount);
                receiver.decreaseMoney(price);
                sender.increaseMoney(price);
            }
        }

        // Update friendship
        sender.tradeWith(receiver, true);

        isAccepted = true;
        return true;
    }


    public void reject() {
        if (!isAccepted && !isRejected) {
            isRejected = true;
            // Update friendship negatively
            sender.tradeWith(receiver, false);
        }
    }


    public void markAsViewed() {
        this.isViewed = true;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public Item getTargetItem() {
        return targetItem;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public boolean isViewed() {
        return isViewed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trade ").append(id).append(": ");

        if (isRequest) {
            sb.append(sender.getUser().getUsername())
                    .append(" requests ")
                    .append(amount).append(" ")
                    .append(item.getName());

            if (targetItem != null) {
                sb.append(" in exchange for ")
                        .append(targetAmount).append(" ")
                        .append(targetItem.getName());
            } else {
                sb.append(" for ")
                        .append(price).append(" gold");
            }
        } else {
            sb.append(sender.getUser().getUsername())
                    .append(" offers ")
                    .append(amount).append(" ")
                    .append(item.getName());

            if (targetItem != null) {
                sb.append(" in exchange for ")
                        .append(targetAmount).append(" ")
                        .append(targetItem.getName());
            } else {
                sb.append(" for ")
                        .append(price).append(" gold");
            }
        }

        if (isAccepted) {
            sb.append(" [Accepted]");
        } else if (isRejected) {
            sb.append(" [Rejected]");
        }

        return sb.toString();
    }
}
