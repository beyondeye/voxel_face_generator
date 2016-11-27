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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.game.BaseG3dHudTest;
import com.mygdx.game.GdxTest;

public class VoxelTestWithMergedLightsText extends GdxTest {
	public AssetManager assets;

	public PerspectiveCamera cam;
	public CameraInputController inputController;
	public ModelBatch modelBatch;
	public Model axesModel;
	public ModelInstance axesInstance;
	public boolean showAxes = true;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Array<Model> models = new Array<Model>();
	public final Color bgColor = new Color(0, 0, 0, 1);

	//----------BaseG3dHudTest
	public final static int PREF_HUDWIDTH = 640;
	public final static int PREF_HUDHEIGHT = 480;
	public final static float rotationSpeed = 0.02f * 360f; // degrees per second
	public final static float moveSpeed = 0.25f; // cycles per second

	protected Stage hud;
	protected float hudWidth, hudHeight;
	protected Skin skin;
	protected Label fpsLabel;
	protected BaseG3dHudTest.CollapsableWindow modelsWindow;
	protected CheckBox gridCheckBox, rotateCheckBox, moveCheckBox;
	protected final StringBuilder stringBuilder = new StringBuilder();
	protected final Matrix4 transform = new Matrix4();
	protected float moveRadius = 2f;

	protected String modelNames[] = new String[] {"car.obj", "cube.obj", "scene.obj", "scene2.obj", "wheel.obj", "g3d/invaders.g3dj",
			"g3d/head.g3db", "g3d/knight.g3dj", "g3d/knight.g3db", "g3d/monkey.g3db", "g3d/ship.obj", "g3d/shapes/cube_1.0x1.0.g3dj",
			"g3d/shapes/cube_1.5x1.5.g3dj", "g3d/shapes/sphere.g3dj", "g3d/shapes/teapot.g3dj", "g3d/shapes/torus.g3dj"};


	//--------ModelTest
	protected Environment environment;

	ObjectMap<ModelInstance, AnimationController> animationControllers = new ObjectMap<ModelInstance, AnimationController>();


	//---------LightsTest
	DirectionalLight dirLight;
	PointLight pointLight;
	Model lightModel;
	Renderable pLight;
	Vector3 center = new Vector3(), transformedCenter = new Vector3(), tmpV = new Vector3();
	float radius = 1f;

	//rendering parameters for voxel models
	static final float boxwidth=1;
	static final float boxheight=1;
	static final float boxdepth=1;

//	SpriteBatch spriteBatch;
//	BitmapFont font;
//	Environment lights;
	//FirstPersonCameraController controller;
	CameraInputController controller;
//	VoxelWorld voxelWorld;

	@Override
	public void create () {
		if (assets == null) assets = new AssetManager();

		DefaultShader.Config config = new DefaultShader.Config();
		//	config.defaultCullFace=GL20.GL_FRONT;
		modelBatch = new ModelBatch(new DefaultShaderProvider(config)); //create a model batch with specified shader config

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = 0.1f;
		cam.far = 1000;
		cam.position.set(16f, 0f, 16f);
		cam.lookAt(16f, 16f, 16f);
		cam.update();

		createAxes();

//		controller = new FirstPersonCameraController(cam);
		controller = new CameraInputController(cam);
		Gdx.input.setInputProcessor(controller);

		//----BaseG3dHudTest
		createHUD();

		Gdx.input.setInputProcessor(new InputMultiplexer(hud, this, inputController));


		//spriteBatch = new SpriteBatch();
		//font = new BitmapFont();
		//see https://github.com/libgdx/libgdx/wiki/ModelBatch



		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.add(new DirectionalLight().set(1f, 1f, 1f, 0, -1, 0)); //new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f)

		showAxes = true;
		onModelClicked("g3d/teapot.g3db");

		//----LightsTest
		environment.clear();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.0f));
		environment.add(dirLight = new DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f));
		environment.add(pointLight = new PointLight().set(0.2f, 0.8f, 0.2f, 0f, 0f, 0f, 100f));

		ModelBuilder mb = new ModelBuilder();
		lightModel = mb.createSphere(1, 1, 1, 10, 10, new Material(ColorAttribute.createDiffuse(1, 1, 1, 1)), VertexAttributes.Usage.Position);
		lightModel.nodes.get(0).parts.get(0).setRenderable(pLight = new Renderable());

		onModelClicked("g3d/teapot.g3db");

		/*
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
		*/
	}

	final float GRID_MIN = -10f;
	final float GRID_MAX = 10f;
	final float GRID_STEP = 1f;

	private void createAxes () {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
		builder.setColor(Color.LIGHT_GRAY);
		for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
			builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
			builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
		}
		builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
		builder.setColor(Color.RED);
		builder.line(0, 0, 0, 100, 0, 0);
		builder.setColor(Color.GREEN);
		builder.line(0, 0, 0, 0, 100, 0);
		builder.setColor(Color.BLUE);
		builder.line(0, 0, 0, 0, 0, 100);
		axesModel = modelBuilder.end();
		axesInstance = new ModelInstance(axesModel);
	}

	protected void createHUD () {
		hud = new Stage(new ScalingViewport(Scaling.fit, PREF_HUDWIDTH, PREF_HUDHEIGHT));
		hudWidth = hud.getWidth();
		hudHeight = hud.getHeight();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		final List<String> modelsList = new List(skin);
		modelsList.setItems(modelNames);
		modelsList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (!modelsWindow.isCollapsed() && getTapCount() == 2) {
					onModelClicked(modelsList.getSelected());
					modelsWindow.collapse();
				}
			}
		});
		modelsWindow = addListWindow("Models", modelsList, 0, -1);

		fpsLabel = new Label("FPS: 999", skin);
		hud.addActor(fpsLabel);
		gridCheckBox = new CheckBox("Show grid", skin);
		gridCheckBox.setChecked(showAxes);
		gridCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				showAxes = gridCheckBox.isChecked();
			}
		});
		gridCheckBox.setPosition(hudWidth - gridCheckBox.getWidth(), 0);
		hud.addActor(gridCheckBox);

		rotateCheckBox = new CheckBox("Rotate", skin);
		rotateCheckBox.setChecked(true);
		rotateCheckBox.setPosition(hudWidth - rotateCheckBox.getWidth(), gridCheckBox.getHeight());
		hud.addActor(rotateCheckBox);

		moveCheckBox = new CheckBox("Move", skin);
		moveCheckBox.setChecked(false);
		moveCheckBox.setPosition(hudWidth - moveCheckBox.getWidth(), rotateCheckBox.getTop());
		hud.addActor(moveCheckBox);
	}

	protected BaseG3dHudTest.CollapsableWindow addListWindow (String title, List list, float x, float y) {
		BaseG3dHudTest.CollapsableWindow window = new BaseG3dHudTest.CollapsableWindow(title, skin);
		window.row();
		ScrollPane pane = new ScrollPane(list, skin);
		pane.setFadeScrollBars(false);
		window.add(pane);
		window.pack();
		window.pack();
		if (window.getHeight() > hudHeight) {
			window.setHeight(hudHeight);
		}
		window.setX(x < 0 ? hudWidth - (window.getWidth() - (x + 1)) : x);
		window.setY(y < 0 ? hudHeight - (window.getHeight() - (y + 1)) : y);
		window.layout();
		window.collapse();
		hud.addActor(window);
		pane.setScrollX(0);
		pane.setScrollY(0);
		return window;
	}

	protected String currentlyLoading;

	protected  void onModelClicked (final String name) {
		if (name == null) return;

		currentlyLoading = "data/" + name;
		assets.load(currentlyLoading, Model.class);
		loading = true;
	}

	protected void getStatus (final StringBuilder stringBuilder) {
		stringBuilder.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
		if (loading) stringBuilder.append(" loading...");

		for (final ModelInstance instance : instances) {
			if (instance.animations.size > 0) {
				stringBuilder.append(" press space or menu to switch animation");
				break;
			}
		}
	}

	protected float rotation, movement;

	protected boolean loading = false;

	/*
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
*/
	protected  void render (final ModelBatch batch, final Array<ModelInstance> instances){

		final float delta = Gdx.graphics.getDeltaTime();
		dirLight.direction.rotate(Vector3.X, delta * 45f);
		dirLight.direction.rotate(Vector3.Y, delta * 25f);
		dirLight.direction.rotate(Vector3.Z, delta * 33f);

		pointLight.position.sub(transformedCenter);
		pointLight.position.rotate(Vector3.X, delta * 50f);
		pointLight.position.rotate(Vector3.Y, delta * 13f);
		pointLight.position.rotate(Vector3.Z, delta * 3f);
		pointLight.position.add(transformedCenter.set(center).mul(transform));

		pLight.worldTransform.setTranslation(pointLight.position);
		batch.render(pLight);

		for (ObjectMap.Entry<ModelInstance, AnimationController> e : animationControllers.entries())
			e.value.update(Gdx.graphics.getDeltaTime());
		batch.render(instances, environment);
	}

	public void render (final Array<ModelInstance> instances) {
		modelBatch.begin(cam);
		if (showAxes) modelBatch.render(axesInstance);
		if (instances != null) render(modelBatch, instances);
		modelBatch.end();
	}
	/*
	@Override
	public void render () {
		if (loading && assets.update()) {
			loading = false;
			onLoaded();
		}

		inputController.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);

		render(instances);
	}
	*/
	@Override
	public void render () {
		transform.idt();
		if (rotateCheckBox.isChecked())
			transform.rotate(Vector3.Y, rotation = (rotation + rotationSpeed * Gdx.graphics.getRawDeltaTime()) % 360);
		if (moveCheckBox.isChecked()) {
			movement = (movement + moveSpeed * Gdx.graphics.getRawDeltaTime()) % 1f;
			final float sm = MathUtils.sin(movement * MathUtils.PI2);
			final float cm = MathUtils.cos(movement * MathUtils.PI2);
			transform.trn(0, moveRadius * cm, moveRadius * sm);
		}

		super.render();

		stringBuilder.setLength(0);
		getStatus(stringBuilder);
		fpsLabel.setText(stringBuilder);
		hud.act(Gdx.graphics.getDeltaTime());
		hud.draw();
	}

	@Override
	public void resize (int width, int height) {
		///spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		hud.getViewport().update(width, height, true);
		hudWidth = hud.getWidth();
		hudHeight = hud.getHeight();

		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
	}

	@Override
	public void dispose() {
		//lightstest
		lightModel.dispose();

		//baseg3dtest
		modelBatch.dispose();
		assets.dispose();
		assets = null;
		axesModel.dispose();
		axesModel = null;

		//baseg3dtest
		skin.dispose();
		skin = null;
	}

	/** Double click title to expand/collapse */
	public static class CollapsableWindow extends Window {
		private boolean collapsed;
		private float collapseHeight = 20f;
		private float expandHeight;

		public CollapsableWindow (String title, Skin skin) {
			super(title, skin);
			addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					if (getTapCount() == 2 && getHeight() - y <= getPadTop() && y < getHeight() && x > 0 && x < getWidth())
						toggleCollapsed();
				}
			});
		}

		public void expand () {
			if (!collapsed) return;
			setHeight(expandHeight);
			setY(getY() - expandHeight + collapseHeight);
			collapsed = false;
		}

		public void collapse () {
			if (collapsed) return;
			expandHeight = getHeight();
			setHeight(collapseHeight);
			setY(getY() + expandHeight - collapseHeight);
			collapsed = true;
			if (getStage() != null) getStage().setScrollFocus(null);
		}

		public void toggleCollapsed () {
			if (collapsed)
				expand();
			else
				collapse();
		}

		public boolean isCollapsed () {
			return collapsed;
		}
	}

	private final Vector3 tmpV1 = new Vector3(), tmpV2 = new Vector3();
	private final BoundingBox bounds = new BoundingBox();

	protected void onLoaded () {
		if (currentlyLoading == null || currentlyLoading.length() == 0) return;

		instances.clear();
		animationControllers.clear();
		final ModelInstance instance = new ModelInstance(assets.get(currentlyLoading, Model.class));
		instance.transform = transform;
		instances.add(instance);
		if (instance.animations.size > 0) animationControllers.put(instance, new AnimationController(instance));
		currentlyLoading = null;

		instance.calculateBoundingBox(bounds);
		cam.position.set(1, 1, 1).nor().scl(bounds.getDimensions(tmpV1).len() * 0.75f + bounds.getCenter(tmpV2).len());
		cam.up.set(0, 1, 0);
		cam.lookAt(0, 0, 0);
		cam.far = 50f + bounds.getDimensions(tmpV1).len() * 2.0f;
		cam.update();

		//---LightsTest
		BoundingBox bounds = instances.get(0).calculateBoundingBox(new BoundingBox());
		bounds.getCenter(center);
		radius = bounds.getDimensions(tmpV).len() * .5f;
		pointLight.position.set(0, radius, 0).add(transformedCenter.set(center).mul(transform));
		pointLight.intensity = radius * radius;
		((ColorAttribute)pLight.material.get(ColorAttribute.Diffuse)).color.set(pointLight.color);
		final float s = 0.2f * radius;
		pLight.worldTransform.setToScaling(s, s, s);
	}

	protected void switchAnimation () {
		for (ObjectMap.Entry<ModelInstance, AnimationController> e : animationControllers.entries()) {
			int animIndex = 0;
			if (e.value.current != null) {
				for (int i = 0; i < e.key.animations.size; i++) {
					final Animation animation = e.key.animations.get(i);
					if (e.value.current.animation == animation) {
						animIndex = i;
						break;
					}
				}
			}
			animIndex = (animIndex + 1) % (e.key.animations.size + 1);
			e.value.animate((animIndex == e.key.animations.size) ? null : e.key.animations.get(animIndex).id, -1, 1f, null, 0.2f);
		}
	}

	@Override
	public boolean keyUp (int keycode) {
		if (keycode == Input.Keys.SPACE || keycode == Input.Keys.MENU) switchAnimation();
		return super.keyUp(keycode);
	}
}
