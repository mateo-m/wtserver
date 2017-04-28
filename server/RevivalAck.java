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
public class RevivalAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_REVIVALUSER_ACK;
    byte slot1, slot2, team;
    
    public RevivalAck(byte recv_buffer [], byte team)
    {
        this.team = team;
        size = 8;
        buffer = ByteBuffer.allocate(512);
        slot1 = recv_buffer[0];
        slot2 = recv_buffer[1];
        System.out.println("Revive " + slot1 + ' ' + slot2);
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
        addByte((byte) slot2);//player team
        addByte((byte) team);//target team
        addByte((byte) 0);
        addByte((byte) slot1);//slot2
        addByte((byte) 0);
        
        
        
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