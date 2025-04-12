package views;

import models.App;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    private Scanner sc;
    public boolean exit = false;
    public App appHandler;

    public AppView() {
        this.currentMenu = new LoginRegisterMenu(this);
        sc = new Scanner(System.in);
    }


    public void appStart() {
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            update(input);
            if(exit){
                break;
            }
        }
    }

    public void update(String input) {
        this.currentMenu.updateMenu(input);
    }



    public void navigateMenu(AppMenu menu){
        this.currentMenu = menu;
    }

}
