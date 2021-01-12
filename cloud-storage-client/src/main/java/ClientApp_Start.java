import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientApp_Start extends JFrame{

    JPanel panel;
    JTextArea jTextArea;
    JTextField jTextField;
    private final ClientNetty clientNetty = new ClientNetty((arg) -> {
        jTextArea.append((String) arg[0]);
    });


    public ClientApp_Start() throws HeadlessException {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        setBounds(dimension.width / 2 - 300, dimension.height / 2 - 250, 600, 500);
        setTitle("Cloud-Storage-ClientApp");
        panel = new JPanel(new BorderLayout());
        JPanel jPanel = new JPanel(new BorderLayout());
        jTextArea = new JTextArea();
        jTextArea.setLineWrap(true);
//        jTextArea.setEnabled(false);
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jTextField = new JTextField();
        JButton send = new JButton("SEND");

        panel.add(jScrollPane, BorderLayout.CENTER);
        panel.add(jPanel, BorderLayout.SOUTH);

        jPanel.add(jTextField, BorderLayout.CENTER);
        jPanel.add(send, BorderLayout.EAST);

        add(panel);
        revalidate();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNetty.sendMessage(jTextField.getText());
                // TODO: 08.01.2021 настроить отчисту поля, возврат коретки, отправку по нажатию на Return
            }
        });
    }

    public static void main(String[] args) {
        new ClientApp_Start();
    }
}