package com.picsofbread.breadlib.structs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Texture2D {
    int width;
    int height;

    private Texture texBackend;
    private Sprite sprBackend;

    public void LoadFromFile(String filename) {
        texBackend = new Texture(Gdx.files.internal(filename)); // FIXME: don't always use files.internal
        sprBackend = new Sprite(texBackend);
    }

    public void Draw(SpriteBatch batch) {
        sprBackend.draw(batch);
    }

    public void SetPosition(int x, int y) {
        sprBackend.setPosition(x, y);
    }
}
