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
public class StartGameAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_STARTGAME_ACK;
    public static byte ATLEAST_2_READY = (byte) 0xAB, SHOULD_BE_EVEN = (byte) 0xC0;
    byte errorCode = 0;
    
    public StartGameAck()
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
        addShort((short)errorCode);
        addByte((byte)0xC8);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
    
    public void setErrorCode(byte errorCode)
    {
        this.errorCode = errorCode;
    }
}