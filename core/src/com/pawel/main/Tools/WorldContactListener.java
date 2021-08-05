package com.pawel.main.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pawel.main.Game;
import com.pawel.main.Sprites.Enemies.Enemy;
import com.pawel.main.Sprites.InteractiveTileObject;
import com.pawel.main.Sprites.Item;
import com.pawel.main.Sprites.Mario;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        /*if(fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass()))
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
        }*/

        switch (cDef) {
            case Game.MARIO_HEAD_BIT | Game.BRICK_BIT:
            case Game.MARIO_HEAD_BIT | Game.COIN_BIT:
                if(fixA.getFilterData().categoryBits == Game.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case Game.ENEMY_HEAD_BIT | Game.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Game.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case Game.ENEMY_BIT | Game.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Game.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Game.MARIO_BIT | Game.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == Game.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
            case Game.ENEMY_BIT | Game.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
            case Game.ITEM_BIT | Game.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Game.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Game.ITEM_BIT | Game.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Game.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                break;

        }
    }

    @Override
    public void endContact(Contact contact) {
        //Gdx.app.log("End contact", "");

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
