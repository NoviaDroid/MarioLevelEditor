/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package competition.gic2010.gameplay.grammaticalbehaviors.bt.Conditions;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.Map;
import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.Node;
import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.Graph;
import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTConstants;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class UnderBrick extends BTLeafNode{


    public UnderBrick(BTNode a_parent)
    {
        super(a_parent);
    }

    public void step() throws IncorrectNodeException 
    {
        super.step();
        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent;
        int marioNodeId = mario.getMarioNodeInMap();
        Graph levelGraph = mario.getMap().getGraph();

        if(marioNodeId != -1)
        {
            Node marioNode = levelGraph.getNode(marioNodeId);
            if(marioNode.getMetadata() == Map.MAP_SIMPLE_BRICK)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
            }
        }
        
        //report
        m_parent.update(m_nodeStatus);
    }
    
    
}
