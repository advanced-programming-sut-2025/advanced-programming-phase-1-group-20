package org.example.common.models.entities;

import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyEvents implements Serializable {
    private List<GameEvent> todayEvents;
    private List<GameEvent> recentEvents; // Last few days
    private Date lastEventDate;

    public DailyEvents() {
        this.todayEvents = new ArrayList<>();
        this.recentEvents = new ArrayList<>();
        this.lastEventDate = null;
    }

    public void addEvent(GameEvent event) {
        Date currentDate = org.example.common.models.App.getGame().getDate();

        // Check if this is a new day
        if (lastEventDate == null || !isSameDay(lastEventDate, currentDate)) {
            // Move today's events to recent events
            recentEvents.addAll(todayEvents);
            todayEvents.clear();

            // Keep only last 7 days of events
            if (recentEvents.size() > 50) { // Limit to prevent memory issues
                recentEvents = recentEvents.subList(recentEvents.size() - 30, recentEvents.size());
            }

            lastEventDate = currentDate;
        }

        todayEvents.add(event);
    }


    public List<GameEvent> getTodayEvents() {
        return new ArrayList<>(todayEvents);
    }


    public List<GameEvent> getRecentEvents() {
        return new ArrayList<>(recentEvents);
    }


    public List<GameEvent> getAllEvents() {
        List<GameEvent> allEvents = new ArrayList<>();
        allEvents.addAll(recentEvents);
        allEvents.addAll(todayEvents);
        return allEvents;
    }


    public List<GameEvent> getEventsByType(GameEvent.EventType type) {
        List<GameEvent> filteredEvents = new ArrayList<>();
        for (GameEvent event : getAllEvents()) {
            if (event.getType() == type) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }


    public List<GameEvent> getEventsInvolvingPlayer(Player player) {
        List<GameEvent> playerEvents = new ArrayList<>();
        for (GameEvent event : getAllEvents()) {
            if (event.getInvolvedPlayers().contains(player.getUser().getUsername())) {
                playerEvents.add(event);
            }
        }
        return playerEvents;
    }


    private boolean isSameDay(Date date1, Date date2) {
        return date1.getDay() == date2.getDay() &&
               date1.getSeason() == date2.getSeason() &&
               date1.getYear() == date2.getYear();
    }


    public void clearEvents() {
        todayEvents.clear();
        recentEvents.clear();
        lastEventDate = null;
    }


    public static class GameEvent implements Serializable {
        public enum EventType {
            BUILDING_PURCHASED,
            MARRIAGE_PROPOSAL,
            MARRIAGE_ACCEPTED,
            MARRIAGE_REJECTED,
            ANIMAL_PURCHASED,
            CROP_HARVESTED,
            FISH_CAUGHT,
            QUEST_COMPLETED,
            GIFT_GIVEN,
            FRIENDSHIP_LEVEL_UP,
            PLAYER_JOINED,
            PLAYER_LEFT,
            SPECIAL_ACHIEVEMENT
        }

        private EventType type;
        private String description;
        private List<String> involvedPlayers;
        private String additionalInfo;
        private Date eventDate;

        public GameEvent(EventType type, String description, List<String> involvedPlayers, String additionalInfo) {
            this.type = type;
            this.description = description;
            this.involvedPlayers = new ArrayList<>(involvedPlayers);
            this.additionalInfo = additionalInfo;
            this.eventDate = org.example.common.models.App.getGame().getDate();
        }

        // Getters
        public EventType getType() { return type; }
        public String getDescription() { return description; }
        public List<String> getInvolvedPlayers() { return new ArrayList<>(involvedPlayers); }
        public String getAdditionalInfo() { return additionalInfo; }
        public Date getEventDate() { return eventDate; }

        @Override
        public String toString() {
            return String.format("[%s] %s - Players: %s - Info: %s",
                type, description, involvedPlayers, additionalInfo);
        }
    }
    public void addBuildingPurchase(Player player, String buildingType) {
        List<String> players = new ArrayList<>();
        players.add(player.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.BUILDING_PURCHASED,
            player.getUser().getUsername() + " purchased a " + buildingType,
            players,
            "Building: " + buildingType
        );
        addEvent(event);
    }

    public void addMarriageProposal(Player proposer, Player target) {
        List<String> players = new ArrayList<>();
        players.add(proposer.getUser().getUsername());
        players.add(target.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.MARRIAGE_PROPOSAL,
            proposer.getUser().getUsername() + " proposed marriage to " + target.getUser().getUsername(),
            players,
            "Proposal"
        );
        addEvent(event);
    }

    public void addMarriageAccepted(Player proposer, Player target) {
        List<String> players = new ArrayList<>();
        players.add(proposer.getUser().getUsername());
        players.add(target.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.MARRIAGE_ACCEPTED,
            proposer.getUser().getUsername() + " and " + target.getUser().getUsername() + " got married!",
            players,
            "Wedding"
        );
        addEvent(event);
    }

    public void addMarriageRejected(Player proposer, Player target) {
        List<String> players = new ArrayList<>();
        players.add(proposer.getUser().getUsername());
        players.add(target.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.MARRIAGE_REJECTED,
            target.getUser().getUsername() + " rejected " + proposer.getUser().getUsername() + "'s marriage proposal",
            players,
            "Rejection"
        );
        addEvent(event);
    }

    public void addAnimalPurchase(Player player, String animalType) {
        List<String> players = new ArrayList<>();
        players.add(player.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.ANIMAL_PURCHASED,
            player.getUser().getUsername() + " purchased a " + animalType,
            players,
            "Animal: " + animalType
        );
        addEvent(event);
    }

    public void addQuestCompleted(Player player, String questName) {
        List<String> players = new ArrayList<>();
        players.add(player.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.QUEST_COMPLETED,
            player.getUser().getUsername() + " completed the quest: " + questName,
            players,
            "Quest: " + questName
        );
        addEvent(event);
    }

    public void addFriendshipLevelUp(Player player1, Player player2, int newLevel) {
        List<String> players = new ArrayList<>();
        players.add(player1.getUser().getUsername());
        players.add(player2.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.FRIENDSHIP_LEVEL_UP,
            player1.getUser().getUsername() + " and " + player2.getUser().getUsername() +
            " reached friendship level " + newLevel,
            players,
            "Level: " + newLevel
        );
        addEvent(event);
    }

    public void addPlayerJoined(Player player) {
        List<String> players = new ArrayList<>();
        players.add(player.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.PLAYER_JOINED,
            player.getUser().getUsername() + " joined the game",
            players,
            "New player"
        );
        addEvent(event);
    }

    public void addPlayerLeft(Player player) {
        List<String> players = new ArrayList<>();
        players.add(player.getUser().getUsername());

        GameEvent event = new GameEvent(
            GameEvent.EventType.PLAYER_LEFT,
            player.getUser().getUsername() + " left the game",
            players,
            "Player departure"
        );
        addEvent(event);
    }
}
