package io.github.annabeths.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

import io.github.annabeths.GeneralControl.ResourceManager;
import io.github.annabeths.GeneralControl.eng1game;

/**
 * A screen to display credits for the game
 * 
 * @since Assessment 2
 * @author James Burnell
 */
public class CreditScreen implements Screen {

	private eng1game game;
	private String creditText;
	/** How fast the credits scroll in px/sec */
	private float scrollSpeed = 100;

	public Stage stage;
	public LabelStyle lblStyle;
	public Label label;
	public Image shardLogo;
	public Image marioLogo;

	public Music music;
	/** How long the music will fade out in seconds */
	public static final float MUSIC_FADE_TIME = 4f;

	public CreditScreen(eng1game game) {
		this.game = game;

		creditText = Gdx.files.internal("ui/credits.txt").readString();

		lblStyle = new LabelStyle();
		lblStyle.font = ResourceManager.debugFont;
		lblStyle.fontColor = Color.WHITE;

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/music/credits.mp3"));

		label = new Label(creditText, lblStyle);
		label.setAlignment(Align.center);
		label.setFontScale(1f);
		label.setHeight(label.getPrefHeight());
		label.setPosition(0, -label.getPrefHeight() - 100);

		shardLogo = new Image(ResourceManager.getTexture("mario/shard-software-logo4.png"));
		shardLogo.setScale(0.5f);

		marioLogo = new Image(ResourceManager.getTexture("mario/full.png"));
	}

	@Override
	public void show() {
		stage = new Stage(new FillViewport(1280, 720));

		label.setWidth(stage.getWidth());
		stage.addActor(label);

		shardLogo.setPosition((stage.getWidth() - shardLogo.getWidth() * 0.5f) / 2,
				label.getY() - shardLogo.getHeight() * 0.5f);
		stage.addActor(shardLogo);

		marioLogo.setPosition((stage.getWidth() - marioLogo.getWidth()) / 2,
				shardLogo.getY() - marioLogo.getHeight());
		stage.addActor(marioLogo);

		music.play();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);

		// skip credits
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			returnToMenu();
		}

		// scroll text and images up
		label.moveBy(0, scrollSpeed * delta);
		shardLogo.moveBy(0, scrollSpeed * delta);
		marioLogo.moveBy(0, scrollSpeed * delta);

		// go to the menu if the credits are over
		if (marioLogo.getY(Align.bottom) > stage.getHeight()) {
			returnToMenu();
		}

		music.setVolume(MathUtils.clamp(timeLeft() / MUSIC_FADE_TIME, 0, 1));

		stage.draw();
	}

	public void returnToMenu() {
		game.gotoScreen(Screens.menuScreen);
		music.stop();
		music.dispose();
	}

	/**
	 * How long is left in the credits before they end
	 * 
	 * @return the time left in seconds
	 */
	public float timeLeft() {
		return (stage.getHeight() - marioLogo.getY(Align.bottom)) / scrollSpeed;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
