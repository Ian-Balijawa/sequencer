import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;

public class Group implements Runnable {

    private final MulticastSocket ms; //multicastSocket
    private final InetAddress inetAddress;
    private final int PORT = 6789;
    private final int TIME_TO_LIVE = 1;
    private final MsgHandler msgHandler;
    private static ExecutorService exec;
    private static final long SLEEP_TIME = 1000;


    public Group(String host, MsgHandler msgHandler, String senderName) throws GroupException {
        // contact Sequencer on "host" to join group,
        // create MulticastSocket and thread to listen on it,
        // perform other initialisations
        this.msgHandler = msgHandler;

        try {
            ms = new MulticastSocket(PORT);
            ms.setTimeToLive(TIME_TO_LIVE);
            inetAddress = InetAddress.getByName(host);
            ms.joinGroup(inetAddress);
        } catch (Exception e) {
            throw new GroupException(e.getMessage());
        }
    }

    public InetAddress getInetAddress() {
        return ms.getInetAddress();
    }

    public void send(byte[] msg) throws GroupException {
        // send the given message to all instances of Group using the same sequencer
        try {
            DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, inetAddress, PORT);
            ms.send(datagramPacket);
        } catch (Exception e) {
            throw new GroupException(e.getMessage());
        }
    }

    public void leave() throws GroupException {
        try {
            ms.leaveGroup(inetAddress);
            ms.disconnect();
            ms.close();
        } catch (IOException e) {
            throw new GroupException("Failed to leave group. Error message: " + e.getMessage());
        }
    }

    public void run() {
        byte[] messageBuffer = new byte[256];

        while(true) {
            if(!ms.isClosed()) {
                DatagramPacket datagramPacket = new DatagramPacket(messageBuffer, messageBuffer.length);
                try {
                    ms.receive(datagramPacket);
                    msgHandler.handle(datagramPacket.getLength(), datagramPacket.getData());
                } catch (IOException e) {
                    ms.close();
                    throw new RuntimeException(e);
                }
            }else {
                return;
            }
        }
    }

    public interface MsgHandler {
        public void handle(int count, byte[] msg);
    }

    public static class GroupException extends Exception {
        public GroupException(String s) {
            super(s);
        }
    }

    public static class HeartBeater extends Thread {

        private final HeartBeaterHandler handler;
        public HeartBeater(HeartBeaterHandler handler){
            this.handler = handler;
        }

        int i = 0;
        public void run( ){
            while(true){
                handler.handle(i);
                try {
                    Thread.sleep(SLEEP_TIME); // wait for 1 minute
                } catch ( Exception e){
                    System.out.println(e.getMessage());
                }
                i++; // increment the looping variable

            }

        }

        public  interface HeartBeaterHandler{
            void handle(int i);
        }
    }
} 
