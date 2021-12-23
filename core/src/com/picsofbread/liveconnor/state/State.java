package com.picsofbread.liveconnor.state;

public abstract class State {

    public boolean hasBeenCreated = false;

    public abstract void create();
    public abstract void render();
    public abstract void resize(int width, int height);
    public abstract void dispose();
}
