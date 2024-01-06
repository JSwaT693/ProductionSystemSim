package pl.edu.pwr.jswatowski.harvester.gui;

import pl.edu.pwr.jswatowski.harvester.harvesterLogic.Harvester;

import javax.swing.*;

public class HarvesterGUI extends JFrame {
    private JTextField ipTxt;
    private JTextField portTxt;
    private JButton registerBtn;
    private JPanel panel;
    private JTextField harvesterPortTxt;
    private JButton harvestBtn;
    private JButton moveBtn;
    private JButton unregisterBtn;
    private Harvester harvester;

    public HarvesterGUI() {
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
        harvestBtn.addActionListener(e -> harvester.harvest());
        moveBtn.addActionListener(e -> harvester.move());
        unregisterBtn.addActionListener(e -> unregister());

    }

    private void register() {
        var ip = ipTxt.getText();
        var port = Integer.parseInt(portTxt.getText());
        var localPort = Integer.parseInt(harvesterPortTxt.getText());

        enableButtons(true);
        harvester = new Harvester(ip, port, localPort);
    }

    private void unregister() {
        enableButtons(false);
        harvester.unregister();
    }

    private void enableButtons(boolean enable) {
        ipTxt.setEnabled(!enable);
        portTxt.setEnabled(!enable);
        harvesterPortTxt.setEnabled(!enable);
        registerBtn.setEnabled(!enable);
        harvestBtn.setEnabled(enable);
        moveBtn.setEnabled(enable);
        unregisterBtn.setEnabled(enable);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var seederStart = new HarvesterGUI();
            seederStart.setVisible(true);
        });
    }
}
