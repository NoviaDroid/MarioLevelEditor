/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package competition.gic2010.gameplay.grammaticalbehaviors.bt.behaviortree;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.MarioXMLReader;

/**
 *
 * @author Diego
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        BehaviorTree bt = new BehaviorTree(null,new MarioXMLReader());
        bt.execute();
    
    }

}
