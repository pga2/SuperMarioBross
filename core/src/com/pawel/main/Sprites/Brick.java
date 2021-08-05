package com.pawel.main.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.pawel.main.Game;
import com.pawel.main.Scenes.HUD;
import com.pawel.main.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {

        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Game.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(Game.DESTROYED_BIT);
            getCell().setTile(null);
            HUD.addScore(200);
            Game.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else
            Game.manager.get("audio/sounds/bump.wav", Sound.class).play();

    }
}
