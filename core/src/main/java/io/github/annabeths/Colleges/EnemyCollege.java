package io.github.annabeths.Colleges;

import static io.github.annabeths.GeneralControl.ResourceManager.font;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.EnemyBoat;
import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.Projectiles.Projectile;
import io.github.annabeths.Projectiles.ProjectileData;

/**
 * A college hostile to the player and FriendlyBoat
 * @author James Burnell
 * @author Hector Woods
 * @tt.updated Assessment 2
 */
public class EnemyCollege extends College {

	/**
	 * The inaccuracy when firing cannonballs. Measured in degrees and reflected
	 * about the center. i.e. the cannon could be fired anywhere between -x and x
	 * where x is the inaccuracy.
	 */
	public float shootingInaccuracy = 10f;
	public float timeSinceLastShot = 0;

	public ProjectileData projectileType;
	public transient GlyphLayout hpText;
	/** Spawn a boat every n seconds */
	public float boatSpawnTime;
	public float timeSinceLastSpawn;

	/**
	 * Constructor for EnemyCollege
	 * @param position position of the college
	 * @param aliveTexture college's texture
	 * @param islandTexture texture of the island beneath the college
	 * @param controller instance of GameController that the college is aprt of
	 * @param projectileData Type of projectile that the college shoots
	 * @param maxHP max HP for the college
	 */
	public EnemyCollege(Vector2 position, String aliveTexture, String islandTexture,
			GameController controller, ProjectileData projectileData, float maxHP) {
		super(position, aliveTexture, islandTexture, controller);

		deadSprite = initSprite("img/world/castle/castle_dead.png", position,
				new Vector2(100, 100));

		this.maxHP = maxHP * controller.getGameDifficulty().getEnemyHpMul();
		this.HP = this.maxHP;
		range = 500;
		fireRate = 1.5f;
		projectileType = projectileData;
		hpText = new GlyphLayout();
		// updateHpText();

		// Randomize spawn times, based on difficulty
		switch (controller.getGameDifficulty()){
			case EASY:
				boatSpawnTime = MathUtils.random(15, 20);
			case MEDIUM:
				boatSpawnTime = MathUtils.random(10,15);
			case HARD:
				boatSpawnTime = MathUtils.random(5,10);

		}
		boatSpawnTime = MathUtils.random(5, 15);
		// Create a random spawning offset so boats don't spawn simultaneously
		timeSinceLastSpawn = MathUtils.random(boatSpawnTime);
	}

	/**
	 * Update the HP text beneath the college
	 */
	public void updateHpText() {
		hpText.setText(font, getHPString());
	}

	/**
	 * get the new HP text to be displayed beneath the college
	 * @return the hp text
	 */
	public String getHPString() {
		return String.format("%.0f/%.0f", HP, maxHP);
	}

	/**
	 * Called when the college collides with another PhysicsObject.
	 * @param other the object collided with
	 */
	@Override
	public void OnCollision(PhysicsObject other) {
		// if the enemy college is hit by a projectile
		if (other instanceof Projectile && !isDead()) {
			Projectile p = (Projectile) other;
			if (p.isPlayerProjectile()) { // if it's a player projectile
				p.kill();
				if (!isInvulnerable()) {
					damage(p.getDamage());
					if (isDead()) gc.CollegeDestroyed(this);
					updateHpText();
				} else {
					hpText.setText(font, "RESISTED, destroy other colleges first!");
				}
			}
		}
	}

	/**
	 * Called once per frame
	 * @param delta time since last frame
	 */
	public void Update(float delta) {
		if (!isDead()) {
			// increase the time on the timer to allow for fire rate calculation
			timeSinceLastShot += delta;

			PlayerBoat boat = gc.playerBoat;
			// is the player boat in range
			if (isInRange(boat)) {
				if (timeSinceLastShot >= fireRate) {
					ShootAt(boat.getCenter());
					timeSinceLastShot = 0;
				}
			} else {
				// only increase spawn time if player is not in range
				timeSinceLastSpawn += delta;
				checkForSpawnEnemyBoat();
			}
		}
	}

	/**
	 * Draw the college's sprite
	 * @param batch the spritebatch to draw the college
	 */
	public void Draw(SpriteBatch batch) {
		islandSprite.draw(batch);
		if (isDead()) {
			deadSprite.draw(batch);
		} else {
			sprite.draw(batch);
			font.draw(batch, hpText, getCenterX() - hpText.width / 2, getY() - hpText.height / 2);
		}
	}

	/**
	 * Shoot towards a target
	 * @param target the target (Vector2)
	 */
	void ShootAt(Vector2 target) {
		// If fire is disabled, skip calculation.
		if (!DebugUtils.ENEMY_COLLEGE_FIRE) return;
		/*
		 * calculate the shot angle by getting a vector from the center of the college
		 * to the target. Convert to degrees for the inaccuracy calculation.
		 */
		Vector2 directionVec = target.cpy().sub(getCenter());
		float shotAngle = directionVec.angleDeg();

		shotAngle += MathUtils.random(-shootingInaccuracy, shootingInaccuracy);

		float dmgMul = gc.getGameDifficulty().getEnemyDmgMul();

		/*
		 * instantiate a new bullet and pass a reference to the gamecontroller, so it can
		 * be updated and drawn
		 */
		gc.NewPhysicsObject(
				new Projectile(getCenter(), shotAngle, projectileType, false, dmgMul, 1));

	}

	/**
	 * If it is time to spawn a new boat, spawn it and reset the timer.
	 */
	public void checkForSpawnEnemyBoat() {
		if (timeSinceLastSpawn > boatSpawnTime) {
			gc.NewPhysicsObject(new EnemyBoat(gc, new Vector2(position.x + 150, position.y + 150)));
			timeSinceLastSpawn = 0;
		}
	}

	/**
	 * Set invulnerable to false.
	 */
	public void becomeVulnerable() {
		setInvulnerable(false);
		updateHpText();
	}

	/**
	 * get the ProjectileData associated with projectiles the College shoots.
	 * @return ProjectileData
	 */
	public ProjectileData getProjectileType(){
		return projectileType;
	}
}
