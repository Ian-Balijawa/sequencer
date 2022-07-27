import java.util.*;

public class History {

    private final Queue<Message> messagesQueue;
    private final int QUEUE_SIZE = 1024;

    public History(){
        messagesQueue = new LinkedList<>();
    }

    public Message[] getMessagesFromQueue() {
        return messagesQueue.toArray(new Message[0]);
    }

    public Queue<Message> getMessagesQueue() {
        return this.messagesQueue;
    }
    
    public void addMessageToQueue(Message message) {
        if (messagesQueue.size() < QUEUE_SIZE) {
            messagesQueue.add(message);
        }else {
            messagesQueue.remove();
            messagesQueue.add(message);
        }
    }

    public Message findMessageFromQueue(long sequenceNumber){

        for (Message value: messagesQueue){
            if (value.getLastSequence() == sequenceNumber){
                return value;
            }
        }

        return null;
    }
}
