package io.github.annabeths.Boats;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.GameGenerics.IHealth;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.MathHelper;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.Projectile;
import io.github.annabeths.Projectiles.ProjectileData;

public abstract class Boat extends PhysicsObject implements IHealth {
	GameController controller;

	// Boat stats
	protected float HP;
	protected float maxHP;
	protected float speed;
	protected float turnSpeed;
	protected float shotDelay = 0.5f;
	protected float timeSinceLastShot = 0f;

	/**
	 * @author James Burnell
	 * @tt.updated Assessment 2
	 * @param controller the game controller
	 * @param position the position of the boat
	 * @param texLoc the texture file location for the boat
	 */
	public Boat(GameController controller, Vector2 position, String texLoc) {
		this.controller = controller;
		this.position = position.cpy();

		collisionPolygon = new Polygon(new float[] { 0, 0, 0, 68, 25, 100, 50, 68, 50, 0 });

		setSprite(texLoc, position, new Vector2(100, 50));

		collisionPolygon.setPosition(position.x + getLocalCenterX() / 2,
				position.y - getLocalCenterY() / 2 - 10);
		collisionPolygon.setOrigin(25, 50);
		collisionPolygon.setRotation(rotation - 90);
	}

	public abstract void Update(float delta);

	/**
	 * Generic move method for boats to move forward by their set speed, and a
	 * multiplier
	 * @param delta time since last frame
	 * @param multiplier multiplier to set forward or reverse motion (1 or -1)
	 * @since Assessment 1
	 */
	void Move(float delta, int multiplier) {
		// Convention: 0 degrees means the object is pointing right, positive angles are
		// counterclockwise
		Vector2 oldPos = position.cpy();
		position.x += Math.cos(Math.toRadians(rotation)) * speed * delta * multiplier;
		position.y += Math.sin(Math.toRadians(rotation)) * speed * delta * multiplier;

		sprite.setPosition(position.x, position.y);
		collisionPolygon.setPosition(position.x + getLocalCenterX() / 2,
				position.y - getLocalCenterY() / 2 - 10);
		collisionPolygon.setOrigin(25, 50);

		if (!GameMap.isPointInBounds(getCenter())) {
			position = oldPos.cpy();
		}
	}

	/**
	 * Turns the boat by its turn speed, in the direction specified by multiplier.
	 * 
	 * Turn the boat, a positive multiplier will turn it anti-clockwise, negative
	 * clockwise
	 * 
	 * @param delta time since last frame
	 * @param multiplier turn anti-clockwise if +ve, clockwise if -ve
	 * 
	 * @since Assessment 1
	 */
	void Turn(float delta, float multiplier) {
		rotation = rotation + turnSpeed * delta * multiplier;
		rotation = MathHelper.normalizeAngle(rotation);
		sprite.setRotation(rotation);
		collisionPolygon.setRotation(rotation - 90);
	}

	/**
	 * Turns the boat towards a desired angle using the shortest angular distance.
	 * Moves the boat forwards at the same time.
	 * 
	 * @param desiredAngle the angle the boat should end up at
	 * @param delta the time since the last update
	 * @since Assessment 2
	 * @author James Burnell
	 * @author Hector Woods
	 */
	public void moveTowardsDesiredAngle(float desiredAngle, float delta) {
		float angDiff = MathHelper.getAbsDiff2Angles(rotation, desiredAngle);
		boolean turnLeft = Math.abs((rotation + angDiff) % 360 - desiredAngle) < 0.05f;

		if (angDiff > 0.5f) {
			Turn(delta, turnLeft ? 1 : -1);
		}

		Move(delta, 1);
	}

	/**
	 * abstract method for when the Boat shoots a projectile.
	 */
	abstract void Shoot();

	/**
	 * abstract method for when the Boat is destroyed.
	 */
	abstract void Destroy();

	/**
	 * Place the boat somewhere in global space, use this when spawning boats
	 * 
	 * @param x the x position
	 * @param y the y position
	 *
	 * @since Assessment 1
	 */
	void SetPosition(float x, float y) {
		position.x = x;
		position.y = y;
		sprite.setPosition(x, y);
	}

	/** @since Assessment 1 */
	@Override
	public void Draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	/**
	 * Creates a new projectile fired from the boat.
	 * 
	 * @param type the type of projectile to shoot
	 * @param rotationOffset the angle to fire the projectile relative to the boat
	 * @param dmgMul the damage multiplier
	 * @param spdMul the speed multiplier
	 * 
	 * @return A new projectile object
	 * 
	 * @since Assessment 2
	 * @author James Burnell
	 */
	protected Projectile createProjectile(ProjectileData type, float rotationOffset, float dmgMul,
			float spdMul) {
		boolean isPlayer = this instanceof PlayerBoat;
		boolean isFriendly = this instanceof FriendlyBoat || this instanceof PlayerBoat;
		return new Projectile(getCenter(), rotation + rotationOffset, type, isPlayer, isFriendly, dmgMul,
				spdMul);
	}

	/**
	 * getter method for the Boat's HP
	 * @return the Boat's HP
	 */
	@Override
	public float getHealth() {
		return HP;
	}

	/**
	 * setter method for the Boat's HP
	 * @param health the new HP for the boat
	 */
	public void setHealth(float health){
		this.HP = health;
	}
	/**
	 * setter method for the Boat's maximum HP
	 * @param maxHealth the new max HP for the boat
	 */
	public void setMaxHealth(float maxHealth){
		this.maxHP = maxHealth;
	}

	/**
	 * getter method for the Boat's maximum HP
	 * @return the Boat's MaxHP
	 */
	@Override
	public float getMaxHealth() {
		return maxHP;
	}

	/**
	 * Deal damage to the boat.
	 * @param dmg The amount of damage to be dealt.
	 */
	@Override
	public void damage(float dmg) {
		HP = MathUtils.clamp(HP - dmg, 0, maxHP);
	}

	/**
	 * Whether the boat is dead or not.
	 * @return Boolean - true if dead
	 */
	@Override
	public boolean isDead() {
		return killOnNextTick || IHealth.super.isDead();
	}
}
