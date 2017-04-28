package wtserver.client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.nio.ByteBuffer;

/**
 *
 * @author MSI
 */
public class ServerMsg {
    public static short CS_BR_WORLDLIST_ACK = 4353;
    public static short CS_BR_CHAINLIST_ACK = 4355;
    public static short CS_BR_RELAYLIST_ACK = 4357;
    public static short CS_BR_WORLDINFO_ACK = 4359;
    
    public static short CS_CH_LOGIN_ACK = 4865;
    public static short CS_CH_UDPADDR_ACK = 4877;
    public static short CS_CK_UDPSUCCESS_ACK = 4609;
    
    public static short CS_CH_SNSACCOUNT_ACK = 4935;
    public static short CS_CH_EVENTSHOPINFO_ACK = 4951;
    public static short CS_IN_ITEM_LIMITED_ACK = 5211;
    public static short CS_IN_PACKAGE_ITEM_LIST_ACK = 5251;
    public static short CS_CH_GETMACROMESSAGE_ACK = 4909;
    public static short CS_IN_ITEMPRICE_VERSION_ACK = 5185;
    public static short CS_PR_BATTLERESULT_ACK = 5949;
    public static short CS_IN_ITEMPRICE_INFO_ACK = 5183;
    public static short CS_CH_CHAT_ACK = 4879;
    public static short CS_IN_BUYITEM_ACK = 5125;
    public static short CS_IN_GETGOLDCASH_ACK = 5149;
    public static short CS_IN_CHANGE_ITEM_IN_INVEN_ACK = 5227;
    public static short CS_IN_EQUIP_ACK = 5141;
    
    public static short CS_CH_USERINFO_ACK = 4917;
    public static short CS_CH_CRESTUSERINFO_ACK = 4941;
    public static short CS_IN_ITEMLIST_ACK = 5169;
    public static short CS_IN_EQUIPLIST_ACK = 5171;
    public static short CS_IN_CHARITEMLIST_ACK = 5175;
    public static short CS_IN_CHARINFO_ACK = 5173;
    public static short CS_CH_GETPOWERUSERINFO_ACK = 4903;
    public static short CS_CH_SELECTCHAR_ACK = 4873;
    public static short CS_CN_ADVERTALL_ACK = 5409;
    public static short CS_CH_GETEVENTGOLDINFO_ACK = 4907;
    public static short CS_FD_FIELDLIST_ACK = 5633;
    public static short CS_FD_CRM_POPUP_ACK = 5805;
    public static short CS_GI_ITEM_COUNT_ACK = 6671;
    public static short CS_FD_ENTER_ACK = 5639;
    public static short CS_FD_CHANGETEAM_ACK = 5651;
    public static short CS_FD_CHANGERULE_ACK = 5653;
    
    public static short CS_FD_PLAYERDEAD_ACK = 5685;
    public static short CS_FD_ENDROUND_ACK = 5671;
    public static short CS_FD_GAMEPOINTS_ACK = 5683;
    public static short CS_FD_ENDGAME_ACK = 5659;
    public static short CS_CN_CHARS_ACK = 5379;
    public static short CS_FD_RESTARTROUND_ACK = 5669;
    public static short CS_FD_PLANTBOMB_ACK = 5689;
    public static short CS_FD_REMOVEBOMB_ACK = 5691;
    public static short CS_FD_REVIVALUSER_ACK = 5731;
    public static short CS_FD_CHANGEFLAG_ACK = 5687;
    
    public static short CS_CN_CHANGESHEET_ACK = 5407;
    public static short CS_FD_CREATE_ACK = 5645;
    
    public static short CS_FD_CHARS_ACK = 5637;
    public static short CS_FD_UDPSTART_ACK = 5715;
    public static short CS_IN_BUYNOW_ITEMLIST_ACK = 5207;
    public static short CS_FD_INITFIELD_ACK = 5725;
    
    public static short CS_FD_ADD_AISLOT_ACK = 5749;
    public static short CS_FD_STARTGAME_ACK = 5657;
    public static short CS_FD_STARTROUND_ACK = 5667;
    public static short CS_FD_UDPREADY_ACK = 5693;
    public static short CS_FD_CHANGEBOSS_ACK = 5647;
    public static short CS_FD_CHANGESLOTSTATUS_ACK = 5655;
    public static short CS_FD_ENTERGAME_ACK = 5661;
    
    public static short CS_FD_EXIT_ACK = 5643;
    public static short CS_CH_DISCONNECT_ACK = 4883;
    public static short CS_CH_LOGOUT_ACK = 4913;
    
    protected ByteBuffer buffer;
    protected short size = 0;
    
    public ServerMsg()
    {
        
    }
    
    public void addByte(byte b)
    {
        buffer.put(b);
    }
    
    public void addShort(short s)
    {
        buffer.put((byte) (s));
        buffer.put((byte) (s >>> 8));
    }
    
    public void addInteger(int n)
    {
        buffer.put((byte) (n));
        buffer.put((byte) (n >>> 8));
        buffer.put((byte) (n >>> 16));
        buffer.put((byte) (n >>> 24));        
    }
    
    public void addUnicodeString(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            buffer.put((byte) (c & 0xff));
            buffer.put((byte) (c >>> 8));
        }
    }
    
    public void addAsciiString(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            buffer.put((byte) (c & 0xff));
        }
    }
    
    public byte [] getData(short seqNum)
    {
        System.out.println("Overriden");
        return null;
    }
    
    public short getSize()
    {
        return size;
    }
}
