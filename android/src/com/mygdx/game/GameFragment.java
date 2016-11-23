package com.mygdx.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.mygdx.game.voxel.VoxelTest;

public class GameFragment extends AndroidFragmentApplication{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // return the GLSurfaceView on which libgdx is drawing game stuff
//        return initializeForView(new MyGdxGame());
        return initializeForView(new VoxelTest());
//        return initializeForView(new MaterialTest());
//        return initializeForView(new ShaderTest());
//        return initializeForView(new ShaderCollectionTest());
//        return initializeForView(new ModelTest());
//        return initializeForView(new ShadowMappingTest());
//        return initializeForView(new TextureRegion3DTest());
    }
}

