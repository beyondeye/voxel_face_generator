package com.mygdx.game.voxel.reader;

/**
 * Created by daely on 11/23/2016.
 */
public class VoxelDataPerColor //struct in original C# code
{
    public int nvoxels;
    public byte[] x;
    public byte[] y;
    public byte[] z;
    public int voxelrgbacolor;
    public VoxelDataPerColor(int nvoxels,int rgbacolor) {
        this.nvoxels=0;
        x=new byte[nvoxels];
        y=new byte[nvoxels];
        z=new byte[nvoxels];
        this.voxelrgbacolor =rgbacolor;
    }

    public void addVoxel(byte xp, byte yp, byte zp) {
        int i=nvoxels++;
        x[i]=xp;
        y[i]=yp;
        z[i]=zp;
    }
}
