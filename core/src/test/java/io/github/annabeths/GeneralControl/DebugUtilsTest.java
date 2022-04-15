package io.github.annabeths.GeneralControl;

import static io.github.annabeths.GeneralControl.ResourceManager.debugFont;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.AIBoat;
import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.Colleges.College;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.ProjectileRay;

public class DebugUtilsTest {

	public GameController gc;

	@BeforeEach
	public void setup() {
		Gdx.graphics = mock(Graphics.class);
		gc = mock(GameController.class);
		gc.map = mock(GameMap.class);
		gc.colleges = new ArrayList<College>();
		gc.physicsObjects = new ArrayList<PhysicsObject>();
		gc.rays = new ArrayList<ProjectileRay>();
		gc.playerBoat = new PlayerBoat(gc, new Vector2(0, 0));
	}

	@Test
	public void testGenerateDebugText() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Method m = DebugUtils.class.getDeclaredMethod("generateDebugText", GameController.class);
		m.setAccessible(true);
		assertNotNull(m.invoke(null, gc));

		gc.colleges.add(mock(College.class));
		assertNotNull(m.invoke(null, gc));
	}

//	@Test
//	public void testTimeCodeMs() {
//		long time = DebugUtils.timeCodeMs(() -> {
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		});
//		// should not have overhead in the ms range
//		assertEquals(50, time);
//	}

	@Test
	public void testDrawDebugText() {
		debugFont = mock(BitmapFont.class);
		DebugUtils.drawDebugText(gc, mock(SpriteBatch.class));
		verify(debugFont, atLeast(1)).draw(any(), anyString(), anyFloat(), anyFloat());
	}

	@Test
	public void testDrawEntityDebugText() {
		debugFont = mock(BitmapFont.class);
		gc.physicsObjects.add(mock(AIBoat.class));
		DebugUtils.drawEntityDebugText(gc, mock(SpriteBatch.class));
		verify(debugFont, atLeast(1)).draw(any(SpriteBatch.class), anyString(), anyFloat(),
				anyFloat());
	}

	@Test
	public void testDebugDisabled() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Gdx.files = new HeadlessFiles();
		DebugUtils.initDebugSettings();
		assertFalse(DebugUtils.DRAW_DEBUG_COLLISIONS);
		assertFalse(DebugUtils.DRAW_DEBUG_TEXT);
		assertTrue(DebugUtils.ENEMY_COLLEGE_FIRE);

		// ensure the debug field in eng1game is false by default
		Field f = eng1game.class.getDeclaredField("debug");
		f.setAccessible(true);
		assertFalse(f.getBoolean(new eng1game()));
	}

}
