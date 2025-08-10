package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import org.example.client.Main;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;

public class BuildingPlacementScreen implements Screen {
    private final Player player;
    private final String buildingType;
    private final Screen previousScreen;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Texture validTexture;
    private final Texture invalidTexture;
    private final Texture backgroundTexture;
    private final Farm farm;
    private final int buildingWidth;
    private final int buildingHeight;

    public BuildingPlacementScreen(Player player, String buildingType, Screen previousScreen) {
        this.player = player;
        this.buildingType = buildingType;
        this.previousScreen = previousScreen;
        this.batch = Main.getBatch();
        this.farm = player.getCurrentFarm();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.buildingWidth = buildingType.equals("barn") ? 4 : 3;
        this.buildingHeight = buildingType.equals("barn") ? 3 : 3;

        this.validTexture = createColorTexture(Color.GREEN);
        this.invalidTexture = createColorTexture(Color.RED);
        this.backgroundTexture = createGridTexture();
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
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 mousePos = new Vector3(screenX, screenY, 0);
                camera.unproject(mousePos);

                int tileX = (int)(mousePos.x / 60);
                int tileY = (int)(mousePos.y / 60);

                if (isValidPlacement(tileX, tileY)) {
                    placeBuilding(tileX, tileY);
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    Main.getGame().setScreen(previousScreen);
                    return true;
                }
                return false;
            }
        });
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

    private void placeBuilding(int x, int y) {
        for (int i = x; i < x + buildingWidth; i++) {
            for (int j = y; j < y + buildingHeight; j++) {
                Location loc = farm.getItem(i, j);
                if (loc != null) {
                    loc.setTile(buildingType.equals("barn") ? TileType.BARN : TileType.COOP);
                }
            }
        }
        Main.getGame().setScreen(previousScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                batch.draw(backgroundTexture, x * 60, y * 60, 60, 60);
            }
        }

        for (int x = 0; x < Farm.width - buildingWidth + 1; x++) {
            for (int y = 0; y < Farm.height - buildingHeight + 1; y++) {
                batch.setColor(1, 1, 1, 0.5f);
                batch.draw(isValidPlacement(x, y) ? validTexture : invalidTexture,
                    x * 60, y * 60, buildingWidth * 60, buildingHeight * 60);
            }
        }

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        int tileX = (int)(mousePos.x / 60);
        int tileY = (int)(mousePos.y / 60);

        if (tileX >= 0 && tileX < Farm.width - buildingWidth + 1 &&
            tileY >= 0 && tileY < Farm.height - buildingHeight + 1) {

            batch.setColor(1, 1, 0, 0.7f);
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
        validTexture.dispose();
        invalidTexture.dispose();
        backgroundTexture.dispose();
    }

}
