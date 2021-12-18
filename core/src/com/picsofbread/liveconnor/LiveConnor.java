package com.picsofbread.liveconnor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.picsofbread.breadlib.Breadlib;
import com.picsofbread.breadlib.structs.Texture2D;
import com.picsofbread.liveconnor.script.ScriptWrapper;

import javax.script.ScriptException;

public class LiveConnor extends ApplicationAdapter {
	SpriteBatch batch;
	Texture2D img;

	FitViewport gameViewport;
	Stage codeStage;
	VisTextArea textArea;
	String code;
	boolean codeIsDirty;

	ScriptWrapper scriptingEngine;

	ILogCallbackProcessor logCallback;

	Breadlib breadlibInstance;

	@Override
	public void create () {
		VisUI.load();

		batch = new SpriteBatch();
		img = new Texture2D();
		img.LoadFromFile("badlogic.jpg");

		gameViewport = new FitViewport(960, 900);
		codeStage = new Stage(gameViewport);

		breadlibInstance = new Breadlib(logCallback);
		breadlibInstance.create();

		scriptingEngine = new ScriptWrapper();
		scriptingEngine.alias("badlogic_logo", img);
		scriptingEngine.alias("bl", breadlibInstance);
	}

	@Override
	public void resize(int width, int height) {
		gameViewport.setScreenX(width / 2);
	}

	@Override
	public void render () {
		try {
			scriptingEngine.runString("draw();");
		} catch (ScriptException e) {
			// render function isn't declared yet. this is fine.
			logCallback.logThis("no render function!");
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		VisUI.dispose();
	}

	public void codeHasChanged(String code) {
		this.code = code;
		this.codeIsDirty = true;
	}

	public void executeCode() {
		try {
			scriptingEngine.runString(code);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
