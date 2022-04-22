package io.github.annabeths.Collectables;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;

public class PowerupTest {

	GameController gc;
	eng1game game;
	Powerup p;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
	}

	@BeforeEach
	public void setup() {
		game = mock(eng1game.class);
		gc = mock(GameController.class,
				withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));
		p = mock(Powerup.class, withSettings().useConstructor(PowerupType.DAMAGE, new Vector2(0, 0))
				.defaultAnswer(CALLS_REAL_METHODS));
	}

	@Test
	public void testOnCollision() {
		p.OnCollision(gc.playerBoat);
		assertTrue(p.removeOnNextTick());
	}

}
