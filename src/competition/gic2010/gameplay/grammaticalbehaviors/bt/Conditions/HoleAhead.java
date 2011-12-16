package competition.gic2010.gameplay.grammaticalbehaviors.bt.Conditions;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTConstants;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 * @author Diego
 */
public class HoleAhead extends BTLeafNode{

    public HoleAhead(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 
        
        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        /*boolean groundUnderMario = mario.isObstacle(GEBT_MarioAgent.MARIO_X+1,GEBT_MarioAgent.MARIO_X+1,
                                                 GEBT_MarioAgent.MARIO_Y,GEBT_MarioAgent.MARIO_Y);
        */
        
        boolean groundUnderMario = mario.amIOnGround();
        if(groundUnderMario)
        {
            int aheadStartPos = GEBT_MarioAgent.MARIO_Y+1;
            int aheadEndPos = GEBT_MarioAgent.MARIO_Y+2;
            boolean holeAhead = !mario.isObstacle(GEBT_MarioAgent.MARIO_X+1,GEBT_MarioAgent.MARIO_X+1,
                                                 aheadStartPos,aheadEndPos);
            if(holeAhead)
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        
        }
        
        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
