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
public class ChangeItemInInvenAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_IN_CHANGE_ITEM_IN_INVEN_ACK;
    byte wp, slot;
    int id;
    short type;
    
    public ChangeItemInInvenAck(byte recv_buffer [])
    {
        size = 64;
        buffer = ByteBuffer.allocate(size);
        wp = (byte) (recv_buffer[1] & 0xff);
        slot = (byte) (recv_buffer[2] & 0xff);
        id = (int) ((recv_buffer[3] & 0xff) + (recv_buffer[4]*0x100) + (recv_buffer[5]*0x10000) + (recv_buffer[6]*0x1000000));
        type = (short) ((recv_buffer[7] & 0xff) + (recv_buffer[8]*0x100));
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
        addByte((byte)0);
        addByte((byte)0);
        addByte((byte)0);
        addInteger(id);
        addShort(type);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
}
