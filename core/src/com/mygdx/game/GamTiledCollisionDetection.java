package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import java.util.ArrayList;

//Sources: Implementing Collison Detection with Tiled: https://www.youtube.com/watch?v=MT5YwZsQnF8
//													https://github.com/libgdx/libgdx/wiki/Tile-maps
//			Drawing Rectangle to Screen: https://www.youtube.com/watch?v=xdc_1Pf-jnA
public class GamTiledCollisionDetection extends ApplicationAdapter {
	//SpriteBatch batch;
	//Texture img;
	private static final int nCols = 4;
	private static final int nRows = 4;

	//int nHeight = 480;
	//int nWidth = 640;

	SpriteBatch SbBatch;;
	Texture txSprite;
	TextureRegion[] trFrames;
	TextureRegion CurrentFrame;
	float fSpriteX = 0;
	float fSpriteY = 0;
	float fSpriteSpeed = 45f;
	float fTime = 0f;
	Animation aniMain;


	TiledMap tmGameMap;
	OrthogonalTiledMapRenderer OrhtoTmrRenderer;
	OrthographicCamera OcCam;

	ArrayList<Rectangle> arlRectCollisionDetection;
	Rectangle rectSprite;
	String sDirection;

	ShapeRenderer srSpriteRect;
	@Override
	public void create () {
		SbBatch = new SpriteBatch();
		srSpriteRect = new ShapeRenderer();
		txSprite = new Texture(Gdx.files.internal("CinderellaSpriteSheet.png"));
		TextureRegion[][] tmp = TextureRegion.split(txSprite, txSprite.getWidth() / nCols, txSprite.getHeight() / nRows);
		trFrames = new TextureRegion[nCols * nRows];
		int index = 0;
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				trFrames[index++] = tmp[i][j];
			}
		}
		aniMain = new Animation(1f, trFrames);

		//Setting Up TiledMap
		tmGameMap= new TmxMapLoader().load("CollisionDetectionScratchMap.tmx");
		OrhtoTmrRenderer = new OrthogonalTiledMapRenderer(tmGameMap);
		arlRectCollisionDetection = new ArrayList<Rectangle>();

		for(int i = 0; i < 20; i++){
			for(int j = 0; j < 20; j++){
				TiledMapTileLayer TmTlTrees = (TiledMapTileLayer) tmGameMap.getLayers().get("Foreground");
				Cell cCurrentTile = new Cell();

				if(TmTlTrees.getCell(i, j)!= null){
					cCurrentTile = TmTlTrees.getCell(i, j);
					System.out.println(i + " , " + j + " , " + cCurrentTile.getTile().getId());
					//System.out.println(TmTlTrees.getCell(i, j).)
					arlRectCollisionDetection.add(new Rectangle(i * 64, j * 64, 64, 64));
					System.out.println("Added rectangle!");
				}
			}
		}

		//Setting Up Orthographic Camera
		OcCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		OcCam.update();
	}

	@Override
	public void render () {
		//Rendering Sprite
		if (fTime < 4) {
			fTime += Gdx.graphics.getDeltaTime();
		} else {
			fTime = 0;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		CurrentFrame = aniMain.getKeyFrame(0);

		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
			fSpriteX -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			CurrentFrame = aniMain.getKeyFrame(4 + fTime);
			System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Left";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
			fSpriteX += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			CurrentFrame = aniMain.getKeyFrame(8 + fTime);
			System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Right";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)) {
			fSpriteY += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			CurrentFrame = aniMain.getKeyFrame(12 + fTime);
			System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Up";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
			fSpriteY -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			CurrentFrame = aniMain.getKeyFrame(0 + fTime);
			System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Down";
		}
		//Rendering Tiled Map
		OrhtoTmrRenderer.setView(OcCam);
		OrhtoTmrRenderer.render();

		//OrthoGraphic Camera
		OcCam.position.set(fSpriteX, fSpriteY, 0);
		//OcCam.setToOrtho(false, , 0);
		SbBatch.setProjectionMatrix(OcCam.combined);
		OcCam.update();

		//Draw Sprites
		SbBatch.begin();
		SbBatch.draw(CurrentFrame, (int) fSpriteX, (int) fSpriteY);
		System.out.println("Sprite drawn at" + fSpriteX + " and y value" + fSpriteY);
		SbBatch.end();

		srSpriteRect.begin(ShapeType.Filled);
		//srSpriteRect.set(rectSprite);
		srSpriteRect.setColor(Color.BLUE);
		srSpriteRect.rect(fSpriteX, fSpriteY, CurrentFrame.getRegionWidth(), CurrentFrame.getRegionHeight());
		srSpriteRect.end();

		rectSprite = new Rectangle(fSpriteX, fSpriteY,fSpriteX +CurrentFrame.getRegionWidth(),fSpriteY+ CurrentFrame.getRegionHeight());
		//rectSprite = new Rectangle(fSpriteX, fSpriteY, txSprite.getWidth(), txSprite.getHeight());


		for(int i = 0; i < arlRectCollisionDetection.size(); i++){
			if(rectSprite.overlaps(arlRectCollisionDetection.get(i))){
				System.out.println("Collision detected!");
				System.out.println(sDirection);
				if(sDirection == "Up"){
					fSpriteY -= 1f;
					//System.out.println("Rectangle Location:" + arlRectCollisionDetection.get(i));
					//System.out.println("Player Location:" + "X:" + fSpriteX + "Y: " + fSpriteY);
				}
				else if(sDirection == "Down"){
					fSpriteY += 1f;
					//System.out.println("Rectangle Location:" + arlRectCollisionDetection.get(i));
					//System.out.println("Player Location:" + "X:" + fSpriteX + "Y: " + fSpriteY);
				}
				else if(sDirection == "Right") {
					fSpriteX -= 1f;
					//System.out.println("Rectangle Location:" + arlRectCollisionDetection.get(i));
					//System.out.println("Player Location:" + "X:" + fSpriteX + "Y: " + fSpriteY);
				}
				else if(sDirection == "Left"){
					fSpriteX += 1f;
					//System.out.println("Rectangle Location:" + arlRectCollisionDetection.get(i));
					//System.out.println("Player Location:" + "X:" + fSpriteX + "Y: " + fSpriteY);
					//System.out.println("Play Rectangle: " + rectSprite);
				}
			}

		}
	}
}