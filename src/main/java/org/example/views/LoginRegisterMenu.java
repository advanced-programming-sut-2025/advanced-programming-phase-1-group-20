package org.example.views;

import org.example.controllers.LoginRegisterMenuController;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.models.enums.commands.LoginRegisterMenuCommands;
import org.example.models.App;

import java.util.List;

public class LoginRegisterMenu implements AppMenu {
    private AppView appView;
    private User user;
    private LoginRegisterMenuController controller;
    private boolean inPasswordRecoveryFlow = false;

    public LoginRegisterMenu(AppView appView) {
        this.appView = appView;
        user = null;
        controller = new LoginRegisterMenuController(appView, user);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            // Special handling for password recovery flow
            if (command == LoginRegisterMenuCommands.ForgotPass && result.message().startsWith("security_question:")) {
                inPasswordRecoveryFlow = true;
                String question = result.message().substring("security_question:".length());
                displaySecurityQuestion(question);
            }
            else if (command == LoginRegisterMenuCommands.AnswerSecurityQuestion &&
                    result.message().startsWith("password_generated:")) {
                String password = result.message().substring("password_generated:".length());
                displayPasswordOptions(password);
            }
            else if (command == LoginRegisterMenuCommands.GenerateNewPassword &&
                    result.message().startsWith("password_generated:")) {
                String password = result.message().substring("password_generated:".length());
                displayPasswordOptions(password);
            }
            else if (command == LoginRegisterMenuCommands.AcceptPassword ||
                    command == LoginRegisterMenuCommands.SetCustomPassword) {
                inPasswordRecoveryFlow = false;
                System.out.println(result.message());
            }
            else if (command == LoginRegisterMenuCommands.RegisterUser) {
                System.out.println(result.message());
                displaySecurityQuestions();
            }
            else {
                System.out.println(result.message());
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }

    private void displaySecurityQuestion(String question) {
        System.out.println("Security Question: " + question);
        System.out.println("Please enter your answer using: answer -a <your_answer>");
    }


    private void displayPasswordOptions(String generatedPassword) {
        System.out.println("A new password has been generated for you: " + generatedPassword);
        System.out.println("Choose an option:");
        System.out.println("1. Accept this password (type 'accept password')");
        System.out.println("2. Enter your own password (type 'set password -p <your password> -c <confirm password>')");
        System.out.println("3. Generate another password (type 'generate new password')");
        System.out.println("4. Return to login menu (type any other command)");
    }


    private void displaySecurityQuestions() {
        System.out.println("\nPlease choose a security question:");
        for (int i = 0; i < 4; i++) {
            System.out.println((i + 1) + ". " + App.getSecurityQuestion(i));
        }
        System.out.println("Use the command: pick question -q <question_number> -a <answer> -c <answer_confirm>");
    }
}