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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.utils.Array;
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
	FirstPersonCameraController controller;
//	VoxelWorld voxelWorld;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Array<Model> models = new Array<Model>();

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		//see https://github.com/libgdx/libgdx/wiki/ModelBatch
		DefaultShader.Config config = new DefaultShader.Config();
	//	config.defaultCullFace=GL20.GL_FRONT;
		modelBatch = new ModelBatch(new DefaultShaderProvider(config)); //create a model batch with specified shader config
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = 0.1f;
		cam.far = 1000;
		cam.position.set(16f, 0f, 16f);
		cam.lookAt(16f, 16f, 16f);
		cam.update();
		controller = new FirstPersonCameraController(cam);
		Gdx.input.setInputProcessor(controller);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(1f, 1f, 1f, 0, -1, 0));

	//	Texture texture = new Texture(Gdx.files.internal("data/g3d/tiles.png"));
	//	TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

//		FileHandle vxf=Gdx.files.internal("data/vox/face1.vox");
//		FileHandle vxf=Gdx.files.internal("data/vox/face1_thick.vox");
		FileHandle vxf=Gdx.files.internal("data/vox/hair0_thick.vox");
		VoxelData vox= VoxReader.fromMagica(new BinaryReader(vxf));

		//see https://github.com/libgdx/libgdx/wiki/ModelBuilder%2C-MeshBuilder-and-MeshPartBuilder
		ModelBuilder modelBuilder=new ModelBuilder();
		modelBuilder.begin();
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
		Model model = modelBuilder.end();
		ModelInstance instance = new ModelInstance(model);
		models.add(model);
		instances.add(instance);
		//cam.position.set(camX, camY, camZ);
	}

	@Override
	public void render () {
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
		for (int i = 0; i < models.size; i++) {
			models.get(i).dispose();
		}
		models.clear();
	}
}
