package com.mygdx.game.voxel.reader

import com.badlogic.gdx.utils.StringBuilder

import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException


/**
 * Created by daely on 11/23/2016.
 */
class BinaryReader(val streamLen:Int, private val istream:DataInputStream) {
    var position: Int = 0

    fun readByte(): Byte {
        try {
            position += 1
            return istream.read().toByte()
        } catch (e: IOException) {
            return -1
        }

    }

    fun readInt32(): Int {
        try {
            position += 4
            val ch1 = istream.read()
            val ch2 = istream.read()
            val ch3 = istream.read()
            val ch4 = istream.read()
            if (ch1 or ch2 or ch3 or ch4 < 0)
                throw EOFException()
            //            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
            return ch1 + (ch2 shl 8) + (ch3 shl 16) + (ch4 shl 24)
        } catch (e: IOException) {
            return -1
        }

    }

    fun readChars(count: Int): String {
        var i = count
        val sb = StringBuilder(i)
        try {
            position += i
            while (--i >= 0) {
                sb.append(istream.readByte().toChar())
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
            val actualRead = istream.read(res, 0, i)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return res
    }

    fun skipBytes(i: Int) {
        position += i
        try {
            istream.skipBytes(i)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
