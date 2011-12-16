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

package dk.itu.mariolevel.engine.level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import dk.itu.mariolevel.engine.sprites.Sprite;

public class Level implements Serializable
{
	private static final long serialVersionUID = -2222762134065697580L;
	
	public static final String[] BIT_DESCRIPTIONS = {//
	        "BLOCK UPPER", //
	        "BLOCK ALL", //
	        "BLOCK LOWER", //
	        "SPECIAL", //
	        "BUMPABLE", //
	        "BREAKABLE", //
	        "PICKUPABLE", //
	        "ANIMATED",//
	};
	
	public static byte[] TILE_BEHAVIORS = new byte[256];
	
	public static final int BIT_BLOCK_UPPER = 1 << 0;
	public static final int BIT_BLOCK_ALL = 1 << 1;
	public static final int BIT_BLOCK_LOWER = 1 << 2;
	public static final int BIT_SPECIAL = 1 << 3;
	public static final int BIT_BUMPABLE = 1 << 4;
	public static final int BIT_BREAKABLE = 1 << 5;
	public static final int BIT_PICKUPABLE = 1 << 6;
	public static final int BIT_ANIMATED = 1 << 7;
	
	public static final byte SPECIAL_BLOCK_START = -1;
	public static final byte SPECIAL_BLOCK_END = -2;
	public static final byte SPECIAL_BLOCK_GOOMBA = -3;
	public static final byte SPECIAL_BLOCK_RED_KOOPA = -4;
	public static final byte SPECIAL_BLOCK_GREEN_KOOPA = -5;
	public static final byte SPECIAL_BLOCK_FLOWER = -6;
	public static final byte SPECIAL_BLOCK_MUSHROOM = -7;
	
	//private final int FILE_HEADER = 0x271c4178;
	public int length;
	public int height;

	public byte[][] map;
	public byte[][] data;
	
	public SpriteTemplate[][] spriteTemplates;
	
	public int xEnter;
	public int yEnter;
	
	public int xExit;
	public int yExit;
	
	public Level safeCopy;
	
	public Level(int length, int height)
	{
	    this.length = length;
	    this.height = height;
	
	    xExit = 50;
	    yExit = 10;
	
        map = new byte[length][height];
        data = new byte[length][height];
        spriteTemplates = new SpriteTemplate[length][height];
	}
	
	public Level(Level level)
	{
		safeCopy = level;
		
		copyLevel(safeCopy);
	}
	
	protected void copyLevel(Level level) {
	    this.length = level.length;
	    this.height = level.height;
	
	    xEnter = level.xEnter;
	    yEnter = level.yEnter;
	    
	    xExit = level.xExit;
	    yExit = level.yExit;
	
	    map = cloneTwoDimensional(level.map);
        data = cloneTwoDimensional(level.data);
        spriteTemplates =  cloneSpriteTemplate(level.spriteTemplates);
	}
	
	public void reset() {
		copyLevel(safeCopy);
	}
	
	private SpriteTemplate[][] cloneSpriteTemplate(SpriteTemplate[][] input) {
		SpriteTemplate[][] output = new SpriteTemplate[input.length][input[0].length];
		
		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < input[0].length; j++) {
				if(input[i][j] != null)
					output[i][j] = new SpriteTemplate(input[i][j].type);
			}
		}
		
		return output;
	}
	
	private byte[][] cloneTwoDimensional(byte[][] src) {
		byte[][] out = new byte[src.length][];
		
		for(int i = 0; i < src.length; i++) {
			out[i] = src[i].clone();
		}
		
		return out;
	}
	
	public static void loadBehaviors(DataInputStream dis) throws IOException
	{
	    dis.readFully(Level.TILE_BEHAVIORS);
	}
	
	public static void saveBehaviors(DataOutputStream dos) throws IOException
	{
	    dos.write(Level.TILE_BEHAVIORS);
	}
	
	public static Level load(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
	    Level level = (Level) ois.readObject();
	    return level;
	}
	
	public static void save(Level lvl, ObjectOutputStream oos) throws IOException
	{
		// Remove the safecopy, since there's no need to save it
		lvl.safeCopy = null;
		
	    oos.writeObject(lvl);
	}
	
	/**
	 * Animates the unbreakable brick when smashed from below by Mario
	 */
	public void tick()
	{
	    // TODO:!!H! Optimize this!
	    for (int x = 0; x < length; x++)
	        for (int y = 0; y < height; y++)
	            if (data[x][y] > 0) data[x][y]--;
	}
	
	public byte getBlockCapped(int x, int y)
	{
	    if (x < 0) x = 0;
	    if (y < 0) y = 0;
	    if (x >= length) x = length - 1;
	    if (y >= height) y = height - 1;
	    return map[x][y];
	}
	
	public byte getBlock(int x, int y)
	{
	    if (x < 0) x = 0;
	    if (y < 0) return 0;
	    if (x >= length) x = length - 1;
	    if (y >= height) y = height - 1;
	    return map[x][y];
	}
	
	public void setBlock(int x, int y, byte b)
	{
		if(b == SPECIAL_BLOCK_START) {
			xEnter = x;
			yEnter = y;		
			return;
		}
		
		if(b == SPECIAL_BLOCK_END) {
			xExit = x;
			yExit = y;
			return;
		}
		
		if(b == SPECIAL_BLOCK_GOOMBA || b == SPECIAL_BLOCK_GREEN_KOOPA || b == SPECIAL_BLOCK_RED_KOOPA || b == SPECIAL_BLOCK_FLOWER || b == SPECIAL_BLOCK_MUSHROOM) {
			addSprite(x, y, b);
			return;
		}
		
		// Delete enemy first
		if(b == 0 && getSpriteTemplate(x, y) != null) {
			setSpriteTemplate(x, y, null);
			return;
		}
	
	    if (x < 0) return;
	    if (y < 0) return;
	    if (x >= length) return;
	    if (y >= height) return;
	    map[x][y] = b;
	}
	
	private void addSprite(int x, int y, byte b) {
		int kind = -1;
		
		if(b == SPECIAL_BLOCK_GOOMBA) {
			kind = Sprite.KIND_GOOMBA;
		}
		else if(b == SPECIAL_BLOCK_GREEN_KOOPA) {
			kind = Sprite.KIND_GREEN_KOOPA;
		}
		else if(b == SPECIAL_BLOCK_RED_KOOPA) {
			kind = Sprite.KIND_RED_KOOPA;
		}
		else if(b == SPECIAL_BLOCK_FLOWER) {
			kind = Sprite.KIND_ENEMY_FLOWER;
		}
		else if(b == SPECIAL_BLOCK_MUSHROOM) {
			kind = Sprite.KIND_MUSHROOM;
		}
		
		if(kind != -1)
			setSpriteTemplate(x, y, new SpriteTemplate(kind));	
	}
	
	public void setBlockData(int x, int y, byte b)
	{
	    if (x < 0) return;
	    if (y < 0) return;
	    if (x >= length) return;
	    if (y >= height) return;
	    data[x][y] = b;
	}
	
	public byte getBlockData(int x, int y)
	{
	    if (x < 0) return 0;
	    if (y < 0) return 0;
	    if (x >= length) return 0;
	    if (y >= height) return 0;
	    return data[x][y];
	}
	
	public boolean isBlocking(int x, int y, float xa, float ya)
	{
	    byte block = getBlock(x, y);
	    boolean blocking = ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_ALL) > 0;
	    blocking |= (ya > 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_UPPER) > 0;
	    blocking |= (ya < 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_LOWER) > 0;
	
	    return blocking;
	}
	
	public SpriteTemplate getSpriteTemplate(int x, int y)
	{
	    if (x < 0) return null;
	    if (y < 0) return null;
	    if (x >= length) return null;
	    if (y >= height) return null;
	    return spriteTemplates[x][y];
	}
	
	public boolean setSpriteTemplate(int x, int y, SpriteTemplate spriteTemplate)
	{
	    if (x < 0) return false;
	    if (y < 0) return false;
	    if (x >= length) return false;
	    if (y >= height) return false;
	    spriteTemplates[x][y] = spriteTemplate;
	    return true;
	}
}
