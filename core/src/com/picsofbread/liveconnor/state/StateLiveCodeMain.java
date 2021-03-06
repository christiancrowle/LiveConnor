package com.picsofbread.liveconnor.state;

import com.picsofbread.breadlib.Breadlib;
import com.picsofbread.liveconnor.ILogCallbackProcessor;
import com.picsofbread.liveconnor.script.ScriptWrapper;

import javax.script.ScriptException;

public class StateLiveCodeMain extends State {

    public String code;
    public boolean codeIsDirty;
    public boolean shouldExecuteCode = false;

    public ScriptWrapper scriptingEngine;

    public ILogCallbackProcessor logCallback;

    public Breadlib breadlibInstance;

    @Override
    public void create() {
        breadlibInstance = new Breadlib(logCallback);
        breadlibInstance.Create();

        scriptingEngine = new ScriptWrapper();
        scriptingEngine.alias("bl", breadlibInstance);

        codeHasChanged(""); // just so the code isn't null lmao
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if (shouldExecuteCode) {
            try {
                scriptingEngine.runString(code);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            shouldExecuteCode = false; // definitely don't need to do this every frame
        }

        // now we'll run everything in draw()
        try {
            scriptingEngine.runString("draw();");
        } catch (ScriptException e) {
            // render function isn't declared yet. this is fine.
            logCallback.logThis("no render function!");
        }
    }

    @Override
    public void dispose() {
    }

    public void codeHasChanged(String code) {
        this.code = code;
        this.codeIsDirty = true;
    }

    public void executeCode() {
        this.shouldExecuteCode = true;
    }
}
