import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SequencerImpl implements Sequencer {

    private final Group group;
    private final ExecutorService executorService;
    private final Set<String> senders;

    private final History history;

    private final Group.HeartBeater heartBeater;

    public SequencerImpl(String host, Group.MsgHandler handler, Group.HeartBeater.HeartBeaterHandler heartBeaterHandler, String senderName) {
        executorService = Executors.newSingleThreadExecutor();
        senders = new HashSet<>();
        history = new History();

        try {
            group = new Group(host, handler, senderName);
            heartBeater = new Group.HeartBeater(heartBeaterHandler);
            executorService.execute(group);
            heartBeater.start();
        } catch (Group.GroupException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SequencerJoinInfo join(String sender) throws RemoteException, SequencerException {
        // join -- request for "sender" to join sequencer's multicasting service;
        // returns an object specifying the multicast address and the first sequence number to expect
        SequencerJoinInfo sequencerJoinInfo = null;

        try {
            if (!senders.contains(sender.toLowerCase(Locale.ROOT))) {
                InetAddress address = group.getInetAddress();
                sequencerJoinInfo = new SequencerJoinInfo(address, 1);
                senders.add(sender.toLowerCase(Locale.ROOT));
            }
        } catch (Exception e) {
            throw new SequencerException(e.getMessage());
        }


        return sequencerJoinInfo;
    }

    @Override
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived) throws RemoteException {
        // send -- "sender" supplies the msg to be sent, its identifier,
        // and the sequence number of the last received message

        Message message = new Message(
                msgID,
                sender,
                msg,
                (lastSequenceReceived + 1)
        );

        try {
            group.send(Message.toByteStream(message));
            history.addMessageToQueue(message);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public void leave(String sender) throws RemoteException {
        // leave -- tell sequencer that "sender" will no longer need its services

        try {
            if (senders.contains(sender.toLowerCase(Locale.ROOT))) {
                group.leave();
                executorService.shutdown();
                senders.remove(sender.toLowerCase(Locale.ROOT));
            }
        } catch (Group.GroupException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public byte[] getMissing(String sender, long sequence) throws RemoteException, SequencerException {
        // getMissing -- ask sequencer for the message whose sequence number is lost

        Message message = history.findMessageFromQueue(sequence);

        if(message == null){
            throw new SequencerException("Message not found");
        }

        try {
            return Message.toByteStream(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void heartbeat(String sender, long lastSequenceReceived) throws RemoteException {
        // heartbeat -- we have received messages up to number "lastSequenceReceived"

    }
}
