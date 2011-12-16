/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class IncorrectNodeException extends Exception{

    
    IncorrectNodeException()
    {
    }
    
    IncorrectNodeException(String a_message)
    {
        super(a_message);
    }
}
