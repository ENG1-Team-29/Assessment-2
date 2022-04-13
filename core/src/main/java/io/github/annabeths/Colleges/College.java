package io.github.annabeths.Colleges;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.Boat;
import io.github.annabeths.GameGenerics.IHealth;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;

public abstract class College extends PhysicsObject implements IHealth {

	protected float maxHP;
	protected float HP;
	protected int range;
	protected int damage;

	protected float fireRate;

	protected boolean invulnerable;

	public Sprite deadSprite;
	public Sprite islandSprite;
	public String islandTextureFile;
	public Sprite aliveSprite;
	public String aliveTextureFile;

	/**
	 * Used to access player boat and to notify the game controller when the college
	 * has been destroyed
	 */
	protected GameController gc;

	public College(Vector2 pos, String aliveTexture, String islandTexture, GameController gc) {
		this.gc = gc;
		position = pos;

		setSprite(aliveTexture, position, new Vector2(100, 100));
		this.aliveTextureFile = aliveTexture;
		aliveSprite = this.sprite;
		islandSprite = initSprite(islandTexture,
				new Vector2(sprite.getX() - 10, sprite.getY() - 10), new Vector2(120, 120));
		this.islandTextureFile = islandTexture;

		collisionPolygon = new Polygon(new float[] { 0, 0, 100, 0, 100, 100, 0, 100 });
		collisionPolygon.setPosition(position.x, position.y);
	}

	public College() {
	}

	/**
	 * Work out euclidean distance to the other physics object, and then returns
	 * true if the that distance is &lt;= the range of the college.<br>
	 * This will be used to check if the enemy college should attack the player.<br>
	 * This will be used to check if the friendly college should heal the player.
	 * 
	 * @param other the boat to check the range of
	 * @return {@code true} if the boat is in range of the college, {@code false}
	 *         otherwise
	 * @author James Burnell
	 */
	public boolean isInRange(Boat other) {
		return getCenter().dst(other.getCenter()) <= range;
	}

	@Override
	public float getHealth() {
		return HP;
	}

	@Override
	public float getMaxHealth() {
		return maxHP;
	}

	@Override
	public void damage(float dmg) {
		HP = MathUtils.clamp(HP - dmg, 0, maxHP);
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @param damage the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * @return the fireRate
	 */
	public float getFireRate() {
		return fireRate;
	}

	/**
	 * @param fireRate the fireRate to set
	 */
	public void setFireRate(float fireRate) {
		this.fireRate = fireRate;
	}

	@Override
	public boolean isDead() {
		return removeOnNextTick() || IHealth.super.isDead();
	}
}
