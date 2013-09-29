package mas.simulator.user;

import mas.simulator.agent.Agent;
import mas.simulator.env.impl.Starter;

/**
 * User: Lugzan
 */
public class UserStarter extends Starter {
    public UserStarter(int agentNum, int turns, int linearSize, int recLimit) {
        super(agentNum, turns, linearSize, recLimit);
    }

    @Override
    public Agent getAgent(int id) {
        return new UserAgent(id);
    }
}
