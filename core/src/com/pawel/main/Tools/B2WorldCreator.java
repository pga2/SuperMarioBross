package com.pawel.main.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.pawel.main.Game;
import com.pawel.main.Screens.PlayScreen;
import com.pawel.main.Sprites.Brick;
import com.pawel.main.Sprites.Coin;
import com.pawel.main.Sprites.Enemies.Enemy;
import com.pawel.main.Sprites.Enemies.Goomba;
import com.pawel.main.Sprites.Enemies.Turtle;

public class B2WorldCreator {
    private World world;
    private TiledMap map;
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;


    public B2WorldCreator(PlayScreen screen) {

        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        this.world = world;
        this.map = map;

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Game.PPM, (rect.getY() + rect.getHeight() / 2) / Game.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Game.PPM, rect.getHeight() / 2 / Game.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = Game.OBJECT_BIT;
            body.createFixture(fdef);
        }

        //create pipe bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Game.PPM, (rect.getY() + rect.getHeight() / 2) / Game.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Game.PPM, rect.getHeight() / 2 / Game.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }

        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            new Coin(screen, object);
        }

        //create all goombas
        goombas = new Array<>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / Game.PPM, rect.getY() / Game.PPM));
        }

        turtles = new Array<>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / Game.PPM, rect.getY() / Game.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        for(Goomba goomba : goombas) {
            enemies.add(goomba);
        }
        for(Turtle turtle : turtles) {
            enemies.add(turtle);
        }

        //enemies.add(goombas);
        //enemies.add(turtles);
        return enemies;
    }

}
