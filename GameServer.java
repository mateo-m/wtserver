/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wtserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import wtserver.client.ClientMsg;
import wtserver.client.ServerMsg;
import wtserver.database.Users;
import wtserver.database.Users.UserInfo;
import wtserver.server.*;
import wtserver.server.channel.CrestUserInfoAck;
import wtserver.server.channel.EventShopInfoAck;
import wtserver.server.channel.GetPowerUserInfoAck;
import wtserver.server.channel.LoginAck;
import wtserver.server.channel.LogoutAck;
import wtserver.server.channel.SelectCharAck;
import wtserver.server.channel.SnsAccountAck;
import wtserver.server.channel.UdpAddrAck;
import wtserver.server.channel.UdpSuccessAck;
import wtserver.server.channel.UserInfoAck;

/**
 *
 * @author MSI
 */
public class GameServer extends Server {
    
    private ArrayList<Room> rooms;
    
    public GameServer(int port)
    {
        super(port);
        rooms = new ArrayList<Room>();
        Thread thread = new Thread() {
            public void run() {
                startUdpServer();
            }
        };
        Thread roomsThread = new Thread() {
            public void run() {
                try {
                    while(true)
                    {
                        Thread.sleep(1000);
                        for (int i = 0; i < rooms.size(); i++) {
                            Room room = rooms.get(i);
                            if (room != null) {
                                room.process();
                            }
                        }
                    }

                    
                }catch(Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        };
        thread.start();
        roomsThread.start();
    }
    
    public void startUdpServer() {
        try {
            DatagramSocket udpSocket = new DatagramSocket(port);
            while(true)
            {
                byte[] receiveData = new byte[16];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    udpSocket.receive(receivePacket);
                    processUdp(receivePacket.getData());
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
            
        } catch (SocketException ex) {
            System.out.println(ex);
        }
    }
    @Override
    public void startServer() {
        try {
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Game Server Started on port " + port);
            while (true) {
                Socket connectionSocket = socket.accept();
                addClient(new Client(this, connectionSocket));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    @Override
    public void processMsg(Client client, int msgId, byte buffer [])
    {
        if (msgId == ClientMsg.CS_CH_LOGIN_REQ) {
            LoginAck loginAck = new LoginAck(buffer);
            client.setLoginName(loginAck.getLoginName());
            System.out.println("Login Name " + loginAck.getLoginName());
            System.out.println("Login Password " + loginAck.getLoginPassword());
            byte errorcode = 0;
            Users users = new Users();
            int user_id = users.getLogin(loginAck.getLoginName(), "123123");
            System.out.println("IP : " + client.getIp() + " User DB ID " + user_id);
            if(user_id == 0)
                errorcode = 23;
            else {
                UserInfo userInfo = users.getUserInfo(user_id);
                client.setUserId(user_id);
                client.setCallsign(userInfo.callsign);
                client.setUserLevel((byte) userInfo.level);
            }
            client.write(loginAck.getData(client.getSeqNum(), errorcode, client.getSessionKey()), loginAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_UDPADDR_REQ) {
            UdpAddrAck udpAddrAck = new UdpAddrAck();
            client.write(udpAddrAck.getData(client.getSeqNum()), udpAddrAck.getSize());
        }else if (msgId == ClientMsg.CS_CK_UDPSUCCESS_REQ) {
            UdpSuccessAck udpSuccessAck = new UdpSuccessAck();
            client.write(udpSuccessAck.getData(client.getSeqNum(), client.getIp(), client.getPort(), client.getUdpIp(), client.getUdpPort()), udpSuccessAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_USERINFO_REQ) {
            UserInfoAck userInfoAck = new UserInfoAck(client.getLoginName());
            client.write(userInfoAck.getData(client.getSeqNum(), client), userInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_CRESTUSERINFO_REQ) {
            CrestUserInfoAck crestUserInfoAck = new CrestUserInfoAck();
            client.write(crestUserInfoAck.getData(client.getSeqNum()), crestUserInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_ITEMLIST_REQ) {
            System.out.println("Item List Request");
            ItemListAck itemListAck = new ItemListAck(client.getUserId());
            client.write(itemListAck.getData(client.getSeqNum()), itemListAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_EQUIPLIST_REQ) {
            EquipListAck equipListAck = new EquipListAck();
            client.write(equipListAck.getData(client.getSeqNum(), client.getUserId()), equipListAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_CHARITEMLIST_REQ) {
            CharItemListAck charItemListAck = new CharItemListAck();
            client.write(charItemListAck.getData(client.getSeqNum()), charItemListAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_CHARINFO_REQ) {
            CharInfoAck charInfoAck = new CharInfoAck();
            client.write(charInfoAck.getData(client.getSeqNum()), charInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_GETPOWERUSERINFO_REQ) {
            GetPowerUserInfoAck getPowerUserInfoAck = new GetPowerUserInfoAck();
            client.write(getPowerUserInfoAck.getData(client.getSeqNum()), getPowerUserInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_SELECTCHAR_REQ) {
            SelectCharAck selectCharAck = new SelectCharAck();
            client.write(selectCharAck.getData(client.getSeqNum()), selectCharAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_CRM_POPUP_REQ) {
            PopUpAck popUpAck = new PopUpAck();
            client.write(popUpAck.getData(client.getSeqNum()), popUpAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_FIELDLIST_REQ) {
            FieldListAck fieldListAck = new FieldListAck(rooms);
            client.write(fieldListAck.getData(client.getSeqNum()), fieldListAck.getSize());
        }else if (msgId == ClientMsg.CS_CN_ADVERTALL_REQ) {
            ChannelCharsAck channelCharsAck = new ChannelCharsAck();
            client.write(channelCharsAck.getData(client.getSeqNum(), getChannelUsers()), channelCharsAck.getSize());
            AdvertAllAck advertAllAck = new AdvertAllAck();
            client.write(advertAllAck.getData(client.getSeqNum()), advertAllAck.getSize());
        }else if (msgId == ClientMsg.CS_CN_CHANGESHEET_REQ) {
            ChangeSheetAck changeSheetAck = new ChangeSheetAck(buffer);
            client.write(changeSheetAck.getData(client.getSeqNum()), changeSheetAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_CHAT_REQ) {
            ChannelChatAck channelChatAck = new ChannelChatAck(buffer, client.getLoginName());
            int n = client.getRoomNumber();
            if(n == -1)
                writeToChannel(channelChatAck);
            else {
                Room room = getRoomWithNumber((short) n);
                if(room != null)
                {
                    room.write(channelChatAck);
                    if(channelChatAck.getText().equalsIgnoreCase("autowin"))
                    {
                        room.endRound(client.getRoomTeam(), true);
                    }
                }
            }
            //client.write(channelChatAck.getData(client.getSeqNum()), channelChatAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_CREATE_REQ) {
            CreateAck createAck = new CreateAck(buffer);
            client.write(createAck.getData(client.getSeqNum()), createAck.getSize());
            Room room = createRoom(createAck);
            room.addUser(client);
            client.setRoomNumber(room.getRoomNumber());
            RoomCharsAck charsAck = new RoomCharsAck();
            client.write(charsAck.getData(client.getSeqNum(), room), charsAck.getSize());
            FieldListAck fieldListAck = new FieldListAck(rooms);
            client.write(fieldListAck.getData(client.getSeqNum()), fieldListAck.getSize());
            writeToChannel(fieldListAck);
        }else if (msgId == ClientMsg.CS_FD_UDPSTART_REQ) {
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                UdpStartAck udpStartAck = new UdpStartAck(room.getUserSlot(client));
                room.write(udpStartAck);
            }else System.out.println("CS_FD_UDPSTART_REQ Room is Null " + n);
        }else if (msgId == ClientMsg.CS_FD_ADD_AISLOT_REQ) {
            AddAISlotAck addAISlotAck = new AddAISlotAck();
            client.write(addAISlotAck.getData(client.getSeqNum()), addAISlotAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_CHANGERULE_REQ) {
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if (room != null) {
                ChangeRuleAck changeRuleAck = new ChangeRuleAck(buffer);
                room.write(changeRuleAck);
                room.title = changeRuleAck.getRoomTitle();
                room.password = changeRuleAck.getRoomPassword();
                room.map = changeRuleAck.getMap();
                room.time = changeRuleAck.getTime();
                room.wins = changeRuleAck.getWins();
                room.points = changeRuleAck.getPoints();
                room.mode = changeRuleAck.getMode();
                room.roomCapacity = changeRuleAck.getCapacity();
            }
        }else if (msgId == ClientMsg.CS_FD_STARTGAME_REQ) {
            System.out.println("Start Game");
            StartGameAck startGameAck = new StartGameAck();
            //client.write(startGameAck.getData(client.getSeqNum()), startGameAck.getSize());
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                client.setRoomSlotStatus((byte)1);
                if(room.getTeamReadyCount((byte)1) != room.getTeamReadyCount((byte)2)){
                    startGameAck.setErrorCode(StartGameAck.ATLEAST_2_READY);
                    client.write(startGameAck.getData(client.getSeqNum()), startGameAck.getSize());
                    client.setRoomSlotStatus((byte)0);
                }else {
                    //UdpReadyAck udpReadyAck = new UdpReadyAck(room.getUserSlot(client), (byte)1);
                    //room.write(udpReadyAck);
                    room.startGame();
                    room.write(startGameAck);
                }
                

            }else System.out.println("CS_FD_STARTGAME_REQ Room is Null " + n);
        }else if (msgId == ClientMsg.CS_FD_STARTROUND_REQ) {
            System.out.println("Start Round");
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                room.startRound();
                StartRoundAck startRoundAck = new StartRoundAck(room.getRoundNumber(), room.getCurrentTime());
                ChangeSlotStatus changeSlotStatus = new ChangeSlotStatus(room.getUserSlot(client), (byte)4);
                client.setRoomSlotStatus((byte)4);
                room.write(changeSlotStatus);
                client.write(startRoundAck.getData(client.getSeqNum()), startRoundAck.getSize());
            }else System.out.println("CS_FD_STARTROUND_REQ Room is Null " + n);
        }else if (msgId == ClientMsg.CS_FD_UDPREADY_REQ) {
            System.out.println("Getting Ready");
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                UdpReadyAck udpReadyAck = new UdpReadyAck(room.getUserSlot(client), buffer);
                room.write(udpReadyAck);
                client.setRoomSlotStatus(udpReadyAck.userReady());
                if (room.gameStarted) {
                    EnterGameAck enterGameAck = new EnterGameAck((byte) client.getRoomSlot(), client.getRoomTeam(), client.getRoomTeamSlot());
                    room.write(enterGameAck);
                    client.setRoomSlotStatus((byte) 3);
                }
            }else System.out.println("CS_FD_UDPREADY_REQ Room is Null " + n);
        }else if (msgId == ClientMsg.CS_FD_ENTERGAME_REQ) {
            System.out.println("Enter Game");
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                EnterGameAck enterGameAck = new EnterGameAck((byte)client.getRoomSlot(), client.getRoomTeam(), client.getRoomTeamSlot());
                room.write(enterGameAck);
                client.setRoomSlotStatus((byte)3);
            }else System.out.println("CS_FD_ENTERGAME_REQ Room is Null " + n);
        }else if (msgId == ClientMsg.CS_FD_CHANGETEAM_REQ) {
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
            if(room != null)
            {
                room.changeTeam(client);
                ChangeTeamAck changeTeamAck = new ChangeTeamAck((byte)client.getRoomSlot(), client.getRoomTeam(), client.getRoomTeamSlot());
                //client.write(changeTeamAck.getData(client.getSeqNum()), changeTeamAck.getSize());
                room.write(changeTeamAck);
            }
        }else if (msgId == ClientMsg.CS_IN_BUYNOW_ITEMLIST_REQ) {
            BuyNowItemListAck buyNowItemListAck = new BuyNowItemListAck();
            client.write(buyNowItemListAck.getData(client.getSeqNum()), buyNowItemListAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_SNSACCOUNT_REQ) {
            SnsAccountAck snsAccountAck = new SnsAccountAck();
            client.write(snsAccountAck.getData(client.getSeqNum()), snsAccountAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_EVENTSHOPINFO_REQ) {
            EventShopInfoAck eventShopInfoAck = new EventShopInfoAck();
            client.write(eventShopInfoAck.getData(client.getSeqNum()), eventShopInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_ITEM_LIMITED_REQ) {
            ItemLimitedAck itemLimitedAck = new ItemLimitedAck();
            client.write(itemLimitedAck.getData(client.getSeqNum()), itemLimitedAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_PACKAGE_ITEM_LIST_REQ) {
            PackageItemListAck packageItemListAck = new PackageItemListAck();
            client.write(packageItemListAck.getData(client.getSeqNum()), packageItemListAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_GETMACROMESSAGE_REQ) {
            GetMacroMessageAck getMacroMessageAck = new GetMacroMessageAck();
            client.write(getMacroMessageAck.getData(client.getSeqNum()), getMacroMessageAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_GETEVENTGOLDINFO_REQ) {
            GetEventGoldInfoAck getEventGoldInfoAck = new GetEventGoldInfoAck();
            client.write(getEventGoldInfoAck.getData(client.getSeqNum()), getEventGoldInfoAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_ITEMPRICE_VERSION_REQ) {
            ItemPriceVersionAck itemPriceVersionAck = new ItemPriceVersionAck();
            client.write(itemPriceVersionAck.getData(client.getSeqNum()), itemPriceVersionAck.getSize());
        }else if (msgId == ClientMsg.CS_PR_BATTLERESULT_REQ) {
            PrideBattleResultAck prideBattleResultAck = new PrideBattleResultAck();
            client.write(prideBattleResultAck.getData(client.getSeqNum()), prideBattleResultAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_EXIT_REQ) {
            short n = client.getRoomNumber();
            Room room = getRoomWithNumber(n);
                if (room != null) {
                    if(room.getUserSlot(client) != -1)
                    {
                        ExitAck exitAck = new ExitAck(room.getUserSlot(client));
                        room.write(exitAck);
                        room.removeUser(client);
            ChannelCharsAck channelCharsAck = new ChannelCharsAck();
            client.write(channelCharsAck.getData(client.getSeqNum(), getChannelUsers()), channelCharsAck.getSize());
                    }

                } else {
                    ExitAck exitAck = new ExitAck((byte)0);
                    client.write(exitAck.getData(client.getSeqNum()), exitAck.getSize());
                }

        }else if (msgId == ClientMsg.CS_CH_DISCONNECT_REQ) {
            DisconnectAck disconnectAck = new DisconnectAck();
            client.write(disconnectAck.getData(client.getSeqNum()), disconnectAck.getSize());
        }else if (msgId == ClientMsg.CS_CH_LOGOUT_REQ) {
            LogoutAck logoutAck = new LogoutAck();
            client.write(logoutAck.getData(client.getSeqNum()), logoutAck.getSize());
        }else if (msgId == ClientMsg.CS_GI_ITEM_COUNT_REQ) {
            GiftItemCountAck giftItemCountAck = new GiftItemCountAck();
            client.write(giftItemCountAck.getData(client.getSeqNum()), giftItemCountAck.getSize());
        }else if (msgId == ClientMsg.CS_FD_ENTER_REQ) {
            EnterAck enterAck = new EnterAck(buffer);
            System.out.println("Entering Room " + enterAck.getRoomNumber());
            short n = enterAck.getRoomNumber();
             Room room = getRoomWithNumber(n);
            if(room != null)
            {
                if(room.isFull())
                {
                    client.write(enterAck.getData(client.getSeqNum(), client, EnterAck.ROOM_FULL), enterAck.getSize());
                }else if(!room.isOpen)
                {
                    client.write(enterAck.getData(client.getSeqNum(), client, EnterAck.ALMOST_OVER), enterAck.getSize());
                }else {
                    RoomCharsAck charsAck = new RoomCharsAck();
                    room.addUser(client);
                    room.write(enterAck, client);
                    client.write(charsAck.getData(client.getSeqNum(), room), charsAck.getSize());
                }
            }else client.write(enterAck.getData(client.getSeqNum(), client, EnterAck.DOES_NOT_EXIST), enterAck.getSize());
            
        }else if (msgId == ClientMsg.CS_FD_PLAYERDEAD_REQ)
        {
            PlayerDeadAck playerDeadAck = new PlayerDeadAck(buffer);
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                room.playerDead(client, playerDeadAck);
            }
        }else if (msgId == ClientMsg.CS_FD_REVIVALUSER_REQ)
        {
            RevivalAck revivalAck = new RevivalAck(buffer, client.getRoomTeam());
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                room.userRevived(client);
                room.write(revivalAck);
                //client.write(revivalAck.getData(client.getSeqNum()), revivalAck.getSize());
            }
        }else if (msgId == ClientMsg.CS_FD_PLANTBOMB_REQ)
        {
            PlantBombAck plantBombAck = new PlantBombAck();
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                room.write(plantBombAck);
                room.plantBomb();
            }
        }else if (msgId == ClientMsg.CS_FD_REMOVEBOMB_REQ)
        {
            RemoveBombAck removeBombAck = new RemoveBombAck();
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                room.write(removeBombAck);
                room.removeBomb();
            }
        }else if (msgId == ClientMsg.CS_FD_CHANGEFLAG_REQ)
        {
            ChangeFlagAck changeFlagAck = new ChangeFlagAck(buffer);
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                room.write(changeFlagAck);
                room.changeFlag(changeFlagAck.getFlag(), changeFlagAck.getTeam());
            }
        }else if (msgId == ClientMsg.CS_FD_GAMEPOINTS_REQ)
        {
            Room room = getRoomWithNumber(client.getRoomNumber());
            if(room != null)
            {
                GamePointsAck gamePointsAck = new GamePointsAck();
                //room.write(gamePointsAck);
                client.write(gamePointsAck.getData(client.getSeqNum(), client), gamePointsAck.getSize());
                System.out.println("gamePointsAck");
                //client.write(endGameAck.getData(client.getSeqNum()), endGameAck.getSize());
            }
        }else if (msgId == ClientMsg.CS_IN_GETGOLDCASH_REQ)
        {
            GetGoldCashAck getGoldCashAck = new GetGoldCashAck(100000, 10000, 50000);
            client.write(getGoldCashAck.getData(client.getSeqNum()), getGoldCashAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_BUYITEM_REQ)
        {
            BuyItemAck buyItemAck = new BuyItemAck(buffer, client.getUserId());
            byte errorcode = 0;
            client.write(buyItemAck.getData(client.getSeqNum(), errorcode), buyItemAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_CHANGE_ITEM_IN_INVEN_REQ)
        {
            ChangeItemInInvenAck changeItemInInvenAck = new ChangeItemInInvenAck(buffer);
            client.write(changeItemInInvenAck.getData(client.getSeqNum()), changeItemInInvenAck.getSize());
        }else if (msgId == ClientMsg.CS_IN_EQUIP_REQ)
        {
            EquipAck equipAck = new EquipAck(buffer, client.getUserId());
            client.write(equipAck.getData(client.getSeqNum()), equipAck.getSize());
        }else {
            System.out.println("Undefined Msg Recieved " + msgId);
        }
        
        
    }
    
    private void processUdp(byte msg [])
    {
        if(msg[0] == 2 && msg.length == 16)
        {
            byte session_key[] = new byte [6];
            for(int i = 0; i < 6; i++)
            {
                session_key[i] = msg[i + 4];
            }
            int udpIp = (msg[10] & 0xff) + (msg[11] & 0xff)*0x100 + (msg[12] & 0xff)*0x10000 + (msg[13] & 0xff)*0x1000000;
            short udpPort = (short) ((msg[14] & 0xff) + (msg[15] & 0xff)*0x100);
            System.out.println("UDP :" + Integer.toHexString(udpIp) + " " + udpPort);
            Client c = getClient(session_key);
            if(c != null)
            {
                System.out.println("Client found " + c.getLoginName());
                c.UdpAddr(udpIp, udpPort);
            }else {
                System.out.println("Client not found");
            }
        }
    }
    
    public synchronized void writeToChannel(ServerMsg msg)
    {
        ArrayList<Client> clientList = getClientList();
        for (int i = 0; i < clientList.size(); i++) {
            try {
                Client c = clientList.get(i);
                if (c != null) {
                    if(c.getRoomNumber() == -1)
                        c.write(msg.getData(c.getSeqNum()), msg.getSize());
                }
            } catch (Exception ex) {

            }
        }
    }
    
    public synchronized Room createRoom(CreateAck c)
    {
        short n = 0;
        for(int i = n; i < 1000; i++)
        {
            boolean exists = false;
            for(int j = 0; j < rooms.size(); j++)
            {
                Room r = rooms.get(j);
                if(r.getRoomNumber() == i)
                {
                    exists = true;
                    break;
                }
            }
            if(!exists)
            {
                n = (short) i;
                break;
            }
        }
        //n = (short) (n + 1);
        System.out.println("Creating Room " + n + " Mode " + c.getMode() + " Wins " + c.getWins() + " Time " + c.getTime());
        Room newRoom = new Room(c.getRoomTitle(), c.getRoomPassword(), n, c.getCapacity(), c.getMap(), c.getMode(), c.getWins(), c.getPoints(), c.getTime());
        rooms.add(newRoom);
        return newRoom;
    }
    
    public synchronized Room getRoomWithNumber(short n)
    {
        for(int i = 0; i < rooms.size(); i++)
        {
            if(rooms.get(i).getRoomNumber() == n)
                return rooms.get(i);
        }
        return null;
    }
    
    public class Room 
    {
        String title;
        String password;
        short roomNumber;
        byte roomCapacity;
        byte nUsers;
        boolean isOpen;
        boolean gameStarted;
        boolean roundStarted;
        Client users[];
        byte roomMaster;
        byte map, mode, wins, points;
        short time;
        short redScore, blueScore;
        long roundTime, plantTime;
        ArrayList<Client> deadList;
        byte flags[] = new byte [5];
        byte roundNumber;
        
        public Room (String title, String password, short roomNumber, byte roomCapacity, byte map, byte mode, byte wins, byte points, short time)
        {
            gameStarted = false;
            this.title = title;
            this.password = password;
            this.roomNumber = roomNumber;
            this.roomCapacity = roomCapacity;
            this.map = map;
            this.mode = mode;
            this.wins = wins;
            this.points = points;
            this.time = time;
            //users = new ArrayList<Client>();
            users = new Client[17];
            for(int i = 0; i < 17; i++)
                users[i] = null;
            roomMaster = 0;
            nUsers = 0;
            deadList = new ArrayList<Client>();
            deadList.clear();
            isOpen = true;
            roundStarted = false;
            
        }
        
        public void process()
        {
            //Process Timeout
            long currentTime = (System.currentTimeMillis() - roundTime) / 1000;
            if(currentTime > time && roundStarted && gameStarted)
            {
                if (isDeathMatch()) {
                    if (redScore >= blueScore) {
                        endRound((byte) 1, true);
                    }else endRound((byte) 2, true);
                }
                
                if (isIceHold() || isDestruction()) {
                    boolean bEndGame = false;    
                    int r = getTeamDeadCount((byte)1);
                        int b = getTeamDeadCount((byte)2);
                        if(r <= b)
                            redScore++;
                        else blueScore++;
                        if (redScore >= wins || blueScore >= wins) {
                            bEndGame = true;
                        if (redScore >= blueScore) {
                            endRound((byte) 1, bEndGame);
                        }else endRound((byte) 2, bEndGame);
                    }
                }
                if(isConquest())
                {
                    boolean bEndGame = false;
                    int r = getFlagCount((byte) 1);
                    int b = getFlagCount((byte)2);
                         if(r <= b)
                            redScore++;
                        else blueScore++;
                        if (redScore >= wins || blueScore >= wins) {
                            bEndGame = true;
                        if (redScore >= blueScore) {
                            endRound((byte) 1, bEndGame);
                        }else endRound((byte) 2, bEndGame);
                       }
                }
                //endRound((byte)1, true);
            }
            long bombTime = (System.currentTimeMillis() - plantTime) / 1000;
            //System.out.println("bombTime " + bombTime + " " + plantTime);
            if(bombTime > 40 && plantTime != 0)
            {
                plantTime = 0;
                redScore++;
                boolean bEndGame = false;
                if(redScore >= wins)
                    bEndGame = true;
                endRound((byte)1, bEndGame);
            }
        }
        
        public synchronized void write(byte buffer [], short size, Client c)
        {
            c.write(buffer, size);
        }
        
        public void write(EnterAck enterAck, Client client)
        {
            for(int i = 0; i < users.length; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    System.out.println("Sending EnterAck to " + c.getLoginName());
                    write(enterAck.getData(c.getSeqNum(), client, EnterAck.ROOM_OK), enterAck.getSize(), c);
                }
            }
        }
        
        public void write(RoomCharsAck charsAck)
        {
            for(int i = 0; i < users.length; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    System.out.println("Sending RoomCharsAck to " + c.getLoginName());
                    write(charsAck.getData(c.getSeqNum(), this), charsAck.getSize(), c);
                }
            }
        }
        
        public void write(ExitAck exitAck)
        {
            for(int i = 0; i < users.length; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    write(exitAck.getData(c.getSeqNum()), exitAck.getSize(), c);
                }
            }
        }
        
        public void write(ChangeBossAck changeBossAck)
        {
            for(int i = 0; i < users.length; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    write(changeBossAck.getData(c.getSeqNum()), changeBossAck.getSize(), c);
                }
            }
        }
        
        public void write(ServerMsg msg)
        {
            for(int i = 0; i < users.length; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    write(msg.getData(c.getSeqNum()), msg.getSize(), c);
                }
            }
        }
        
        public synchronized void addUser(Client user)
        {
            short slot = -1;
            for(int i = 0; i < users.length; i++)
                if(users[i] == null)
                {
                    users[i] = user;
                    slot = (short)i;
                    break;
                }
            nUsers++;
            user.setRoomNumber(this.roomNumber);
            user.setRoomSlot(slot);
            byte userTeam = 1;
            if(getTeamCount((byte)2) < getTeamCount((byte)1))
                userTeam = 2;
            user.setRoomTeam((byte) userTeam);
            int team_slot = -1;
            for(int j = 0; j < roomCapacity; j++)
            {
                boolean bFound = false;
                for(int i = 0; i < users.length; i++)
                {
                    if(users[i] != null && users[i] != user)
                        if(users[i].getRoomTeam() == user.getRoomTeam())
                            if(users[i].getRoomTeamSlot() == j)
                            {
                                System.out.println("Joining Found Same " + users[i].getLoginName() + " " + users[i].getRoomTeam() + " " + users[i].getRoomTeamSlot());
                                bFound = true;
                            }
                }
                if(bFound == false)
                {
                    user.setRoomTeamSlot((byte) j);
                    user.setRoomSlotStatus((byte)0);
                    break;
                }
            }
        }
        
        public synchronized void changeTeam(Client user)
        {
            byte newTeam = 1;
            if (user.getRoomTeam() == 1) {
                newTeam = 2;
            }
            user.setRoomTeam(newTeam);
            for(int j = 0; j < roomCapacity; j++)
            {
                boolean bFound = false;
                for(int i = 0; i < users.length; i++)
                {
                    if(users[i] != null && users[i] != user)
                        if(users[i].getRoomTeam() == user.getRoomTeam())
                            if(users[i].getRoomTeamSlot() == j)
                            {
                                System.out.println("Found Same " + users[i].getLoginName() + " " + users[i].getRoomTeam() + " " + users[i].getRoomTeamSlot());
                                bFound = true;
                            }
                }
                if(bFound == false)
                {
                    user.setRoomTeamSlot((byte) j);
                    break;
                }
            }
        }
        
        public synchronized void removeUser(Client user)
        {
            byte team = user.getRoomTeam();
            deadList.remove(user);
            for(int i = 0; i < users.length; i++)
                if(users[i] == user)
                {
                    users[i] = null;
                    if(i == roomMaster)
                        roomMaster = makeNewMaster();
                    break;
                }  
            nUsers--;
            user.setRoomNumber((short)-1);
            user.setRoomSlot((short)-1);
            user.setRoomTeam((byte)-1);
            user.setRoomTeamSlot((byte)-1);
            user.setRoomSlotStatus((byte)0);
            if(nUsers == 0)
                rooms.remove(this);
            if(getTeamCount(team) == 0)
            {
                if(team == 2){
                    redScore++;
                    endRound((byte)1, true);
                }else if(team == 1)
                {
                    blueScore++;
                    endRound((byte)2, true);
                }
                
            }
        }
        
        public byte makeNewMaster()
        {
            byte newMaster = 0;
            for(int i = 0; i < users.length; i++)
            {
                if(users[i] != null)
                {
                    newMaster = (byte)i;
                    break;
                }
            }
            ChangeBossAck changeBossAck = new ChangeBossAck(newMaster);
            write(changeBossAck);
            return newMaster;
        }
        
        public void startGame()
        {
            if(gameStarted == false)
            {
                gameStarted = true;
                redScore = 0;
                blueScore = 0;
                roundNumber = 1;
                for(int i = 0; i < users.length; i++)
                    if(users[i] != null)
                        if(users[i].getRoomSlotStatus() == 1)
                        {
                            Client client = users[i];
                            client.setRoomSlotStatus((byte)3);
                            ChangeSlotStatus changeSlotStatus = new ChangeSlotStatus(this.getUserSlot(client), (byte)3);
                            write(changeSlotStatus);
                        }
                
            }

        }
        
        public void startRound()
        {
            roundTime = System.currentTimeMillis();
            roundStarted = true;
            for(int i = 0; i < 5; i++)
                flags[i] = 0;
        }
        
        public void endGame()
        {
            gameStarted = false;
        }
        
        public void endRound(byte winTeam, boolean bEndGame)
        {
            if(!roundStarted)
                return;
            roundNumber++;
            Room room = this;
            EndRoundAck endRoundAck = new EndRoundAck(roundNumber, winTeam, (byte) redScore, (byte) blueScore);
            room.write(endRoundAck);
            EndGameAck endGameAck = new EndGameAck();
            RestartRoundAck restartRoundAck = new RestartRoundAck();
            Boolean isEndGame = new Boolean(bEndGame);
            deadList.clear();
            if (isEndGame) {
                endGame();
            }
            roundStarted = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isEndGame) {
                        roundStarted = true;
                        room.write(restartRoundAck);
                    } else {
                        room.write(endGameAck);
                        for (int i = 0; i < users.length; i++) {
                            if (users[i] != null) {
                                users[i].setRoomSlotStatus((byte)0);
                            }
                        }
                    }
                }
            }, 10000);
        }
        
        public void playerDead(Client c, PlayerDeadAck playerDeadAck)
        {
            Room room = this;
            boolean bEndGame = false;
            if(c != null)
            {
                if(c.roomTeam == 2)
                {
                    if (isDeathMatch()) {
                        redScore++;
                        if (redScore >= points) {
                            endRound((byte)1, bEndGame);
                        }
                    }

                    if (isIceHold() || isDestruction()) {
                        deadList.add(c);
                        if(isTeamDead(c.roomTeam))
                        {
                        redScore++;
                        if (redScore >= wins)
                            bEndGame = true;
                        endRound((byte)1, bEndGame);
                        }
                    }

                }
                    
                if (c.roomTeam == 1) {
                    if (isDeathMatch()) {
                        blueScore++;
                        if (blueScore >= points) {
                            endRound((byte)2, bEndGame);
                        }
                    }

                    if (isIceHold() || isDestruction()) {
                        deadList.add(c);
                        if(isTeamDead(c.roomTeam))
                        {
                        blueScore++;
                        if (blueScore >= wins)
                            bEndGame = true;
                        endRound((byte)2, bEndGame);
                        }
                    }
                }
            }
            playerDeadAck.setScore(room.getRedScore(), room.getBlueScore());
            playerDeadAck.setSlot((byte)c.getRoomSlot());
            room.write(playerDeadAck);
            int killer_id = users[playerDeadAck.getKillerSlot()].getUserId();
            int dead_id = c.getUserId();
            Users users = new Users();
            users.registerKill(killer_id, dead_id);
        }
        
        public void plantBomb()
        {
            plantTime = System.currentTimeMillis();
            roundTime = roundTime + 60000;
        }
        
        public void removeBomb()
        {
            plantTime = 0;
            blueScore++;
            boolean bEndGame = false;
            if (blueScore >= wins) {
                bEndGame = true;
            }
            endRound((byte)2, bEndGame);
        }
        
        public void changeFlag(byte flag, byte team)
        {
            if(flag >= 0 && flag < 5)
            {
                flags[flag] = team;
            }
            boolean same = true;
            for(int i = 0; i < 5; i++)
            {
                if(flags[i] != flags[0])
                {
                    same = false;
                    break;
                }
            }
            if(same)
            {
                boolean bEndGame = false;
                if(team == 1)
                    redScore++;
                else if(team == 2)
                    blueScore++;
                if (redScore >= wins || blueScore >= wins)
                    bEndGame = true;
                endRound(team, bEndGame);
                
                for(int i = 0; i < 5; i++)
                    flags[i] = 0;
            }
        }
        
        public int getFlagCount(byte team)
        {
            int count = 0;
            for(int i = 0; i < 5; i++)
                if(flags[i] == team)
                    count++;
            return count;
        }
        
        public ArrayList<Client> getUsers()
        {
            ArrayList<Client> clients = new ArrayList<Client>();
            for(int i = 0; i < users.length; i++)
                if(users[i] != null)
                {
                    clients.add(users[i]);
                }
            return clients;
        }
        
        public byte getUserSlot(Client c)
        {
            for(int i = 0; i < users.length; i++)
            {
                if(users[i] == c)
                    return (byte)i;
            }
            return -1;
        }
        
        public short getRoomNumber()
        {
            return roomNumber;
        }

        public String getRoomTitle()
        {
            return title;
        }
 
        public String getRoomPassword()
        {
            return password;
        }
        
        public byte getNumberOfUsers()
        {
            return nUsers;
        }
        
        public byte getMap() {
            return map;
        }

        public byte getMode() {
            return mode;
        }

        public byte getWins() {
            return wins;
        }

        public byte getPoints() {
            return points;
        }

        public byte getCapacity() {
            return roomCapacity;
        }

        public short getTime() {
            return time;
        }
      
        public short getRedScore() {
            return redScore;
        }
        
        public short getBlueScore() {
            return blueScore;
        }
        
        public short getCurrentTime()
        {
            long currentTime = (short) ((System.currentTimeMillis() - roundTime) / 1000);
            System.out.println("Current Time " + currentTime + " " + (short) (time - currentTime));
            return (short) (time - currentTime);
        }
        
        public byte getRoundNumber()
        {
            return roundNumber;
        }
        
        public boolean isDeathMatch()
        {
            if(mode == 2 || mode == 18 || mode == 10)
                return true;
            return false;
        }
        public boolean isConquest() {
            if (mode == 1 || mode == 17 || mode == 9 || mode == -127) {
                return true;
            }
            return false;
        }
        public boolean isIceHold()
        {
            if(mode == 32 || mode == 48)
                return true;
            return false;
        }
        public boolean isDestruction() {
            if (mode == 4 || mode == 20 || mode == -124) {
                return true;
            }
            return false;
        }
        public boolean isExconquest() {
            if (mode == 65) {
                return true;
            }
            return false;
        }
        public boolean isWolfhunt() {
            if (mode == -128) {
                return true;
            }
            return false;
        }
        public boolean isFull()
        {
            return roomCapacity == nUsers;
        }
        public int getTeamCount(byte team)
        {
            int count = 0;
            for(int i = 0; i < 17; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    if(c.getRoomTeam() == team)
                        count++;
                }
            }
            return count;
        }
        public int getTeamReadyCount(byte team)
        {
            int count = 0;
            for(int i = 0; i < 17; i++)
            {
                Client c = users[i];
                if(c != null)
                {
                    if(c.getRoomTeam() == team && c.getRoomSlotStatus() > 0)
                        count++;
                }
            }
            return count;
        }
        public boolean isTeamDead(byte team)
        {
            if(isDestruction() && (plantTime != 0 && team == 2) )
                return false;
            int count = 0;
            for(int i = 0; i < deadList.size(); i++)
            {
                Client c = deadList.get(i);
                if(c != null)
                {
                    if(c.getRoomTeam() == team)
                        count++;
                }
            }
            return (count == getTeamCount(team));
        }
        public int getTeamDeadCount(byte team)
        {
            int count = 0;
            for(int i = 0; i < deadList.size(); i++)
            {
                Client c = deadList.get(i);
                if(c != null)
                {
                    if(c.getRoomTeam() == team)
                        count++;
                }
            }
            return count;
        }
        public int getRoomMaster()
        {
            return roomMaster;
        }
        public void userRevived(Client c) {
            deadList.remove(c);
        }
    }
    @Override
    public synchronized void removeClient(Client c)
    {
        int n = c.getRoomNumber();
        if(n >= 0)
        {
            Room room = getRoomWithNumber((short) n);
            if(room != null)
            {
                ExitAck exitAck = new ExitAck(room.getUserSlot(c));
                room.write(exitAck);
                room.removeUser(c);
            }
        }
        super.removeClient(c);
    }
}
