package com.mygdx.game.voxel.reader;

import android.util.SparseArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daely on 11/24/2016.
 */

public class VoxelData {
    public final int nvoxels;
    public final int sizex,sizey,sizez;
    public ArrayList<VoxelDataPerColor> dataPerColor;
    public VoxelData(int sizex, int sizey, int sizez, MagicaVoxelDataBuffer buffer, int[] colors) {
        this.nvoxels=buffer.nvoxels;
        this.sizex=sizex;
        this.sizey=sizey;
        this.sizez=sizez;
        VoxelDataPerColor[] cmp = new VoxelDataPerColor[256];
        dataPerColor= new ArrayList<>();
        for (int icolor = 0; icolor < 256; icolor++) {
            VoxelDataPerColor vdpc=null;
            int nv=buffer.colorcount[icolor]; //n.voxel with this color
            if(nv>0) {
                vdpc=new VoxelDataPerColor(nv,colors[icolor]);
                dataPerColor.add(vdpc);
            }
            cmp[icolor]=vdpc;
        }
        //now fill it
        for (int i = 0; i < nvoxels; i++) {
            int c=buffer.color[i];
            cmp[c].addVoxel(buffer.x[i],buffer.y[i],buffer.z[i]);
        }
    }
    public int getNColors() { return dataPerColor.size(); }
}
