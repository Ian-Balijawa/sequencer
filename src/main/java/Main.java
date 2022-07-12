import java.io.IOException;
import java.net.MulticastSocket;
import java.time.ZoneId;
import java.util.*;
import java.time.Month;
import java.time.LocalDate;
public class Main {
    public static void main(String[] args) throws IOException {
        int min = 1;
        int max = 254;
        // Random number section
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);

        // Date section
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();

        //multicast IPAddress
        String multicastAddress = "234." + day + "."+ month + "." + random_int;
        System.out.println("Multicast address is : " + multicastAddress);

        // Getting input from the user
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your Message:");
        String message = input.nextLine();
        System.out.println("Your input message is :"+ message );

        //Message ID
        String message_id = "15786"+ date;

        System.out.println("The message id is : " + message_id);

        //SequencerImpl testsequencer = new SequencerImpl(multicastAddress,);

        

    }
}
