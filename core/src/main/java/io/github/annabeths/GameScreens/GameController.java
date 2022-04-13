package io.github.annabeths.GameScreens;

import static io.github.annabeths.Level.GameMap.BORDER_BRIM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.annabeths.Boats.EnemyBoat;
import io.github.annabeths.Boats.NeutralBoat;
import io.github.annabeths.Boats.PlayerBoat;
import io.github.annabeths.Collectables.Powerup;
import io.github.annabeths.Collectables.PowerupType;
import io.github.annabeths.Colleges.College;
import io.github.annabeths.Colleges.EnemyCollege;
import io.github.annabeths.Colleges.PlayerCollege;
import io.github.annabeths.GameGenerics.GameObject;
import io.github.annabeths.GameGenerics.PhysicsObject;
import io.github.annabeths.GeneralControl.DebugUtils;
import io.github.annabeths.GeneralControl.eng1game;
import io.github.annabeths.Level.GameMap;
import io.github.annabeths.Projectiles.ProjectileData;
import io.github.annabeths.Projectiles.ProjectileRay;
import io.github.annabeths.UI.HUD;

public class GameController implements Screen {

	private eng1game game;
	public ArrayList<GameObject> gameObjects;
	public ArrayList<PhysicsObject> physicsObjects;
	public ArrayList<College> colleges;
	public ArrayList<ProjectileRay> rays;
	public GameMap map;
	public PlayerBoat playerBoat;
	public EnemyCollege bossCollege;
	public Powerup powerUp;

	public float timer = 10 * 60 + 0;

	// UI Related Variables
	public OrthographicCamera camera;
	public SpriteBatch batch;
	public ShapeRenderer sr;
	public HUD hud;

	// Player Stats
	public int xp = 0;
	public int plunder = 0;

	float xpTick = 1f;
	float xpTickMultiplier = 1f;

	// passes the game class so that we can change scene back later
	public GameController(eng1game game) {
		this.game = game;
		gameObjects = new ArrayList<GameObject>();
		physicsObjects = new ArrayList<PhysicsObject>();
		colleges = new ArrayList<College>();
		rays = new ArrayList<ProjectileRay>();

		camera = new OrthographicCamera();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.viewportWidth = Gdx.graphics.getWidth();

		// Create the player boat and place it in the center of the screen
		playerBoat = new PlayerBoat(this, new Vector2(500, 500));
		physicsObjects.add(playerBoat);

		generateGameObjects();

	}

	@Override
	public void show() {
		hud = new HUD(this);
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		map = new GameMap(this);
	}

	private void generateGameObjects() {
		// Generate a list of random college textures
		// TODO: Make textures unique
		List<String> collegeTextures = MathUtils.random.ints(5, 0, 9)
				.mapToObj(tn -> String.format("img/world/castle/castle%d.png", (tn + 1)))
				.collect(Collectors.toList());

		Vector2 collegePlayer = new Vector2(BORDER_BRIM, BORDER_BRIM);
		Vector2 college1 = new Vector2(BORDER_BRIM, GameMap.getMapHeight() - 100 - BORDER_BRIM);
		Vector2 college2 = new Vector2(GameMap.getMapWidth() - 100 - BORDER_BRIM, BORDER_BRIM);
		Vector2 college3 = new Vector2(GameMap.getMapWidth() - 100 - BORDER_BRIM,
				GameMap.getMapHeight() - 100 - BORDER_BRIM);
		List<Vector2> collegePos = Arrays.asList(college1, college2, college3);
		Vector2 collegeBoss = new Vector2((GameMap.getMapWidth() - 100) / 2,
				(GameMap.getMapHeight() - 100) / 2);

		// get the texture for colleges to sit on
		String islandTexture = "img/world/island.png";
		PlayerCollege p = new PlayerCollege(collegePlayer, collegeTextures.get(0), islandTexture,
				this, false);
		physicsObjects.add(p); // add college to physics object, for updates
		colleges.add(p); // also add a reference to the colleges list

		// create the boss college
		bossCollege = new EnemyCollege(collegeBoss, collegeTextures.get(1), islandTexture, this,
				ProjectileData.BOSS, 200);

		bossCollege.setInvulnerable(true);
		physicsObjects.add(bossCollege);
		colleges.add(bossCollege);

		// create some enemy colleges
		for (int i = 0; i < 3; i++) {
			EnemyCollege e = new EnemyCollege(collegePos.get(i), collegeTextures.get(i + 2),
					islandTexture, this, ProjectileData.STOCK, 200);
			physicsObjects.add(e);
			colleges.add(e);
		}

		// create some powerups
		for (int i = 0; i < 5; i++) {
			// TODO: Prevent powerups spawning on top of colleges
			physicsObjects
					.add(new Powerup(PowerupType.randomPower(), GameMap.getRandomPointInBounds()));
		}

		// create some boats
		physicsObjects.add(new NeutralBoat(this,
				new Vector2(GameMap.getMapWidth() / 3, GameMap.getMapHeight() / 3)));
		physicsObjects.add(new NeutralBoat(this,
				new Vector2(2 * GameMap.getMapWidth() / 3, GameMap.getMapHeight() / 3)));
		physicsObjects.add(new NeutralBoat(this,
				new Vector2(GameMap.getMapWidth() / 3, 2 * GameMap.getMapHeight() / 3)));
		physicsObjects.add(new EnemyBoat(this,
				new Vector2(2 * GameMap.getMapWidth() / 3, 2 * GameMap.getMapHeight() / 3)));
	}

	public void logic(float delta) {
		timer -= delta;
		if (timer <= 0) gameOver();

		// give the player XP and Plunder each frame, normalized using delta
		xpTick -= delta * xpTickMultiplier;
		if (xpTick <= 0) {
			xp += 1;
			plunder += 1;
			xpTick += 1;
		}

		hud.Update(delta);
		map.Update(delta);

		UpdateObjects(delta); // update all physicsobjects
		ClearKilledObjects(); // clear any 'killed' objects

		if (bossCollege.isDead()) { // if the boss college is dead, the game is won
			game.gotoScreen(Screens.gameWinScreen);
		}
	}

	@Override
	public void render(float delta) {
		// do updates here
		logic(delta);

		// do draws here
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set the sprite batch to use the correct camera
		batch.setProjectionMatrix(camera.combined);

		// begin the sprite batch
		batch.begin();

		map.Draw(batch);

		// draw all the physics objects
		for (PhysicsObject physicsObject : physicsObjects) {
			physicsObject.Draw(batch);
		}

		if (DebugUtils.DRAW_DEBUG_TEXT) DebugUtils.drawEntityDebugText(this, batch);

		hud.Draw(batch); // this resets camera projection matrix

		if (DebugUtils.DRAW_DEBUG_TEXT) DebugUtils.drawDebugText(this, batch);

		// end the sprite batch
		batch.end();

		sr.setProjectionMatrix(camera.combined);

		renderRays();

		// this should be off during normal gameplay, but can be on to debug collisions
		if (DebugUtils.DRAW_DEBUG_COLLISIONS) DebugUtils.drawDebugCollisions(this, sr);
	}

	/** Renders ProjectileRay objects */
	public void renderRays() {
		sr.begin(ShapeType.Filled);

		Random rCol = new Random();

		rays.forEach(r -> {
			float ratio = r.getRemainShowTime() / r.getShowTime();
			sr.setColor(Color.BLACK);
			sr.rectLine(r.getOrigin(), r.getFarthestHitPoint(), 6 * ratio);

			rCol.setSeed(r.hashCode()); // unique color for each ray
			sr.setColor(rCol.nextFloat(), rCol.nextFloat(), rCol.nextFloat(), 1);
			sr.rectLine(r.getOrigin(), r.getFarthestHitPoint(), 4 * ratio);
		});

		sr.end();
	}

	/**
	 * Updates all physics objects in the {@link #physicsObjects} list
	 * 
	 * @param delta time since last frame
	 */
	public void UpdateObjects(float delta) {
		for (int i = 0; i < physicsObjects.size(); i++) {
			PhysicsObject current = physicsObjects.get(i);

			current.Update(delta);

			for (int j = 0; j < physicsObjects.size(); j++) {
				PhysicsObject other = physicsObjects.get(j);
				if (i == j) continue;

				if (current.CheckCollisionWith(other)) {
					current.OnCollision(other);
				}
			}
		}

		// Update rays
		rays.forEach(r -> r.Update(delta));

		// XP is increased if player is in dangerous position
		xpTickMultiplier = isPlayerInDanger() ? 2f : 1f;
	}

	/**
	 * Tests if player is in danger. The player is in danger if it is in range of an
	 * {@link EnemyCollege} or an {@link EnemyBoat}.
	 * 
	 * @return {@code true} if within range of an enemy college or boat,
	 *         {@code false} otherwise or player is invincible.
	 * @author James Burnell
	 */
	public boolean isPlayerInDanger() {
		return !playerBoat.isInvincible()
				&& (isEnemyCollegeNearPlayer() || isEnemyBoatNearPlayer());
	}

	/**
	 * Tests if player is in range of an {@link EnemyBoat}.
	 * 
	 * @return {@code true} if within range of an enemy boat, {@code false}
	 *         otherwise.
	 * @author James Burnell
	 */
	public boolean isEnemyBoatNearPlayer() {
		return physicsObjects.stream().anyMatch(p -> p instanceof EnemyBoat
				&& p.getCenter().dst2(playerBoat.getCenter()) < 500 * 500);
	}

	/**
	 * Tests if player is in range of an {@link EnemyCollege}.
	 * 
	 * @return {@code true} if within range of an enemy college, {@code false}
	 *         otherwise.
	 * @author James Burnell
	 */
	public boolean isEnemyCollegeNearPlayer() {
		return colleges.stream()
				.anyMatch(c -> c instanceof EnemyCollege && c.isInRange(playerBoat));
	}

	/**
	 * Called when a college is destroyed Makes sure the boss college will be made
	 * vulnerable after the rest of the colleges are destroyed, and spawns a
	 * friendly college in the place of the enemy college.
	 */
	public void CollegeDestroyed(EnemyCollege oldCollege) {
		AddXP(100);

		boolean foundCollege = physicsObjects.stream().filter(c -> c instanceof EnemyCollege)
				.anyMatch(c -> {
					EnemyCollege e = (EnemyCollege) c;
					// there is still a normal college alive
					return e.getHealth() > 0 && !e.isInvulnerable();
				});

		if (!foundCollege) {
			bossCollege.becomeVulnerable();
		}

		PlayerCollege newFriendlyCollege = new PlayerCollege(oldCollege);
		physicsObjects.add(newFriendlyCollege);
	}

	/**
	 * Goes through all the {@link PhysicsObject} and removes ones from the list
	 * that have had the flag set ({@link GameObject#removeOnNextTick()}) in a safe
	 * manner
	 */
	public void ClearKilledObjects() {
		Iterator<PhysicsObject> p = physicsObjects.iterator();
		while (p.hasNext()) {
			PhysicsObject current = p.next();
			if (current.removeOnNextTick()) {
				p.remove();
			}
		}
		// Clean up rays
		Iterator<ProjectileRay> ri = rays.iterator();
		while (ri.hasNext()) {
			if (ri.next().removeOnNextTick()) {
				ri.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	public void gameOver() {
		game.timeUp = timer <= 0;
		game.gotoScreen(Screens.gameOverScreen);
	}

	/**
	 * Called to give a reference to a new {@link PhysicsObject} to the
	 * {@link #physicsObjects} list
	 * 
	 * @param obj the object to add
	 */
	public void NewPhysicsObject(PhysicsObject obj) {
		// A new PhysicsObject has been created, add it to the list so it receives
		// updates
		physicsObjects.add(obj);
	}

	/**
	 * Add XP to the player's amount. Gives the player an equal amount of gold and
	 * XP
	 * 
	 * @param amount the amount of XP to add
	 */
	public void AddXP(int amount) {
		xp += amount;
		plunder += amount;
	}

}
