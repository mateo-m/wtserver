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
public class StartRoundAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_STARTROUND_ACK;
    byte roundNumber;
    short roundTime;
    
    public StartRoundAck(byte roundNumber, short roundTime)
    {
        this.roundNumber = roundNumber;
        this.roundTime = roundTime;
        size = 8;
        buffer = ByteBuffer.allocate(64);
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

        /*byte b[] = {0x00, 0x01, 0x5D, 0x02, 0x00, 0x00, 0x0, 0x00, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A};
        
        for(int i = 0; i < b.length; i++)
        {
            addByte(b[i]);
        }*/
        
         addByte((byte)0);
         addByte(roundNumber);
         addShort((short) (roundTime + 5));
        
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
