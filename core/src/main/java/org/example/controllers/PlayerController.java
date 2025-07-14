package org.example.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.example.Main;
import org.example.models.MapDetails.Farm;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;

public class PlayerController {
    private Player player;
    private Farm farm;


    public PlayerController(Player player , Farm farm) {
        this.player = player;
        this.farm = farm;
    }



    public void update() {
        float delta = Gdx.graphics.getDeltaTime();

        player.getPlayerSprite().draw(Main.getBatch());
        handlePlayerInput();
    }


    public void handlePlayerInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            player.setPosX(player.getPosX() - player.getSpeed());
            player.updatePosition();
            player.getPlayerSprite().setFlip(true, false);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            player.setPosX(player.getPosX() + player.getSpeed());
            player.updatePosition();
            player.getPlayerSprite().setFlip(false, false);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            player.setPosY(player.getPosY() + player.getSpeed());
            player.updatePosition();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            player.setPosY(player.getPosY() - player.getSpeed());
            player.updatePosition();
        }
    }


    public Player getPlayer() {
        return player;
    }
}
