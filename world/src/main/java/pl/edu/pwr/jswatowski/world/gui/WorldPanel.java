package pl.edu.pwr.jswatowski.world.gui;

import pl.edu.pwr.jswatowski.world.worldLogic.Field;

import javax.swing.*;
import java.awt.*;

public class WorldPanel extends JPanel {

    private static final int MARGIN = 10;
    private Field[][] board;

    @Override
    protected void paintComponent(Graphics g) {
        var size = getSize();
        int squareHeight = (size.height - 2 * MARGIN) / 5;
        int squareWidth = (size.width - 2 * MARGIN) / 5;
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        for(var i = 0; i < 5; i++) {
            for (var j = 0; j < 5; j++) {
                g.drawRect(MARGIN + i * squareWidth, MARGIN + j * squareHeight, squareWidth, squareHeight);
                if (board[i][j].isMachine()) {
                    g.drawString(board[i][j].getMachine().getRole().toString() + " " + board[i][j].getMachine().getId(),5 * MARGIN + i * squareWidth, 5 * MARGIN + j * squareHeight);
                }
                if (board[i][j].hasPlants()) {
                    g.drawString(board[i][j].getPlants().toString(), 5 * MARGIN + i * squareWidth, 10 * MARGIN + j * squareHeight);
                }
            }
        }
    }

    public void updateBoard(Field[][] board) {
        this.board = board;
        repaint();
    }
}
