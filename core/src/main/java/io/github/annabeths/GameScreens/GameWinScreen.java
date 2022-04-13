package io.github.annabeths.GameScreens;

import static io.github.annabeths.GeneralControl.ResourceManager.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.annabeths.GeneralControl.eng1game;

public class GameWinScreen implements Screen {

	private SpriteBatch batch;
	GlyphLayout winTextLayout;
	eng1game game;

	public GameWinScreen(eng1game g) {
		game = g;
		winTextLayout = new GlyphLayout(font, "YOU WIN! Press ENTER to return to the menu");
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			game.gotoScreen(Screens.menuScreen);
		}

		// do draws
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		font.getData().setScale(1);
		// the below line centers the text on the center of the screen
		font.draw(batch, winTextLayout, Gdx.graphics.getWidth() / 2 - winTextLayout.width / 2,
				Gdx.graphics.getHeight() / 2 + winTextLayout.height / 2);
		batch.end();
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
