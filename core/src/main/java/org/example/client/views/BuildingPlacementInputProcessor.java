package org.example.client.views;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import org.example.client.views.menu.BuildingPlacementScreen;

import static org.example.client.Main.getGame;

public class BuildingPlacementInputProcessor extends InputAdapter {
    private final BuildingPlacementScreen screen;

    public BuildingPlacementInputProcessor(BuildingPlacementScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 mousePos = new Vector3(screenX, screenY, 0);
//        screen.getWorldController().getCamera().unproject(mousePos);

        int tileX = (int)(mousePos.x / 60);
        int tileY = (int)(mousePos.y / 60);

//        screen.placeBuilding(tileX, tileY);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
//            getGame().setScreen(screen.getWorldController().getPreviousScreen());
            return true;
        }
        return false;
    }
}
