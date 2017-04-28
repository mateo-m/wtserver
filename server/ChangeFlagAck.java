/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server;

import java.nio.ByteBuffer;
import java.util.Random;
import wtserver.Client;
import wtserver.client.ServerMsg;

/**
 *
 * @author MSI
 */
public class ChangeFlagAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_CHANGEFLAG_ACK;
    byte flag, team;
    
    public ChangeFlagAck(byte recv_buffer [])
    {
        this.team = team;
        size = 8;
        buffer = ByteBuffer.allocate(32);
        flag = recv_buffer[0];
        team = recv_buffer[1];
    }
    
    public byte [] getData(short seqNum)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short)0);
        byte checksum = 0;
        addByte(checksum);
        
        addByte((byte) 0);
        addByte((byte) flag);
        addByte((byte) team);
        
        
        size = (short) buffer.position();
        if((size - 8) % 16 > 0)
        {
            size -= (size - 8) % 16;
            size += 16;
        }
        short pSize = (short) ((size - 8) / 16);
        buffer.position(5);
        addShort(pSize);
        for(int i = 0; i < 7; i++)
        {
            checksum += buffer.get(i);
        }
        addByte(checksum);
        return buffer.array();
    }
    
    public byte getFlag()
    {
        return flag;
    }
    
    public byte getTeam()
    {
        return team;
    }
}