package io.github.annabeths.GeneralControl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GameScreens.GameOverScreen;
import io.github.annabeths.GameScreens.GameWinScreen;
import io.github.annabeths.GameScreens.Menu;
import io.github.annabeths.GameScreens.Screens;
import io.github.annabeths.GameScreens.Splash;

public class eng1game extends Game {

	SpriteBatch batch;
	Texture img;
	Menu menuScreen;
	GameController gameScreen;

	public boolean timeUp = false;

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
		gameScreen = new GameController(this);
		// gotoScreen(Screens.splashScreen);
		// splash screen commented out for now, in order to make testing faster,
		// splash will be re-added when the game is done
		// for now go directly to the menu
		gotoScreen(Screens.menuScreen);
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
			gameScreen = new GameController(this);
			setScreen(gameScreen);
			break;
		case gameOverScreen:
			GameOverScreen gameOverScreen = new GameOverScreen(this,
					timeUp ? "Time Up! ENTER to go to menu, R to restart"
							: "You Died! ENTER to go to menu, R to restart");
			setScreen(gameOverScreen);
			break;
		case gameWinScreen:
			GameWinScreen gameWinScreen = new GameWinScreen(this);
			setScreen(gameWinScreen);
		}
	}

	@Override
	public void render() {
		/*
		 * ScreenUtils.clear(1, 0, 0, 1); batch.begin(); batch.draw(img, 0, 0);
		 * batch.end();
		 */
		super.render();
	}

	@Override
	public void dispose() {
		/*
		 * batch.dispose(); img.dispose();
		 */
		super.dispose();
	}
}
