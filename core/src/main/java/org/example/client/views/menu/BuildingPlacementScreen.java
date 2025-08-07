package org.example.client.views.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import org.example.client.Main;
import org.example.client.controllers.gameplay.WorldController;
import org.example.client.views.BuildingPlacementInputProcessor;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;

import static org.example.client.Main.getGame;

public class BuildingPlacementScreen implements Screen {
    private final WorldController worldController;
    private final SpriteBatch batch;
    private final String buildingType;
    private final Player player;
    private final Texture validTexture;
    private final Texture invalidTexture;
    private final Texture backgroundTexture;
    private final Farm farm;
    private final int buildingWidth;
    private final int buildingHeight;

    public BuildingPlacementScreen(Player player, String buildingType, WorldController worldController) {
        this.player = player;
        this.buildingType = buildingType;
        this.worldController = worldController;
        this.batch = Main.getBatch();
        this.farm = player.getCurrentFarm();

        if (buildingType.equals("barn")) {
            buildingWidth = 4;
            buildingHeight = 3;
        }
        else {
            buildingWidth = 3;
            buildingHeight = 3;
        }

        validTexture = createColorTexture(Color.GREEN);
        invalidTexture = createColorTexture(Color.RED);
        backgroundTexture = createGridTexture();
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createGridTexture() {
        Pixmap pixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        pixmap.setColor(Color.GRAY);
        pixmap.drawRectangle(0, 0, 60, 60);
        return new Texture(pixmap);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new BuildingPlacementInputProcessor(this));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                batch.draw(backgroundTexture, x * 60, y * 60, 60, 60);
            }
        }

        for (int x = 0; x < Farm.width - buildingWidth + 1; x++) {
            for (int y = 0; y < Farm.height - buildingHeight + 1; y++) {
                if (isValidPlacement(x, y)) {
                    batch.setColor(0, 1, 0, 0.5f);
                    batch.draw(validTexture, x * 60, y * 60, buildingWidth * 60, buildingHeight * 60);
                } else {
                    batch.setColor(1, 0, 0, 0.3f);
                    batch.draw(invalidTexture, x * 60, y * 60, buildingWidth * 60, buildingHeight * 60);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldController.getCamera().unproject(mousePos);
        int tileX = (int)(mousePos.x / 60);
        int tileY = (int)(mousePos.y / 60);

        if (tileX >= 0 && tileX < Farm.width - buildingWidth + 1 &&
            tileY >= 0 && tileY < Farm.height - buildingHeight + 1) {

            if (isValidPlacement(tileX, tileY)) {
                batch.setColor(1, 1, 0, 0.7f);
            }
            else {
                batch.setColor(1, 0.5f, 0, 0.7f);
            }

            batch.draw(validTexture, tileX * 60, tileY * 60,
                buildingWidth * 60, buildingHeight * 60);
        }

        batch.end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private boolean isValidPlacement(int startX, int startY) {
        for (int x = startX; x < startX + buildingWidth; x++) {
            for (int y = startY; y < startY + buildingHeight; y++) {
                Location loc = farm.getItem(x, y);
                if (loc == null || loc.getTile() != TileType.Dirt || loc.getItem() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeBuilding(int x, int y) {
        if (isValidPlacement(x, y)) {
            if (buildingType.equals("barn")) {
                worldController.placeBarn(x, y);
            } else {
                worldController.placeCoop(x, y);
            }
            getGame().setScreen(worldController.getPreviousScreen());
        }
    }

}
