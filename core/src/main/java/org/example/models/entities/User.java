package org.example.models.entities;

import org.example.models.Items.Item;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.models.utils.PasswordUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Gender gender;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private boolean stayLoggedIn;
    private int securityQuestionIndex;
    private String securityAnswer;
    private int mostEarnedMoney;
    private int gamesPlayed;
    private List<Item> inventory;

    public User(String username, String password, String email, String nickname, Gender gender) {
        this.username = username;
        // Hash the password before storing it
        this.password = PasswordUtils.hashPassword(password);
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.inventory = new ArrayList<>();
        this.mostEarnedMoney = 0;
        this.gamesPlayed = 0;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password; // Returns the hashed password
    }

    public void setPassword(String newPassword) {
        this.password = PasswordUtils.hashPassword(newPassword);
    }

    public boolean verifyPassword(String plainPassword) {
        return PasswordUtils.verifyPassword(plainPassword, this.password);
    }

    public void setPasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStayLoggedIn() {
        return stayLoggedIn;
    }

    public void setStayLoggedIn(boolean stayLoggedIn) {
        this.stayLoggedIn = stayLoggedIn;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public int getSecurityQuestionIndex() {
        return securityQuestionIndex;
    }

    public void setSecurityQuestionIndex(int securityQuestionIndex) {
        this.securityQuestionIndex = securityQuestionIndex;
    }

    public int getMostEarnedMoney() {
        return mostEarnedMoney;
    }

    public void setMostEarnedMoney(int earnedMoney) {
        if (earnedMoney > this.mostEarnedMoney) {
            this.mostEarnedMoney = earnedMoney;
        }
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void playGame() {
        gamesPlayed++;
    }

    // for implementing the player's character (for graphic)
    public Gender getGender() {
        return this.gender;
    }

    public List<Item> getInventory() {
        if (inventory == null) {
            inventory = new ArrayList<>();
        }
        return inventory;
    }

    public void addToInventory(Item item) {
        if (inventory == null) {
            inventory = new ArrayList<>();
        }
        inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        if (inventory != null) {
            inventory.remove(item);
        }
    }
}