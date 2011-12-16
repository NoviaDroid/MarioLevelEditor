/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package competition.gic2010.gameplay.grammaticalbehaviors.bt.Actions;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTConstants;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

import dk.itu.mariolevel.engine.sprites.Mario;

/**
 *
 * @author Diego
 */
public class VerticalJump extends BTLeafNode{

    public VerticalJump(BTNode a_parent)
    {
        super(a_parent);
    }

    public void step() throws IncorrectNodeException 
    {
        super.step();

        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 
                
        mario.setAction(Mario.KEY_JUMP,true);
        
        //report a success
        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        //m_parent.update(m_nodeStatus);
    }

}
