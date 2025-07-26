
package state;

import java.util.HashMap;
import java.util.Map;


public class StateMachine implements State {

    /**
     * Contains all states of this state machine.
     */
    private final Map<String, State> states;
    /**
     * Current active state.
     */
    private State currentState;

    /**
     * Creates a state machine.
     */
    public StateMachine() {
        states = new HashMap<>();
        currentState = new EmptyState();
        states.put(null, currentState);
    }

    /**
     * Adds a state with specified name.
     *
     * @param name  Name of the state
     * @param state The state to add
     */
    public void add(String name, State state) {
        states.put(name, state);
    }

    /**
     * Changes the current state.
     *
     * @param name Name of the desired state
     */
    public void change(String name) {
        currentState.exit();
        currentState = states.get(name);
        currentState.enter();
    }

    @Override
    public void input() {
        currentState.input();
    }

    @Override
    public void update() {
        currentState.update();
    }

    @Override
    public void update(float delta) {
        currentState.update(delta);
    }

    @Override
    public void render() {
        currentState.render();
    }

    @Override
    public void render(float alpha) {
        currentState.render(alpha);
    }

    @Override
    public void enter() {
        currentState.enter();
    }

    @Override
    public void exit() {
        currentState.exit();
    }

}
