package models;

import models.enums.PlayerEnums.Gender;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private final Gender gender;
    private boolean stayLoggedIn;
    private int securityQuestionIndex;
    private String securityAnswer;
    private int mostEarnedMoney;
    private int gamesPlayed;
    private List<Item> inventory;

    public User(String username, String password, String email, String nickname, Gender gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
