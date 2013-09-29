package mas.simulator.user;

import mas.simulator.agent.event.Events;
import mas.simulator.dumbImpl.Agents;

/**
 * User: Lugzan
 */
public class UserAgent extends Agents.MyAgent {
    public UserAgent(int id) {
        super(id);
    }

    @Override
    public void react(Events.AgentEvent ev) {
        //write your code here
        System.out.println("Agent #" + getId() + " received " + ev);

        if (ev instanceof Events.TimerEvent || ev instanceof Events.InitEvent) {
            mainComputer().startTimer(1, "");
        }
    }
}
