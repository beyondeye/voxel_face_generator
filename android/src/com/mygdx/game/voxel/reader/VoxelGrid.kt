package com.mygdx.game.voxel.reader

/**
 * Created by daely on 11/23/2016.
 */

class VoxelGrid internal constructor(val xsize: Int, val ysize: Int, val zsize: Int, voxelBuffer: MagicaVoxelDataBuffer, voxColors: IntArray) {
    init {
        // now push the voxel data into our voxel chunk structure
        for (i in 0..voxelBuffer.nvoxels - 1) {
            val c = voxColors[voxelBuffer.color[i].toInt()]
            setVoxelColor(voxelBuffer.x[i].toInt(), voxelBuffer.y[i].toInt(), voxelBuffer.z[i].toInt(), c)
        }
    }

    private val data = IntArray(xMaxSize * zMaxSize * yMaxSize) //TODO hardcoded buffer size!! for fastest voxel data access??
    fun setVoxelColor(x: Int, y: Int, z: Int, color: Int) {
        // do not store this voxel if it lies out of range of the voxel chunk (32x128x32)
        if (x >= xMaxSize || y >= yMaxSize || z >= zMaxSize) return
        val voxelIdx = getVoxelIdx(x, y, z)
        data[voxelIdx] = color
    }

    fun getVoxelColor(x: Int, y: Int, z: Int): Int {
        if (x >= xMaxSize || y >= yMaxSize || z >= zMaxSize) throw IllegalArgumentException()
        val voxelIdx = getVoxelIdx(x, y, z)
        return data[voxelIdx]
    }

    internal fun getVoxelIdx(x: Int, y: Int, z: Int): Int {
        return x + z * xMaxSize + y * xMaxSize * zMaxSize
    }

    companion object {
        internal val xMaxSize = 32
        internal val zMaxSize = 128
        internal val yMaxSize = 32
    }
}
