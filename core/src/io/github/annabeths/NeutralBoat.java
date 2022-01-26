package io.github.annabeths;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NeutralBoat extends AIBoat {

    public NeutralBoat(GameController controller, Vector2 initialPosition, Vector2 mapSize){
        xpValue = 25;
        plunderValue = 25;
        
        this.HP = 100;
		this.maxHP = 100;
		this.speed = 75;
		this.turnSpeed = 150;

        this.controller = controller;

        this.initialPosition = initialPosition.cpy();
        destination = initialPosition.cpy(); // Force the boat to set a new destination on initialisation
        position = initialPosition.cpy();

        collisionPolygon.setPosition(position.x + GetCenterX()/2, position.y - GetCenterY()/2 - 10);
		collisionPolygon.setOrigin(25,50);
        collisionPolygon.setRotation(rotation - 90);

		sprite.setPosition(initialPosition.x, initialPosition.y);

        this.mapSize = mapSize.cpy();
		mapBounds = new Array<Vector2>(true, 4);
		mapBounds.add(new Vector2(0,0));
		mapBounds.add(new Vector2(mapSize.x, 0));
		mapBounds.add(new Vector2(mapSize.x, mapSize.y));
		mapBounds.add(new Vector2(0, mapSize.y));

    }

    public void Update(float delta){
        MoveToDestination(delta);
    }

    public void Destroy(){
        killOnNextTick = true;
    }

    public void Shoot(){
        // Ignore, neutral boats do not shoot
    }

    public void OnCollision(PhysicsObject object){
        if(object instanceof PlayerBoat){
            // Hit by player, destroy and add XP
            controller.xp += xpValue;
            controller.plunder += plunderValue;
            Destroy();
        } else{
            Destroy();
        }
    }
}
