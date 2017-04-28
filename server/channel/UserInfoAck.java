/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver.server.channel;

import wtserver.client.ServerMsg;
import java.nio.ByteBuffer;
import java.util.Random;
import wtserver.Client;

/**
 *
 * @author MSI
 */
public class UserInfoAck extends ServerMsg{
    private final short msgId = ServerMsg.CS_CH_USERINFO_ACK;
    String callSign;
    
    public UserInfoAck(String callSign)
    {
        size = 8;
        buffer = ByteBuffer.allocate(256);
        this.callSign = callSign;
    }
    
    public byte [] getData(short seqNum, Client c)
    {
        buffer.position(0);
        byte random = (byte) new Random().nextInt();
        addByte(random);
        addShort(msgId);
        addShort(seqNum);
        addShort((short)0);
        byte checksum = 0;
        addByte(checksum);
        int n = callSign.length() * 2;
        addByte((byte) 0);
        addByte((byte) n);
        size += 2;
        size += n;
        addUnicodeString(callSign);
        //TODO
        byte unks[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xF4, 0x4C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xD0, 0x07, 0x00, 0x00, 0x00, 0x3D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x13, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] gold = ByteBuffer.allocate(4).putInt(100000).array();
        unks[6] = gold[3];
        unks[7] = gold[2];
        unks[8] = gold[1];
        unks[9] = gold[0];
        unks[31] = c.getUserLevel();
        for(int i = 0; i < unks.length; i++)
            addByte(unks[i]);
        
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
