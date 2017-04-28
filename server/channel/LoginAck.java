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
public class LoginAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_CH_LOGIN_ACK;
    private String loginName = "";
    private String loginPassword = "";
    public LoginAck()
    {
        size = 24 + 16;
        buffer = ByteBuffer.allocate(size);
    }
    public LoginAck(byte recv_buffer [])
    {
        size = 24 + 16;
        buffer = ByteBuffer.allocate(size);
        String hash1 = "";
        short b = recv_buffer[2];
        for(int i = 0; i < b; i++)
        {
            char c = (char) recv_buffer[3 + i];
            hash1 += c;
        }
        short b1 = recv_buffer[3 + b];
        for(int i = 0; i < b1; i++)
        {
            char c = (char) recv_buffer[4 + b + i];
            loginName += c;
        }
        short b2 = recv_buffer[4 + b + b1];
        for(int i = 0; i < b2; i++)
        {
            char c = (char) recv_buffer[6 + b + b1 + i];
            loginPassword += c;
        }
    }
    
    public byte [] getData(short seqNum, byte errorcode, byte [] session_key)
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

        addByte((byte) errorcode);
        for(int i = 0; i < 6; i++)
        {
            addByte(session_key[i]);
        }

        byte b[] = {0x00, 0x31, 0x08, 0x07, (byte)0xF0, 0x18, 0x04, 0x00, (byte)0xAD, (byte)0xE1, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x3D, 0x00, (byte)0xFF, 0x00, 0x00};
        
        for(int i = 7; i < b.length; i++)
        {
            addByte(b[i]);
        }
        
        
        return buffer.array();
    }
    
    public String getLoginName()
    {
        return loginName;
    }

    public String getLoginPassword()
    {
        return loginPassword;
    }
}