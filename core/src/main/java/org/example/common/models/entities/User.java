package org.example.common.models.entities;

import org.example.common.models.Items.Item;
import org.example.common.models.enums.PlayerEnums.Gender;
import org.example.utils.PasswordUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String jwtToken;
    private long tokenExpirationTime;
    private String refreshToken;
    private long refreshTokenExpirationTime;

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

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void playGame() {
        gamesPlayed++;
    }

    // for implementing the player's character (for graphic)
    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Item> getInventory() {
        if (inventory == null) {
            inventory = new ArrayList<>();
        }
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
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


    public String getJwtToken() {
        return jwtToken;
    }


    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public long getTokenExpirationTime() {
        return tokenExpirationTime;
    }


    public void setTokenExpirationTime(long tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }

    public void setRefreshTokenExpirationTime(long refreshTokenExpirationTime) {
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    public boolean isTokenValid() {
        return jwtToken != null && tokenExpirationTime > System.currentTimeMillis();
    }

    public boolean isRefreshTokenValid() {
        return refreshToken != null && refreshTokenExpirationTime > System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
