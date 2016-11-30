package com.mygdx.game.voxel.reader

/**
 * Created by daely on 11/23/2016.
 * struct in original C# code
 */
class VoxelDataPerColor(nvoxels: Int, val voxelColorIndex:Int,val voxelrgbacolor: Int) {
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

    private class Voxcoords(val xx:Int,val zz:Int)
    /**
     * function that sort VoxelDataPerColor according to increasing y and return a int[] array with number of voxel for each distinct y
     */
    fun sortY():List<Int> {
        val cmap = hashMapOf<Int,MutableList<Voxcoords>>()
        for (i in x.indices) {
            val cury=y[i].toInt()
            var curyCoordList=cmap[cury]
            if(curyCoordList==null) {
                curyCoordList= mutableListOf<Voxcoords>()
                cmap.set(cury,curyCoordList)
            }
            curyCoordList.add(Voxcoords(x[i].toInt(),z[i].toInt()))
        }
        var ylist=cmap.keys.sortedByDescending { it }
        var ivox=0
        var ycounts= mutableListOf<Int>()
        ylist.forEach { cury->
            val curYCoordList=cmap[cury]!!
            ycounts.add(curYCoordList.size)
            curYCoordList.forEach {
                x[ivox]= it.xx.toByte()
                y[ivox]= cury.toByte()
                z[ivox]= it.zz.toByte()
                ++ivox
            }
        }
        return  ycounts
    }
}
