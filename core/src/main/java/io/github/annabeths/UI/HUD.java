package io.github.annabeths.UI;

import static io.github.annabeths.GeneralControl.ResourceManager.font;
import static io.github.annabeths.GeneralControl.ResourceManager.getTexture;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;

import io.github.annabeths.Collectables.PowerupType;
import io.github.annabeths.GameGenerics.GameObject;
import io.github.annabeths.GameGenerics.Upgrades;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.Projectiles.ProjectileData;

public class HUD extends GameObject {

	Stage stage;

	/* Shop objects */

	TextButton shopButton;
	TextButtonStyle shopButtonStyle;

	TextButtonStyle upgradeButtonStyle;
	TextButton upgradeButton1;
	TextButton upgradeButton2;

	Image upgradeMenuBackground;

	/* HUD objects */

	TextTooltipStyle toolTipStyle;

	/** The image that serves as a base for the HUD */
	Image hudBg;

	Group hudGroup;

	LabelStyle lblStyleBlk, lblStyleWht;
	Label hpText;
	Label timerText;
	Label xpText;
	Label plunderText;

	Map<PowerupType, Image> powerupIcons;
	Map<PowerupType, Label> powerupQuanityLabels;
	Map<PowerupType, Label> powerupTimeLabels;

	ProgressBar healthBar;
	ProgressBar xpBar;

	private GameController gc;

	/** Used to disable certain player behaviors when hovering over a button */
	public boolean hoveringOverButton = false;
	boolean upgradeMenuOpen = false;
	boolean upgradeMenuInitialised = false; // Set to true once initialized
	public boolean usePlunderShop = true;

	Upgrades upgrade1;
	int upgrade1cost;
	float upgrade1amount;
	Upgrades upgrade2;
	int upgrade2cost;
	float upgrade2amount;

	public HUD(GameController gameController) {
		gc = gameController;

		setupStyles();

		stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		Gdx.input.setInputProcessor(stage);

		// Background image
		hudBg = new Image(getTexture("ui/hud-ui.png"));
		hudBg.setScaling(Scaling.fit);

		// HUD group object
		hudGroup = new Group();
		hudGroup.addActor(hudBg);

		setupProgressBars(); // health and xp bars
		setupPowerups(); // powerup display
		setupShopButton(); // button to open shop
		setupLabels(); // hud text

		hudGroup.setWidth(stage.getViewport().getScreenWidth());
		hudGroup.setScale(stage.getViewport().getScreenWidth() / 1920f);
		stage.addActor(hudGroup);
	}

	public void setupLabels() {
		hpText = new Label("#", lblStyleWht);
		timerText = new Label("#", lblStyleBlk);
		xpText = new Label("#", lblStyleWht);
		plunderText = new Label("#", lblStyleBlk);

		hpText.setPosition(820, 88);
		hpText.setFontScale(0.9f);
		timerText.setPosition(1920 - 158, 172);
		timerText.setFontScale(1.5f);
		plunderText.setPosition(1920 - 180, 112);
		plunderText.setFontScale(1.5f);
		xpText.setPosition(820, 18);
		xpText.setFontScale(0.9f);

		hudGroup.addActor(hpText);
		hudGroup.addActor(timerText);
		hudGroup.addActor(xpText);
		hudGroup.addActor(plunderText);
	}

	public void setupStyles() {
		Drawable buttonBg = new TextureRegionDrawable(getTexture("ui/button.png"));
		lblStyleBlk = new LabelStyle(font, Color.BLACK);
		lblStyleWht = new LabelStyle(font, Color.WHITE);
		toolTipStyle = new TextTooltipStyle(lblStyleBlk, buttonBg);

		shopButtonStyle = new TextButtonStyle();
		shopButtonStyle.font = font;
		shopButtonStyle.fontColor = Color.BLACK;
		shopButtonStyle.up = buttonBg;

		upgradeButtonStyle = new TextButtonStyle();
		upgradeButtonStyle.font = font;
		upgradeButtonStyle.fontColor = Color.BLACK;
		upgradeButtonStyle.up = new TextureRegionDrawable(getTexture("ui/upgradebutton.png"));
	}

	/** Set up the powerup section of the HUD */
	public void setupPowerups() {
		List<PowerupType> powerups = Arrays.asList(PowerupType.values());
		// initialize icon images
		powerupIcons = powerups.stream().collect(
				Collectors.toMap(Function.identity(), p -> new Image(getTexture(p.getTexture()))));
		powerupQuanityLabels = powerups.stream()
				.collect(Collectors.toMap(Function.identity(), p -> new Label("", lblStyleBlk)));
		powerupTimeLabels = powerups.stream()
				.collect(Collectors.toMap(Function.identity(), p -> new Label("", lblStyleWht)));

		int startX = 50;
		int startY = 190;
		int gap = 68;

		// place the powerupIcons on the HUD
		powerupIcons.values().forEach(p -> p.setSize(50, 50));
		// adjust font size for quantity labels
		powerupQuanityLabels.values().forEach(p -> p.setFontScale(1.5f));
		int i = 0;
		for (int y = 0; y < 3; y++) {
			PowerupType pt = powerups.get(i++);
			powerupIcons.get(pt).setPosition(startX, startY - gap * y, Align.center);
			powerupQuanityLabels.get(pt).setPosition(startX + 30, startY - gap * y - 10,
					Align.bottomLeft);
			powerupTimeLabels.get(pt).setBounds(startX - 25, startY - 25 - gap * y, 50, 50);
		}
		for (int y = 0; y < 2; y++) {
			PowerupType pt = powerups.get(i++);
			powerupIcons.get(pt).setPosition(startX + 125, startY - gap * (y + 1), Align.center);
			powerupQuanityLabels.get(pt).setPosition(startX + 30 + 125, startY - gap * (y + 1) - 10,
					Align.bottomLeft);
			powerupTimeLabels.get(pt).setBounds(startX - 25 + 125, startY - 25 - gap * (y + 1), 50,
					50);
		}
		powerupTimeLabels.values().forEach(l -> l.setAlignment(Align.center));

		// create tool tips
		for (i = 0; i < powerupIcons.size(); i++) {
			PowerupType p = PowerupType.values()[i];
			TextTooltip tip = new TextTooltip(
					String.format(" %s\n Press %d to activate ", p.getName(), i + 1), toolTipStyle);
			tip.setInstant(true);
			powerupIcons.get(p).addListener(tip);
		}

		// add icons to HUD
		powerupIcons.values().forEach(p -> hudGroup.addActor(p));
		// add labels to HUD
		powerupQuanityLabels.values().forEach(l -> hudGroup.addActor(l));
	}

	/** Set up the display bars such as health and XP */
	public void setupProgressBars() {
		ProgressBarStyle hStyle = new ProgressBarStyle();
		hStyle.knobBefore = new TextureRegionDrawable(barKnob(Color.WHITE));
		hStyle.knobAfter = new TextureRegionDrawable(barKnob(Color.BLACK));
		healthBar = new ProgressBar(0, gc.playerBoat.getMaxHealth(), 1, false, hStyle);
		healthBar.setBounds(812, 92, 377, 35);
		healthBar.setAnimateDuration(0.5f);

		TextTooltip tip = new TextTooltip(" How much health you have ", toolTipStyle);
		tip.setInstant(true);
		healthBar.addListener(tip);

		ProgressBarStyle xpStyle = new ProgressBarStyle();
		xpStyle.knobBefore = new TextureRegionDrawable(barKnob(Color.FOREST));
		xpStyle.knobAfter = new TextureRegionDrawable(barKnob(Color.BLACK));
		xpBar = new ProgressBar(0, gc.getXpRequiredForNextLevel(), 0.5f, false, xpStyle);
		xpBar.setBounds(812, 24, 377, 35);
		xpBar.setAnimateDuration(0.25f);

		tip = new TextTooltip(" The total experience you have ", toolTipStyle);
		tip.setInstant(true);
		xpBar.addListener(tip);

		hudGroup.addActor(healthBar);
		hudGroup.addActor(xpBar);
	}

	/**
	 * Generates a 2x35 texture to be used by progress bars.
	 * 
	 * @param col the color of the knob
	 * @return the knob texture
	 * @see ProgressBarStyle
	 */
	public static Texture barKnob(Color col) {
		Pixmap p = new Pixmap(2, 35, Pixmap.Format.RGB888);
		p.setColor(col);
		p.fill();
		return new Texture(p);
	}

	@Override
	public void Update(float delta) {
		/* Update label text */
		hpText.setText(String.format("%.0f/%.0f", gc.playerBoat.getHealth(),
				gc.playerBoat.getMaxHealth()));
		xpText.setText(String.format("Level %d + %.0fxp", gc.getXpLevel(), gc.getXpInLevel()));
		timerText.setText(generateTimeString((int) gc.timer));
		plunderText.setText("$" + Integer.toString(gc.getPlunder()));

		powerupIcons.forEach((p, i) -> {
			boolean f = gc.playerBoat.activePowerups.containsKey(p);
			i.setColor(f ? Color.DARK_GRAY : Color.WHITE);
		});
		powerupQuanityLabels.forEach((p, l) -> {
			l.setText("x" + gc.playerBoat.collectedPowerups.getOrDefault(p, 0));
		});
		powerupTimeLabels.forEach((p, l) -> {
			float t = gc.playerBoat.activePowerups.getOrDefault(p, 0f);
			if (t > 0) {// add label if powerup is active
				l.setText((int) t + "s");
				if (!l.hasParent()) hudGroup.addActor(l);
			} else {// remove powerup if powerup is inactive
				l.remove();
			}
		});

		/* Update progress bars */
		healthBar.setRange(0, gc.playerBoat.getMaxHealth());
		healthBar.setValue(gc.playerBoat.getHealth());
		healthBar.setColor(gc.playerBoat.isInvincible() ? Color.ROYAL : Color.SCARLET);
		xpBar.setRange(0, gc.getXpRequiredForNextLevel());
		xpBar.setValue(gc.getXpInLevel());

		usePlunderShop = gc.isPlayerInRangeOfFriendlyCollege();

		// if gc.playerboat distance from friendly college less than x
		shopButton.setColor(usePlunderShop ? Color.GOLD : Color.WHITE);
	}

	/**
	 * Converts seconds into a human readable string
	 * 
	 * @param seconds the number of seconds
	 * @return The time string
	 */
	public static String generateTimeString(int seconds) {
		int sec = seconds % 60;
		int min = seconds / 60;
		return min > 0 ? String.format("%d\'%02d\"", min, sec) : String.format("%02d seconds", sec);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		stage.act();
		stage.draw();

		Camera camera = stage.getViewport().getCamera();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (DebugUtils.DRAW_DEBUG_TEXT) DebugUtils.drawDebugText(gc, batch);
		batch.end();
	}

	/** Set up the shop button on the HUD */
	public void setupShopButton() {
		shopButton = new TextButton("Shop", shopButtonStyle);

		TextTooltip tip = new TextTooltip(" Buy upgrades with plunder and experience! ",
				toolTipStyle);
		tip.setInstant(true);
		shopButton.addListener(tip);

		shopButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				upgradeMenuOpen = !upgradeMenuOpen;
				ToggleMenu();
			}

			// Giving them enter and exit functions so that the player can't fire with left
			// click while hovering over a button.
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = true;
					// System.out.println("Hovering over");
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = false;
				}
			}
		});

		// shopButton.setPosition(1655, 16);
		shopButton.setBounds(1658, 15, 250, 75);

		hudGroup.addActor(shopButton);
	}

	public void ToggleMenu() {
		// Put the XP menu drawing calls in its own function so that render doesn't get
		// too cluttered

		// Initialize the menu if it hasn't been, this avoids repeatedly creating new
		// buttons.
		if (!upgradeMenuInitialised) setupShopMenu();

		// Add/re-add the UI elements back to the stage
		if (upgradeMenuOpen) {
			updateShopMenu();
			stage.addActor(upgradeMenuBackground);
			stage.addActor(upgradeButton1);
			stage.addActor(upgradeButton2);
		} else {
			// Remove all menu elements from the stage
			upgradeMenuBackground.remove();
			upgradeButton1.remove();
			upgradeButton2.remove();
		}
	}

	/**
	 * Creates the menu for the first time, and also generates the first set of
	 * upgrades.
	 */
	public void setupShopMenu() {
		// Create the background
		upgradeMenuBackground = new Image(getTexture("ui/background.png"));
		upgradeMenuBackground.setPosition(
				Gdx.graphics.getWidth() / 2 - upgradeMenuBackground.getWidth() / 2,
				Gdx.graphics.getHeight() / 2 - upgradeMenuBackground.getHeight() / 2);

		upgradeMenuBackground.addListener(new ClickListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = true;
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = false;
				}
			}
		});

		// Create the upgrade buttons
		upgradeButton1 = new TextButton("", upgradeButtonStyle);
		upgradeButton2 = new TextButton("", upgradeButtonStyle);

		upgradeButton1.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (usePlunderShop) {
					if (gc.getPlunder() >= 500
							&& gc.playerBoat.activeProjectileType != ProjectileData.RAY) {
						gc.subtractPlunder(500);
						gc.playerBoat.activeProjectileType = ProjectileData.RAY;
						updateShopMenu();
					}
				} else {
					if (gc.getXpLevel() >= upgrade1cost) {
						gc.subtractXpLevels(upgrade1cost);
						BuyUpgrade(1);
						RandomiseUpgrades();
					}
				}

				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = true;
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = false;
				}
			}
		});

		upgradeButton2.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (usePlunderShop) {
					if (gc.getPlunder() > upgrade2cost * 10) {
						gc.subtractPlunder(upgrade2cost * 10);
						BuyUpgrade(2);
						RandomiseUpgrades();
					}
				} else {
					if (gc.getXpLevel() >= upgrade2cost) {
						gc.subtractXpLevels(upgrade2cost);
						BuyUpgrade(2);
						RandomiseUpgrades();
					}
				}

				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = true;
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					hoveringOverButton = false;
				}
			}
		});

		upgradeMenuInitialised = true;

		RandomiseUpgrades();

		stage.addActor(upgradeMenuBackground);
		stage.addActor(upgradeButton1);
		stage.addActor(upgradeButton2);
	}

	/**
	 * Generates a string to present upgrade purchase information to the user.
	 * 
	 * @param upgrade the type of upgrade to buy
	 * @param amount how much the upgrade will increase
	 * @param cost how much the upgrade will cost
	 * @param currency what the user will pay in
	 * @return The upgrade purchase information
	 */
	public static String getUpgradeText(Upgrades upgrade, float amount, int cost, String currency) {
		String u1a = (upgrade == Upgrades.projectiledamage || upgrade == Upgrades.projectilespeed)
				? ((int) (amount * 100) + "%")
				: (int) amount + "";
		return String.format("Upgrade:\n%s + %s\nCost:\n%d %s", upgrade.label, u1a, cost, currency);
	}

	public void updateShopMenu() {
		if (usePlunderShop) {
			// plunder upgrades
			upgradeButton1.setText(
					gc.playerBoat.activeProjectileType == ProjectileData.RAY ? "Already bought"
							: "Upgrade:\nRay bullets\nCost:\n500 plunder");
			upgradeButton2.setText(
					getUpgradeText(upgrade2, upgrade2amount, upgrade2cost * 10, "plunder"));
		} else {
			upgradeButton1
					.setText(getUpgradeText(upgrade1, upgrade1amount, upgrade1cost, "Levels"));
			upgradeButton2
					.setText(getUpgradeText(upgrade2, upgrade2amount, upgrade2cost, "Levels"));
		}

		upgradeButton1.setPosition(
				Gdx.graphics.getWidth() / 2 - upgradeMenuBackground.getWidth() / 2 + 15,
				Gdx.graphics.getHeight() / 2 + upgradeMenuBackground.getHeight() / 2
						- upgradeButton1.getHeight() - 15);
		upgradeButton2.setPosition(Gdx.graphics.getWidth() / 2 + 35, Gdx.graphics.getHeight() / 2
				+ upgradeMenuBackground.getHeight() / 2 - upgradeButton2.getHeight() - 15);
	}

	void BuyUpgrade(int upgrade) {
		switch (upgrade) {
		case 1:
			gc.playerBoat.Upgrade(upgrade1, upgrade1amount);
			break;
		case 2:
			gc.playerBoat.Upgrade(upgrade2, upgrade2amount);
			break;
		}
	}

	void RandomiseUpgrades() {
		switch (MathUtils.random(5)) {
		case 0: // Max Health
			upgrade1 = Upgrades.maxhealth;
			upgrade1amount = 10;
			upgrade1cost = 5;
			break;
		case 1: // Speed
			upgrade1 = Upgrades.speed;
			upgrade1amount = 6.25f;
			upgrade1cost = 5;
			break;
		case 2: // Turn Speed
			upgrade1 = Upgrades.turnspeed;
			upgrade1amount = 7.5f;
			upgrade1cost = 5;
			break;
		case 3: // Damage
			upgrade1 = Upgrades.projectiledamage;
			upgrade1amount = 0.1f;
			upgrade1cost = 5;
			break;
		case 4: // Speed
			upgrade1 = Upgrades.projectilespeed;
			upgrade1amount = 0.05f;
			upgrade1cost = 5;
			break;
		case 5: // Defense
			upgrade1 = Upgrades.defense;
			upgrade1amount = 1f;
			upgrade1cost = 6;
			break;
		}

		switch (MathUtils.random(5)) {
		case 0: // Max Health
			upgrade2 = Upgrades.maxhealth;
			upgrade2amount = 10;
			upgrade2cost = 5;
			break;
		case 1: // Speed
			upgrade2 = Upgrades.speed;
			upgrade2amount = 6.25f;
			upgrade2cost = 5;
			break;
		case 2: // Turn Speed
			upgrade2 = Upgrades.turnspeed;
			upgrade2amount = 7.5f;
			upgrade2cost = 5;
			break;
		case 3: // Damage
			upgrade2 = Upgrades.projectiledamage;
			upgrade2amount = 0.1f;
			upgrade2cost = 5;
			break;
		case 4: // Speed
			upgrade2 = Upgrades.projectilespeed;
			upgrade2amount = 0.05f;
			upgrade2cost = 5;
			break;
		case 5: // Defense
			upgrade2 = Upgrades.defense;
			upgrade2amount = 1f;
			upgrade2cost = 6;
			break;
		}

		updateShopMenu();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
