package io.github.annabeths.GameScreens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.annabeths.GeneralControl.ResourceManager;
import io.github.annabeths.GeneralControl.eng1game;

public class MenuTest {

	eng1game game;
	Menu m;

	@BeforeEach
	public void setup() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException {
		game = mock(eng1game.class);

		m = mock(Menu.class, withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));
		m.menuTextLayout = mock(GlyphLayout.class);
		Field f = Menu.class.getDeclaredField("batch");
		f.setAccessible(true);
		f.set(m, mock(SpriteBatch.class));

		ResourceManager.font = mock(BitmapFont.class,
				withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
		Gdx.input = mock(Input.class);
		Gdx.gl = mock(GL20.class);
		Gdx.graphics = mock(Graphics.class);
		Gdx.app = mock(Application.class);
	}

	@Test
	public void testConstruction() {
		assertDoesNotThrow(() -> new Menu(game));
	}

	@Test
	public void testRender() {
		assertDoesNotThrow(() -> m.render(1f));
		// test renders menu text
		verify(ResourceManager.font, times(1)).draw(any(), eq(m.menuTextLayout), anyFloat(),
				anyFloat());
	}

	@Test
	public void testEnterPressed() {
		when(Gdx.input.isKeyJustPressed(Keys.ENTER)).thenReturn(false);
		m.render(1f);
		verify(game, never()).gotoScreen(any());

		when(Gdx.input.isKeyJustPressed(Keys.ENTER)).thenReturn(true);
		m.render(1f);
		verify(game, times(1)).gotoScreen(any());
	}

	@Test
	public void testEscPressed() {
		when(Gdx.input.isKeyJustPressed(Keys.ESCAPE)).thenReturn(false);
		m.render(1f);
		verify(Gdx.app, never()).exit();

		when(Gdx.input.isKeyJustPressed(Keys.ESCAPE)).thenReturn(true);
		m.render(1f);
		verify(Gdx.app, times(1)).exit();
	}

}
