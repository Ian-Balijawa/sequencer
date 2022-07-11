import java.util.*;

public class History {

    private final Queue<Message> messages;

    public History(){
        messages = new LinkedList<>();
    }

    public Message[] getMessages() {
        return messages.toArray(new Message[0]);
    }

    public void addMessage(Message message) {
        if (messages.size() < 1024) {
            messages.add(message);
        }else {
            messages.remove();
            messages.add(message);
        }
    }

    public static class Message {
        private final byte[] msg;
        private final long msgID;
        private final String sender;

        private final long lastSequence;

        public  Message(long msgID, String sender, byte[] msg, long lastSequence) {
            this.msg = msg;
            this.sender = sender;
            this.msgID = msgID;
            this.lastSequence = lastSequence;
        }

        public long getLastSequence() {
            return lastSequence;
        }

        public byte[] getMsg() {
            return msg;
        }

        public String getSender() {
            return sender;
        }

        public long getMsgID() {
            return msgID;
        }
    }
}
