package io.github.annabeths.GameScreens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.annabeths.GeneralControl.Difficulty;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;

public class GameDifScreenTest {

	GameDifScreen gds;
	eng1game game;
	Stage s;

	@BeforeAll
	public static void init() {
		TestHelper.setupEnv();
	}

	@BeforeEach
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		game = mock(eng1game.class);
		gds = mock(GameDifScreen.class,
				withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));

		s = mock(Stage.class);

		Field fs = GameDifScreen.class.getDeclaredField("stage");
		fs.setAccessible(true);
		fs.set(gds, s);
	}

	@Test
	public void testSetupButtons()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Method setupBtn = GameDifScreen.class.getDeclaredMethod("setupButtons");
		setupBtn.setAccessible(true);

		assertDoesNotThrow(() -> setupBtn.invoke(gds));
		TextButton[] btns = buttons();

		for (TextButton b : btns) {
			assertNotNull(b);
		}
	}

	public TextButton[] buttons() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field fb = GameDifScreen.class.getDeclaredField("buttons");
		fb.setAccessible(true);
		return (TextButton[]) fb.get(gds);
	}

	@Test
	public void testClickListener()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		testSetupButtons(); // ensure buttons are set up

		TextButton[] buttons = buttons();

		ClickListener cl = (ClickListener) buttons[0].getListeners().get(1);
		cl.clicked(null, 0, 0);
		verify(game, times(1)).setDifficulty(eq(Difficulty.EASY));

		ClickListener cl2 = (ClickListener) buttons[1].getListeners().get(1);
		cl2.clicked(null, 0, 0);
		verify(game, times(1)).setDifficulty(eq(Difficulty.MEDIUM));

		ClickListener cl3 = (ClickListener) buttons[2].getListeners().get(1);
		cl3.clicked(null, 0, 0);
		verify(game, times(1)).setDifficulty(eq(Difficulty.HARD));
	}

	@Test
	public void testRender() {
		assertDoesNotThrow(() -> gds.render(1f));
		verify(s, times(1)).act();
		verify(s, times(1)).draw();
	}

	@Test
	public void testDispose() {
		gds.dispose();
		verify(s, times(1)).dispose();
	}

}
