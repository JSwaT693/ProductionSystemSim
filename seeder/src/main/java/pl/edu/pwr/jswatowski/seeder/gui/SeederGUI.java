package pl.edu.pwr.jswatowski.seeder.gui;

import pl.edu.pwr.jswatowski.seeder.seederLogic.Seeder;

import javax.swing.*;

public class SeederGUI extends JFrame {
    private JTextField ipTxt;
    private JTextField portTxt;
    private JButton registerBtn;
    private JPanel panel;
    private JTextField seederPortTxt;
    private JButton seedBtn;
    private JButton moveBtn;
    private JButton unregisterBtn;
    private Seeder seeder;

    public SeederGUI() {
        run();
    }

    private void run() {
        setTitle("Seeder");
        setContentPane(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 270);
        setLocationRelativeTo(null);
        setVisible(true);

        registerBtn.addActionListener(e -> register());
        seedBtn.addActionListener(e -> seeder.seed());
        moveBtn.addActionListener(e -> seeder.move());
        unregisterBtn.addActionListener(e -> unregister());

    }

    private void register() {
        var ip = ipTxt.getText();
        var port = Integer.parseInt(portTxt.getText());
        var localPort = Integer.parseInt(seederPortTxt.getText());

        enableButtons(true);
        seeder = new Seeder(ip, port, localPort);
    }

    private void unregister() {
        enableButtons(false);
        seeder.unregister();
    }

    private void enableButtons(boolean enable) {
        ipTxt.setEnabled(!enable);
        portTxt.setEnabled(!enable);
        seederPortTxt.setEnabled(!enable);
        registerBtn.setEnabled(!enable);
        seedBtn.setEnabled(enable);
        moveBtn.setEnabled(enable);
        unregisterBtn.setEnabled(enable);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var seederStart = new SeederGUI();
            seederStart.setVisible(true);
        });
    }
}
