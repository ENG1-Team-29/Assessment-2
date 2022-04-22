package io.github.annabeths.GeneralControl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;

import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GameScreens.GameDifScreen;
import io.github.annabeths.GameScreens.GameOverScreen;
import io.github.annabeths.GameScreens.GameWinScreen;
import io.github.annabeths.GameScreens.Menu;
import io.github.annabeths.GameScreens.Screens;
import io.github.annabeths.GameScreens.Splash;

public class eng1gameTest {

	eng1game game;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
	}

	@BeforeEach
	public void setup() {
		game = mock(eng1game.class,
				withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
		game.gameScreen = mock(GameController.class);
		game.menuScreen = mock(Menu.class);
		doNothing().when(game).setScreen(any());
	}

	@Test
	public void testConstructor() {
		assertDoesNotThrow(() -> new eng1game(true));
		assertDoesNotThrow(() -> new eng1game());
	}

	@Test
	public void testRemoveGameScreen() {
		game.removeGameScreen();
		assertNull(game.gameScreen);
	}

	@Test
	public void testSetFullscreen() {
		game.setFullscreen();
		verify(Gdx.graphics, times(1)).setFullscreenMode(any());
	}

	@Test
	public void testSetDifficulty() {
		game.setDifficulty(Difficulty.MEDIUM);
		verify(game.gameScreen, times(1)).setDifficulty(any());

		game.gameScreen = null;
		assertDoesNotThrow(() -> game.setDifficulty(Difficulty.MEDIUM));
	}

	@Test
	public void testGotoScreenSplash() {
		game.gotoScreen(Screens.splashScreen);
		verify(game, times(1)).setScreen(any(Splash.class));
	}

	@Test
	public void testGotoScreenMenu() {
		game.gotoScreen(Screens.menuScreen);
		verify(game, times(1)).setScreen(any(Menu.class));
	}

	@Test
	public void testGotoScreenGame() {
		game.gameScreen = null;
		game.gotoScreen(Screens.gameScreen);
		verify(game, times(1)).setScreen(eq(game.gameScreen));
		assertNotNull(game.gameScreen);
	}

	@Test
	public void testGotoScreenGameOver() {
		game.gotoScreen(Screens.gameOverScreen);
		verify(game, times(1)).setScreen(any(GameOverScreen.class));
	}

	@Test
	public void testGotoScreenGameWin() {
		game.gotoScreen(Screens.gameWinScreen);
		verify(game, times(1)).setScreen(any(GameWinScreen.class));
	}

	@Test
	public void testGotoScreenGameDif() {
		game.gotoScreen(Screens.gameDifScreen);
		verify(game, times(1)).setScreen(any(GameDifScreen.class));
	}

}
