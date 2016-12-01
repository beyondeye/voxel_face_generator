package com.mygdx.game.voxel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.mygdx.game.voxel.reader.BinaryReader
import com.mygdx.game.voxel.reader.VoxIO
import com.mygdx.game.voxel.reader.VoxelData

import java.security.MessageDigest
import java.util.ArrayList

/**
 * Created by daely on 11/28/2016.
 */
//TODO add three possible rotations
//TODO generate face background programmatically
//TODO separate hair and hat block: each block that can have variable color should be independent
//TODO single block depth for faces for saving memory: in fondo sono delle maschere!!
//TODO put fragment in framelayout below another view
//BLOCKS: eye block (with color)
//??guancette rosse block
//face shape block (with color)
//bocca (with color)
//ciglia (with color)
//tatuaggio in faccia (with color)
//capelli (uomo/donna
//cappelli trecce
//capelli basette
//vestito (uomo donna) e colore
//mano che tiene cose
//ali bloc
//cravatta block (con colore)
//orecchini block (con colore)
//occhiali block
//animaletto block
//cappello block

class FaceParts(val voxels:Array<VoxelData>,val rot:Pair<Float,Float>)

class FacePartsWeights internal constructor(partCounts:List<Int>,partNamesIdxs: MutableMap<String,Int>) {
    private val partNamesIdxs: MutableMap<String,Int>
    val partWeights: List<Array<Int>>
    val partWeightsTot: MutableList<Int>
    init {
        this.partNamesIdxs=partNamesIdxs
        partWeights = partCounts.map { count-> Array<Int>(count){1} }
        partWeightsTot = partCounts.map { it }.toMutableList()
    }
    fun setWeight(name:String, newWeight:Int,firstIdx:Int,lastIdx_:Int?=null){
        val lastIdx= lastIdx_?:firstIdx
        val partIdx=partNamesIdxs.get(name) ?:return
        val weightsVec=partWeights[partIdx]
        for(i in firstIdx..lastIdx) {
            weightsVec[i]=newWeight
        }
        partWeightsTot[partIdx]=weightsVec.sum()
    }
}

class FacePartsGenerator(internal var basePath: String) {
    companion object {
        private val phiThetaRotPartName = "PhiThetaRotation"
    }
    private val partNamesIdxs: MutableMap<String,Int>
    internal val partNames: MutableList<String>
    internal val partCounts: MutableList<Int>
    internal val rots:MutableList<Pair<Float,Float>>
    private var rand: MersenneTwisterFast? = null
    private var idDigestMode: Int = 0


    init {
        rand = null
        idDigestMode = 1 //MD5
        partCounts = mutableListOf()
        partNames = mutableListOf()
        partNamesIdxs= hashMapOf()
        rots=mutableListOf()
        addPart(phiThetaRotPartName,0)
    }

    /**
     * search for file names from 1 to count
     * @param name
     * *
     * @param count
     */
    fun addPart(name: String, count: Int) {
        val nxtidx=partNames.count()
        partNamesIdxs.put(name,nxtidx)
        partNames.add(name)
        partCounts.add(count)
    }




    fun addPhiThetaRot(phiRot:Float,thetaRot:Float) {
        rots.add(Pair(phiRot,thetaRot))
        ++partCounts[0]
    }

    fun nParts(): Int {
        return partCounts.size
    }

    fun getFaceParts(id: String, w:FacePartsWeights): FaceParts? {
        val selectedParts = getFacePartIdxFromId(id,w) ?: return null
        val nparts = nParts()
        val rot=rots[selectedParts[0]] //first part is always the rotation
        val voxels = Array<VoxelData>(nparts-1) { ipart ->
            val pn = partNames[ipart+1] //skip the first ipart that is the rotation
            val defaultRGBAVoxPalette = getRGBAVoxelPalette(basePath, pn + "_palette.png") ?: return null
            val vox = getVoxelData(basePath, defaultRGBAVoxPalette, pn + selectedParts[ipart+1] + ".vox") ?: return null
            vox
        }
        return FaceParts(voxels,rot)
    }
    fun createWeights()= FacePartsWeights(partCounts,partNamesIdxs)
    private fun getFacePartIdxFromId(id: String,w:FacePartsWeights): IntArray? {
        var digest: ByteArray? = null
        when (idDigestMode) {
            0 -> digest = digestVerbatim(id)
            1 -> digest = digestMD5(id)
            2 -> digest = digestSHA256(id)
        }
        if (digest == null) return null
        val seed = byteToInt(digest)
        val mtf = MersenneTwisterFast(seed)
        val nparts_ = nParts()
        val selectedParts = IntArray(nparts_)
        for (i in 0..nparts_ - 1) {
            selectedParts[i] = selectPart(i,mtf,w)
        }
        return selectedParts
    }
    private fun selectPart(partIdx:Int,mtf:MersenneTwisterFast,w:FacePartsWeights):Int {
        //return mtf.nextInt(partCounts[partIdx])
        return indexFromWeights(w.partWeights[partIdx],w.partWeightsTot[partIdx],mtf)
    }
    private fun indexFromWeights(weights: Array<Int>,total:Int, rndgen: MersenneTwisterFast): Int {
//        var total = 0.0
//        for (i in weights.indices) {
//            total += weights[i].toDouble()
//        }
        var position =  rndgen.nextInt(total)
        for (i in weights.indices) {
            position -= weights[i]
            if (position <= 0) return i
        }
        throw Error("Funny probabilities array!")
    }
    private fun byteToInt(bytes: ByteArray): IntArray {
        val len = bytes.size
        val ints = IntArray(len)
        for (i in 0..len - 1) {
            ints[i] = bytes[i].toInt()
        }
        return ints
    }

    private fun digestVerbatim(id: String): ByteArray? =
            try {
                id.toByteArray(charset("UTF-8"))
            } catch (e: Exception) {
                null
            }

    private fun digestMD5(id: String): ByteArray? =
            try {
                val md = MessageDigest.getInstance("MD5")
                md.digest(id.toByteArray(charset("UTF-8")))
            } catch (e: Exception) {
                null
            }

    private fun digestSHA256(id: String): ByteArray? =
            try {
                val md = MessageDigest.getInstance("SHA-256")
                md.digest(id.toByteArray(charset("UTF-8")))
            } catch (e: Exception) {
                null
            }

    private fun getVoxelData(voxpath: String, defaultRGBAVoxPalette: IntArray, voxname: String): VoxelData? {
        val vxf1 = Gdx.files.internal(voxpath + voxname)
        return VoxIO.fromMagica(vxf1.toBinaryReader(), defaultRGBAVoxPalette)
    }

    private fun getRGBAVoxelPalette(voxpath: String, voxpalettename: String): IntArray? {
        val vxf1 = Gdx.files.internal(voxpath + voxpalettename)
        val pixmap = Pixmap(vxf1)
        val res = IntArray(256)
        for (i in 0..255) {
            res[i] = pixmap.getPixel(i, 0)
        }
        return res
    }

}
