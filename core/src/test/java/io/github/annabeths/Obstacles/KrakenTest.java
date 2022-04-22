package io.github.annabeths.Obstacles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;
import io.github.annabeths.Projectiles.Projectile;

public class KrakenTest {

	GameController gc;
	eng1game game;
	Kraken k;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
	}

	@BeforeEach
	public void setup() {
		game = mock(eng1game.class);
		gc = mock(GameController.class,
				withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));
		gc.playerBoat = mock(PlayerBoat.class, withSettings().useConstructor(gc, new Vector2(0, 0))
				.defaultAnswer(CALLS_REAL_METHODS));
		k = mock(Kraken.class, withSettings().useConstructor(gc, new Vector2(0, 0))
				.defaultAnswer(CALLS_REAL_METHODS));
	}

	@Test
	public void testDamage() {
		k.health = k.getMaxHealth();
		k.damage(1000);
		assertEquals(0, k.getHealth());

		k.health = k.getMaxHealth();
		k.damage(1);
		assertEquals(k.getMaxHealth() - 1, k.getHealth());
	}

	@Test
	public void testOnCollisionBoat() {
		PlayerBoat pb = mock(PlayerBoat.class);
		k.OnCollision(pb);
		verify(pb, times(1)).damage(anyFloat());
	}

	@Test
	public void testOnCollisionProj() {
		Projectile p = mock(Projectile.class);
		when(p.isPlayerProjectile()).thenReturn(true);
		k.OnCollision(p);
		verify(p, times(1)).kill();
		verify(k, times(1)).damage(anyFloat());
	}

	@Test
	public void testShoot() {
		gc.playerBoat.position.set(1000, 1000);
		k.Shoot();
		verify(k, times(0)).ShotgunShot();

		gc.playerBoat.position.set(0, 0);
		k.Shoot();
		verify(k, times(1)).ShotgunShot();
	}

	@Test
	public void testShotgunShot() {
		gc.physicsObjects.clear();
		k.ShotgunShot();
		assertEquals(6, gc.physicsObjects.size());
	}

	@Test
	public void testMove() {
		k.timeOnCurrentDirection = 10;
		Vector2 pos = k.position.cpy();
		k.Move(1f);
		assertNotEquals(pos, k.position);
	}

	@Test
	public void testUpdateShoot() {
		k.Update(10f);
		verify(k, times(1)).Shoot();
	}

	@Test
	public void testUpdateNoShoot() {
		k.Update(0f);
		verify(k, never()).Shoot();
	}

	@Test
	public void testDirectionSprite() {
		assertEquals(1, k.frameCounter);
		k.Update(0.3f);
		assertEquals(2, k.frameCounter);
		k.Update(0.3f);
		assertEquals(3, k.frameCounter);
		k.Update(0.3f);
		assertEquals(1, k.frameCounter);
	}

}
