package com.mygdx.game.voxel.reader

/**
 * Created by daely on 11/23/2016.
 * it was defined as struct  in original C# code
 */
internal class MagicaVoxelDataBuffer(var nvoxels: Int) {
    var x: ByteArray
    var y: ByteArray
    var z: ByteArray
    var color: ShortArray
    var colorcount: ShortArray

    init {
        x = ByteArray(nvoxels)
        y = ByteArray(nvoxels)
        z = ByteArray(nvoxels)
        color = ShortArray(nvoxels)
        colorcount = ShortArray(256)
    }

    fun readVoxel(stream: BinaryReader, i: Int, subsample: Boolean) {
        if(subsample) throw IllegalArgumentException("subsample not supported anymore")
        x[i] =  stream.ReadByte()
        y[i] =  stream.ReadByte()
        z[i] =  stream.ReadByte()
        val c = (stream.ReadByte() - 1 and 0xff).toShort()
        color[i] = c
        colorcount[c.toInt()]++
    }
}