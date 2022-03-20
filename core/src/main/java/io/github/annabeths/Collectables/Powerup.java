package io.github.annabeths.Collectables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GeneralControl.ResourceManager;

/** @author Ben Faulkner */
public class Powerup extends PhysicsObject {

	// id of powerup given
	private PowerupType powerup;

	public Powerup(PowerupType powerup, Vector2 initialPosition) {
		sprite = new Sprite(ResourceManager.getTexture(powerup.getTexture()));
		sprite.setSize(50, 50);
		setCenter(initialPosition);
		sprite.setPosition(position.x, position.y);

		this.powerup = powerup;

		collisionPolygon = new Polygon(new float[] { 0, 25, 25, 50, 50, 25, 25, 0 });
		collisionPolygon.setOrigin(8, 8);
		collisionPolygon.setPosition(position.x, position.y);
	}

	@Override
	public void OnCollision(PhysicsObject other) {
		if (other instanceof PlayerBoat) {
			PlayerBoat boat = (PlayerBoat) other;
			// give powerup based on PowerID
			// then disappear
			boat.receivePower(powerup);

			System.out.println("Collected powerup - " + powerup.getName());
			killOnNextTick = true;
		}
	}

	@Override
	public void Draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

}
