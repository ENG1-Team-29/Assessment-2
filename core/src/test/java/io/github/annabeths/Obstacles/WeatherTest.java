package io.github.annabeths.Obstacles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;

public class WeatherTest {

	GameController gc;
	Weather w;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
	}

	@BeforeEach
	public void setup() {
		gc = mock(GameController.class, withSettings().useConstructor(mock(eng1game.class))
				.defaultAnswer(CALLS_REAL_METHODS));
		w = mock(Weather.class, withSettings().useConstructor(gc, new Vector2(0, 0), 0)
				.defaultAnswer(CALLS_REAL_METHODS));
	}

	@Test
	public void testToggleLightning() {
		w.damageActive = false;
		w.timeUntilNextLightningStrike = -1;
		w.timeSinceLastStrike = -1;
		w.timeSinceStrikeStarted = -1;

		w.toggleLightning();
		assertTrue(w.damageActive);
		assertNotEquals(-1, w.timeUntilNextLightningStrike);
		assertEquals(0, w.timeSinceLastStrike);

		w.toggleLightning();
		assertFalse(w.damageActive);
		assertEquals(0, w.timeSinceStrikeStarted);
	}

	@Test
	public void testOnCollisionDamage() {
		w.damageActive = true;
		PlayerBoat pb = mock(PlayerBoat.class);
		assertDoesNotThrow(() -> w.OnCollision(pb));
		verify(gc, times(1)).addXp(anyFloat());
		verify(pb, times(1)).damage(anyFloat());
	}

	@Test
	public void testOnCollisionNoDamage() {
		w.damageActive = false;
		PlayerBoat pb = mock(PlayerBoat.class);
		assertDoesNotThrow(() -> w.OnCollision(pb));
		verify(gc, times(1)).addXp(anyFloat());
		verify(pb, never()).damage(anyFloat());
	}

	@Test
	public void testOnCollisionNonBoat() {
		w.damageActive = true;
		Kraken kraken = mock(Kraken.class);
		assertDoesNotThrow(() -> w.OnCollision(kraken));
		verify(gc, never()).addXp(anyFloat());
		verify(kraken, never()).damage(anyFloat());
	}

	@Test
	public void testMove() {
		w.timeOnCurrentDirection = 10;
		Vector2 pos = w.position.cpy();
		w.Move(1f);
		assertNotEquals(pos, w.position);
	}

	@Test
	public void testMoveOutOfBounds() {
		w.position.set(-2000, 0);
		w.Move(1f);
		verify(w, times(1)).kill();
	}

	@Test
	public void testUpdateDamage() {
		w.damageActive = true;
		w.Update(1f);
		verify(w, times(1)).Move(anyFloat());
		verify(w, times(1)).toggleLightning();
	}

	@Test
	public void testUpdateNoDamage() {
		w.damageActive = false;
		w.Update(10f);
		verify(w, times(1)).Move(anyFloat());
		verify(w, times(1)).toggleLightning();
	}

	@Test
	public void testGetDirection() {
		assertNotNull(w.getDirection());
	}

}
