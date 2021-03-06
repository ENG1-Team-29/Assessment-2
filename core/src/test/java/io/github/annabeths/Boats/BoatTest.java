package io.github.annabeths.Boats;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Colleges.College;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.MathHelper;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.Projectile;
import io.github.annabeths.Projectiles.ProjectileData;

public class BoatTest {

	public GameController gc;
	public Boat b;

	@BeforeEach
	public void setup() {
		gc = mock(GameController.class);
		gc.map = mock(GameMap.class);
		gc.colleges = new ArrayList<College>();
		gc.playerBoat = new PlayerBoat(gc, new Vector2(0, 0));

		// Create new generic boat
		b = newBoat();
	}

	private Boat newBoat() {
		Boat result = new Boat(gc, new Vector2(0, 0), "") {
			@Override
			public void Update(float delta) {
			}

			@Override
			void Shoot() {
			}

			@Override
			void Destroy() {
			}

			@Override
			public void OnCollision(PhysicsObject other) {
			}
		};
		result.maxHP = 100;
		result.HP = result.maxHP;
		result.turnSpeed = 1;
		return result;
	}

	@Test
	public void testSetPosition() {
		assertDoesNotThrow(() -> b.SetPosition(5, 7));
		assertEquals(5, b.getX());
		assertEquals(7, b.getY());
		assertEquals(5, b.sprite.getX());
		assertEquals(7, b.sprite.getY());
	}

	@Test
	public void testDamageClamp() {
		b.damage(1000); // damage more than max health
		assertEquals(0, b.getHealth());

		b.HP = b.maxHP; // reset
		b.damage(-1000); // heal more than max health
		assertEquals(b.maxHP, b.getHealth());
	}

	@Test
	public void testIsDead() {
		assertFalse(b.isDead()); // ensure the boat is alive to start with
		b.kill();
		assertTrue(b.isDead());

		b = newBoat(); // reset
		assertFalse(b.isDead());
		b.HP = -1;
		assertTrue(b.isDead());
	}

	@Test
	public void testDraw() {
		assertDoesNotThrow(() -> b.Draw(mock(SpriteBatch.class)));
	}

	@Test
	public void testRotation() {
		b.rotation = 180;
		// turn left
		float rot = b.rotation;
		b.Turn(0.05f, 1);
		assertTrue(b.rotation > rot);

		// turn right
		rot = b.rotation;
		b.Turn(0.05f, -1);
		assertTrue(b.rotation < rot);
	}

	@Test
	public void testRotationConsistency() {
		b.Turn(1, 389);
		// ensure rotation is matched
		assertEquals(b.rotation, b.sprite.getRotation());
		assertEquals(b.rotation - 90, b.collisionPolygon.getRotation());
	}

	@Test
	public void testRotationLimits() {
		// left rotate limit
		b.Turn(1, 389); // turn 389 degrees left
		assertEquals(29, b.rotation);

		b.rotation = 0; // reset

		// turn right
		b.Turn(1, -13);
		assertEquals(360 - 13, b.rotation);
	}

	@Test
	public void testCreateProjectile() {
		b.rotation = 13;
		Projectile p = b.createProjectile(ProjectileData.STOCK, 137, 1.5f, 1.5f);
		assertNotNull(p);
		assertEquals(ProjectileData.STOCK.getDamage() * 1.5f, p.getDamage());
		assertEquals(ProjectileData.STOCK.getSpeed() * 1.5f, p.getSpeed());
		assertFalse(p.isPlayerProjectile());
		assertEquals(b.rotation + 137, p.rotation);
	}

	private void testAngleDir(float resetAng, float goal, boolean shouldTurnLeft) {
		b.rotation = resetAng;
		b.moveTowardsDesiredAngle(goal, 10f);
		if (MathHelper.getAbsDiff2Angles(resetAng, goal) < 0.5f) {
			assertEquals(resetAng, b.rotation);
			return;
		}

		float trueAng = resetAng + (shouldTurnLeft ? 10 : -10);
		float diff = MathHelper.getAbsDiff2Angles(trueAng, b.rotation);
		assertTrue(diff < 0.05f, String.format("init: %.1f | goal: %.1f | true: %.1f | curr: %.1f",
				resetAng, goal, trueAng, b.rotation));
	}

	@Test
	public void testMoveTowardsDesiredAngle() {
		for (float currentAng = 0; currentAng < 360; currentAng += 15.5f) {
			for (float desiredAng = 0; desiredAng < 360; desiredAng += 11.511f) {
				float angDiff = MathHelper.getAbsDiff2Angles(currentAng, desiredAng);
				float test = (currentAng + angDiff) % 360;
				boolean turnLeft = Math.abs(test - desiredAng) < 0.05f;
				testAngleDir(currentAng, desiredAng, turnLeft);
			}
		}
	}

	@Test
	public void testMoveTowardsDesiredAngleAtZero() {

		// @0 - should turn left
		testAngleDir(0, 150, true);

		// @0 - should turn right
		testAngleDir(0, 275, false);
	}

	@Test
	public void testMoveTowardsDesiredAngleAt180() {
		final float resetVal = 180f;
		// @180 - should turn left
		testAngleDir(resetVal, 359, true);

		// @180 - should turn left
		testAngleDir(resetVal, 250, true);

		// @180 - should turn right
		testAngleDir(resetVal, 1, false);

		// @180 - should turn right
		testAngleDir(resetVal, 100, false);
	}

	@Test
	public void testMoveTowardsDesiredAngleAt90() {
		final float resetVal = 90f;
		// should turn left
		testAngleDir(resetVal, 179, true);

		// should turn left
		testAngleDir(resetVal, 200, true);

		// should turn right
		testAngleDir(resetVal, 1, false);

		// should turn right
		testAngleDir(resetVal, 359, false);

		// should turn right
		testAngleDir(resetVal, 285, false);
	}

	@Test
	public void testMoveTowardsDesiredAngleAt270() {
		final float resetVal = 270f;
		// should turn left
		testAngleDir(resetVal, 359, true);

		// should turn left
		testAngleDir(resetVal, 89, true);

		// should turn right
		testAngleDir(resetVal, 180, false);

		// should turn right
		testAngleDir(resetVal, 95, false);

		// should turn right
		testAngleDir(resetVal, 185, false);
	}

	@Test
	public void testMoveTowardsDesiredAngleAt225() {
		final float resetVal = 225f;

		// should turn left
		testAngleDir(resetVal, 45, true);

		// should turn right
		testAngleDir(resetVal, 90, false);
	}

}
