package com.mygdx.game.voxel.reader

/**
 * Created by daely on 11/23/2016.
 * struct in original C# code
 */
class VoxelDataPerColor(nvoxels: Int, var voxelrgbacolor: Int) {
    var nvoxels: Int = 0
    var x: ByteArray
    var y: ByteArray
    var z: ByteArray

    init {
        this.nvoxels = 0
        x = ByteArray(nvoxels)
        y = ByteArray(nvoxels)
        z = ByteArray(nvoxels)
    }

    fun addVoxel(xp: Byte, yp: Byte, zp: Byte) {
        val i = nvoxels++
        x[i] = xp
        y[i] = yp
        z[i] = zp
    }
}
