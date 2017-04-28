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

/**
 *
 * @author MSI
 */
public class EquipAck extends ServerMsg {
    private final short msgId = ServerMsg.CS_IN_EQUIP_ACK;

    public EquipAck(byte recv_buffer [], int usr_id)
    {
        size = 8;
        buffer = ByteBuffer.allocate(32);
        byte n = recv_buffer[0];
        Users users = new Users();
        for(int i = 0; i < n; i++)
        {
            int index = i * 89;
            byte slot = recv_buffer[1 + index];
            int primary = (int) ((recv_buffer[2 + index] & 0xff) + (recv_buffer[3 + index] *0x100) + (recv_buffer[4 + index] *0x10000) + (recv_buffer[5 + index] *0x1000000));
            int secondary = (int) ((recv_buffer[12 + index] & 0xff) + (recv_buffer[13 + index] *0x100) + (recv_buffer[14 + index] *0x10000) + (recv_buffer[15 + index] *0x1000000));
            int grenade1 = (int) ((recv_buffer[17 + index] & 0xff) + (recv_buffer[18 + index] *0x100) + (recv_buffer[19 + index] *0x10000) + (recv_buffer[20 + index] *0x1000000));
            int grenade2 = (int) ((recv_buffer[21 + index] & 0xff) + (recv_buffer[22 + index] *0x100) + (recv_buffer[23 + index] *0x10000) + (recv_buffer[24 + index] *0x1000000));
            int power1 = (int) ((recv_buffer[25 + index] & 0xff) + (recv_buffer[26 + index] *0x100) + (recv_buffer[27 + index] *0x10000) + (recv_buffer[28 + index] *0x1000000));
            int power2 = (int) ((recv_buffer[29 + index] & 0xff) + (recv_buffer[30 + index] *0x100) + (recv_buffer[31 + index] *0x10000) + (recv_buffer[32 + index] *0x1000000));
            int power3 = (int) ((recv_buffer[33 + index] & 0xff) + (recv_buffer[34 + index] *0x100) + (recv_buffer[35 + index] *0x10000) + (recv_buffer[36 + index] *0x1000000));
            int power4 = (int) ((recv_buffer[37 + index] & 0xff) + (recv_buffer[38 + index] *0x100) + (recv_buffer[39 + index] *0x10000) + (recv_buffer[40 + index] *0x1000000));
            int power5 = (int) ((recv_buffer[41 + index] & 0xff) + (recv_buffer[42 + index] *0x100) + (recv_buffer[43 + index] *0x10000) + (recv_buffer[44 + index] *0x1000000));
            int knife = (int) ((recv_buffer[85 + index] & 0xff) + (recv_buffer[86 + index] *0x100) + (recv_buffer[87 + index] *0x10000) + (recv_buffer[88 + index] *0x1000000));
            System.out.println("Equip " + slot + " " + primary + " " + secondary + " " + knife + " " + grenade1 + " " + grenade2 + " " + power1 + " " + power2 + " " + power3 + " " + power4 + " " + power5);
            
            int wpslot = slot + 1;
            if(wpslot == 0)
                break;
            if(primary >= 1000000)
                users.setEquip("primary_wp" + wpslot, primary, usr_id);
            if(secondary >= 1000000)
                users.setEquip("secondary_wp" + wpslot, secondary, usr_id);
            if(knife >= 1000000)
                users.setEquip("knife_wp" + wpslot, knife, usr_id);
            if(grenade1 >= 1000000)
                users.setEquip("grenade_wp" + wpslot, grenade1, usr_id);
            if(grenade2 >= 1000000)
                users.setEquip("nade_wp" + wpslot, grenade2, usr_id);
            if(slot != 0)
                continue;
            if(power1 >= 1000000)
                users.setEquip("powerup1" + wpslot, power1, usr_id);
            if(power2 >= 1000000)
                users.setEquip("powerup2" + wpslot, power2, usr_id);
            if(power3 >= 1000000)
                users.setEquip("powerup3" + wpslot, power3, usr_id);
            if(power4 >= 1000000)
                users.setEquip("powerup1" + wpslot, power4, usr_id);
            if(power5 >= 1000000)
                users.setEquip("powerup1" + wpslot, power5, usr_id);

        }
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