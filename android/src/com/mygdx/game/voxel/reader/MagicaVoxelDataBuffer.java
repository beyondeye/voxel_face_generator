package com.mygdx.game.voxel.reader;

/**
 * Created by daely on 11/23/2016.
 */
class MagicaVoxelDataBuffer //struct in original C# code
{
    public int nvoxels;
    public byte[] x;
    public byte[] y;
    public byte[] z;
    public short[] color;
    public short[] colorcount;
    public MagicaVoxelDataBuffer(int nvoxels) {
        this.nvoxels=nvoxels;
        x=new byte[nvoxels];
        y=new byte[nvoxels];
        z=new byte[nvoxels];
        color=new short[nvoxels];
        colorcount=new short[256];
    }

    public void readVoxel(BinaryReader stream,int i, Boolean subsample)
    {
        x[i] = (byte) (subsample ? stream.ReadByte() / 2 : stream.ReadByte());
        y[i] = (byte) (subsample ? stream.ReadByte() / 2 : stream.ReadByte());
        z[i] = (byte) (subsample ? stream.ReadByte() / 2 : stream.ReadByte());
        short c = ((short) ((stream.ReadByte()-1)&0xff));
        color[i]= c;
        colorcount[c]++;
    }
}
