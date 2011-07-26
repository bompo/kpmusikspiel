package com.jumpandrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.shader.FastBloomShader;
import com.shader.TVShader;
import com.shader.TransShader;

public class Resources {
	
	public Sound music = Gdx.audio.newSound(Gdx.files.internal("data/test.wav"));
	
	public Mesh blockModel;
	public Mesh playerModel;
	public Mesh targetModel;
	public Mesh quadModel;
	public Mesh wireCubeModel;
	public Mesh sphereModel;
	public Mesh sphereSliceModel;

	public float[] clearColor = { 0.0f, 0.0f, 0.0f, 1.0f };
	public float[] backgroundWireColor = { 1.0f, 0.8f, 0.8f, 0.07f };
	public float[] wireCubeColor = { 1.0f, 0.1f, 0.1f, 0.04f };
	public float[] wireCubeEdgeColor = { 1.0f, 0.1f, 0.1f, 0.5f };
	public float[] blockColor = { 1.0f, 0.1f, 0.1f, 0.2f };
	public float[] blockEdgeColor = { 1.0f, 0.1f, 0.1f, 0.8f };
	public float[] movableBlockColor = { 1.0f, 0.8f, 0.1f, 0.8f };
	public float[] movableBlockEdgeColor = { 1.0f, 0.8f, 0.1f, 0.2f };
	public float[] switchBlockColor = { 0.2f, 0.2f, 0.2f, 0.8f };
	public float[] switchBlockEdgeColor = { 0.2f, 0.2f, 0.2f, 0.2f };
	public float[] playerColor = { 1.0f, 1.0f, 0.0f, 0.4f };
	public float[] playerEdgeColor = { 1.0f, 1.0f, 0.0f, 0.4f };
	public float[] portalColor = { 1f, 1f, 0f, 0.3f };
	public float[] portalColor2 = { 0.03f, 0.3f, 0.73f, 0.3f };
	public float[] portalColor3 = { 0f, 1f, 1f, 0.3f };
	public float[] portalColor4 = { 0f, 0.8f, 0.4f, 0.3f }; 
	public float[] portalColor5 = { 0.89f, 0.21f, 0.15f, 0.3f };
	public float[] portalEdgeColor = { 1.0f, 1.0f, 1.0f, 0.5f };
	public float[] targetColor = { 0.0f, 1.0f, 0.1f, 0.5f };
	public float[] targetEdgeColor = { 0.0f, 1.0f, 0.1f, 0.4f };
	public float[] jumpBlockColor = { 1f, 1f, 0f, 0.3f };
	public float[] jumpBlockEdgeColor = { 1.0f, 1.0f, 1.0f, 0.5f };
	public float[] enemySpawnerColor = { 0.1f, 1.0f, 1.0f, 0.2f };
	public float[] enemySpawnerEdgeColor = { 0.1f,1.0f, 1.0f, 0.8f };
	public float[] enemyColor = { 0.4f, 0.7f, 0.7f, 0.3f };
	public float[] enemyEdgeColor = { 0.4f, 0.7f, 0.7f, 0.5f };
	public float[] powerUpColor = { 0.7f, 0.5f, 0.4f, 0.3f };
	public float[] powerUpEdgeColor = { 0.7f, 0.5f, 0.4f, 0.5f };
	
	public ShaderProgram transShader;
	public ShaderProgram bloomShader;
	public ShaderProgram tvShader;
	
	public int m_i32TexSize = 128;
	public float m_fTexelOffset;
	
	public Preferences prefs = Gdx.app.getPreferences("kpmusikspiel");
	public boolean bloomOnOff = false;

	public static Resources instance;

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	public Resources() {
		reInit();
		initShader();
	}

	public void reInit() {
		blockModel = ObjLoader.loadObj(Gdx.files.internal("data/cube.obj").read());
		blockModel.getVertexAttribute(Usage.Position).alias = "a_vertex";

		playerModel = ObjLoader.loadObj(Gdx.files.internal("data/sphere_small.obj").read());
		playerModel.getVertexAttribute(Usage.Position).alias = "a_vertex";

		sphereModel = ObjLoader.loadObj(Gdx.files.internal("data/sphere.obj").read());
		sphereModel.getVertexAttribute(Usage.Position).alias = "a_vertex";

		targetModel = ObjLoader.loadObj(Gdx.files.internal("data/cylinder.obj").read());
		targetModel.getVertexAttribute(Usage.Position).alias = "a_vertex";
		
		sphereSliceModel= ObjLoader.loadObj(Gdx.files.internal("data/sphere_slice.obj").read());
		sphereSliceModel.getVertexAttribute(Usage.Position).alias = "a_vertex";
		
		quadModel = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 4, "a_vertex"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord"));
		float[] vertices = { -1.0f, 1.0f, 0.0f, 1.0f, // Position 0
				0.0f, 0.0f, // TexCoord 0
				-1.0f, -1.0f, 0.0f, 1.0f, // Position 1
				0.0f, 1.0f, // TexCoord 1
				1.0f, -1.0f, 0.0f, 1.0f, // Position 2
				1.0f, 1.0f, // TexCoord 2
				1.0f, 1.0f, 0.0f, 1.0f, // Position 3
				1.0f, 0.0f // TexCoord 3
		};
		short[] indices = { 0, 1, 2, 0, 2, 3 };
		quadModel.setVertices(vertices);
		quadModel.setIndices(indices);

		wireCubeModel = new Mesh(true, 20, 20, new VertexAttribute(Usage.Position, 4, "a_vertex"));
		float[] vertices2 = {
				// front face
				-1.0f, 1.0f, 1.0f, 1.0f, // 0
				1.0f, 1.0f, 1.0f, 1.0f, // 1
				1.0f, -1.0f, 1.0f, 1.0f, // 2
				-1.0f, -1.0f, 1.0f, 1.0f, // 3

				// left face
				-1.0f, 1.0f, 1.0f, 1.0f, // 0
				-1.0f, 1.0f, -1.0f, 1.0f, // 4
				-1.0f, -1.0f, -1.0f, 1.0f, // 7
				-1.0f, -1.0f, 1.0f, 1.0f, // 3

				// bottom face
				-1.0f, -1.0f, 1.0f, 1.0f, // 3
				1.0f, -1.0f, 1.0f, 1.0f, // 2
				1.0f, -1.0f, -1.0f, 1.0f, // 6
				-1.0f, -1.0f, -1.0f, 1.0f, // 7

				// back face
				-1.0f, -1.0f, -1.0f, 1.0f, // 7
				-1.0f, 1.0f, -1.0f, 1.0f, // 4
				1.0f, 1.0f, -1.0f, 1.0f, // 5
				1.0f, -1.0f, -1.0f, 1.0f, // 6

				// right face
				1.0f, -1.0f, -1.0f, 1.0f, // 6
				1.0f, -1.0f, 1.0f, 1.0f, // 2
				1.0f, 1.0f, 1.0f, 1.0f, // 1
				1.0f, 1.0f, -1.0f, 1.0f, // 5
		};
		short[] indices2 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
		wireCubeModel.setVertices(vertices2);
		wireCubeModel.setIndices(indices2);
		
		bloomOnOff = !prefs.getBoolean("bloom");
		
		if(music!=null) music.stop();
		music = Gdx.audio.newSound(Gdx.files.internal("data/test.wav"));
		
	}

	public void initShader() {
		transShader = new ShaderProgram(TransShader.mVertexShader, TransShader.mFragmentShader);
		if (transShader.isCompiled() == false) {
			Gdx.app.log("ShaderTest", transShader.getLog());
			System.exit(0);
		}

		// BLOOOOOOMMMM from powervr examples
		// Blur render target size (power-of-two)
		if(Gdx.graphics.getWidth()<=1000) {
			m_i32TexSize = 128;
		} else {
			m_i32TexSize = 256;
		}

		// Texel offset for blur filter kernle
		m_fTexelOffset = 1.0f / (float) m_i32TexSize / 4.f;

		// Altered weights for the faster filter kernel
		float w1 = 0.0555555f;
		float w2 = 0.2777777f;
		float intraTexelOffset = (w2 / (w1 + w2)) * m_fTexelOffset;
		m_fTexelOffset += intraTexelOffset;

		bloomShader = new ShaderProgram(FastBloomShader.mVertexShader, FastBloomShader.mFragmentShader);
		if (bloomShader.isCompiled() == false) {
			Gdx.app.log("ShaderTest", bloomShader.getLog());
			System.exit(0);
		}
		
		tvShader = new ShaderProgram(TVShader.mVertexShader, TVShader.mFragmentShader);
		if (tvShader.isCompiled() == false) {
			Gdx.app.log("ShaderTest", tvShader.getLog());
			System.exit(0);
		}
	}

	public void dispose() {
		blockModel.dispose();
		playerModel.dispose();
		targetModel.dispose();
		quadModel.dispose();
		wireCubeModel.dispose();
		
		transShader.dispose();
		bloomShader.dispose();
	}

}
