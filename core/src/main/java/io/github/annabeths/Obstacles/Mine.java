package io.github.annabeths.Obstacles;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.Boat;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;

public class Mine extends ObstacleEntity {

	public Mine(GameController controller, Vector2 position) {
		super(controller, position, "img/entity/mine.png", new Vector2(50, 50));
		Polygon poly = new Polygon(new float[] { 0, 25, 25, 50, 50, 25, 25, 0 });
		poly.setPosition(position.x - getLocalCenterX(), position.y + 25);
		poly.setOrigin(0, 0);
		poly.setRotation(rotation - 90);
		setCenter(position);
		this.collisionPolygon = poly;
	}

	@Override
	public void OnCollision(PhysicsObject other) {
		if (other instanceof Boat) {
			((Boat) other).damage(50);
			kill();
		}
	}

	@Override
	public void Update(float delta) {

	}

}
