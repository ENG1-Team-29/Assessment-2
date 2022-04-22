package io.github.annabeths.GeneralControl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.GdxRuntimeException;

import io.github.annabeths.Collectables.PowerupType;

/**
 * Handles resource management of textures, audio, and fonts
 * 
 * @author James Burnell
 */
public class ResourceManager {

	public static AssetManager assets;

	public static BitmapFont font;
	public static BitmapFont debugFont;

	public static Texture nullTex;

	// Prevent instantiation
	private ResourceManager() {
	}

	/**
	 * Initialize resource manager with a specific {@link AssetManager}
	 * 
	 * @param assetMan the asset manager to use
	 */
	public static void init(AssetManager assetMan) {
		assets = assetMan;

		long time = DebugUtils.timeCodeMs(() -> {
			try {
				font = new BitmapFont(Gdx.files.internal("fonts/bobcat.fnt"), false);
				debugFont = new BitmapFont(Gdx.files.internal("fonts/cozette.fnt"), false);
				debugFont.getData().setScale(0.5f);
			} catch (GdxRuntimeException e) {
			}
		});

		Gdx.app.log("ResourceManager", String.format("Loaded fonts in %dms\\n", time));

		time = DebugUtils.timeCodeMs(() -> {
			nullTex = genNullTex();
			/* World Textures */
			loadWorldTexture("island.png");
			loadWorldTexture("grass.png");
			for (int i = 0; i < 3; i++) {
				loadWorldTexture("water/water" + (i + 1) + ".png");
			}
			for (int i = 0; i < 9; i++) {
				loadWorldTexture("castle/castle" + (i + 1) + ".png");
			}
			loadWorldTexture("castle/castle_dead.png");
			/* Entity Textures */
			loadEntityTexture("cannonball.png");
			loadEntityTexture("kraken_cannonball.png");
			loadEntityTexture("boat_neutral.png");
			loadEntityTexture("boat1.png");
			loadEntityTexture("boat2.png");
			loadEntityTexture("kraken1.png");
			loadEntityTexture("kraken2.png");
			loadEntityTexture("kraken3.png");
			loadEntityTexture("weather1.png");
			loadEntityTexture("weather2.png");
			loadEntityTexture("mine.png");
			/* UI Textures */
			loadUITexture("button.png");
			loadUITexture("background.png");
			loadUITexture("upgradebutton.png");
			loadUITexture("hud-ui.png");
			loadUITexture("instructions.png");
			/* Powerup Textures */
			for (PowerupType p : PowerupType.values()) {
				loadTexture(p.getTexture());
			}

			assets.finishLoading();
		});

		Gdx.app.log("ResourceManager", String.format("Loaded textures in %dms\n", time));
	}

	public static void loadUITexture(String location) {
		loadTexture("ui/" + location);
	}

	public static void loadWorldTexture(String location) {
		loadTexture("img/world/" + location);
	}

	public static void loadEntityTexture(String location) {
		loadTexture("img/entity/" + location);
	}

	/**
	 * Queues a texture from a file location to be loaded
	 * 
	 * @param location the location of the texture
	 */
	public static void loadTexture(String location) {
		if (Gdx.files.internal(location).exists()) {
			assets.load(location, Texture.class);
		} else {
			Gdx.app.error("texture loader", location + " does not exist");
		}
	}

	/**
	 * Get a pre-loaded texture from a location
	 * 
	 * @param location the location of the texture
	 * @return The Texture object, or {@link #nullTex} if the location is null or
	 *         file is missing
	 * 
	 */
	public static Texture getTexture(String location) {
		if (location == null || assets == null) return nullTex;
		if (assets.contains(location)) return assets.get(location);
		return nullTex;
	}

	/**
	 * Generates a 2x2 texture to be used when a texture is not loaded.
	 * 
	 * @return the null texture
	 */
	public static Texture genNullTex() {
		Pixmap p = new Pixmap(2, 2, Pixmap.Format.RGB888);
		p.setColor(Color.PURPLE);
		p.drawPixel(0, 0);
		p.drawPixel(1, 1);
		p.setColor(Color.BLACK);
		p.drawPixel(1, 0);
		p.drawPixel(0, 1);
		return new Texture(p);
	}

}
