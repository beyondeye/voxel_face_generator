package com.mygdx.game.voxel.reader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StringBuilder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daely on 11/23/2016.
 */
public class BinaryReader {
    private FileHandle fh;
    private DataInputStream rs;
    int length;
    int pos;
    public BinaryReader(FileHandle fh) {
        this.fh=fh;
        byte[] bytes=fh.readBytes();
        length=bytes.length;
        pos=0;
        this.rs= new DataInputStream(new ByteArrayInputStream(bytes));
    }
    public byte ReadByte() {
        try {
            pos+=1;
            return (byte) rs.read();
        } catch (IOException e) {
            return -1;
        }
    }

    public int ReadInt32() {
        try {
            pos+=4;
            return rs.readInt();
        } catch (IOException e) {
            return -1;
        }
    }

    public String ReadChars(int i) {
        StringBuilder sb = new StringBuilder(i);
        try {
            pos+=i;
            while (--i >= 0) {
                sb.append(rs.readByte());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public byte[] ReadBytes(int i) {
        pos+=i;
        byte[] res=new byte[i];
        try {
            int actualRead=rs.read(res,0,i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public void skipBytes(int i) {
        pos+=i;
        try {
            rs.skipBytes(i);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getPosition() {
        return pos;
    }

    public int getLength() {
        return length;
    }
}
