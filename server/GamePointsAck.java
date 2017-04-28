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
import wtserver.database.Users;
import wtserver.database.Users.UserInfo;

/**
 *
 * @author MSI
 */
public class GamePointsAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_FD_GAMEPOINTS_ACK;

    public GamePointsAck()
    {
        size = 64;
        buffer = ByteBuffer.allocate(size);
    }
    
    public byte [] getData(short seqNum, Client c)
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
        
        Users users = new Users();
        users.addPoints(c.getUserId(), 3000, 2500);
        UserInfo userInfo = users.getUserInfo(c.getUserId());
        addByte((byte) 0);

        addInteger(userInfo.gold);
        addInteger(0);
        addInteger(userInfo.gp);
        addInteger(2000);
        addByte((byte) 0);
        addByte((byte) 0x18);
        addInteger(0);
        addInteger(0); //bonus gold
        addInteger(0); //bonus gp
        addInteger(6);
        
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            addByte((byte)0);
        }
        return buffer.array();
    }
    

        
}