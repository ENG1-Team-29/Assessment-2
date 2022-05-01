package io.github.annabeths.GameScreens;

import static io.github.annabeths.GeneralControl.ResourceManager.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.annabeths.GeneralControl.ResourceManager;
import io.github.annabeths.GeneralControl.eng1game;
import io.github.annabeths.UI.HUD;

/**
 * @author James Burnell
 * @tt.updated Assessment 2
 */
public class Menu implements Screen {
	private SpriteBatch batch;
	GlyphLayout menuTextLayout;
	eng1game game;

	Texture instructions;
	/** whether or not the instructions should be rendered */
	boolean showInstructions;
	/** whether or not the instructions have been shown at least once */
	boolean instructionsBeenShown;
	private HUD hud;

	public Menu(eng1game g) {
		game = g;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		// layouts can be used to manage text to allow it to be centered
		menuTextLayout = new GlyphLayout();
		menuTextLayout.setText(font,
				"press ENTER to PLAY\n"
				+ "press I to toggle INSTRUCTIONS\n"
				+ "press C for credits\n"
				+ "press ESCAPE to QUIT");
		instructions = ResourceManager.getTexture("ui/instructions.png");
		// create example HUD
		hud = new HUD(GameController.getMockForHUD());
		hud.Update(1f);
	}

	@Override
	public void render(float delta) {
		// do updates

		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			toggleInstructions();
		} else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			// Ensure the player has seen the instructions
			if (instructionsBeenShown) {
				// if the ENTER key is pressed, switch to the difficulty screen
				showInstructions = false;
				game.gotoScreen(Screens.gameDifScreen);
			} else {
				toggleInstructions();
			}
		} else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		} else if (Gdx.input.isKeyJustPressed(Keys.C)) {
			game.gotoScreen(Screens.credits);
		}

		// do draws
		ScreenUtils.clear(Color.BLACK);

		batch.begin(); // start batch
		font.getData().setScale(1);
		// the below line centers the text on the center of the screen
		font.draw(batch, menuTextLayout, Gdx.graphics.getWidth() / 2 - menuTextLayout.width / 2,
				Gdx.graphics.getHeight() / 2 + menuTextLayout.height / 2);
		if (showInstructions)
			batch.draw(instructions, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch.end(); // end batch
		if (showInstructions) hud.Draw(batch);
	}

	public void toggleInstructions() {
		showInstructions = !showInstructions;
		instructionsBeenShown = true;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
