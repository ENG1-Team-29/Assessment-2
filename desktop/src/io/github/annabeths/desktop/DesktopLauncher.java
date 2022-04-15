package io.github.annabeths.desktop;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.GeneralControl.eng1game;

public class DesktopLauncher {

	public static void main(String[] arg) {
		List<String> argList = Arrays.asList(arg);

		boolean debug = argList.contains("--debug") || argList.contains("-d");

		if (debug) {
			DebugUtils.DRAW_DEBUG_COLLISIONS = true;
			DebugUtils.DRAW_DEBUG_TEXT = true;
			DebugUtils.ENEMY_COLLEGE_FIRE = false;
		}

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setWindowedMode(1280, 720);
		config.setTitle("Team Mario's Pirate Game");
		new Lwjgl3Application(new eng1game(debug), config);
	}
}
