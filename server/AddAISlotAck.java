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
public class AddAISlotAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_ADD_AISLOT_ACK;
    
    public AddAISlotAck()
    {
        size = 8;
        buffer = ByteBuffer.allocate(128);
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

        byte b[] = {0x00, 0x01, 0x06, 0x00, 0x01, 0x01, 0x37, 0x03, (byte)0xB9, 0x0B, (byte)0xE9, 0x03, 0x29, 0x23, 0x00, 0x00, 0x00, 0x00, (byte)0xD1, 0x07, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        for(int i = 0; i < b.length; i++)
        {
            addByte(b[i]);
        }
        
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
