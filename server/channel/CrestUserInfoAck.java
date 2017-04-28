/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server.channel;

import wtserver.client.ServerMsg;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 *
 * @author MSI
 */
public class CrestUserInfoAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_CH_CRESTUSERINFO_ACK;
    
    public CrestUserInfoAck()
    {
        size = 24;
        buffer = ByteBuffer.allocate(size);
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
        addInteger(0x3000000);
        addInteger(0x1000000);
        addInteger(0x2000000);
        return buffer.array();
    }
}
