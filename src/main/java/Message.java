import java.io.*;

public class Message implements Serializable {
    private final byte[] message;
    private final long messageID;
    private final String sender;

    private final long lastSequence;

    public Message(long msgID, String sender, byte[] message, long lastSequence) {
        this.message = message;
        this.sender = sender;
        this.messageID = msgID;
        this.lastSequence = lastSequence;
    }

    public long getLastSequence() {
        return lastSequence;
    }

    public byte[] getMsg() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public long getMsgID() {
        return messageID;
    }

    public static byte[] toByteStream(Message message) throws Exception {
        byte[] data;

        try (ByteArrayOutputStream bas = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bas)) {
            oos.writeObject(message);
            data = bas.toByteArray();
        }
        return data;
    }

    public static Message fromByteStream(byte[] data) throws Exception {
        Message message;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            message = (Message) ois.readObject();
        }

        return message;
    }
}
