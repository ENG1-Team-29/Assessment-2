package io.github.annabeths;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GameController implements Screen {

    eng1game game;
    ArrayList<GameObject> gameObjects;
    ArrayList<PhysicsObject> physicsObjects;
    float testRot = 0;
    private SpriteBatch batch;
    
    BitmapFont font;
    GlyphLayout hpTextLayout;

    private Boat playerBoat;

    public GameController(eng1game g){ //passes the game class so that we can change scene back lateraaaaaa
        game = g;
        gameObjects = new ArrayList<GameObject>();
        physicsObjects = new ArrayList<PhysicsObject>();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        
        // Create text object for player HP and load font
        font = new BitmapFont(Gdx.files.internal("fonts/bobcat.fnt"), false);
		hpTextLayout = new GlyphLayout();
		
        
        // Create the player boat and place it in the centre of the screen
        playerBoat = new PlayerBoat(this);
        playerBoat.SetPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2); // place the player 
    }

    @Override
    public void render(float delta) {
        // do updates here
    	
    	playerBoat.Update(delta);

        if(Gdx.input.isKeyJustPressed(Keys.SPACE))
        {
            System.out.println("aaaa");
            physicsObjects.add(new Projectile(playerBoat.x, playerBoat.y, playerBoat.rotation, new Texture(Gdx.files.internal("mario/yanderedev.jpg"))));
        }

        if (physicsObjects.size() > 0)
        {
            for (PhysicsObject physicsObject : physicsObjects) {
                physicsObject.Update(delta);
            }
        }
        

        // do draws here
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin(); //begin the sprite batch
        
        playerBoat.sprite.draw(batch); // draw the player boat

        if (physicsObjects.size() > 0)
        {
            for (PhysicsObject physicsObject : physicsObjects) {
                physicsObject.Draw(batch);
            }
        }


        // Draw the text showing the player's HP
        hpTextLayout.setText(font, "HP: " + playerBoat.HP);
        font.getData().setScale(1);
        font.draw(batch, hpTextLayout, 5, Gdx.graphics.getHeight() - 10);
        batch.end(); //end the sprite batch
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

    public void gameOver(){

    }
}