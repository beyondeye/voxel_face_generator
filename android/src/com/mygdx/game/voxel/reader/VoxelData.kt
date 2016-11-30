package com.mygdx.game.voxel.reader

import android.util.SparseArray

import java.lang.reflect.Array
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by daely on 11/24/2016.
 */

class VoxelData internal constructor(val sizex: Int, val sizey: Int, val sizez: Int, buffer: MagicaVoxelDataBuffer, colors: IntArray) {
    val nvoxels: Int
    var dataPerColor: ArrayList<VoxelDataPerColor>

    init {
        this.nvoxels = buffer.nvoxels
        val cmp = arrayOfNulls<VoxelDataPerColor>(256)
        dataPerColor = ArrayList<VoxelDataPerColor>()
        for (icolor in 0..255) {
            var vdpc: VoxelDataPerColor? = null
            val nv = buffer.colorcount[icolor].toInt() //n.voxel with this color
            if (nv > 0) {
                vdpc = VoxelDataPerColor(nv, icolor,colors[icolor])
                dataPerColor.add(vdpc)
            }
            cmp[icolor] = vdpc
        }
        //now fill it
        for (i in 0..nvoxels - 1) {
            val c = buffer.color[i].toInt()
            cmp[c]!!.addVoxel(buffer.x[i], buffer.y[i], buffer.z[i])
        }
    }

    val nColors: Int
        get() = dataPerColor.size
}
