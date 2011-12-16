/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package competition.gic2010.gameplay.grammaticalbehaviors.bt.Actions;

import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTConstants;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.BTNode;
import competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class NOP extends BTLeafNode{
 
    public NOP(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
          
        //NO BUTTONS TO PRESS IN THIS ACTION.
        
        //report a success
        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
    }
}
