import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class SequencerImpl implements Sequencer  {
    // statements
    public SequencerJoinInfo join(String sender)
            throws RemoteException, SequencerException, UnknownHostException {

        InetAddress group = InetAddress.getByName("227.8.9.0");
        SequencerJoinInfo information = new SequencerJoinInfo(group,32423);
        return information;
    };

    // send -- "sender" supplies the msg to be sent, its identifier,
    // and the sequence number of the last received message
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived)
            throws RemoteException{

    };

    // leave -- tell sequencer that "sender" will no longer need its services
    public void leave(String sender)
            throws RemoteException {

    };

    // getMissing -- ask sequencer for the message whose sequence number is "sequence"
    public byte[] getMissing(String sender, long sequence)
            throws RemoteException, SequencerException {

        byte [] buffer = new byte[1000];
        return buffer;

    };

    // heartbeat -- we have received messages up to number "lastSequenceReceived"
    public void heartbeat(String sender, long lastSequenceReceived)
            throws RemoteException{

        // heart beat testing.

    };
}
