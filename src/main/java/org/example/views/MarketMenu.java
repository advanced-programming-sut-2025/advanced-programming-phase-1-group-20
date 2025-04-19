package org.example.views;


import controllers.MarketController;
import models.Player.Player;
import views.AppMenu;
import views.AppView;

public class MarketMenu implements AppMenu {
    private AppView appView;
    private MarketController controller;
    private Player player;

    public MarketMenu(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.controller = new MarketController(appView, player);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }
}
