package io.github.annabeths.Obstacles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.Boat;
import io.github.annabeths.GameGenerics.IHealth;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Projectiles.Projectile;
import io.github.annabeths.Projectiles.ProjectileData;

/**
 * Kraken Obstacle. Attacks the player and other ships if they get close enough
 * 
 * @author Hector Woods
 */
public class Kraken extends ObstacleEntity implements IHealth {

	final float timeBetweenDirectionChanges = 0.25f;
	final float speed = 75;
	private float attackRange = 1000;
	protected float maxHealth;
	protected float health;
	private ProjectileData projectileType = ProjectileData.KRAKEN;
	private float plunderValue = 500;
	private float xpValue = 250;
	final float timeBetweenShots = 2;
	private float timeSinceLastShot = 0;

	String frame1 = "img/entity/kraken1.png";
	String frame2 = "img/entity/kraken2.png";
	String frame3 = "img/entity/kraken3.png";
	int frameCounter = 1;

	Vector2 direction = new Vector2(1, 0);
	float timeOnCurrentDirection = 0;

	public Kraken(GameController controller, Vector2 position) {
		super(controller, position, "img/entity/kraken1.png", new Vector2(200, 200));
		Polygon poly = new Polygon(new float[] { 0, 75, 75, 150, 150, 75, 75, 0 });
		poly.setPosition(position.x - getLocalCenterX(), position.y);
		poly.setOrigin(0, 0);
		poly.setRotation(rotation - 90);
		setCenter(position);
		this.maxHealth = 500 * controller.getGameDifficulty().getEnemyHpMul();
		this.health = maxHealth;

		this.collisionPolygon = poly;
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public float getMaxHealth() {
		return maxHealth;
	}

	@Override
	public void damage(float dmg) {
		health = MathUtils.clamp(health - dmg, 0, getMaxHealth());
		if (isDead()) {
			kill();
			controller.addPlunder(plunderValue);
			controller.addXp(xpValue);
		}
	}

	@Override
	public void OnCollision(PhysicsObject other) {
		if (other instanceof Boat) {
			((Boat) other).damage(5 * Gdx.graphics.getDeltaTime());
		} else if (other instanceof Projectile && ((Projectile) other).isPlayerProjectile()) {
			other.kill();
			damage(((Projectile) other).getDamage());
		}
	}

	public void Move(float delta) {
		timeOnCurrentDirection += delta;
		if (timeOnCurrentDirection >= timeBetweenDirectionChanges) {
			timeOnCurrentDirection = 0;
			direction = new Vector2(MathUtils.randomSign(), MathUtils.randomSign());
			frameCounter = Math.max(1, (frameCounter + 1) % 4);

			String newFrame = null;
			switch (frameCounter) {
			case 1:
				newFrame = frame1;
				break;
			case 2:
				newFrame = frame2;
				break;
			case 3:
				newFrame = frame3;

			}
			setSprite(newFrame, position, new Vector2(200, 200));
		}

		position.add(direction.cpy().scl(delta * speed));

		sprite.setPosition(position.x - 95, position.y - 145);
		collisionPolygon.setPosition(position.x - getLocalCenterX(), position.y);
		collisionPolygon.setRotation(rotation - 90);
	}

	@Override
	public void Update(float delta) {
		Move(delta);
		timeSinceLastShot += delta;
		if (timeSinceLastShot >= timeBetweenShots) {
			timeSinceLastShot = 0;
			Shoot();
		}
	}

	public void ShotgunShot() {
		Vector2 origin = new Vector2(position.x - 25, position.y - 25);

		float direction = new Vector2(origin.x - controller.playerBoat.position.x,
				origin.y - controller.playerBoat.position.y).angleDeg();

		for (int i = 0; i < 6; i++) {
			Projectile proj = createProjectile(projectileType, direction + (15 * i) + 135,
					controller.getGameDifficulty().getEnemyDmgMul(), 1,
					new Vector2(origin.x + i, origin.y + i));

			// Add the projectile to the GameController's physics objects list
			// so it receives updates
			controller.NewPhysicsObject(proj);
		}
	}

	public void Shoot() {
		float dst = getCenter().dst(controller.playerBoat.getCenter());
		if (dst <= attackRange) {
			ShotgunShot();
		}
	}

}
