package com.mygdx.game.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.mygdx.game.voxel.reader.BinaryReader;
import com.mygdx.game.voxel.reader.VoxReader;
import com.mygdx.game.voxel.reader.VoxelData;

import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * Created by daely on 11/28/2016.
 */
//TODO add three possible rotations
//TODO generate face background programmatically
    //TODO separate hair and hat block: each block that can have variable color should be independent
    //TODO single block depth for faces for saving memory: in fondo sono delle maschere!!
    //TODO put fragment in framelayout below another view
    //BLOCKS: eye block (with color)
    //??guancette rosse block
    //face shape block (with color)
    //bocca (with color)
    //ciglia (with color)
    //tatuaggio in faccia (with color)
    //capelli (uomo/donna
    //cappelli trecce
    //capelli basette
    //vestito (uomo donna) e colore
    //mano che tiene cose
    //ali bloc
    //cravatta block (con colore)
    //orecchini block (con colore)
    //occhiali block
    //animaletto block
    //cappello block

public class FaceParts {
    String basePath;
    ArrayList<String> partNames;
    ArrayList<Integer> partCounts;
    MersenneTwisterFast rand;
    int idDigestMode;
    public FaceParts(String basePath) {
        this.basePath=basePath;
        rand = null;
        idDigestMode=0;
        partCounts=new ArrayList<>();
        partNames = new ArrayList<>();
    }

    /**
     * search for file names from 1 to count
     * @param name
     * @param count
     */
    public void addPart(String name,int count) {
        partNames.add(name);
        partCounts.add(count);
    }
    public int nParts() { return  partCounts.size();}
    public VoxelData[] getFaceParts(String id) {
        int[] selectedParts = getFacePartIdxFromId(id);
        if (selectedParts == null) return null;
        int nparts=nParts();
        VoxelData[] res=new VoxelData[nparts];
        for (int i = 0; i < nparts; i++) {
            String pn=partNames.get(i);
            int[] defaultRGBAVoxPalette= getRGBAVoxelPalette(basePath,pn+"_palette.png");
            if(defaultRGBAVoxPalette==null) return null;
            VoxelData vox = getVoxelData(basePath, defaultRGBAVoxPalette,pn+selectedParts[i]+".vox");
            if(vox==null) return null;
            res[i]=vox;
        }
        return res;
    }

    private int[] getFacePartIdxFromId(String id) {
        byte[] digest=null;
        switch (idDigestMode) {
            case 0:
                digest= digestVerbatim(id);
                break;
            case 1:
                digest= digestMD5(id);
                break;
            case 2:
                digest= digestSHA256(id);
                break;
        }
        if(digest==null) return null;
        int[] seed=byteToInt(digest);
        MersenneTwisterFast mtf= new MersenneTwisterFast(seed);
        int nparts_=nParts();
        int[] selectedParts=new int[nparts_];
        for (int i = 0; i < nparts_; i++) {
            selectedParts[i]=mtf.nextInt(partCounts.get(i));
        }
        return selectedParts;
    }

    private int[] byteToInt(byte[] bytes) {
        int len=bytes.length;
       int[] ints=new int[len];
        for (int i = 0; i < len; i++) {
            ints[i]=bytes[i];
        }
        return ints;
    }

    private byte[] digestVerbatim(String id) {
        try {
            return id.getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }
    }
    private byte[] digestMD5(String id) {
        byte[] digest=null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            digest = md.digest(id.getBytes("UTF-8"));
        } catch (Exception e) {
            return null;
        }
        return digest;
    }
    private byte[] digestSHA256(String id) {
        byte[] digest=null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(id.getBytes("UTF-8"));
        } catch (Exception e) {
            return null;
        }
        return digest;
    }
    private VoxelData getVoxelData(String voxpath, int[] defaultRGBAVoxPalette, String voxname) {
        FileHandle vxf1= Gdx.files.internal(voxpath+ voxname);
        return VoxReader.fromMagica(new BinaryReader(vxf1),defaultRGBAVoxPalette);
    }
    private int[] getRGBAVoxelPalette(String voxpath, String voxpalettename) {
        FileHandle vxf1= Gdx.files.internal(voxpath+ voxpalettename);
        Pixmap pixmap= new Pixmap(vxf1);
        int[] res= new int[256];
        for (int i = 0; i < 256; i++) {
            res[i]=pixmap.getPixel(i,0);
        }
        return res;
    }
}
