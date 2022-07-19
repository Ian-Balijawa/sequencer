import java.net.*;
import java.util.*;
import java.io.*;
import java.rmi.*;

public class Group implements Runnable {

    private final MulticastSocket multicastSocket;
    private final InetAddress inetAddress;
    private final int PORT = 6789;
    private final int TTL = 1;
    private final MsgHandler handler;

    public Group(String host, MsgHandler handler, String senderName) throws GroupException {
        // contact Sequencer on "host" to join group,
        // create MulticastSocket and thread to listen on it,
        // perform other initialisations
        this.handler = handler;

        try {
            multicastSocket = new MulticastSocket(PORT);
            multicastSocket.setTimeToLive(TTL);
            inetAddress = InetAddress.getByName(host);
            multicastSocket.joinGroup(inetAddress);
        } catch (Exception e) {
            throw new GroupException(e.getMessage());
        }

    }

    public InetAddress getInetAddress() {
        return multicastSocket.getInetAddress();
    }

    public void send(byte[] msg) throws GroupException {
        // send the given message to all instances of Group using the same sequencer
        try {
            DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, inetAddress, PORT);
            multicastSocket.send(datagramPacket);
        } catch (Exception e) {
            throw new GroupException(e.getMessage());
        }
    }

    public void leave() throws GroupException {
        // leave group
        try {
            multicastSocket.leaveGroup(inetAddress);
            multicastSocket.disconnect();
            multicastSocket.close();
        } catch (IOException e) {
            throw new GroupException("Failed to leave group. Error message: " + e.getMessage());
        }
    }

    public void run() {
        byte[] messageBuffer = new byte[256];
        // repeatedly: listen to MulticastSocket created in constructor, and on receipt
        // of a datagram call "handle" on the instance
        // of Group.MsgHandler which was supplied to the constructor

        while(true) {
            if(!multicastSocket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length);
                try {
                    multicastSocket.receive(packet);
                    handler.handle(packet.getLength(), packet.getData());
                } catch (IOException e) {
                    multicastSocket.close();
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
        // This thread sends heartbeat messages when required
    }
} 
