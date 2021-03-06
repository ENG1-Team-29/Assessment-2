package io.github.annabeths.Boats;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Collectables.Powerup;
import io.github.annabeths.Collectables.PowerupType;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Projectiles.Projectile;

/**
 * A boat that attacks instances of PlayerBoat and FriendlyBoat.
 * Spawned at EnemyCollege. Grants plunder and xp when killed.
 * 
 * @since Assessment 2
 * @author James Burnell
 * @author Hector Woods
 */
public class EnemyBoat extends AttackBoat {

	/**
	 * Constructor for EnemyBoat
	 * @param controller the game controller
	 * @param position the position of the boat
	 */
	public EnemyBoat(GameController controller, Vector2 position) {
		super(controller, position, "img/entity/boat_enemy.png");

		xpValue = 100;
		plunderValue = 50;

		this.maxHP = 100 * controller.getGameDifficulty().getEnemyHpMul();
		this.HP = this.maxHP;
		this.speed = 75;
		this.turnSpeed = 150;
		// uncomment for fun
		// this.shotDelay = 0.01f;
	}

	/**
	 * Get the nearest PlayerBoat or FriendlyBoat in range.
	 * @return an instance of Boat (either a PlayerBoat or FriendlyBoat)
	 */
	public Boat getNearestTarget() {
		if (controller.playerBoat.getCenter().dst(getCenter()) < approachRange) {
			return controller.playerBoat;
		}

		FriendlyBoat nearestTarget = null;
		float shortestDistance = 1000;
		for (PhysicsObject obj : controller.physicsObjects) {
			if (obj instanceof FriendlyBoat) {
				float dst = obj.getCenter().dst(getCenter());
				if (dst < shortestDistance) {
					nearestTarget = (FriendlyBoat) obj;
					shortestDistance = dst;
				}
			}
		}
		return nearestTarget;
	}

	/**
	 * Mark the boat to be destroyed on the start of the next frame and a chance
	 * to drop a powerup at the location of the boat
	 */
	@Override
	public void Destroy() {
		killOnNextTick = true;
		if (MathUtils.randomBoolean(0.2f)) {
			controller.NewPhysicsObject(new Powerup(PowerupType.randomPower(), getCenter()));
		}

	}

	/**
	 * Called when the boat collides with another PhysicsObject.
	 * @param other the object collided with
	 */
	@Override
	public void OnCollision(PhysicsObject other) {
		float dmgToInflict = 0;
		// whether the object belongs to the player
		boolean objWasPlayer = false;

		if (other instanceof Projectile) {
			Projectile p = (Projectile) other;
			if (p.isPlayerProjectile()) {
				objWasPlayer = true;
			}
			if(p.isFriendlyProjectile()){
				other.kill();
				dmgToInflict = p.getDamage();
			}
		} else if (other instanceof PlayerBoat) {
			((PlayerBoat) other).damage(50);
			Destroy();
			controller.addXp((getHealth() / getMaxHealth()) * xpValue);
			controller.addPlunder(plunderValue);
		}

		if (objWasPlayer && getHealth() - dmgToInflict <= 0){
			controller.addXp(xpValue);
			controller.addPlunder(plunderValue);
		}
		damage(dmgToInflict);
	}

}
