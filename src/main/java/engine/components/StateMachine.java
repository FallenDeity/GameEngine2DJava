package engine.components;

import engine.components.sprites.AnimationState;
import engine.components.sprites.Frame;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.*;

public class StateMachine extends Component {
	private final HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
	private final List<AnimationState> states = new ArrayList<>();
	private transient AnimationState currentState = null;
	private String defaultStateTitle = "";

	public void refresh() {
		states.forEach(AnimationState::refresh);
	}

	public void addState(String from, String to, String onTrigger) {
		this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
	}

	public void addState(AnimationState state) {
		this.states.add(state);
	}

	public void setDefaultState(String title) {
		Optional<AnimationState> foundState = states.stream().filter(state -> state.name.equals(title)).findFirst();
		foundState.ifPresent(state -> {
			defaultStateTitle = title;
			if (currentState == null) {
				currentState = state;
			}
		});
	}

	public void trigger(String trigger) {
		String newState = stateTransfers.get(new StateTrigger(currentState.name, trigger));
		if (newState != null) {
			int newStateIndex = states.indexOf(states.stream().filter(s -> s.name.equals(newState)).findFirst().orElse(null));
			if (newStateIndex != -1) {
				currentState = states.get(newStateIndex);
			}
		}
	}

	@Override
	public void update(float dt) {
		if (currentState != null) {
			currentState.update(dt);
			SpriteRenderer renderer = gameObject.getComponent(SpriteRenderer.class);
			if (renderer != null) {
				renderer.setSprite(currentState.getSprite());
			}
		}
	}

	@Override
	public void editorUpdate(float dt) {
		update(dt);
	}

	@Override
	public void imGui() {
		for (AnimationState state : states) {
			ImString name = new ImString(state.name);
			ImGui.inputText("Name", name);
			state.name = name.get();
			ImBoolean loop = new ImBoolean(state.getLoop());
			ImGui.checkbox("Loop", loop);
			state.setLoop(loop.get());
			int idx = 0;
			for (Frame frame : state.frames) {
				float[] frameTime = {frame.frameTime};
				ImGui.dragFloat("Frame " + idx++ + " Time", frameTime);
				frame.frameTime = frameTime[0];
			}
		}
	}

	@Override
	public void start() {
		for (AnimationState state : states) {
			if (state.name.equals(defaultStateTitle)) {
				currentState = state;
				break;
			}
		}
	}

	private static class StateTrigger {
		public final String state;
		public final String trigger;

		public StateTrigger(String state, String trigger) {
			this.state = state;
			this.trigger = trigger;
		}

		@Override
		public boolean equals(Object o) {
			if (o.getClass() != StateTrigger.class) return false;
			StateTrigger t2 = (StateTrigger) o;
			return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state, trigger);
		}
	}
}
