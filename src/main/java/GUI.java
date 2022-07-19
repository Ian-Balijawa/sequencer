import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private final GUIHandler handler;
    private final DefaultListModel<String> listModel;

    public GUI(GUIHandler handler){
        super("Multicast");
        this.handler = handler;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Creating the Frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter message");
        JTextField tf = new JTextField(10);
        JButton send = new JButton("Send");
        JButton stressTest = new JButton("Stress test");
        JScrollPane scrollPane = new JScrollPane();
        panel.add(label);
        panel.add(tf);
        panel.add(send);
        panel.add(stressTest);

        listModel = new DefaultListModel<>();
        JList<String> jList = new JList<>(listModel);
        send.addActionListener(actionEvent -> {
            handler.getTextInput(tf.getText());
        });

        scrollPane.setViewportView(jList);
        jList.setLayoutOrientation(JList.VERTICAL);

        stressTest.addActionListener(actionEvent -> {
            handler.stressTest();
        });

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.SOUTH, panel);
        this.getContentPane().add(BorderLayout.CENTER, jList);
        this.getContentPane().add(BorderLayout.WEST, scrollPane);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void close(){
        this.close();
    }


    public void queueMessage(String message){
        listModel.addElement(message);
    }

    public interface GUIHandler{
        void getTextInput(String message);
        void stressTest();
    }
}
