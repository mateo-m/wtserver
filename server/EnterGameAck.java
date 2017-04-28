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
public class EnterGameAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_ENTERGAME_ACK;
    byte slot, team, teamslot;

    public EnterGameAck(byte slot, byte team, byte teamslot)
    {
        size = 8;
        buffer = ByteBuffer.allocate(32);
        this.slot = slot;
        this.team = team;
        this.teamslot = teamslot;
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
        addByte((byte) slot);
        addByte((byte) teamslot);
        addByte((byte) team);
        addByte((byte) 0xC8);
        
        
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
}