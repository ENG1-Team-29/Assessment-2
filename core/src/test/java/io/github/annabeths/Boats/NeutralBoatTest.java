package io.github.annabeths.Boats;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Colleges.College;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.Projectile;
import io.github.annabeths.Projectiles.ProjectileData;

public class NeutralBoatTest {

	private static GameController gc;
	private NeutralBoat nb;

	@BeforeAll
	public static void init() {
		gc = mock(GameController.class);
		gc.map = mock(GameMap.class);
		gc.colleges = new ArrayList<College>();
		gc.physicsObjects = new ArrayList<PhysicsObject>();
		doCallRealMethod().when(gc).NewPhysicsObject(any(PhysicsObject.class));
	}

	@BeforeEach
	public void setup() {
		nb = new NeutralBoat(gc, new Vector2(0, 0));
	}

	@Test
	public void testDestroyKills() {
		assertFalse(nb.removeOnNextTick());
		nb.Destroy();
		assertTrue(nb.removeOnNextTick());
	}

	@Test
	public void testDestroyDropsPowerupRandomly() {
		// The number of powerups should be less than the number of destroy calls
		int runs = 10000;
		for (int i = 0; i < runs; i++) {
			nb.Destroy();
		}
		assertTrue(gc.physicsObjects.size() < runs);
	}

	@Test
	public void testOnCollisionPlayerBoat() {
		PlayerBoat pb = new PlayerBoat(gc, new Vector2(0, 0));
		int xp = (int) gc.xp;
		int gold = gc.plunder;
		nb.OnCollision(pb);
		assertTrue(gc.xp > xp);
		assertTrue(gc.plunder > gold);
		assertTrue(nb.removeOnNextTick());
	}

	@Test
	public void testOnCollisionProjectileKill() {
		Projectile p = new Projectile(new Vector2(), 0, ProjectileData.STOCK, true);
		nb.OnCollision(p);
		assertTrue(p.removeOnNextTick());
	}

	@Test
	public void testOnCollisionProjectilePlayer() {
		Projectile p = new Projectile(new Vector2(), 0, ProjectileData.STOCK, true);
		int xp = (int) gc.xp;
		nb.OnCollision(p);
		assertTrue(gc.xp > xp);
		assertEquals(nb.getMaxHealth() - p.getDamage(), nb.getHealth());
	}

	@Test
	public void testOnCollisionProjectileNotPlayer() {
		Projectile p = new Projectile(new Vector2(), 0, ProjectileData.STOCK, false);
		int xp = (int) gc.xp;
		nb.OnCollision(p);
		assertEquals(xp, gc.xp); // no xp for non player projectiles
		assertEquals(nb.getMaxHealth() - p.getDamage(), nb.getHealth());
	}

	@Test
	public void testOnCollisionOther() {
		GenericBoat g = new GenericBoat(new Vector2(0, 0));
		float hp = nb.getHealth();
		assertDoesNotThrow(() -> nb.OnCollision(g));
		assertEquals(hp, nb.getHealth()); // no damage
	}

}
