package io.github.annabeths.UI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.Collectables.PowerupType;
import io.github.annabeths.GameGenerics.Upgrades;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.GeneralControl.ResourceManager;
import io.github.annabeths.GeneralControl.TestHelper;
import io.github.annabeths.GeneralControl.eng1game;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.ProjectileData;

public class HUDTest {

	public static GameController gc;
	public static HUD hud;

	@BeforeAll
	public static void init() {
		// setup Gdx
		TestHelper.setupEnv();

		// setup resources
		ResourceManager.nullTex = ResourceManager.genNullTex();
	}

	@BeforeEach
	public void setup() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException {
		// setup game controller
		gc = mock(GameController.class, withSettings().useConstructor(mock(eng1game.class))
				.defaultAnswer(CALLS_REAL_METHODS));
		gc.map = mock(GameMap.class);

		hud = mock(HUD.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
		hud.stage = mock(Stage.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
		hud.hudGroup = mock(Group.class);
		// set the game controller
		Field fgc = HUD.class.getDeclaredField("gc");
		fgc.setAccessible(true);
		fgc.set(hud, gc);
	}

	@Test
	public void testTimeStringFormatting() {
		HashMap<Integer, String> cases = new HashMap<>();
		cases.put(0, "00 seconds");
		cases.put(17, "17 seconds");
		cases.put(60, "1'00\"");
		cases.put(600, "10'00\"");
		cases.put(659, "10'59\"");

		for (HashMap.Entry<Integer, String> entry : cases.entrySet()) {
			Integer key = entry.getKey();
			String val = entry.getValue();

			assertEquals(val, HUD.generateTimeString(key));
		}
	}

	@Test
	public void testGetUpgradeText() {
		String value = HUD.getUpgradeText(Upgrades.defense, 15, 10, "Levels");
		assertTrue(value.contains(Upgrades.defense.label));
		assertTrue(value.contains("15"));
		assertTrue(value.contains("10 Levels"));

		value = HUD.getUpgradeText(Upgrades.projectiledamage, 0.3f, 10, "Levels");
		assertTrue(value.contains(Upgrades.projectiledamage.label));
		assertTrue(value.contains("30%"));
		assertTrue(value.contains("10 Levels"));
	}

	@Test
	public void testBarKnob() {
		Texture t = HUD.barKnob(Color.WHITE);
		assertEquals(35, t.getHeight());
		assertEquals(2, t.getWidth());
	}

	@Test
	public void testSetupStyles() {
		assertDoesNotThrow(() -> hud.setupStyles());
		assertNotNull(hud.lblStyleBlk);
		assertNotNull(hud.lblStyleWht);
		assertNotNull(hud.toolTipStyle);
		assertNotNull(hud.shopButtonStyle);
		assertNotNull(hud.upgradeButtonStyle);
	}

	@Test
	public void testSetupProgressBars() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		hud.setupProgressBars();
		assertNotNull(hud.healthBar);
		assertNotNull(hud.xpBar);
	}

	@Test
	public void testSetupShopMenu() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.setupShopMenu());
		assertNotNull(hud.upgradeMenuBackground);
		assertNotNull(hud.upgradeButton1);
		assertNotNull(hud.upgradeButton2);
		assertTrue(hud.upgradeMenuInitialised);
	}

	@Test
	public void testSetupShopButton() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.setupShopButton());
		assertNotNull(hud.shopButton);
	}

	@Test
	public void testUpdateShopMenu() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		hud.upgradeButton1 = mock(TextButton.class);
		hud.upgradeButton2 = mock(TextButton.class);
		assertDoesNotThrow(() -> hud.updateShopMenu());
		verify(hud.upgradeButton1, times(1)).setText(anyString());
		verify(hud.upgradeButton2, times(1)).setText(anyString());
	}

	@Test
	public void testUpdateShopMenuPlunder() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		hud.upgradeButton1 = mock(TextButton.class);
		hud.upgradeButton2 = mock(TextButton.class);
		hud.usePlunderShop = true;
		assertDoesNotThrow(() -> hud.updateShopMenu());
		verify(hud.upgradeButton1, times(1)).setText(anyString());
		verify(hud.upgradeButton2, times(1)).setText(anyString());
	}

	@Test
	public void testSetupPowerups() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.setupPowerups());
		assertNotNull(hud.powerupIcons);
		assertNotNull(hud.powerupQuanityLabels);
		assertNotNull(hud.powerupTimeLabels);
		assertEquals(PowerupType.values().length, hud.powerupIcons.size());
		assertEquals(PowerupType.values().length, hud.powerupQuanityLabels.size());
		assertEquals(PowerupType.values().length, hud.powerupTimeLabels.size());
	}

	@Test
	public void testResize() {
		hud.resize(1280, 720);
		verify(hud.stage.getViewport(), times(1)).update(anyInt(), anyInt(), anyBoolean());
	}

	@Test
	public void testSetupLabels() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.setupLabels());
		assertNotNull(hud.hpText);
		assertNotNull(hud.plunderText);
		assertNotNull(hud.timerText);
		assertNotNull(hud.xpText);
	}

	@Test
	public void testUpdate() {
		try {
			hud.setupStyles();
			hud.setupLabels();
			hud.setupProgressBars();
			hud.setupPowerups();
			hud.setupShopButton();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.Update(1f));
	}

	@Test
	public void testUpdatePowerup() {
		try {
			hud.setupStyles();
			hud.setupLabels();
			hud.setupProgressBars();
			hud.setupPowerups();
			hud.setupShopButton();
		} catch (Exception e) {
		}
		gc.playerBoat.activePowerups.put(PowerupType.DAMAGE, 1f);
		assertDoesNotThrow(() -> hud.Update(1f));
	}

	@Test
	public void testRandomizeUpgrades() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		// run the test enough times to overcome chance
		for (int i = 0; i < 1000; i++) {
			// values are not important, just that it does not error
			assertDoesNotThrow(() -> hud.RandomiseUpgrades());
		}
	}

	@Test
	public void testBuyUpgrade() {
		gc.playerBoat = mock(PlayerBoat.class);
		hud.BuyUpgrade(1);
		verify(gc.playerBoat, times(1)).Upgrade(any(), anyFloat());
		hud.BuyUpgrade(2);
		verify(gc.playerBoat, times(2)).Upgrade(any(), anyFloat());
	}

	@Test
	public void testDraw() {
		DebugUtils.DRAW_DEBUG_TEXT = false;
		assertDoesNotThrow(() -> hud.Draw(mock(SpriteBatch.class)));
		verify(hud.stage, times(1)).draw();
		verify(hud.stage, times(1)).act();

		DebugUtils.DRAW_DEBUG_TEXT = true;
		assertDoesNotThrow(() -> hud.Draw(mock(SpriteBatch.class)));
	}

	@Test
	public void testToggleMenuNoShop() {
		try {
			hud.setupStyles();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.ToggleMenu());
	}

	@Test
	public void testToggleMenu() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		assertDoesNotThrow(() -> hud.ToggleMenu());
	}

	@Test
	public void testToggleMenuOpen() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		hud.upgradeMenuOpen = true;
		assertDoesNotThrow(() -> hud.ToggleMenu());
	}

	@Test
	public void testPressUpgrade1() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
			gc.setXp(100000);
		} catch (Exception e) {
		}
		ClickListener cl1 = (ClickListener) hud.upgradeButton1.getListeners().get(1);
		assertDoesNotThrow(() -> cl1.touchDown(null, 0, 0, 0, 0));
		verify(hud, times(1)).BuyUpgrade(eq(1));
		verify(hud, times(2)).RandomiseUpgrades(); // one for setup, one for call
	}

	@Test
	public void testPressUpgrade1Plunder() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
			gc.setPlunder(100000);
		} catch (Exception e) {
		}
		hud.usePlunderShop = true;
		ClickListener cl1 = (ClickListener) hud.upgradeButton1.getListeners().get(1);
		assertDoesNotThrow(() -> cl1.touchDown(null, 0, 0, 0, 0));
		assertEquals(ProjectileData.RAY, gc.playerBoat.activeProjectileType);
		verify(hud, times(2)).updateShopMenu(); // one for setup, one for call
	}

	@Test
	public void testPressUpgrade1NoPlunder() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
			gc.setPlunder(0);
		} catch (Exception e) {
		}
		hud.usePlunderShop = true;
		ClickListener cl1 = (ClickListener) hud.upgradeButton1.getListeners().get(1);
		assertDoesNotThrow(() -> cl1.touchDown(null, 0, 0, 0, 0));
		assertNotEquals(ProjectileData.RAY, gc.playerBoat.activeProjectileType);
		verify(hud, times(1)).updateShopMenu(); // one for setup, none for call
	}

	@Test
	public void testPressUpgrade2() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
			gc.setXp(100000);
		} catch (Exception e) {
		}
		ClickListener cl2 = (ClickListener) hud.upgradeButton2.getListeners().get(1);
		assertDoesNotThrow(() -> cl2.touchDown(null, 0, 0, 0, 0));
		verify(hud, times(1)).BuyUpgrade(eq(2));
		verify(hud, times(2)).RandomiseUpgrades(); // one for setup, one for call
	}

	@Test
	public void testPressUpgrade2Plunder() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
			gc.setPlunder(100000);
		} catch (Exception e) {
		}
		hud.usePlunderShop = true;
		ClickListener cl2 = (ClickListener) hud.upgradeButton2.getListeners().get(1);
		assertDoesNotThrow(() -> cl2.touchDown(null, 0, 0, 0, 0));
		verify(hud, times(1)).BuyUpgrade(eq(2));
		verify(hud, times(2)).RandomiseUpgrades(); // one for setup, one for call
	}

	@Test
	public void testEnterExit1() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		ClickListener cl1 = (ClickListener) hud.upgradeButton1.getListeners().get(1);
		cl1.enter(null, 0, 0, -1, null);
		assertTrue(hud.hoveringOverButton);
		cl1.exit(null, 0, 0, -1, null);
		assertFalse(hud.hoveringOverButton);
	}

	@Test
	public void testEnterExit2() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		ClickListener cl2 = (ClickListener) hud.upgradeButton2.getListeners().get(1);
		cl2.enter(null, 0, 0, -1, null);
		assertTrue(hud.hoveringOverButton);
		cl2.exit(null, 0, 0, -1, null);
		assertFalse(hud.hoveringOverButton);
	}

	@Test
	public void testEnterExitMenu() {
		try {
			hud.setupStyles();
			hud.setupShopMenu();
		} catch (Exception e) {
		}
		ClickListener cl2 = (ClickListener) hud.upgradeMenuBackground.getListeners().get(0);
		cl2.enter(null, 0, 0, -1, null);
		assertTrue(hud.hoveringOverButton);
		cl2.exit(null, 0, 0, -1, null);
		assertFalse(hud.hoveringOverButton);
	}

	@Test
	public void testShopButton() {
		try {
			hud.setupStyles();
			hud.setupShopButton();
		} catch (Exception e) {
		}
		ClickListener cl = (ClickListener) hud.shopButton.getListeners().get(2);

		cl.enter(null, 0, 0, -1, null);
		assertTrue(hud.hoveringOverButton);
		cl.exit(null, 0, 0, -1, null);
		assertFalse(hud.hoveringOverButton);

		boolean flag = hud.upgradeMenuOpen;
		cl.clicked(null, 0, 0);
		assertFalse(hud.upgradeMenuOpen == flag);
		cl.clicked(null, 0, 0);
		assertTrue(hud.upgradeMenuOpen == flag);
	}

//	@Test
//	public void testDraw() {
//		assertDoesNotThrow(() -> hud.Draw(sb));
//	}

}
