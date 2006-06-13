package edu.iastate.ato.agent ;

import java.io.BufferedReader ;
import java.io.DataOutputStream ;
import java.io.IOException ;
import java.io.InputStreamReader ;
import java.io.UnsupportedEncodingException ;
import java.net.InetAddress ;
import java.net.ServerSocket ;
import java.net.Socket ;
import java.net.URLDecoder ;
import java.net.URLEncoder ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.Map ;

class Buddy
{
    String id ; // the id i give to him
    String name = "", host, port ; // buddy's information
    String myID ; // my id that the buddy give me. (I use this to identify myself)

    public String toString()
    {
        return name ;
    }
}

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-25</p>
 */
public class MoAgent extends Thread
{
    static String enc = "GB2312" ;

    int port ;
    String ip ;
    public String myName = "Anonymous" ;

    public MoAgent(String myName)
    {
        this.myName = myName ;
    }

    int counter = 0 ;

    // hash table: buddy id -> buddy information
    Map<String, Buddy> myBuddyList = new HashMap() ;

    // hash table: buddy id -> chat window(session)
    Map<String, ChatPanel> chatWindows = new HashMap() ;

    /**
     * Get or create a chat window (start a chat session)
     * @param user String
     * @param buddyHost String
     * @param buddyPort String
     * @return ChatPanel
     */
    public ChatPanel getChatWindow(Buddy b)
    {
        // get the chat panel
        ChatPanel window = chatWindows.get(b.id) ;
        if(window != null) // session is on
        {
            return window ;
        }
        else // create the window
        {
            // create new
            window = new ChatPanel(b) ;
            chatWindows.put(b.id, window) ;
            return window ;
        }

    }

    public static String WHO_ARE_YOU = "WhoAreYou!" ;
    public String sendWhoAreYou(String buddyHost, String buddyPort)
    {
        String cmd = WHO_ARE_YOU ;
        return sendToServer(cmd, buddyHost, buddyPort) ;
    }

    public static String I_ADDED_YOU = "I_added_you_as_buddy" ;
    public String addBuddy(String buddyHost, String buddyPort)
    {
        String buddyName = sendWhoAreYou(buddyHost, buddyPort) ;

        // tell the buddy he is added by me
        sendToServer(I_ADDED_YOU + myName + ":" + ip + ":" + port,
            buddyHost, buddyPort) ;
        return buddyName ;
    }

    public void handleAddBuddy(DataOutputStream outToClient,
        String msg) throws IOException
    {
        if(messageListener != null)
        {
            if(msg.startsWith(I_ADDED_YOU))
            {
                String str = msg.substring(I_ADDED_YOU.length()) ;
                String s[] = str.split(":", 3) ;
                String buddyName = s[0] ;
                String buddyHost = s[1] ;
                String buddyPort = s[2] ;
                messageListener.onBeAddedAsBuddy(buddyName, buddyHost,
                    buddyPort) ;
            }
        }
        outToClient.writeBytes(OK + "\n") ;
    }

    ChatListener messageListener ;
    public void addMessageListener(ChatListener messageListener)
    {
        this.messageListener = messageListener ;
    }

    public static String YOUR_ID = ">>your_id=" ;

    // return the buddy information if succeed
    public Buddy sendID(String myID, String buddyName, String buddyHost,
        String buddyPort)
    {
        String buddyID = (counter++) + "" ;

        String cmd = YOUR_ID + myID + ":" + buddyID ;
        String res = sendToServer(cmd, buddyHost, buddyPort) ;
        if(OK.equals(res))
        {
            Buddy b = new Buddy() ;
            b.id = buddyID ;
            b.name = buddyName ;
            b.myID = myID ;
            b.host = buddyHost ;
            b.port = buddyPort ;
            return b ;
        }
        return null ;
    }

    // get my id assigned by my buddy
    public void handleYourID(DataOutputStream outToClient,
        String msg) throws IOException
    {
        if(msg.startsWith(YOUR_ID))
        {
            String str = msg.substring(YOUR_ID.length()) ;
            String s[] = str.split(":", 2) ;
            String senderID = s[0] ; // the id i give to my buddy
            String myID = s[1] ; // i will use this id to send message to the sender
            Buddy b = myBuddyList.get(senderID) ;
            b.myID = myID ;
            outToClient.writeBytes(OK + "\n") ;
        }

    }

    public void run()
    {
        try
        {
            ServerSocket welcomeSocket = new ServerSocket(0) ; // choose any free port

            port = welcomeSocket.getLocalPort() ;
            InetAddress thisIp = InetAddress.getLocalHost() ;
            ip = thisIp.getHostAddress() ;

            System.out.println("\nMOEditor Agent starts " +
                new Date().toString()) ;
            System.out.println("Port: " + port) ;
            System.out.println("Host: " + ip) ;

            String clientSentence ;
            while(true)
            {
                Socket connectionSocket = welcomeSocket.accept() ;

                BufferedReader inFromClient = new BufferedReader
                    (new InputStreamReader(connectionSocket.getInputStream())) ;

                DataOutputStream outToClient = new DataOutputStream(
                    connectionSocket.getOutputStream()) ;

                clientSentence = inFromClient.readLine() ;

                // decode
                try
                {
                    clientSentence = URLDecoder.decode(clientSentence, enc) ;
                }
                catch(UnsupportedEncodingException ex1)
                {
                }

                // handle the sentence
                System.out.println(clientSentence) ;
                if(clientSentence.equals(HELLO))
                {
                    sendToClient(outToClient, OK) ;
                }
                else if(clientSentence.startsWith(WHO_ARE_YOU)) // normal word
                {
                    sendToClient(outToClient, myName) ;
                }
                else if(clientSentence.startsWith(MSG)) // normal word
                {
                    handleMessage(outToClient, clientSentence) ;
                }
                else if(clientSentence.startsWith(START))
                {
                    handleStartSession(outToClient, clientSentence) ;
                }
                else if(clientSentence.startsWith(YOUR_ID))
                {
                    handleYourID(outToClient, clientSentence) ;
                }
                else if(clientSentence.startsWith(this.I_ADDED_YOU))
                {
                    handleAddBuddy(outToClient, clientSentence) ;
                }
                else
                {
                    sendToClient(outToClient, UNKNOWN) ;
                }

            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace() ;
        }

    }

    static String OK = "ok" ;
    static String FAIL = ">>fail" ;
    static String UNKNOWN = "Unknown Command" ;
    static String HELLO =
        "HiNihaoTHisISSOMESTRANGEWORDTHATWILLNEVERBESENTBYUSERS" ;
    static String MSG = "indus://" ;
    static String USER = "user=" ;
    static String CONTENT = "content=" ;
    static String HOST = "host=" ;
    static String PORT = "port=" ;

    static String START = ">>>>>>" ;

    static public boolean isServerOn(String serverHost, String serverPort)
    {
        String res = sendToServer(HELLO, serverHost, serverPort) ;
        return OK.equals(res) ;
    }

    public void sendToClient(DataOutputStream outToClient,
        String msg) throws IOException
    {
        System.out.println("Send back: " + msg) ;
        outToClient.writeBytes(msg + "\n") ;
    }

    // return: my id assigned by the buddy
    public String startSession(String myHost, String myPort,
        String myName, String buddyHost, String buddyPort)
    {
        String str = MoAgent.START + myName + "@" + myHost + ":" + myPort ;
        return sendToServer(str, buddyHost, buddyPort) ;
    }

    public void handleStartSession(DataOutputStream outToClient,
        String msg) throws IOException
    {
        if(msg.startsWith(START))
        {
            String buddyLabel = msg.substring(START.length()) ; // guest@123.34.23.56:90
            String s[] = buddyLabel.split("@", 2) ;
            String name = s[0] ;
            s = s[1].split(":", 2) ;
            String host = s[0] ;
            String port = s[1] ;

            // find the ID of this buddy
            // get user id
            String buddyID = (counter++) + "" ;
            Buddy b = new Buddy() ;
            b.id = buddyID ;
            b.name = name ;
            b.host = host ;
            b.port = port ;
            this.myBuddyList.put(buddyID, b) ;

            outToClient.writeBytes(buddyID + "\n") ;
        }
    }

    static public String sendMessage(String msg, Buddy buddy)
    {
        String str = MSG + buddy.myID + ":" + msg ;
        return sendToServer(str, buddy.host, buddy.port) ;
    }

    public void handleMessage(DataOutputStream outToClient,
        String msg) throws IOException
    {
        if(msg.startsWith(MSG))
        {
            String str = msg.substring(MSG.length()) ;
            String s[] = str.split(":", 2) ;
            String client_id = s[0] ;
            String content = s[1] ;
            sendToClient(outToClient, OK) ;

            // find buddy information
            Buddy b = myBuddyList.get(client_id) ;
            if(b != null)
            {
                // show it
                ChatPanel window = getChatWindow(b) ;
                window.append(b.name, content) ;
                window.showMe() ;

            }
        }
    }

    static public String sendToServer(String cmd, String serverHost,
        String serverPort)
    {
        String str = cmd ;
        try
        {
            str = URLEncoder.encode(cmd, enc) ;
        }
        catch(UnsupportedEncodingException ex)
        {
        }
        System.out.println(str) ;

        try
        {
            return sendToServer(str, serverHost, Integer.parseInt(serverPort)) ;
        }
        catch(NumberFormatException ex)
        {
            return null ;
        }
    }

    static public String sendToServer(String cmd, String serverHost,
        int serverPort)
    {
        String res ;

        try
        {
            Socket clientSocket = new Socket(serverHost, serverPort) ;

            DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream()) ;

            BufferedReader inFromServer = new BufferedReader
                (new InputStreamReader(clientSocket.getInputStream())) ;

            outToServer.writeBytes(cmd + '\n') ;
            res = inFromServer.readLine() ;
            clientSocket.close() ;
            return res ;
        }
        catch(Exception ex)
        {
            //ex.printStackTrace() ;
        }
        return FAIL ;
    }

    // TEST
    public static void main(String[] args)
    {
        MoAgent server = new MoAgent("bob") ;
        server.start() ;

        for(int i = 0 ; i < 999999999 ; i++)
        {} // wait for server launch

        String res = MoAgent.sendToServer("Hello 1", server.ip, server.port) ;
        System.out.println(res) ;
        res = MoAgent.sendToServer("Hello 2", server.ip, server.port) ;
        System.out.println(res) ;
    }

    public String getIp()
    {
        return ip ;
    }

    public int getPort()
    {
        return port ;
    }

    public ChatPanel startChat(String buddyHost, String buddyPort)
    {
        String buddyName = this.sendWhoAreYou(buddyHost, buddyPort) ;
        if(buddyName == null)
        {
            return null ;
        }
        return startChat(buddyName, buddyHost, buddyPort) ;
    }

    // start talk with a buddy
    public ChatPanel startChat(String buddyName, String buddyHost,
        String buddyPort)
    {
        // if sever on?
        if(!isServerOn(buddyHost, buddyPort))
        {
            return null ;
        }

        // if the session is already open
        Buddy b = myBuddyList.get(buddyName) ;
        if(b == null)
        {
            // server on, send shake hand
            String myID = startSession(ip, port + "", myName, buddyHost,
                buddyPort) ;
            if(myID == null)
            {
                return null ;
            }

            // tell buddy his id
            b = sendID(myID, buddyName, buddyHost, buddyPort) ;
            if(b == null)
            {
                return null ;
            }

            // buddy information should already be created
            myBuddyList.put(b.id, b) ;
        }
        // create a chat window
        return getChatWindow(b) ;
    }
}
