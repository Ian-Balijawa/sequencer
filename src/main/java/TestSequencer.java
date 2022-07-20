import java.rmi.*;
import java.util.*;

public class TestSequencer {
    static Stack<Long> seguenceStack; //stack of sequences
    static Sequencer testsequencer;
    static  GUIScreen guiScreen;

    public static void main(String[] args) {
        seguenceStack = new Stack<>();

        Date date = new Date();

        //static multicast IPAddress
        String multicastAddress = "234.20.7.1";

        // Getting input from the user
        String sender = null;
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Enter your name: ");
            sender = input.nextLine();
        }
        String finalSender = sender;

        GUIScreen.Handler guiHandler = new GUIScreen.Handler() {
            @Override
            public void getTextInput(String message) {
                send(date, message, testsequencer, finalSender);
            }

            @Override
            public void stressTest() {
                for (int i = 0; i <= 30; i++){
                    send(date, "Message: " + i, testsequencer, finalSender);
                }
            }
        };

        Group.MsgHandler handler = (count, msg) -> {
            try {
                Message messageFrom = Message.fromByteStream(msg);
                String message = new String(messageFrom.getMsg());

                guiScreen.queueMessage("Message from " + messageFrom.getSender() + ": " + message);

                seguenceStack.push(messageFrom.getLastSequence());

            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        };

        Group.HeartBeater.HeartBeaterHandler heartBeaterHandler = (int i) -> {
            send(date, "pinging: " + i, testsequencer, finalSender);
        };

        guiScreen = new GUIScreen(guiHandler);
        testsequencer = new SequencerImpl(multicastAddress, handler, heartBeaterHandler, sender);

        try {
            testsequencer.join(sender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void send(Date date, String message, Sequencer testsequencer, String sender) {

        try {
            String messageId = "15786" + date.getTime();
            if (message.toLowerCase().trim().equals("quit")) {
                if(guiScreen != null) {
                    guiScreen.close();
                }
                testsequencer.leave(sender);
            }else if(!message.toLowerCase().trim().isEmpty()) {
                long lastSequence = 0;
                if (!seguenceStack.empty()) lastSequence = seguenceStack.peek();
                testsequencer.send(sender, message.trim().getBytes(), Long.parseLong(messageId), lastSequence);
                seguenceStack.push(lastSequence + 1);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
