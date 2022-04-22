package io.github.annabeths.Collectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameGenerics.PhysicsObject;

/** @author Ben Faulkner */
public class Powerup extends PhysicsObject {

	// id of powerup given
	private PowerupType powerup;

	public Powerup(PowerupType powerup, Vector2 initialPosition) {

		setSprite(powerup.getTexture(), initialPosition, new Vector2(50, 50));
		setCenter(initialPosition);

		this.powerup = powerup;

		collisionPolygon = new Polygon(new float[] { 0, 25, 25, 50, 50, 25, 25, 0 });
		collisionPolygon.setOrigin(8, 8);
		collisionPolygon.setPosition(position.x, position.y);
	}

	@Override
	public void OnCollision(PhysicsObject other) {
		if (other instanceof PlayerBoat) {
			PlayerBoat boat = (PlayerBoat) other;

			// remove if player successfully received powerup
			if (boat.receivePower(powerup)) {
				Gdx.app.log("Powerup", "Collected powerup - " + powerup.getName());
				kill();
			}
		}
	}

	@Override
	public void Draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

}
