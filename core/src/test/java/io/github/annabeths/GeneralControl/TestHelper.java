package io.github.annabeths.GeneralControl;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

public class TestHelper {

	public static void initFonts() {
		ResourceManager.font = mock(BitmapFont.class, withSettings().defaultAnswer(RETURNS_MOCKS));

		try {
			Field fData = BitmapFont.class.getDeclaredField("data");
			fData.setAccessible(true);
			fData.set(ResourceManager.font, mock(BitmapFontData.class));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}

		ResourceManager.debugFont = mock(BitmapFont.class);
	}

}
