/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package dk.itu.mariolevel.ai.environments;

import dk.itu.mariolevel.ai.agents.Agent;
import dk.itu.mariolevel.engine.sprites.Mario;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:51:57 PM
 * Package: .Environments
 */

public interface Environment
{
public static final int numberOfKeys = 6;

public static final int MARIO_KEY_DOWN = Mario.KEY_DOWN;
public static final int MARIO_KEY_JUMP = Mario.KEY_JUMP;
public static final int MARIO_KEY_LEFT = Mario.KEY_LEFT;
public static final int MARIO_KEY_RIGHT = Mario.KEY_RIGHT;
public static final int MARIO_KEY_SPEED = Mario.KEY_SPEED;
public static final int MARIO_STATUS_WIN = Mario.STATUS_WIN;
public static final int MARIO_STATUS_DEAD = Mario.STATUS_DEAD;
public static final int MARIO_STATUS_RUNNING = Mario.STATUS_RUNNING;

public void resetDefault();

public void reset(String setUpOptions);

public void tick();

public float[] getMarioFloatPos(Agent agent);

public int getMarioMode(Agent agent);

public float[] getEnemiesFloatPos(Agent agent);

public boolean isMarioOnGround(Agent agent);

public boolean isMarioAbleToJump(Agent agent);

public boolean isMarioCarrying(Agent agent);

public boolean isMarioAbleToShoot(Agent agent);

// OBSERVATION

public int getReceptiveFieldWidth();

public int getReceptiveFieldHeight();


public byte[][] getMergedObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies);

public byte[][] getLevelSceneObservationZ(Agent agent, int ZLevelScene);

public byte[][] getEnemiesObservationZ(Agent agent, int ZLevelEnemies);

// OBSERVATION FOR AmiCo Agents

public int[] getSerializedFullObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies);

/**
 * Serializes the LevelScene observation from 22x22 byte array to a 1x484 byte array
 *
 * @param ZLevelScene -- Zoom Level of the levelScene the caller expects to get
 * @return byte[] with sequenced elements of corresponding getLevelSceneObservationZ output
 */
public int[] getSerializedLevelSceneObservationZ(Agent agent, int ZLevelScene);

/**
 * Serializes the LevelScene observation from 22x22 byte array to a 1x484 byte array
 *
 * @param ZLevelEnemies -- Zoom Level of the enemies observation the caller expects to get
 * @return byte[] with sequenced elements of corresponding <code>getLevelSceneObservationZ</code> output
 */
public int[] getSerializedEnemiesObservationZ(Agent agent, int ZLevelEnemies);

public int[] getSerializedMergedObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies);

public float[] getCreaturesFloatPos(Agent agent);

// KILLS

public int getKillsTotal(Agent agent);

public int getKillsByFire(Agent agent);

public int getKillsByStomp(Agent agent);

public int getKillsByShell(Agent agent);

int getMarioStatus(Agent agent);

/**
 * @return int array filled with data about Mario :
 *         marioState[0] = this.getMarioStatus();
 *         marioState[1] = this.getMarioMode();
 *         marioState[2] = this.isMarioOnGround() ? 1 : 0;
 *         marioState[3] = this.isMarioAbleToJump() ? 1 : 0;
 *         marioState[4] = this.isMarioAbleToShoot() ? 1 : 0;
 *         marioState[5] = this.isMarioCarrying() ? 1 : 0;
 *         marioState[6] = this.getKillsTotal();
 *         marioState[7] = this.getKillsByFire();
 *         marioState[8] = this.getKillsByStomp();
 *         marioState[9] = this.getKillsByStomp();
 *         marioState[10] = this.getKillsByShell();
 *         marioState[11] = this.getTimeLeft();
 */
public int[] getMarioState(Agent agent);

void performAction(Agent agent, boolean[] action);

boolean isLevelFinished(Agent agent);

void reset();

public int getIntermediateReward(Agent agent);

public int[] getMarioEgoPos();

public int getTimeSpent(Agent agent);

}


