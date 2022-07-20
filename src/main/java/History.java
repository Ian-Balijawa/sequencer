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

    public Message findMessage(long sequenceNumber){

        for (Message value: messages){
            if (value.getLastSequence() == sequenceNumber){
                return value;
            }
        }

        return null;
    }
}
