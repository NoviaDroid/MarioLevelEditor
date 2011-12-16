package competition.gic2010.gameplay.grammaticalbehaviors.bt.Conditions;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTConstants;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 * @author Diego
 */
public class EnemyAheadUp extends BTLeafNode {
    
    public EnemyAheadUp(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 
        
        int aheadStartPos = GEBT_MarioAgent.MARIO_Y;
        int aheadEndPos = GEBT_MarioAgent.MARIO_Y+2;
        boolean enemyAhead = mario.isEnemy(GEBT_MarioAgent.MARIO_X-5,GEBT_MarioAgent.MARIO_X-1,
                                                   aheadStartPos,aheadEndPos);
        
        if(enemyAhead)
            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        else
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        
        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
