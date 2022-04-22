package io.github.annabeths.GameScreens;

import static io.github.annabeths.GeneralControl.Difficulty.EASY;
import static io.github.annabeths.GeneralControl.Difficulty.HARD;
import static io.github.annabeths.GeneralControl.Difficulty.MEDIUM;
import static io.github.annabeths.GeneralControl.ResourceManager.font;
import static io.github.annabeths.GeneralControl.ResourceManager.getTexture;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.annabeths.GeneralControl.Difficulty;
import io.github.annabeths.GeneralControl.eng1game;

/**
 * The menu where the game difficulty is selected
 * 
 * @author James Burnell
 */
public class GameDifScreen implements Screen {

	private Stage stage;

	private eng1game game;

	/** Array of the menu buttons */
	private TextButton[] buttons = new TextButton[4];

	public GameDifScreen(eng1game g) {
		game = g;
	}

	private void setupButtons() {
		// Size of each button
		Vector2 btnSize = new Vector2(150, 350);
		// Array of colors the text should be
		Color[] textColors = { Color.GREEN, Color.ORANGE, Color.RED, Color.BLACK };
		// Array of text to display on the buttons
		String[] buttonText = { "EASY", "MEDIUM", "HARD", "RETURN\nTO MENU" };
		Difficulty[] buttonDiff = { EASY, MEDIUM, HARD };

		// y position of the buttons
		float btnY = (Gdx.graphics.getHeight() - btnSize.y) / 2;
		// horizontal margin of each button
		float btnXMarg = 50;
		// Total width of buttons
		float menuWidth = (btnSize.x + btnXMarg) * 4 - btnXMarg;
		// left-most x position of menu
		float menuXPos = (Gdx.graphics.getWidth() - menuWidth) / 2;

		// Set up the styles of the buttons
		for (int i = 0; i < 4; i++) {
			// Define style for the button
			TextButtonStyle style = new TextButtonStyle();
			style.font = font;
			style.fontColor = textColors[i];
			style.up = new TextureRegionDrawable(getTexture("ui/upgradebutton.png"));

			// Define the button
			buttons[i] = new TextButton(buttonText[i], style);
			buttons[i].setSize(btnSize.x, btnSize.y);
			float x = menuXPos + i * (btnSize.x + btnXMarg);
			buttons[i].setPosition(x, btnY);

			// Define button click actions
			if (i < buttonDiff.length) {
				final int index = i;
				clickListener(buttons[i], event -> {
					game.setDifficulty(buttonDiff[index]);
					game.gotoScreen(Screens.gameScreen);
				});
			}

			// Add button to stage
			stage.addActor(buttons[i]);
		}

		// Return to menu
		clickListener(buttons[3], event -> game.gotoScreen(Screens.menuScreen));
	}

	/**
	 * Created a new click listener based on the action given and assigns it as a
	 * new listener to the actor. Used to make code cleaner elsewhere.
	 * 
	 * @param act the actor to apply the listener to
	 * @param actions the actions to run upon clicking the actor
	 * @author James Burnell
	 */
	private void clickListener(Actor act, Consumer<InputEvent> actions) {
		act.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				actions.accept(event);
			}
		});
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		setupButtons();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.DARK_GRAY);
		stage.act();
		stage.draw();
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
		stage.dispose();
	}

}
