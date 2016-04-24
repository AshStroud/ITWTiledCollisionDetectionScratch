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
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
//			Implementing Collision Detection using Object Layers: http://stackoverflow.com/questions/22152925/libgdx-tiled-map-collision-detection
//			Orthographic Camera Properties: http://www.gamefromscratch.com/post/2014/04/16/LibGDX-Tutorial-11-Tiled-Maps-Part-1-Simple-Orthogonal-Maps.aspx
public class GamTiledCollisionDetection extends ApplicationAdapter {
	private static final int nCols = 4;
	private static final int nRows = 4;

	SpriteBatch sbBatch;
	Texture txSprite;
	TextureRegion[] artrFrames;
	TextureRegion trCurrentFrame;
	float fSpriteX = 0;
	float fSpriteY = 0;
	float fSpriteSpeed = 45f;
	float fTime = 0f;
	Animation aniMain;


	TiledMap tmGameMap;
	OrthogonalTiledMapRenderer orthotmrRenderer;
	OrthographicCamera ocMainCam;

	ArrayList<Rectangle> arlRectObjectBounds = new ArrayList<Rectangle>();
	Rectangle rectSprite;
	String sDirection;

	RectangleMapObject rmoCollisionRect;
	MapObjects moCollisionDetection;
	Rectangle rectObjectBounds;
	@Override
	public void create () {
		sbBatch = new SpriteBatch();
		rectSprite = new Rectangle();
		txSprite = new Texture(Gdx.files.internal("CinderellaSpriteSheet.png"));
		TextureRegion[][] tmp = TextureRegion.split(txSprite, txSprite.getWidth() / nCols, txSprite.getHeight() / nRows);
		artrFrames = new TextureRegion[nCols * nRows];
		int index = 0;
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				artrFrames[index++] = tmp[i][j];
			}
		}
		aniMain = new Animation(1f, artrFrames);

		ocMainCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ocMainCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ocMainCam.update();

		//Setting Up TiledMap
		tmGameMap= new TmxMapLoader().load("CollisionDetectionScratchMap.tmx");
		orthotmrRenderer = new OrthogonalTiledMapRenderer(tmGameMap);
		//Creating Bounds for Collision Detection
		moCollisionDetection = tmGameMap.getLayers().get("Object Layer 1").getObjects();
		for (int i = 0; i < moCollisionDetection.getCount(); i++) {
			rmoCollisionRect = (RectangleMapObject) moCollisionDetection.get(i);
			rectObjectBounds = rmoCollisionRect.getRectangle();

			arlRectObjectBounds.add(rectObjectBounds);
			System.out.println("Rectangle Added!");
		}
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
		trCurrentFrame = aniMain.getKeyFrame(0);

		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
			fSpriteX -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			trCurrentFrame = aniMain.getKeyFrame(4 + fTime);
			//System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Left";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
			fSpriteX += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			trCurrentFrame = aniMain.getKeyFrame(8 + fTime);
			//System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Right";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)) {
			fSpriteY += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			trCurrentFrame = aniMain.getKeyFrame(12 + fTime);
			//System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Up";
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
			fSpriteY -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			trCurrentFrame = aniMain.getKeyFrame(0 + fTime);
			//System.out.println("Player Sprite X:" + fSpriteX + "PlayerSpriteY:" + fSpriteY);
			sDirection = "Down";
		}
		//Rendering Tiled Map
		ocMainCam.update();
		orthotmrRenderer.setView(ocMainCam);
		orthotmrRenderer.render();
		ocMainCam.update();

		//Draw Sprites
		sbBatch.begin();
		sbBatch.draw(trCurrentFrame, (int) fSpriteX, (int) fSpriteY);
		sbBatch.end();

		rectSprite.set(fSpriteX, fSpriteY, trCurrentFrame.getRegionWidth(), trCurrentFrame.getRegionHeight());

		for (int i = 0; i < arlRectObjectBounds.size(); i++) {
			if(rectSprite.overlaps(arlRectObjectBounds.get(i))) {
				System.out.println("Collision Detected");
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

//Old Collision Detection Code

		/*for(int i = 0; i < arlRectCollisionDetection.size(); i++){
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
		}*/
//Old Setup for Collision Detection
/*for(int i = 0; i < 20; i++){
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
		}*/