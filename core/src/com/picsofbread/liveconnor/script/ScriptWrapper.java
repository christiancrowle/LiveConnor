package com.picsofbread.liveconnor.script;


import org.mozilla.javascript.Context;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptWrapper {

    private ScriptEngineManager manager;
    private ScriptEngine engine;

    public ScriptWrapper() {
        manager = new ScriptEngineManager();
        initialize("rhino");
    }

    public void initialize(String language) {
        engine = manager.getEngineByName(language);
    }

    public void runString(String code) throws ScriptException {
        engine.eval(code);
    }

    public void alias(String alias, Object whatClass) {
        engine.put(alias, whatClass);
    }
}
