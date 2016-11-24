package com.mygdx.game.voxel.reader;

/**
 * Created by daely on 11/23/2016.
 */

public class VoxelData {
    static final int xMaxSize = 32;
    static final int zMaxSize = 128;
    static final int yMaxSize = 32;
    public final int xsize,ysize,zsize;
    public VoxelData(int xsize,int ysize,int zsize) {
        this.xsize=xsize;
        this.ysize=ysize;
        this.zsize=zsize;
    }
    private int[] data = new int[xMaxSize * zMaxSize * yMaxSize]; //TODO hardcoded buffer size!! for fastest voxel data access??
    public void setVoxelColor(int x, int y, int z, int color) {
        // do not store this voxel if it lies out of range of the voxel chunk (32x128x32)
        if (x >= xMaxSize || y >= yMaxSize ||z >= zMaxSize) return;
        int voxelIdx = getVoxelIdx(x, y, z);
        data[voxelIdx]=color;
    }
    public int getVoxelColor(int x, int y, int z) {
        if (x >= xMaxSize || y >= yMaxSize ||z >= zMaxSize) throw new IllegalArgumentException();
        int voxelIdx = getVoxelIdx(x, y, z);
        return data[voxelIdx];
    }
    int getVoxelIdx(int x, int y, int z) {
        return x+ z * xMaxSize + y*xMaxSize*zMaxSize;
    }
}