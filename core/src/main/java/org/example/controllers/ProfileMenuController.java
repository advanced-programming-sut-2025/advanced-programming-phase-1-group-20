//package org.example.controllers;
//
//import org.example.models.entities.User;
//import org.example.utils.AssetManager;
//import org.example.views.ProfileMenuScreen;
//
//import static org.example.Main.getGame;
//
//public class ProfileMenuController {
//    private ProfileMenuScreen screen;
//    private int currentImageIndex = 0;
//    private float timeSinceLastChange = 0;
//    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;
//
//    public void setView(ProfileMenuScreen screen) {
//        this.screen = screen;
//    }
//
//    public void update(float delta) {
//        timeSinceLastChange += delta;
//        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
//            timeSinceLastChange = 0;
//            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getProfileMenuImagesCount();
//            screen.updateBackground(AssetManager.getAssetManager().getProfileMenuTexture(currentImageIndex));
//        }
//    }
//
//    public void handleSaveChanges(String username, String password, String email, String nickname) {
//        User user = App.currentUser;
//        boolean changed = false;
//
//        if (!username.equals(user.getUsername())) {
//            if (username.isEmpty()) {
//                screen.showError("Username cannot be empty");
//                return;
//            }
//            if (App.userExists(username)) {
//                screen.showError("Username already taken");
//                return;
//            }
//            user.setUsername(username);
//            changed = true;
//        }
//
//        if (!password.isEmpty()) {
//            if (password.length() < 8) {
//                screen.showError("Password must be at least 8 characters");
//                return;
//            }
//            user.setPassword(password);
//            changed = true;
//        }
//
//        if (!email.equals(user.getEmail())) {
//            if (!isValidEmail(email)) {
//                screen.showError("Invalid email format");
//                return;
//            }
//            user.setEmail(email);
//            changed = true;
//        }
//
//        if (!nickname.equals(user.getNickname())) {
//            user.setNickname(nickname);
//            changed = true;
//        }
//
//        if (changed) {
//            App.updateUser(user);
//            screen.showSuccess("Changes saved successfully!");
//        } else {
//            screen.showError("No changes detected");
//        }
//    }
//
//    public void handleBackButton() {
//        getGame().getScreen().dispose();
////        getGame().setScreen(new MainMenuView(new MainMenuController()));
//    }
//
//    public void handleChangeAvatar() {
//        getGame().getScreen().dispose();
////        getGame().setScreen(new ChooseAvatarMenuView(new ChooseAvatarMenuController()));
//    }
//
//    private boolean isValidEmail(String email) {
//        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
//        return email.matches(regex);
//    }
//}
