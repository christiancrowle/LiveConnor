package com.picsofbread.liveconnor;

import com.badlogic.gdx.ApplicationAdapter;
import com.picsofbread.liveconnor.state.State;
import com.picsofbread.liveconnor.state.States;

public class LiveConnor extends ApplicationAdapter {

	private State currentState;

	public void changeState(State state) {
		currentState = state;

		if (!state.hasBeenCreated)
			currentState.create();
	}

	@Override
	public void create() {
		changeState(States.STATE_LIVE_CODE_MAIN);
	}

	@Override
	public void resize(int width, int height) {
		currentState.resize(width, height);
	}

	@Override
	public void render() {
		currentState.render();
	}
	
	@Override
	public void dispose() {

	}
}
