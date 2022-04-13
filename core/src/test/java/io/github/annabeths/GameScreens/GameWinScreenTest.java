package io.github.annabeths.GameScreens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.annabeths.GeneralControl.ResourceManager;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;

public class GameWinScreenTest {

	eng1game game;
	GameWinScreen gws;
	SpriteBatch batch;

	@BeforeAll
	public static void init() {
		Gdx.gl = mock(GL20.class);
		Gdx.graphics = mock(Graphics.class);
		TestHelper.initFonts();
	}

	@BeforeEach
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		game = mock(eng1game.class);
		gws = mock(GameWinScreen.class,
				withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));

		Gdx.input = mock(Input.class);

		batch = mock(SpriteBatch.class);
		Field f = GameWinScreen.class.getDeclaredField("batch");
		f.setAccessible(true);
		f.set(gws, batch);
	}

	@Test
	public void testRender() {
		assertDoesNotThrow(() -> gws.render(1f));
		verify(ResourceManager.font, times(1)).draw(any(), eq(gws.winTextLayout), anyFloat(),
				anyFloat());
	}

	@Test
	public void testReturnToMenu() {
		when(Gdx.input.isKeyJustPressed(Keys.ENTER)).thenReturn(false);
		gws.render(1f);
		verify(game, never()).gotoScreen(any());

		when(Gdx.input.isKeyJustPressed(Keys.ENTER)).thenReturn(true);
		gws.render(1f);
		verify(game, times(1)).gotoScreen(eq(Screens.menuScreen));
	}

}
