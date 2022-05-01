package io.github.annabeths.GeneralControl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.annabeths.GameScreens.CreditScreen;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GameScreens.GameDifScreen;
import io.github.annabeths.GameScreens.GameOverScreen;
import io.github.annabeths.GameScreens.GameWinScreen;
import io.github.annabeths.GameScreens.Menu;
import io.github.annabeths.GameScreens.Screens;
import io.github.annabeths.GameScreens.Splash;

/** @tt.updated Assessment 2 */
public class eng1game extends Game {

	SpriteBatch batch;
	Texture img;
	Menu menuScreen;
	GameController gameScreen;

	/**
	 * A placeholder for the difficulty so the gamecontroller can be instantiated
	 */
	private Difficulty diff = Difficulty.MEDIUM;

	public boolean timeUp = false;

	/** @see GameController#getGameScore() */
	public int gameScore = 0;

	/**
	 * This debug value is controlled by arguments passed to the game. This value
	 * must NOT be changed manually from FALSE.
	 */
	private boolean debug = false;

	public eng1game(boolean debug) {
		this.debug = debug;
	}

	public eng1game() {
	}

	@Override
	public void create() {
		ResourceManager.init(new AssetManager());
		if (!debug) DebugUtils.initDebugSettings();
		// create a menu and game screen, then switch to a new splash screen
		menuScreen = new Menu(this);
		gameScreen = new GameController(this, diff);
		if (DebugUtils.SKIP_SPLASH) {
			gotoScreen(Screens.menuScreen);
		} else {
			gotoScreen(Screens.splashScreen);
		}
	}

	/**
	 * Disposes of an old game screen so that we don't have multiple running at once
	 */
	public void removeGameScreen() {
		gameScreen.dispose();
		gameScreen = null;
	}

	/**
	 * Uses the {@link Screens} enumeration to change between any screen.
	 * 
	 * @param s the screen to switch to
	 */
	public void gotoScreen(Screens s) {
		switch (s) {
		case splashScreen: // creates a new splash screen
			Splash splashScreen = new Splash(this);
			setScreen(splashScreen);
			break;
		case menuScreen: // switch back to the menu screen
			setScreen(menuScreen);
			break;
		case gameScreen: // switch back to the game screen
			gameScreen = new GameController(this, diff);
			setScreen(gameScreen);
			break;
		case gameOverScreen:
			removeGameScreen();
			GameOverScreen gameOverScreen = new GameOverScreen(this,
					timeUp ? "Time Up! ENTER to go to menu, R to restart"
							: "You Died! ENTER to go to menu, R to restart");
			setScreen(gameOverScreen);
			break;
		case gameWinScreen:
			removeGameScreen();
			GameWinScreen gameWinScreen = new GameWinScreen(this);
			setScreen(gameWinScreen);
			break;
		case gameDifScreen:
			GameDifScreen gameDifScreen = new GameDifScreen(this);
			setScreen(gameDifScreen);
			break;
		case credits:
			setScreen(new CreditScreen(this));
			break;
		}
	}

	public void setFullscreen() {
		Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		Gdx.graphics.setFullscreenMode(currentMode);
		Gdx.app.log("eng1game", "Switched to Fullscreen");
	}

	public void setDifficulty(Difficulty difficulty) {
		Gdx.app.log("eng1game", String.format("Set game difficulty to %s", difficulty.toString()));
		if (gameScreen != null) gameScreen.setDifficulty(difficulty);
		this.diff = difficulty;
	}
}
