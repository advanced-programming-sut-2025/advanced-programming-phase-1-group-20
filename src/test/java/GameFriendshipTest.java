import org.example.models.Player.Player;
import org.example.models.entities.Friendship;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.PlayerEnums.Gender;

import java.util.ArrayList;
import java.util.List;

public class GameFriendshipTest {

    public static void main(String[] args) {
        testPlayersHaveLevel0RelationshipsWhenGameStarts();
        System.out.println("All tests passed!");
    }

    private static void testPlayersHaveLevel0RelationshipsWhenGameStarts() {
        // Create test users
        User user1 = new User("player1", "password1", "player1@example.com", "Player One", Gender.Male);
        User user2 = new User("player2", "password2", "player2@example.com", "Player Two", Gender.Female);
        User user3 = new User("player3", "password3", "player3@example.com", "Player Three", Gender.Male);

        // Create players from users
        Player player1 = new Player(user1);
        Player player2 = new Player(user2);
        Player player3 = new Player(user3);

        // Create a list of players
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        // Create a new game with these players
        Game game = new Game(players, player1);

        // Verify that all players have Level0 relationships with each other
        Friendship friendship12 = player1.getFriendship(player2);
        Friendship friendship13 = player1.getFriendship(player3);
        Friendship friendship23 = player2.getFriendship(player3);

        // Check that friendships exist
        if (friendship12 == null) {
            throw new AssertionError("Friendship between player1 and player2 should exist");
        }
        if (friendship13 == null) {
            throw new AssertionError("Friendship between player1 and player3 should exist");
        }
        if (friendship23 == null) {
            throw new AssertionError("Friendship between player2 and player3 should exist");
        }

        // Check that all friendships are at Level0
        if (friendship12.getLevel() != Friendship.LEVEL_0) {
            throw new AssertionError("Friendship between player1 and player2 should be at Level0");
        }
        if (friendship13.getLevel() != Friendship.LEVEL_0) {
            throw new AssertionError("Friendship between player1 and player3 should be at Level0");
        }
        if (friendship23.getLevel() != Friendship.LEVEL_0) {
            throw new AssertionError("Friendship between player2 and player3 should be at Level0");
        }

        // Check that XP is 0 for all friendships
        if (friendship12.getXp() != 0) {
            throw new AssertionError("XP for friendship between player1 and player2 should be 0");
        }
        if (friendship13.getXp() != 0) {
            throw new AssertionError("XP for friendship between player1 and player3 should be 0");
        }
        if (friendship23.getXp() != 0) {
            throw new AssertionError("XP for friendship between player2 and player3 should be 0");
        }

        System.out.println("[DEBUG_LOG] All players have Level0 relationships with each other when game starts");
        System.out.println("testPlayersHaveLevel0RelationshipsWhenGameStarts: PASSED");
    }
}
