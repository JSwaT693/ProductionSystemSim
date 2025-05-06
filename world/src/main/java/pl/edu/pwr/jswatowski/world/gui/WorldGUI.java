package pl.edu.pwr.jswatowski.world.gui;

import pl.edu.pwr.jswatowski.world.worldLogic.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WorldGUI extends JFrame {

    private WorldPanel worldPanel;
    private final World world;

    public WorldGUI() throws IOException {
        world = new World();
        init();
    }

    private void init() {
        setVisible(true);
        setTitle("World");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 820);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);


        worldPanel = new WorldPanel();
        updateWorld();

        add(worldPanel);
        initRefresher();
    }

    private void initRefresher() {
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                updateWorld();
            }
        });
        timer.start();
    }

    private void updateWorld() {
        var board = world.getBoard();
        worldPanel.updateBoard(board);
    }

    public static void main(String[] args) throws IOException {
        var worldGUI = new WorldGUI();
    }
}
