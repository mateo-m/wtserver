/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server;

import java.nio.ByteBuffer;
import java.util.Random;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class PlayerDeadAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_PLAYERDEAD_ACK;
    private byte killer;
    private byte slot;
    private short weapon;
    private short redScore, blueScore;

    public PlayerDeadAck(byte recv_buffer [])
    {
        size = 24;
        buffer = ByteBuffer.allocate(size);
        killer = recv_buffer[1];
        weapon = (short) (recv_buffer[8] + (recv_buffer[9] * 0x100));
        
        System.out.println("Killer " + killer + " Weapon " + weapon);
    }
    
    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        short pSize = (short) ((size - 8) / 16);
        addShort(pSize);
        byte checksum = 0;
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);
        
        addByte((byte) 0);
        //addByte((byte) 2);
        //addByte((byte) 0);
        addShort((short) redScore); //Red Score
        //addByte((byte) 1);
        //addByte((byte) 0);
        addShort((short) blueScore); //Red Score
        addByte((byte) 0);
        addByte((byte) slot);//dead slot
        addByte((byte) 1);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
    
    public void setScore(short redScore, short blueScore)
    {
        this.redScore = redScore;
        this.blueScore = blueScore;
    }
    
    public void setSlot(byte slot)
    {
        this.slot = slot;
    }
        
    public byte getKillerSlot()
    {
        return killer;
    }
}