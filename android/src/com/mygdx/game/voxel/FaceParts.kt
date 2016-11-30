package com.mygdx.game.voxel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.mygdx.game.voxel.reader.BinaryReader
import com.mygdx.game.voxel.reader.VoxReader
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

class FaceParts(internal var basePath: String) {
    internal var partNames: ArrayList<String>
    internal var partCounts: ArrayList<Int>
    internal var rand: MersenneTwisterFast? = null
    internal var idDigestMode: Int = 0

    init {
        rand = null
        idDigestMode = 1 //MD5
        partCounts = ArrayList<Int>()
        partNames = ArrayList<String>()
    }

    /**
     * search for file names from 1 to count
     * @param name
     * *
     * @param count
     */
    fun addPart(name: String, count: Int) {
        partNames.add(name)
        partCounts.add(count)
    }

    fun nParts(): Int {
        return partCounts.size
    }

    fun getFaceParts(id: String): Array<VoxelData>? {
        val selectedParts = getFacePartIdxFromId(id) ?: return null
        val nparts = nParts()
        val res = Array<VoxelData>(nparts) { ipart ->
            val pn = partNames[ipart]
            val defaultRGBAVoxPalette = getRGBAVoxelPalette(basePath, pn + "_palette.png") ?: return null
            val vox = getVoxelData(basePath, defaultRGBAVoxPalette, pn + selectedParts[ipart] + ".vox") ?: return null
            vox
        }
        return res
    }

    private fun getFacePartIdxFromId(id: String): IntArray? {
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
            selectedParts[i] = mtf.nextInt(partCounts[i])
        }
        return selectedParts
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
        return VoxReader.fromMagica(BinaryReader(vxf1), defaultRGBAVoxPalette)
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
