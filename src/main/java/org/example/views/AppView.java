package org.example.views;

import org.example.models.App;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    public boolean exit = false;
    public App appHandler;
    private Scanner scanner;

    public AppView() {
        this.currentMenu = new LoginRegisterMenu(this);
        scanner = new Scanner(System.in);
    }


    public void appStart() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            update(input);
            if (exit) {
                break;
            }
        }
    }

    public void update(String input) {
        this.currentMenu.updateMenu(input);
    }


    public void navigateMenu(AppMenu menu) {
        this.currentMenu = menu;
    }

}
