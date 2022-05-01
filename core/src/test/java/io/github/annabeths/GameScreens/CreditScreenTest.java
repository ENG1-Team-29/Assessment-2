package io.github.annabeths.GameScreens;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;

public class CreditScreenTest {

	CreditScreen cs;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
		Gdx.files = mock(Files.class, withSettings().defaultAnswer(RETURNS_MOCKS));
	}

	@BeforeEach
	public void setup() {
		cs = mock(CreditScreen.class, withSettings().useConstructor(mock(eng1game.class))
				.defaultAnswer(CALLS_REAL_METHODS));
		cs.stage = mock(Stage.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
		when(cs.stage.getHeight()).thenReturn(720f);
	}

	@Test
	public void testRender() {
		assertDoesNotThrow(() -> cs.render(1f));
	}

	@Test
	public void testEscReturnToMenu() {
		when(Gdx.input.isKeyJustPressed(Keys.ESCAPE)).thenReturn(false);
		cs.render(1f);
		verify(cs, never()).returnToMenu();

		when(Gdx.input.isKeyJustPressed(Keys.ESCAPE)).thenReturn(true);
		cs.render(1f);
		verify(cs, times(1)).returnToMenu();
	}

	@Test
	public void testEndReturnToMenu() {
		cs.marioLogo = mock(Image.class);
		when(cs.marioLogo.getY(anyInt())).thenReturn(725f);

		cs.render(1f);
		verify(cs, times(1)).returnToMenu();
	}

	@Test
	public void testResize() {
		cs.resize(100, 100);

		verify(cs.stage.getViewport(), times(1)).update(eq(100), eq(100), eq(true));
	}

}
