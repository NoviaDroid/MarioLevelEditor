/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of the Mario AI nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
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


package dk.itu.mariolevel.engine.level;

import java.util.Random;

/**
 * This class is simple to use. Just call <b>createLevel</b> method with params:
 * <ul>
 * MarioAIOptions args, that contains: ... TODO:TASK:[M]
 * <p/>
 * <li>length -- length of the level in cells. One cell is 16 pixels long</li>
 * <li>height -- height of the level in cells. One cell is 16 pixels long </li>
 * <li>seed -- use this param to make a globalRandom level.
 * On different machines with the same seed param there will be one level</li>
 * <li>levelDifficulty -- use this param to change difficult of the level.
 * On different machines with the same seed param there will be one level</li>
 * <li>levelType -- levelType of the level. One of Overground, Underground, Castle.</li>
 * </ul>
 *
 * @see #TYPE_OVERGROUND
 * @see #TYPE_UNDERGROUND
 * @see #TYPE_CASTLE
 */

public class LevelGenerator
{
	public static final int TYPE_OVERGROUND = 0;
	public static final int TYPE_UNDERGROUND = 1;
	public static final int TYPE_CASTLE = 2;
	
	public static final int DEFAULT_FLOOR = -1;
	
	private static int length;
	private static int height;
	private static Level level;
	
	private static Random globalRandom = new Random(0);

	private static int levelType;
	
	private static final int INFINITE_FLOOR_HEIGHT = Integer.MAX_VALUE;
	
	private LevelGenerator() {}
	
	public static Level createEditorLevel(int width, int levelHeight) {
		length = width;
		height = levelHeight;
		
		level = new Level(length, height);
		
		int floor = height-1;
		
		buildStraight(0, level.length, true, floor, 2);
		
		fixWalls();
		
		level.xEnter = 2;
		level.yEnter = 2;
		
	    level.xExit = level.length - 2;
	    level.yExit = floor;
		
		return level;
	}
	
	// parameter safe should be set to true iff length of the Straight > 10.
	// minimal length = 2
	//floorHeight - height of the floor. used for building of the top part of the dead end separator
	
	private static int buildStraight(int xo, int maxLength, boolean safe, int vfloor, int floorHeight)
	{
	    int length;
	    if (floorHeight != INFINITE_FLOOR_HEIGHT)
	    {
	        length = maxLength;
	    } else
	    {
	        length = globalRandom.nextInt(8) + 2;//globalRandom.nextInt(50)+1) + 2;
	        if (safe) length = 10 + globalRandom.nextInt(5);
	        if (length > maxLength) length = maxLength;
	    }
	
	
	    int floor = vfloor;
	    if (vfloor == DEFAULT_FLOOR)
	    {
	        floor = height - 1 - globalRandom.nextInt(4);
	    } else
	    {
	        globalRandom.nextInt();
	    }
	
	    int y1 = height;
	    if (floorHeight != INFINITE_FLOOR_HEIGHT)
	    {
	        y1 = floor + floorHeight;
	    }
	
	    for (int x = xo; x < xo + length; x++)
	        for (int y = floor; y < y1; y++)
	            if (y >= floor)
	                level.setBlock(x, y, (byte) (1 + 9 * 16));

	    return length;
	}
	
	private static void fixWalls()
	{
	    boolean[][] blockMap = new boolean[length + 1][height + 1];
	    for (int x = 0; x < length + 1; x++)
	    {
	        for (int y = 0; y < height + 1; y++)
	        {
	            int blocks = 0;
	            for (int xx = x - 1; xx < x + 1; xx++)
	            {
	                for (int yy = y - 1; yy < y + 1; yy++)
	                {
	                    if (level.getBlockCapped(xx, yy) == (byte) (1 + 9 * 16)) blocks++;
	                }
	            }
	            blockMap[x][y] = blocks == 4;
	        }
	    }
	    blockify(level, blockMap, length + 1, height + 1);
	}
	
	private static void blockify(Level level, boolean[][] blocks, int width, int height)
	{
	    int to = 0;
	    if (levelType == LevelGenerator.TYPE_CASTLE)
	        to = 4 * 2;
	    else if (levelType == LevelGenerator.TYPE_UNDERGROUND)
	        to = 4 * 3;
	
	    boolean[][] b = new boolean[2][2];
	    for (int x = 0; x < width; x++)
	    {
	        for (int y = 0; y < height; y++)
	        {
	            for (int xx = x; xx <= x + 1; xx++)
	            {
	                for (int yy = y; yy <= y + 1; yy++)
	                {
	                    int _xx = xx;
	                    int _yy = yy;
	                    if (_xx < 0) _xx = 0;
	                    if (_yy < 0) _yy = 0;
	                    if (_xx > width - 1) _xx = width - 1;
	                    if (_yy > height - 1) _yy = height - 1;
	                    b[xx - x][yy - y] = blocks[_xx][_yy];
	                }
	            }
	
	            if (b[0][0] == b[1][0] && b[0][1] == b[1][1])
	            {
	                if (b[0][0] == b[0][1])
	                {
	                    if (b[0][0])
	                    {
	                        level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
	                    } else
	                    {
	                        // KEEP OLD BLOCK!
	                    }
	                } else
	                {
	                    if (b[0][0])
	                    {
	                        level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
	                    } else
	                    {
	                        level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
	                    }
	                }
	            } else if (b[0][0] == b[0][1] && b[1][0] == b[1][1])
	            {
	                if (b[0][0])
	                {
	                    level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
	                } else
	                {
	                    level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
	                }
	            } else if (b[0][0] == b[1][1] && b[0][1] == b[1][0])
	            {
	                level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
	            } else if (b[0][0] == b[1][0])
	            {
	                if (b[0][0])
	                {
	                    if (b[0][1])
	                    {
	                        level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
	                    } else
	                    {
	                        level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
	                    }
	                } else
	                {
	                    if (b[0][1])
	                    {
	                        level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
	                    } else
	                    {
	                        level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
	                    }
	                }
	            } else if (b[0][1] == b[1][1])
	            {
	                if (b[0][1])
	                {
	                    if (b[0][0])
	                    {
	                        level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
	                    } else
	                    {
	                        level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
	                    }
	                } else
	                {
	                    if (b[0][0])
	                    {
	                        level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
	                    } else
	                    {
	                        level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
	                    }
	                }
	            } else
	            {
	                level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
	            }
	        }
	    }
	}

}