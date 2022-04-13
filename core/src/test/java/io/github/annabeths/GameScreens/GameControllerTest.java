package io.github.annabeths.GameScreens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.EnemyBoat;
import io.github.annabeths.Boats.NeutralBoat;
import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.Collectables.Powerup;
import io.github.annabeths.Collectables.PowerupType;
import io.github.annabeths.Colleges.EnemyCollege;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.ProjectileData;
import io.github.annabeths.Projectiles.ProjectileRay;
import io.github.annabeths.UI.HUD;

public class GameControllerTest {

	GameController gc;
	eng1game game;
	SpriteBatch batch;
	ShapeRenderer sr;

	@BeforeAll
	public static void init() {
		Gdx.gl = mock(GL20.class);
		Gdx.graphics = mock(Graphics.class);
		TestHelper.initFonts();
	}

	@BeforeEach
	public void setup() {
		game = mock(eng1game.class);
		gc = mock(GameController.class,
				withSettings().useConstructor(game).defaultAnswer(CALLS_REAL_METHODS));
		gc.map = mock(GameMap.class);
		gc.hud = mock(HUD.class);
		gc.sr = sr = mock(ShapeRenderer.class);
		gc.batch = batch = mock(SpriteBatch.class);
		gc.sr = mock(ShapeRenderer.class);
		Gdx.input = mock(Input.class);
	}

	@Test
	public void testTimerRange() {
		assertTrue(gc.timer >= 5 * 60);
		assertTrue(gc.timer <= 10 * 60);
	}

	@Test
	public void testLogicTimer() {
		float time = gc.timer;
		gc.logic(1);
		assertEquals(time - 1, gc.timer);

		gc.timer = 0;
		gc.logic(1);
		verify(gc, times(1)).gameOver();
	}

	@Test
	public void testGameOver() {
		gc.gameOver();
		assertFalse(game.timeUp);
		verify(game, times(1)).gotoScreen(Screens.gameOverScreen);
	}

	@Test
	public void testGameOverTimeUp() {
		gc.timer = -1;
		gc.gameOver();
		assertTrue(game.timeUp);
		verify(game, times(1)).gotoScreen(Screens.gameOverScreen);
	}

	@Test
	public void testLogicBossDefeated() {
		gc.bossCollege.kill();
		gc.logic(1);
		verify(game, times(1)).gotoScreen(Screens.gameWinScreen);
	}

	@Test
	public void testLogicCallsUpdates() {
		gc.logic(1);
		verify(gc, times(1)).UpdateObjects(anyFloat());
		verify(gc, times(1)).ClearKilledObjects();
		verify(gc.hud, times(1)).Update(anyFloat());
		verify(gc.map, times(1)).Update(anyFloat());
	}

	@Test
	public void testLogicIncreaseXpPlunder() {
		float xp = gc.xp;
		float plunder = gc.plunder;
		gc.logic(1);
		assertTrue(gc.xp > xp);
		assertTrue(gc.plunder > plunder);
	}

	@Test
	public void testLogicNotIncreaseXpPlunderUnderTime() {
		float xp = gc.xp;
		float plunder = gc.plunder;
		gc.logic(0);
		assertEquals(xp, gc.xp);
		assertEquals(plunder, gc.plunder);
	}

	@Test
	public void testRender() {
		assertDoesNotThrow(() -> gc.render(1));
		verify(batch, atLeast(1)).begin();

		// Assert that endings are balanced with beginnings
		long beginCount = mockingDetails(batch).getInvocations().stream()
				.filter(i -> i.getMethod().getName().equals("begin")).count();
		long endCount = mockingDetails(batch).getInvocations().stream()
				.filter(i -> i.getMethod().getName().equals("begin")).count();
		assertEquals(beginCount, endCount);
	}

	@Test
	public void testRenderDebug() {
		DebugUtils.DRAW_DEBUG_COLLISIONS = true;
		DebugUtils.DRAW_DEBUG_TEXT = true;
		assertDoesNotThrow(() -> gc.render(1));
		verify(batch, atLeast(1)).begin();

		// Assert that endings are balanced with beginnings
		long beginCount = mockingDetails(batch).getInvocations().stream()
				.filter(i -> i.getMethod().getName().equals("begin")).count();
		long endCount = mockingDetails(batch).getInvocations().stream()
				.filter(i -> i.getMethod().getName().equals("begin")).count();
		assertEquals(beginCount, endCount);
	}

	@Test
	public void testRenderRay() {
		ProjectileRay ray = mock(ProjectileRay.class,
				withSettings().useConstructor(new Vector2(0, 0), 0f, ProjectileData.RAY, false)
						.defaultAnswer(CALLS_REAL_METHODS));
		gc.rays.add(ray);

		assertDoesNotThrow(() -> gc.renderRays());

		verify(gc.sr, times(1)).begin(any(ShapeType.class));
		verify(gc.sr, atLeast(1)).setColor(any());
		verify(gc.sr, atLeast(1)).setColor(anyFloat(), anyFloat(), anyFloat(), anyFloat());
		verify(gc.sr, atLeast(1)).rectLine(any(), any(), anyFloat());
		verify(gc.sr, times(1)).end();
	}

	@Test
	public void testIsEnemyCollegeNearPlayer() {
		assertFalse(gc.isEnemyCollegeNearPlayer());

		gc.playerBoat.setCenter(new Vector2(GameMap.getMapWidth() / 2, GameMap.getMapHeight() / 2));

		assertTrue(gc.isEnemyCollegeNearPlayer());
	}

	@Test
	public void testIsEnemyBoatNearPlayer() {
		assertFalse(gc.isEnemyBoatNearPlayer());

		PhysicsObject boat = gc.physicsObjects.stream().filter(p -> p instanceof EnemyBoat)
				.findFirst().get();
		gc.playerBoat.setCenter(boat.getCenter());

		assertTrue(gc.isEnemyBoatNearPlayer());
	}

	@Test
	public void testIsPlayerInDanger() {
		// ensure invincibility powerup doesn't spawn on player
		gc.physicsObjects.removeIf(p -> p instanceof Powerup);

		assertFalse(gc.isPlayerInDanger());

		gc.playerBoat.setCenter(new Vector2(GameMap.getMapWidth() / 2, GameMap.getMapHeight() / 2));
		assertTrue(gc.isPlayerInDanger());

		PhysicsObject boat = gc.physicsObjects.stream().filter(p -> p instanceof EnemyBoat)
				.findFirst().get();
		gc.playerBoat.setCenter(boat.getCenter());
		assertTrue(gc.isPlayerInDanger());

		gc.playerBoat.receivePower(PowerupType.INVINCIBILITY);
		assertFalse(gc.isPlayerInDanger());

		gc.playerBoat.setCenter(new Vector2(GameMap.getMapWidth() / 2, GameMap.getMapHeight() / 2));
		assertFalse(gc.isPlayerInDanger());
	}

	@Test
	public void testUpdateObjects() {
		assertDoesNotThrow(() -> gc.UpdateObjects(1));

		ProjectileRay ray = mock(ProjectileRay.class);
		gc.rays.add(ray);
		gc.UpdateObjects(1);
		verify(ray, times(1)).Update(anyFloat());

		doReturn(false).when(gc).isPlayerInDanger();
		gc.UpdateObjects(1);
		float xpMul = gc.xpTickMultiplier;

		doReturn(true).when(gc).isPlayerInDanger();
		gc.UpdateObjects(1);
		assertNotEquals(xpMul, gc.xpTickMultiplier);
	}

	@Test
	public void testUpdateObjectsCollision() {
		NeutralBoat nb = mock(NeutralBoat.class, withSettings()
				.useConstructor(gc, gc.playerBoat.position).defaultAnswer(CALLS_REAL_METHODS));
		gc.physicsObjects.add(nb);

		gc.UpdateObjects(1);
		verify(nb, times(1)).OnCollision(any(PlayerBoat.class));
	}

	@Test
	public void testAddXP() {
		gc.AddXP(10);
		assertEquals(10, gc.plunder);
		assertEquals(10, gc.xp);
	}

	@Test
	public void testCollegeDestroyed() {
		gc.bossCollege = mock(EnemyCollege.class);
		EnemyCollege col = mock(EnemyCollege.class, withSettings().useConstructor(new Vector2(0, 0),
				"", "", gc, ProjectileData.BOSS, 100));
		gc.CollegeDestroyed(col);
		verify(gc.bossCollege, never()).becomeVulnerable();

		// kill all colleges
		gc.physicsObjects.stream()
				.filter(p -> p instanceof EnemyCollege && !p.equals(gc.bossCollege)).forEach(p -> {
					EnemyCollege ep = (EnemyCollege) p;
					ep.damage(ep.getHealth());
				});

		// test that the boss college becomes vulnerable
		gc.CollegeDestroyed(col);
		verify(gc.bossCollege, times(1)).becomeVulnerable();
	}

	@Test
	public void testClearKilledObjects() {
		ProjectileRay ray = mock(ProjectileRay.class);
		when(ray.removeOnNextTick()).thenReturn(true);
		gc.rays.add(ray);
		gc.rays.add(mock(ProjectileRay.class));
		gc.ClearKilledObjects();
		assertEquals(1, gc.rays.size());
	}

}
