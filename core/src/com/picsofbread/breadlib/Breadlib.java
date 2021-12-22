package com.picsofbread.breadlib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.picsofbread.breadlib.structs.Texture2D;
import com.picsofbread.liveconnor.ILogCallbackProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Breadlib {

    private ILogCallbackProcessor logCallback;

    public Breadlib(ILogCallbackProcessor logCallback) {
        this.logCallback = logCallback;
    }

    // region Extra
    
    public void Log(String message) {
        logCallback.logThis(message);
    }
    // endregion

    // region Internal state
    private SpriteBatch batch; // we use a spritebatch internally to render all of our stuff in 2d mode
    private OrthographicCamera defaultCamera2D;

    
    public void Create() { // has to be called first!
        batch = new SpriteBatch();

        defaultCamera2D = new OrthographicCamera();
        defaultCamera2D.setToOrtho(false, 800, 480);
    }

    
    public boolean IsThreadOk() {
        return Thread.currentThread().getName().toLowerCase().contains("glthread");
    }
    // endregion

    // region Core
    
    public void ClearBackground(Color color) {
        ScreenUtils.clear(color);
    }

    
    public void ClearBackground(float r, float g, float b, float a) {
        ScreenUtils.clear(r, g, b, a);
    }

    
    public void BeginDrawing() {
        BeginMode2D(defaultCamera2D);
    }

    
    public void EndDrawing() {
        EndMode2D();
    }

    
    public void BeginMode2D(OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    
    public void EndMode2D() {
        batch.end();
    }

    // keyboard handling. this is wrong.
    
    public boolean IsKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }

    
    public boolean IsKeyDown(int key) {
        return IsKeyPressed(key);
    }

    
    public boolean IsKeyReleased(int key) {
        return !IsKeyPressed(key);
    }

    
    public boolean IsKeyUp(int key) {
        return IsKeyReleased(key);
    }

    
    public int GetKeyPressed() throws IllegalAccessException { // REFLECTION!?!?!?
        for (Field f : Input.Keys.class.getDeclaredFields()) {
            if (IsKeyPressed(f.getInt(null)))
                return f.getInt(null);
        }

        return 0;
    }

    
    public String GetCharPressed() throws IllegalAccessException {
        return Input.Keys.toString(GetKeyPressed());
    }

    
    public boolean IsGamepadAvailable(int gamepad) {
        return false;
    }

    
    public String GetGamepadName(int gamepad) {
        return "gamepad";
    }

    
    public boolean IsGamepadButtonPressed(int gamepad, int button) {
        return false;
    }

    
    public boolean IsGamepadButtonDown(int gamepad, int button) {
        return false;
    }

    
    public boolean IsGamepadButtonReleased(int gamepad, int button) {
        return false;
    }

    
    public boolean IsGamepadButtonUp(int gamepad, int button) {
        return false;
    }

    
    public int GetGamepadButtonPressed() {
        return 0;
    }

    
    public int GetGamepadAxisCount(int gamepad) {
        return 0;
    }

    
    public float GetGamepadAxisMovement(int gamepad, int axis) {
        return 0f;
    }

    
    public int SetGamepadMappings(String mappings) {
        return 0;
    }

    // Input-related functions: mouse
    
    public boolean IsMouseButtonPressed(int button) {
        return false;
    }

    
    public boolean IsMouseButtonDown(int button) {
        return false;
    }

    
    public boolean IsMouseButtonReleased(int button) {
        return false;
    }

    
    public boolean IsMouseButtonUp(int button) {
        return false;
    }

    
    public int GetMouseX() {
        return Gdx.input.getX();
    }

    
    public int GetMouseY() {
        return Gdx.input.getY();
    }

    
    public Vector2 GetMousePosition() {
        return new Vector2(GetMouseX(), GetMouseY());
    }

    
    public Vector2 GetMouseDelta() {
        return new Vector2(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
    }

    
    public void SetMousePosition(int x, int y) {
        Gdx.input.setCursorPosition(x, y);
    }

    
    public void SetMouseOffset(int offsetX, int offsetY) {
        int posX = GetMouseX() + offsetX;
        int posY = GetMouseY() + offsetY;
        SetMousePosition(posX, posY);
    }

    
    public void SetMouseScale(float scaleX, float scaleY) {
        // what does this function even do
        // no really, raylib's source says it's useful for rendering to different size targets
        // but why. it's a point in space. how does it get scaled.
        // FIXME?
    }

    
    public float GetMouseWheelMove() {
        // FIXME: not till we rewrite this with an InputProcessor
        return 0f;
    }

    
    public void SetMouseCursor(int cursor) {
        // FIXME: I don't care
    }

    // FIXME: Input-related functions: touch
    
    public int GetTouchX() {
        return GetMouseX(); // FIXME: is this the same thing?
    }

    
    public int GetTouchY() {
        return GetMouseY();
    }

    
    public Vector2 GetTouchPosition(int index) {
        return GetMousePosition();
    }

    
    public int GetTouchPointId(int index) {
        return 0;
    }

    
    public int GetTouchPointCount() {
        return 0;
    }
    // endregion

    // region Textures
    
    public Texture2D LoadTexture(String filename) {
        Texture2D tex = new Texture2D();
        tex.LoadFromFile(filename);
        return tex;
    }

    
    public void DrawTexture(Texture2D texture, int posX, int posY, Color tint) {
        texture.SetPosition(posX, posY);
        texture.Draw(batch);
    }
    // endregion
}
