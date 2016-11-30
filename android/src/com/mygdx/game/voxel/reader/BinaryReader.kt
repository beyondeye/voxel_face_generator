package com.mygdx.game.voxel.reader

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.StringBuilder

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.InputStream

/**
 * Created by daely on 11/23/2016.
 */
class BinaryReader(fh: FileHandle) {
    private val rs: DataInputStream
    var length: Int = 0
        internal set
    var position: Int = 0
        internal set

    init {
        val bytes = fh.readBytes()
        length = bytes.size
        position = 0
        this.rs = DataInputStream(ByteArrayInputStream(bytes))
    }

    fun readByte(): Byte {
        try {
            position += 1
            return rs.read().toByte()
        } catch (e: IOException) {
            return -1
        }

    }

    fun readInt32(): Int {
        try {
            position += 4
            val ch1 = rs.read()
            val ch2 = rs.read()
            val ch3 = rs.read()
            val ch4 = rs.read()
            if (ch1 or ch2 or ch3 or ch4 < 0)
                throw EOFException()
            //            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
            return ch1 + (ch2 shl 8) + (ch3 shl 16) + (ch4 shl 24)
        } catch (e: IOException) {
            return -1
        }

    }

    fun readChars(i: Int): String {
        var i = i
        val sb = StringBuilder(i)
        try {
            position += i
            while (--i >= 0) {
                sb.append(rs.readByte().toChar())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    fun readBytes(i: Int): ByteArray {
        position += i
        val res = ByteArray(i)
        try {
            val actualRead = rs.read(res, 0, i)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return res
    }

    fun skipBytes(i: Int) {
        position += i
        try {
            rs.skipBytes(i)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
