package org.example;

import com.badlogic.gdx.Game;
import org.example.views.AppView;

public class Main extends Game {
    public static void main(String[] args) {
        AppView appView = new AppView();
        appView.appStart();
    }


    @Override
    public void create() {

    }
}
