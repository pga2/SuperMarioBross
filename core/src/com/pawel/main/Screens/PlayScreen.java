package com.pawel.main.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawel.main.Game;
import com.pawel.main.Scenes.HUD;
import com.pawel.main.Sprites.Enemies.Enemy;
import com.pawel.main.Sprites.Item;
import com.pawel.main.Sprites.ItemDef;
import com.pawel.main.Sprites.Mario;

import com.pawel.main.Sprites.Mushroom;
import com.pawel.main.Tools.B2WorldCreator;
import com.pawel.main.Tools.WorldContactListener;
import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen implements Screen {

    private final B2WorldCreator creator;
    private Game game;
    private TextureAtlas atlas;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private HUD hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Mario player;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    //private Goomba goomba;



    public PlayScreen(Game game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack.txt");

        this.game = game;
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gamePort = new StretchViewport(Game.V_WIDTH / Game.PPM, Game.V_HEIGHT / Game.PPM, gameCam);
        hud = new HUD(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Game.PPM);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        gameCam.position.set(gamePort.getWorldWidth() /2, gamePort.getWorldHeight() / 2, 0);

        creator = new B2WorldCreator(this);

        player = new Mario(this);



        world.setContactListener(new WorldContactListener());
        music = Game.manager.get("audio/music/mario_music.ogg");
        music.setLooping(true);
        music.play();

        //goomba = new Goomba(this, 5.64f, .32f);

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();


    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {

            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class) {
                try {
                    items.add(new Mushroom(this, idef.position.x, idef.position.y));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if(player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt) {
        handleInput(dt);

        world.step(1/60f, 6, 2);
        player.update(dt);

        hud.update(dt);
        if(player.currentState != Mario.State.DEAD)
            gameCam.position.x = player.b2body.getPosition().x;
        gameCam.update();
        renderer.setView(gameCam);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + (224 / Game.PPM)) {
                enemy.b2body.setActive(true);
            }
        }
        for(Item item : items)
            item.update(dt);
        handleSpawningItems();
    }

    @Override
    public void render(float delta) {
        //update logic from render
        update(delta);

        //clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render game map
        renderer.render();
        //render our Box2DDebugLines
        game.batch.setProjectionMatrix(gameCam.combined);

        b2dr.render(world, gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);

        for(Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for(Item item : items)
            item.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();

        if(gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }

    public boolean gameOver() {
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }


    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
