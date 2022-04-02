package io.github.annabeths.Boats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Colleges.College;
import io.github.annabeths.Colleges.EnemyCollege;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Level.GameMap;

public class AIBoatTest {

	public GameController gc;
	public AIBoat b;

	@BeforeEach
	public void setup() {
		gc = mock(GameController.class);
		gc.map = mock(GameMap.class);
		gc.colleges = new ArrayList<College>();
		initColleges();
		gc.playerBoat = new PlayerBoat(gc, new Vector2(0, 0));
		when(gc.map.getMapHeight()).thenReturn(1000f);
		when(gc.map.getMapWidth()).thenReturn(1000f);
		doCallRealMethod().when(gc.map).getRandomPointInBounds();

		// Create new generic boat
		b = newBoat();
	}

	private void initColleges() {
		Vector2 pos = new Vector2(10, 10);
		EnemyCollege c = mock(EnemyCollege.class);

		doCallRealMethod().when(c).getLocalCenterX();
		doCallRealMethod().when(c).getLocalCenterY();
		doCallRealMethod().when(c).getX();
		doCallRealMethod().when(c).getY();
		doCallRealMethod().when(c).setCenter(any(Vector2.class));

		c.sprite = new Sprite();
		Polygon collisionPolygon = new Polygon(new float[] { 0, 0, 100, 0, 100, 100, 0, 100 });
		collisionPolygon.setPosition(pos.x, pos.y);
		c.setCenter(pos);
		c.collisionPolygon = collisionPolygon;
		gc.colleges.add(c);
	}

	private AIBoat newBoat() {
		AIBoat result = new AIBoat(gc, new Vector2(0, 0), "") {
			@Override
			void Shoot() {
			}

			@Override
			void Destroy() {
			}

			@Override
			public void OnCollision(PhysicsObject other) {
			}
		};
		result.maxHP = 100;
		result.HP = result.maxHP;
		result.turnSpeed = 1;
		result.speed = 1;
		return result;
	}

	@Test
	public void testGetAngleToDestNull() {
		b.destination = null;
		assertEquals(-1, b.getAngleToDest());
	}

	@Test
	public void testGetAngleToDest() {
		b.setCenter(new Vector2(0, 0));
		b.destination = new Vector2(100, 100);
		assertEquals(45, b.getAngleToDest());

		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				b.destination = new Vector2(x, y);
				float angle = (float) Math.atan2(b.destination.y, b.destination.x)
						* MathUtils.radiansToDegrees;
				assertEquals(angle, b.getAngleToDest());
			}
		}
	}

	@Test
	public void testSetDestination() {
		Vector2 prev = b.position;
		b.SetDestination(new Vector2(1337, 100));
		assertEquals(new Vector2(1337, 100), b.GetDestination());
		assertEquals(prev, b.initialPosition);
	}

	@Test
	public void testMoveToDestinationInBound() {
		// point is in bounds
		when(gc.map.isPointInBounds(any(Vector2.class))).thenReturn(true);
		b.setCenter(new Vector2(0, 0));
		b.rotation = 45;
		b.SetDestination(new Vector2(100, 100));
		b.MoveToDestination((float) Math.sqrt(2));

		assertEquals(new Vector2(1, 1), b.getCenter());
	}

	@Test
	public void testMoveToDestinationOutBound() {
		// point is not in bounds
		when(gc.map.isPointInBounds(any(Vector2.class))).thenReturn(false);
		b.setCenter(new Vector2(0, 0));
		b.SetDestination(new Vector2(-100, -100));
		b.MoveToDestination(1);

		assertEquals(new Vector2(0, 0), b.getCenter()); // should not move
	}

	@Test
	public void testIsDestValidInvalid() {
		Vector2 target = new Vector2(10, 10);
		b.setCenter(new Vector2(0, 0));
		assertFalse(b.isDestValid(target));
	}

	@Test
	public void testIsDestValidValid() {
		Vector2 target = new Vector2(10, 0);
		b.setCenter(new Vector2(0, 0));
		assertTrue(b.isDestValid(target));
	}

	/**
	 * Only has to return a non-null point. Validation testing is done in
	 * {@link AIBoat#isDestValid(Vector2)}.
	 */
	@Test
	public void testGetNewRandomValidTarget() {
		assertNotNull(b.getNewRandomValidTarget());
	}

	@Test
	public void testUpdateDestination() {
		Vector2 old = b.GetDestination();
		b.destinationThreshold = 0; // ensure update does not happen
		b.updateDestination();
		assertEquals(old, b.GetDestination());

		b.destinationThreshold = 100000; // ensure boat is within threshold to force update
		b.updateDestination();
		assertNotEquals(old, b.GetDestination());
	}

	@Test
	public void testGetDestinationThreshold() {
		b.destinationThreshold = 1337;
		assertEquals(1337, b.getDestinationThreshold());
	}

	@Test
	public void testUpdateHP() {
		// Update method should destroy boat if HP is zero
		b.Update(1);
		assertFalse(b.isDead());
		b.HP = 0;
		b.Update(1);
		assertTrue(b.isDead());
	}

}