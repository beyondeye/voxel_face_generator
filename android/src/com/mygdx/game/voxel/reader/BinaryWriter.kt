package com.mygdx.game.voxel.reader

import java.io.DataOutputStream
import java.io.IOException


/**
 * Created by daely on 11/23/2016.
 */
class BinaryWriter(private val ostream:DataOutputStream) {
    var position: Int = 0

    fun writeByte(b:Int) {
        try {
            position += 1
            return ostream.writeByte(b)
        } catch (e: IOException) {
        }

    }


    /**
     writeInt from DataOutputStream write byte in an order not matching [BinaryReader.readInt32]
    public final void writeInt(int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);
        incCount(4);
    }
    */
    fun writeInt32(i:Int) {
        try {
            position += 4
            ostream.writeByte((i ushr 0 and 0xff))
            ostream.writeByte((i ushr 8 and 0xff))
            ostream.writeByte((i ushr 16 and 0xff))
            ostream.writeByte((i ushr 24 and 0xff))
        } catch (e: IOException) {
        }
    }
    fun writeShort(s:Short) {
        try {
            position += 2
            ostream.writeByte((s.toInt() ushr 0 and 0xff))
            ostream.writeByte((s.toInt() ushr 8 and 0xff))
        } catch (e: IOException) {
        }
    }
    fun writeChars(s:String){
        val bytes=s.toByteArray(Charsets.UTF_8)
        position+=bytes.size
        bytes.forEach { ostream.writeByte(it.toInt()) }
    }

    fun writeBytes(bytes:ByteArray)  {
        position += bytes.size
        bytes.forEach { ostream.writeByte(it.toInt()) }
    }
}
