package io.github.annabeths.Projectiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.NeutralBoat;
import io.github.annabeths.Colleges.College;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GameScreens.GameController;
import io.github.annabeths.Level.GameMap;

public class ProjectileRayTest {

	public List<PhysicsObject> objects;
	/** Array of objects where the order does not change */
	public PhysicsObject[] sortedObjects;
	public ProjectileData pd = ProjectileData.STOCK;
	public static GameController gc;

	@BeforeAll
	public static void init() {
		gc = mock(GameController.class);
		gc.map = mock(GameMap.class);
		gc.colleges = new ArrayList<College>();
	}

	@BeforeEach
	public void setupWorld() {
		objects = new ArrayList<PhysicsObject>();

		// line of boats
		for (int i = 0; i < 5; i++) {
			objects.add(new NeutralBoat(gc, new Vector2(100 + i * 150, 0)));
		}

		// boat at different y
		objects.add(new NeutralBoat(gc, new Vector2(100, 100)));

		// store order
		sortedObjects = objects.toArray(PhysicsObject[]::new);

		// randomize order with seeded random
		Collections.shuffle(objects, new Random(2022_03_17));
	}

	@Test
	public void testIntersection() {
		// default ray facing to the right
		ProjectileRay ray = new ProjectileRay(new Vector2(0, 25), 0, pd, true);
		List<PhysicsObject> objs = ray.getIntersectingObjects(objects);

		// should contain the first three objects in the array
		for (int i = 0; i < 3; i++) {
			assertTrue(objs.contains(sortedObjects[i]));
		}

		// should not contain any other object
		for (int i = 3; i < sortedObjects.length; i++) {
			assertFalse(objs.contains(sortedObjects[i]));
		}
	}

	@Test
	public void testIntersectionSorting() {
		ProjectileRay ray = new ProjectileRay(new Vector2(0, 25), 0, pd, true);
		List<PhysicsObject> objs = ray.getSortedIntersectingObjects(objects);

		// each element should match the sorted order
		for (int i = 0; i < objs.size(); i++) {
			assertEquals(sortedObjects[i], objs.get(i));
		}
	}

	@Test
	public void testLimitIntersection() {
		ProjectileRay ray = new ProjectileRay(new Vector2(0, 25), 0, pd, true);
		List<PhysicsObject> objs = ray.getNClosestIntersectingObjects(objects, 2);

		// should contain the first three objects in the array
		for (int i = 0; i < 2; i++) {
			assertTrue(objs.contains(sortedObjects[i]));
		}

		// should not contain any other object
		for (int i = 2; i < sortedObjects.length; i++) {
			assertFalse(objs.contains(sortedObjects[i]));
		}
	}

	@Test
	public void testFireMethod() {
		NeutralBoat gb = (NeutralBoat) sortedObjects[0];

		ProjectileRay ray = new ProjectileRay(new Vector2(0, 25), 0, pd, true);
		ray.fireRay(objects, 1);

		assertEquals(gb.getMaxHealth() - pd.damage, gb.getHealth());

		// ensure the rest of the objects are unaffected
		for (int i = 1; i < sortedObjects.length; i++) {
			gb = (NeutralBoat) sortedObjects[i];
			assertEquals(gb.getMaxHealth(), gb.getHealth());
		}
	}

	@Test
	public void testRemoveAfterShown() {
		ProjectileRay ray = new ProjectileRay(new Vector2(0, 25), 0, pd, true);
		float showTime = ray.getShowTime();
		ray.Update(showTime); // skip to end of show period

		assertTrue(ray.removeOnNextTick());
	}

}
