package org.example.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.example.Main;
import org.example.models.MapDetails.Farm;
import org.example.models.MapDetails.GameMap;

public class WorldController {
    private PlayerController playerController;
    private Farm farm;
    private OrthographicCamera camera;

    public WorldController(PlayerController playerController , Farm farm, OrthographicCamera camera) {
        this.playerController = playerController;
        this.farm = farm;
        this.camera = camera;
    }


    public void update(){
        camera.position.set(playerController.getPlayer().getPosX(), playerController.getPlayer().getPosY(), 0);
        camera.update();
        Main.getBatch().setProjectionMatrix(camera.combined);


        farm.getBackgroundSprite().draw(Main.getBatch());

        playerController.getPlayer().getPlayerSprite().draw(Main.getBatch());
    }

}
