import java.rmi.RemoteException;
import java.time.ZoneId;
import java.util.*;
import java.time.LocalDate;

public class TestSequencer {

    static Stack<Long> seguences;
    static Sequencer testsequencer;

    public static void main(String[] args) {
        seguences = new Stack<>();
        int min = 1;
        int max = 254;
        // Random number section
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // Date section
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        //multicast IPAddress
//        String multicastAddress = "234." + day + "." + month + "." + random_int;
        String multicastAddress = "230.0.0.0";

        // Getting input from the user
        String sender = null;
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your name: ");
        sender = input.nextLine();


        String finalSender = sender;
        Group.MsgHandler handler = (count, msg) -> {
            try {
                Scanner it = new Scanner(System.in);
                Message messageFrom = Message.fromByteStream(msg);
                String message = new String(messageFrom.getMsg());
                if (!Objects.equals(messageFrom.getSender(), finalSender)) {
                    System.out.println("Message from " + messageFrom.getSender() + " :" + message);
                }
                seguences.push(messageFrom.getLastSequence());
                send(date, it, testsequencer, finalSender);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        };

        testsequencer = new SequencerImpl(multicastAddress, handler, sender);

        try {
            testsequencer.join(sender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        send(date, input, testsequencer, sender);
    }

    private static void send(Date date, Scanner input, Sequencer testsequencer, String sender) {

        try {
            String message_id = "15786" + date.getTime();
            System.out.print("Enter message: ");
            String message = input.nextLine();
            if (message.toLowerCase().trim().equals("exit")) {
                input.close();
                testsequencer.leave(sender);
            }
            long lastSequence = 0;
            if (!seguences.empty()) lastSequence = seguences.peek();
            testsequencer.send(sender, message.trim().getBytes(), Long.parseLong(message_id), lastSequence);
            seguences.push(lastSequence + 1);
        } catch (RemoteException e) {
            input.close();
            throw new RuntimeException(e);
        }
    }
}
