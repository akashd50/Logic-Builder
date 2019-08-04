package com.akashd50.lb.logic;

import com.akashd50.lb.objects.Texture;
import java.util.HashMap;

public class TextureContainer {
    private HashMap<String, Texture> textures;
    public TextureContainer(){
        textures = new HashMap<>();
    }

    public void addTexture(Texture t){
        textures.put(t.getTag(), t);
    }

    public Texture getTexture(String tag){
        return textures.get(tag);
    }
}
