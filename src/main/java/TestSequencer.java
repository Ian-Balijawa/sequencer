import java.rmi.RemoteException;
import java.time.ZoneId;
import java.util.*;
import java.time.LocalDate;
public class TestSequencer {
    static Stack<Long> seguences;
    static Sequencer testsequencer;
    static  GUI gui;

    public static void main(String[] args) {
        seguences = new Stack<>();

        int min = 1;
        int max = 254;
        // Random number selection
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // Date section
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        // dynamic multicast IPAddress
        //  String multicastAddress = "234." + day + "." + month + "." + random_int;

        //static multicast IPAddress
        String multicastAddress = "234.20.7.1";

        // Getting input from the user
        String sender = null;
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your name: ");
        sender = input.nextLine();
        String finalSender = sender;

        GUI.GUIHandler guiHandler = new GUI.GUIHandler() {
            @Override
            public void getTextInput(String message) {
                send(date, message, testsequencer, finalSender);
            }

            @Override
            public void stressTest() {
                for (int i = 0; i <= 30; i++){
                    send(date, "message: " + i, testsequencer, finalSender);
                }
            }
        };


        Group.MsgHandler handler = (count, msg) -> {
            try {
                Message messageFrom = Message.fromByteStream(msg);
                String message = new String(messageFrom.getMsg());

                gui.queueMessage("Message from " + messageFrom.getSender() + ": " + message);

                seguences.push(messageFrom.getLastSequence());

            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        };

        Group.HeartBeater.HeartBeaterHandler heartBeaterHandler = (int i) -> {
            send(date, "pinging: " + i, testsequencer, finalSender);
        };

        gui = new GUI(guiHandler);
        testsequencer = new SequencerImpl(multicastAddress, handler, heartBeaterHandler, sender);

        try {
            testsequencer.join(sender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void send(Date date, String message, Sequencer testsequencer, String sender) {

        try {
            String message_id = "15786" + date.getTime();
            if (message.toLowerCase().trim().equals("exit")) {
                if(gui != null) {
                    gui.close();
                }
                testsequencer.leave(sender);
            }else if(!message.toLowerCase().trim().isEmpty()) {
                long lastSequence = 0;
                if (!seguences.empty()) lastSequence = seguences.peek();
                testsequencer.send(sender, message.trim().getBytes(), Long.parseLong(message_id), lastSequence);
                seguences.push(lastSequence + 1);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
