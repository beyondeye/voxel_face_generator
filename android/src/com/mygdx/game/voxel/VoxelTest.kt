/*******************************************************************************
 * Copyright 2011 See AUTHORS file.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mygdx.game.voxel


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.mygdx.game.GdxTest
import com.mygdx.game.voxel.reader.BinaryReader
import com.mygdx.game.voxel.reader.VoxReader
import com.mygdx.game.voxel.reader.VoxelData
import java.io.ByteArrayInputStream
import java.io.DataInputStream

fun FileHandle.toBinaryReader():BinaryReader {
    val bytes = this.readBytes()
    val length = bytes.size
    return BinaryReader(length, DataInputStream(ByteArrayInputStream(bytes)))
}

class VoxelTest(val id: String) : GdxTest() {

    lateinit internal var spriteBatch: SpriteBatch
    lateinit internal var font: BitmapFont
    lateinit internal var modelBatch: ModelBatch
    lateinit internal var cam: PerspectiveCamera
    lateinit internal var lights: Environment
    lateinit internal var controller: CameraInputController
    var assets: AssetManager? = null
    //	VoxelWorld voxelWorld;
    var instances: Array<ModelInstance>? = null
    var models: Array<Model>? = null

    private val tmpV1 = Vector3()
    private val tmpV2 = Vector3()
    private val bounds = BoundingBox()

    override fun create() {
        val voxpath = "data/vox/"
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
        val facePartsGen = FacePartsGenerator(voxpath)
        facePartsGen.addPart("nface", 8)
        facePartsGen.addPart("nmouth", 2)
        facePartsGen.addPart("neyes", 14)
        facePartsGen.addPart("neyebrows", 8)
        facePartsGen.addPart("nhair", 8)
        val phirot = 25f
        val thetarot = 25f
        facePartsGen.addPhiThetaRot(0f,0f)
        facePartsGen.addPhiThetaRot(phirot,thetarot)
        facePartsGen.addPhiThetaRot(-phirot,thetarot)

        val faceparts = facePartsGen.getFaceParts(id)
        if(faceparts==null) return
        val camerarot=faceparts.rot

        spriteBatch = SpriteBatch()
        font = BitmapFont()

        instances = Array<ModelInstance>()
        models = Array<Model>()
        if (assets == null) assets = AssetManager()

        //see https://github.com/libgdx/libgdx/wiki/ModelBatch
        val config = DefaultShader.Config()
        //	config.defaultCullFace=GL20.GL_FRONT;
        modelBatch = ModelBatch(DefaultShaderProvider(config)) //create a model batch with specified shader config
        setCameraPosition(camerarot.first, camerarot.second)
        //		controller = new FirstPersonCameraController(cam);
        controller = CameraInputController(cam)
        Gdx.input.inputProcessor = controller

        lights = Environment()
        lights.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f))
        lights.add(PointLight().set(1f, 1f, 1f, 64f, -64f, 64f, 100f))
        lights.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f))

        //		lights.add(new DirectionalLight().set(1f, 1f, 1f, 16f, -10f, 16f));
        //		lights.add( new DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f));
        //		lights.add( new PointLight().set(0.8f, 0.8f, 0.8f, 30f, -10f, 16f, 100f));

        //	Texture texture = new Texture(Gdx.files.internal("data/g3d/tiles.png"));
        //	TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

        val voxparts=faceparts.voxels
        //		FileHandle vxf=Gdx.files.internal("data/vox/face1.vox");
        //		FileHandle vxf=Gdx.files.internal("data/vox/face1_thick.vox");
        //		int[] defaultRGBAVoxPalette= getRGBAVoxelPalette(voxpath,"face1_thick_palette.png");
        //		VoxelData vox1 = getVoxelData(voxpath, defaultRGBAVoxPalette,"face1_thick.vox");
        //		VoxelData vox2 = getVoxelData(voxpath, defaultRGBAVoxPalette,"hair0_thick.vox");
        //		VoxelData vox3 = getVoxelData(voxpath, defaultRGBAVoxPalette,"eyes0.vox");
        //		VoxelData vox4 = getVoxelData(voxpath, defaultRGBAVoxPalette,"mouth0.vox");

        //see https://github.com/libgdx/libgdx/wiki/ModelBuilder%2C-MeshBuilder-and-MeshPartBuilder
        val bgmodelBuilder = ModelBuilder()
        bgmodelBuilder.begin()
        addWhiteBackground(bgmodelBuilder)
        val bgmodel = bgmodelBuilder.end()
        val bginstance = ModelInstance(bgmodel)
        models!!.add(bgmodel)
        instances!!.add(bginstance)

        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        for (i in voxparts.indices) {
            addVoxModelPart(modelBuilder, voxparts[i])
        }
        val model = modelBuilder.end()
        val instance = ModelInstance(model)
        models!!.add(model)
        instances!!.add(instance)

        val materials = arrayOf("diffuse_green", "badlogic_normal", "brick01", "brick02", "brick03", "chesterfield", "cloth01", "cloth02", "elephant01", "elephant02", "fur01", "grass01", "metal01", "metal02", "mirror01", "mirror02", "moon01", "plastic01", "stone01", "stone02", "wood01", "wood02")

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

    private fun setCameraPosition(phiRotation: Float, thetaRotation: Float) {
        val camFoV = 45f
        cam = PerspectiveCamera(camFoV, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.near = 0.1f
        cam.far = 1000f
        val faceCenter = Vector3(15.5f, 15.5f, 18.0f)
        val camPosition = Vector3(faceCenter).add(0f, -48f, 0f)
        cam.position.set(camPosition)
        cam.lookAt(faceCenter)
        cam.rotateAround(faceCenter, Vector3.Z, phiRotation) //45 degress
        val p = Vector3(faceCenter).sub(cam.position)
        val px = p.x
        val py = p.y
        p.x = -py //normal vector in the xy projection
        p.y = px //normal vector in the xy projection
        cam.rotateAround(faceCenter, p, thetaRotation) //45 degress
        cam.update()
    }

    internal var currentMaterial: String? = null
    internal var currentlyLoading: String? = null
    internal var loadingMaterial = false
    private fun setMaterial(name: String?) {
        if (name == null) return
        if (currentlyLoading != null) {
            Gdx.app.error("ModelTest", "Wait for the current model/material to be loaded.")
            return
        }

        currentlyLoading = "data/g3d/materials/$name.g3dj"
        loadingMaterial = true
        if (name != currentMaterial) assets!!.load<Model>(currentlyLoading, Model::class.java)
        loading = true
    }

    internal var loading = false
    private fun onLoaded() {
        if (currentlyLoading == null || currentlyLoading!!.isEmpty()) return

        if (loadingMaterial) {
            loadingMaterial = false
            if (currentMaterial != null && currentMaterial != currentlyLoading) assets!!.unload(currentMaterial)
            currentMaterial = currentlyLoading
            currentlyLoading = null
            for (i in 1..instances!!.size - 1) { //start from 1: skip bg model instance
                val instance = instances!!.get(i)
                if (instance != null) {
                    val materials = instance.materials
                    for (j in 0..materials.size - 1) {
                        val mat = instance.materials.get(j)
                        mat.clear()
                        mat.set(assets!!.get<Model>(currentMaterial, Model::class.java).materials.get(0))

                    }
                }

            }
        }
    }

    private fun getVoxelData(voxpath: String, defaultRGBAVoxPalette: IntArray, voxname: String): VoxelData? {
        val vxf1 = Gdx.files.internal(voxpath + voxname)
        return VoxReader.fromMagica(vxf1.toBinaryReader(), defaultRGBAVoxPalette)
    }

    private fun getRGBAVoxelPalette(voxpath: String, voxpalettename: String): IntArray {
        val vxf1 = Gdx.files.internal(voxpath + voxpalettename)
        val pixmap = Pixmap(vxf1)
        val res = IntArray(256)
        for (i in 0..255) {
            res[i] = pixmap.getPixel(i, 0)
        }
        return res
    }


    /**
     * from https://github.com/libgdx/libgdx/wiki/Taking-a-Screenshot
     */
    internal fun takeScreenShot() {
        val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)
        val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
        BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
        PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap)
        pixmap.dispose()
    }

    private fun addWhiteBackground(modelBuilder: ModelBuilder) {
        val bgSize=500f
        val bgMaterial = Material(ColorAttribute.createDiffuse(Color(1.0f, 1.0f, 1.0f, 1.0f)))
        val bgMeshBuilder = modelBuilder.part(
                "bg",
                GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong(),
                bgMaterial)
        BoxShapeBuilder.build(bgMeshBuilder, 16f, 64f, 16f,bgSize, 10f, bgSize) //a large white background
    }

    private fun addVoxModelPart(modelBuilder: ModelBuilder, vox: VoxelData) {
        for (icolor in 0..vox.nColors - 1) {
            val curVoxData = vox.dataPerColor[icolor]
            val material = Material(ColorAttribute.createDiffuse(Color(curVoxData.voxelrgbacolor)))
            //TODO check which vertexatributes to use
            val meshBuilder = modelBuilder.part(
                    "color" + icolor,
                    GL20.GL_TRIANGLES,
                    (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong(),
                    material)
            for (iv in 0..curVoxData.nvoxels - 1) {

                val x = curVoxData.x[iv] * boxwidth
                val y = curVoxData.y[iv] * boxheight
                val z = curVoxData.z[iv] * boxdepth

                BoxShapeBuilder.build(meshBuilder, x, y, z, boxwidth, boxheight, boxdepth)

            }
        }
    }

    override fun render() {
        if (loading && assets!!.update()) {
            loading = false
            onLoaded()
        }
        //Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        modelBatch.begin(cam)
        //TODO take a look at https://github.com/libgdx/libgdx/wiki/ModelBatch#sorting-render-calls
        modelBatch.render(instances!!, lights)
        modelBatch.end()
        controller.update()

        spriteBatch.begin()
        font.draw(spriteBatch, "fps: " + Gdx.graphics.framesPerSecond + "camdir:" + cam.direction.toString(), 0f, 20f)
        spriteBatch.end()
    }


    override fun resize(width: Int, height: Int) {
        spriteBatch.projectionMatrix.setToOrtho2D(0f, 0f, width.toFloat(), height.toFloat())
        cam.viewportWidth = width.toFloat()
        cam.viewportHeight = height.toFloat()
        cam.update()
    }

    override fun dispose() {
        modelBatch.dispose()
        assets!!.dispose()
        assets = null
        models = null
        instances = null
    }

    companion object {
        internal val boxwidth = 1f
        internal val boxheight = 1f
        internal val boxdepth = 1f
    }
}
