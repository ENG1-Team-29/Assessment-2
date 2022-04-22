package io.github.annabeths.GeneralControl;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TestHelper {

	public static void setupEnv() {
		Gdx.graphics = mock(Graphics.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		HeadlessNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Gdx.app = mock(Application.class);
		Gdx.gl20 = mock(GL20.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		Gdx.gl = Gdx.gl20;
		Gdx.audio = mock(Audio.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		Gdx.input = mock(Input.class);

		ResourceManager.nullTex = ResourceManager.genNullTex();

		initFonts();
	}

	public static void initFonts() {
		ResourceManager.font = mock(BitmapFont.class,
				withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
		ResourceManager.debugFont = mock(BitmapFont.class,
				withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
	}

}
