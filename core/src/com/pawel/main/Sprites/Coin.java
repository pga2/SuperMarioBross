package com.pawel.main.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pawel.main.Game;
import com.pawel.main.Scenes.HUD;
import com.pawel.main.Screens.PlayScreen;

public class Coin extends InteractiveTileObject{

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Game.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario)
    {
        Gdx.app.log("coin", "collision");
        if(getCell().getTile() != tileSet.getTile(BLANK_COIN)) {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Game.PPM), Mushroom.class));
                Game.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else
                Game.manager.get("audio/sounds/coin.wav", Sound.class).play();

        } else
            Game.manager.get("audio/sounds/bump.wav", Sound.class).play();
        getCell().setTile(tileSet.getTile((BLANK_COIN)));
        HUD.addScore(100);

    }

}
