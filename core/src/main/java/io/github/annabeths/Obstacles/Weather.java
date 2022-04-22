package io.github.annabeths.Obstacles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.Boat;
import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Level.GameMap;

/**
 * Weather. The player gets more points for sailing through it, but they risk
 * taking damage from lightning
 * 
 * @author Hector Woods
 */
public class Weather extends ObstacleEntity {

	final float timeBetweenDirectionChanges = 3f;
	final float speed = 75;
	String frame1 = "img/entity/weather1.png";
	String frame2 = "img/entity/weather2.png";

	Vector2 direction;
	float timeOnCurrentDirection = 0;
	/**
	 * The weather starts on one side of the map and "trends" to the other side.<br>
	 * <ul>
	 * <li>0 means it starts north, trends south</li>
	 * <li>1 means south-north</li>
	 * <li>2 means east-west</li>
	 * <li>3 means west-east</li>
	 * </ul>
	 */
	int directionTrend;

	boolean damageActive = false;
	float maxTimeBetweenLightningStrikes = 10;
	float timeUntilNextLightningStrike;
	float timeSinceLastStrike = 0;
	float timeStrikeActive = 0.75f;
	float timeSinceStrikeStarted = 0;

	public Weather(GameController controller, Vector2 position, int dir) {
		super(controller, position, "img/entity/weather1.png", new Vector2(100, 100));
		Polygon poly = new Polygon(new float[] { 0, 50, 50, 100, 100, 50, 50, 0 });
		poly.setPosition(position.x - getLocalCenterX(), position.y);
		poly.setOrigin(0, 0);
		poly.setRotation(rotation - 90);
		setCenter(position);
		directionTrend = dir;
		this.collisionPolygon = poly;
		ChangeDirection();
		timeUntilNextLightningStrike = MathUtils.random(maxTimeBetweenLightningStrikes);
	}

	public void toggleLightning() {
		if (damageActive) {
			setSprite(frame1, position, new Vector2(100, 100));
			timeSinceStrikeStarted = 0;
		} else {
			setSprite(frame2, position, new Vector2(100, 100));
			timeUntilNextLightningStrike = MathUtils.random(maxTimeBetweenLightningStrikes);
			timeSinceLastStrike = 0;
		}
		damageActive = !damageActive;
	}

	@Override
	public void OnCollision(PhysicsObject other) {
		if (other instanceof PlayerBoat) {
			// The player gets xp for sailing through bad weather
			controller.addXp(5 * Gdx.graphics.getDeltaTime());
		}
		if (!damageActive) {
			return; // Only deal damage when lightning frames are showing
		}
		if (other instanceof Boat) {
			((Boat) other).damage(1f * controller.getGameDifficulty().getEnemyDmgMul());
		}
	}

	public void ChangeDirection() {
		switch (directionTrend) {
		case 0: // North -> South
			direction = new Vector2(MathUtils.randomSign(), -1);
			break;
		case 1: // South -> North
			direction = new Vector2(MathUtils.randomSign(), 1);
			break;
		case 2: // East -> West
			direction = new Vector2(1, MathUtils.randomSign());
			break;
		case 3: // West -> East
			direction = new Vector2(-1, MathUtils.randomSign());
		}
	}

	public void Move(float delta) {
		timeOnCurrentDirection += delta;
		if (timeOnCurrentDirection >= timeBetweenDirectionChanges) {
			timeOnCurrentDirection = 0;
			ChangeDirection();
		}

		position.add(direction.cpy().scl(delta * speed));

		sprite.setPosition(position.x - 50, position.y - 70);
		collisionPolygon.setPosition(position.x - getLocalCenterX(), position.y);
		collisionPolygon.setRotation(rotation - 90);
		if (position.x < -1500 || position.x > GameMap.getMapWidth() + 1500 || position.y < -1500
				|| position.y > GameMap.getMapHeight() + 1500) {
			kill();
		}
	}

	@Override
	public void Update(float delta) {
		// Update the sprite and check state of lightning
		if (damageActive) {
			timeSinceStrikeStarted += delta;
			if (timeSinceStrikeStarted >= timeStrikeActive) {
				toggleLightning();
			}
		} else {
			timeSinceLastStrike += delta;
			if (timeSinceLastStrike >= timeUntilNextLightningStrike) {
				toggleLightning();
			}
		}

		Move(delta);
	}

	/**
	 * @return the direction
	 */
	public Vector2 getDirection() {
		return direction;
	}

	/**
	 * @return the direction trend
	 */
	public int getDirectionTrend() {
		return directionTrend;
	}
}
