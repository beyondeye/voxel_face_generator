/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.game.voxel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.GdxTest;
import com.mygdx.game.voxel.reader.BinaryReader;
import com.mygdx.game.voxel.reader.VoxReader;
import com.mygdx.game.voxel.reader.VoxelData;
import com.mygdx.game.voxel.reader.VoxelDataPerColor;

public class VoxelTest extends GdxTest {
	static final float boxwidth=1;
	static final float boxheight=1;
	static final float boxdepth=1;

	SpriteBatch spriteBatch;
	BitmapFont font;
	ModelBatch modelBatch;
	PerspectiveCamera cam;
	Environment lights;
	CameraInputController controller;
	public AssetManager assets;
//	VoxelWorld voxelWorld;
	public Array<ModelInstance> instances = null;
	public Array<Model> models = null;

	private final Vector3 tmpV1 = new Vector3(), tmpV2 = new Vector3();
	private final BoundingBox bounds = new BoundingBox();

	@Override
	public void create () {
		String voxpath="data/vox/";
		/*
		FileHandle[] files = Gdx.files.internal(voxpath).list();
		for(FileHandle file: files) {
			if(!file.extension().equals("vox")) continue;
			String nameNoExt=file.nameWithoutExtension();
			String name_no_num=null;
			for (int i = nameNoExt.length()-1; i >=0; i++) {
				if(!Character.isDigit(nameNoExt.charAt(i))) {
					name_no_num = nameNoExt.substring(0,i);
					break;
				}
			}
			int[] defaultRGBAVoxPalette= getRGBAVoxelPalette(voxpath,name_no_num+"_palette.png");
			VoxelData vox = getVoxelData(voxpath, defaultRGBAVoxPalette,file.name());
			saveCompressedVox(vox,"")
		}
		*/
		FaceParts faceParts=new FaceParts(voxpath);
		faceParts.addPart("nface",8);
		faceParts.addPart("nmouth",2);
		faceParts.addPart("neyes",14);
		faceParts.addPart("neyebrows",8);
		faceParts.addPart("nhair",8);

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		instances = new Array<ModelInstance>();
		models = new Array<Model>();
		if (assets == null) assets = new AssetManager();

		//see https://github.com/libgdx/libgdx/wiki/ModelBatch
		DefaultShader.Config config = new DefaultShader.Config();
	//	config.defaultCullFace=GL20.GL_FRONT;
		modelBatch = new ModelBatch(new DefaultShaderProvider(config)); //create a model batch with specified shader config
		cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = 0.1f;
		cam.far = 1000;
		cam.position.set(16f, -32f, 16f);
		cam.lookAt(16f, 16f, 16f);
		cam.update();
//		controller = new FirstPersonCameraController(cam);
		controller = new CameraInputController(cam);
		Gdx.input.setInputProcessor(controller);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.f));
		lights.add( new PointLight().set(1f, 1f, 1f, 64f, -64f, 64f, 100f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));

//		lights.add(new DirectionalLight().set(1f, 1f, 1f, 16f, -10f, 16f));
//		lights.add( new DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f));
//		lights.add( new PointLight().set(0.8f, 0.8f, 0.8f, 30f, -10f, 16f, 100f));

	//	Texture texture = new Texture(Gdx.files.internal("data/g3d/tiles.png"));
	//	TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

		VoxelData[] voxparts=faceParts.getFaceParts("2");
//		FileHandle vxf=Gdx.files.internal("data/vox/face1.vox");
//		FileHandle vxf=Gdx.files.internal("data/vox/face1_thick.vox");
//		int[] defaultRGBAVoxPalette= getRGBAVoxelPalette(voxpath,"face1_thick_palette.png");
//		VoxelData vox1 = getVoxelData(voxpath, defaultRGBAVoxPalette,"face1_thick.vox");
//		VoxelData vox2 = getVoxelData(voxpath, defaultRGBAVoxPalette,"hair0_thick.vox");
//		VoxelData vox3 = getVoxelData(voxpath, defaultRGBAVoxPalette,"eyes0.vox");
//		VoxelData vox4 = getVoxelData(voxpath, defaultRGBAVoxPalette,"mouth0.vox");

		//see https://github.com/libgdx/libgdx/wiki/ModelBuilder%2C-MeshBuilder-and-MeshPartBuilder
		ModelBuilder bgmodelBuilder=new ModelBuilder();
		bgmodelBuilder.begin();
		addWhiteBackground(bgmodelBuilder);
		Model bgmodel = bgmodelBuilder.end();
		ModelInstance bginstance = new ModelInstance(bgmodel);
		models.add(bgmodel);
		instances.add(bginstance);

		ModelBuilder modelBuilder=new ModelBuilder();
		modelBuilder.begin();
		for (int i = 0; i < voxparts.length; i++) {
			addVoxModelPart(modelBuilder,voxparts[i]);
		}
		Model model = modelBuilder.end();
		ModelInstance instance = new ModelInstance(model);
		models.add(model);
		instances.add(instance);

		String materials[] = new String[] {"diffuse_green", "badlogic_normal", "brick01", "brick02", "brick03",
				"chesterfield", "cloth01", "cloth02", "elephant01", "elephant02", "fur01", "grass01", "metal01", "metal02", "mirror01",
				"mirror02", "moon01", "plastic01", "stone01", "stone02", "wood01", "wood02"};

		//setMaterial("cloth01");
//		setMaterial("chesterfield");
		//TODO: check documentation of turning on/off continuous renedering
		//Continuous & non continuous rendering · libgdx/libgdx Wiki
		//https://github.com/libgdx/libgdx/wiki/Continuous-%26-non-continuous-rendering


		/*instance.calculateBoundingBox(bounds);
		cam.position.set(1, 1, 1).nor().scl(bounds.getDimensions(tmpV1).len() * 0.75f + bounds.getCenter(tmpV2).len());
		cam.up.set(0, 0, 1);
		cam.lookAt(16f, 16f, 16f);
		cam.far = 50f + bounds.getDimensions(tmpV1).len() * 2.0f;
		cam.update();
		//cam.position.set(camX, camY, camZ);
		*/
	}

	String currentMaterial=null;
	String currentlyLoading=null;
	boolean loadingMaterial=false;
	private void setMaterial(String name) {
		if (name == null) return;
		if (currentlyLoading != null) {
			Gdx.app.error("ModelTest", "Wait for the current model/material to be loaded.");
			return;
		}

		currentlyLoading = "data/g3d/materials/" + name + ".g3dj";
		loadingMaterial = true;
		if (!name.equals(currentMaterial)) assets.load(currentlyLoading, Model.class);
		loading = true;
	}
	boolean loading=false;
	private void onLoaded() {
		if (currentlyLoading == null || currentlyLoading.length() == 0) return;

		if (loadingMaterial) {
			loadingMaterial = false;
			if (currentMaterial != null && !currentMaterial.equals(currentlyLoading)) assets.unload(currentMaterial);
			currentMaterial = currentlyLoading;
			currentlyLoading = null;
			for (int i = 1; i < instances.size; i++) { //start from 1: skip bg model instance
				ModelInstance instance=instances.get(i);
				if (instance != null) {
					Array<Material> materials= instance.materials;
					for (int j = 0; j < materials.size; j++) {
						Material mat=instance.materials.get(j);
						mat.clear();
						mat.set(assets.get(currentMaterial, Model.class).materials.get(0));

					}
				}

			}
		}
	}

	private VoxelData getVoxelData(String voxpath, int[] defaultRGBAVoxPalette, String voxname) {
		FileHandle vxf1= Gdx.files.internal(voxpath+ voxname);
		return VoxReader.fromMagica(new BinaryReader(vxf1),defaultRGBAVoxPalette);
	}
	private int[] getRGBAVoxelPalette(String voxpath, String voxpalettename) {
		FileHandle vxf1= Gdx.files.internal(voxpath+ voxpalettename);
		Pixmap pixmap= new Pixmap(vxf1);
		int[] res= new int[256];
		for (int i = 0; i < 256; i++) {
			res[i]=pixmap.getPixel(i,0);
		}
		return res;
	}


	/**
	 * from https://github.com/libgdx/libgdx/wiki/Taking-a-Screenshot
	 */
	void takeScreenShot() {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap);
		pixmap.dispose();
	}

	private void addWhiteBackground(ModelBuilder modelBuilder) {
		Material bgMaterial = new Material(ColorAttribute.createDiffuse(new Color(1.0f,1.0f,1.0f,1.0f)));
		MeshPartBuilder bgMeshBuilder = modelBuilder.part(
				"bg",
				GL20.GL_TRIANGLES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
				bgMaterial);
		BoxShapeBuilder.build(bgMeshBuilder, 16f, 64f, 16f, 200f, 10f, 200f); //a large white background
	}

	private void addVoxModelPart(ModelBuilder modelBuilder, VoxelData vox) {
		for (int icolor = 0; icolor < vox.getNColors(); icolor++) {
			VoxelDataPerColor curVoxData = vox.dataPerColor.get(icolor);
			Material material = new Material(ColorAttribute.createDiffuse(new Color(curVoxData.voxelrgbacolor)));
			//TODO check which vertexatributes to use
			MeshPartBuilder meshBuilder = modelBuilder.part(
					"color"+icolor,
					GL20.GL_TRIANGLES,
					VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
					material);
			for (int iv = 0; iv < curVoxData.nvoxels; iv++) {

				float x = curVoxData.x[iv] * boxwidth;
				float y = curVoxData.y[iv] * boxheight;
				float z = curVoxData.z[iv] * boxdepth;

				BoxShapeBuilder.build(meshBuilder, x, y, z, boxwidth, boxheight, boxdepth);

			}
		}
	}

	@Override
	public void render () {
		if (loading && assets.update()) {
			loading = false;
			onLoaded();
		}
		//Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(cam);
		//TODO take a look at https://github.com/libgdx/libgdx/wiki/ModelBatch#sorting-render-calls
		modelBatch.render(instances, lights);
		modelBatch.end();
		controller.update();

		spriteBatch.begin();
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond()+ "camdir:"+ cam.direction.toString() , 0, 20);
		spriteBatch.end();
	}



	@Override
	public void resize (int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		assets.dispose();
		assets = null;
		models=null;
		instances=null;
	}
}
