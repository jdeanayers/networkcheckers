
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GameFrame extends JFrame implements Runnable, MouseListener {

    private Thread thread;
    private boolean thisPlayerTurn;
    private boolean connected;
    private Socket sock;

    private char[][] boardpieces;

    public GameFrame(String ip) {
        initComponents();
        try {
            sock = new Socket(ip, 6010);
            ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
            int turn = inputstream.readInt();
            if (turn == 1) {
                thisPlayerTurn = true;
            } else {
                thisPlayerTurn = false;
            }
        } catch (IOException e) {
        }
        thread = new Thread(this);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("End Turn");

        jButton2.setText("Surrender");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(66, 66, 66)
                .addComponent(jButton2)
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 372, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(51, 51, 51))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initializeBoard() {
        boardpieces = new char[8][8];
        //r represents red piece, R represents kinged red piece, 
        //w represents white piece, W represents kinged white piece
        for (int i = 1; i < 8; i += 2) {
            boardpieces[i][0] = 'w';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[i][1] = 'w';
        }
        for (int i = 1; i < 8; i += 2) {
            boardpieces[i][2] = 'w';
        }

        for (int i = 1; i < 8; i += 2) {
            boardpieces[i][6] = 'r';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[i][7] = 'r';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[i][5] = 'r';
        }
    }

    public void run() {
        initializeBoard();
        GameBoard board = new GameBoard(boardpieces);
        board.setVisible(true);
        board.setBounds(0, 0, 320, 320);
        add(board);
        board.repaint();

        while (true) {
            //checkOpponentAction();
        }
    }

    private void checkOpponentAction() {
        if (!thisPlayerTurn) {
            try {
                ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
                Action obj = (Action) inputstream.readObject();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void sendTurnData(Action action) {
        try {
            ObjectOutputStream toServer = new ObjectOutputStream(sock.getOutputStream());
            toServer.writeObject(action);
            toServer.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

class GameBoard extends JPanel {

    char[][] boardpieces;
    BufferedImage board;
    BufferedImage red;
    BufferedImage white;
    BufferedImage redKing;
    BufferedImage whiteKing;

    public GameBoard(char[][] boardpieces) {
        this.boardpieces = boardpieces;
        try {
            board = ImageIO.read(new File("res\\checkerboard.png"));
            red = ImageIO.read(new File("res\\red.png"));
            white = ImageIO.read(new File("res\\white.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(board, 0, 0, null);

        for (int i = 0; i < boardpieces.length; i++) {
            for (int k = 0; k < boardpieces.length; k++) {
                if (boardpieces[i][k] == 'r') {
                    g.drawImage(red, i * 40, k * 40, null);
                } else if (boardpieces[i][k] == 'w') {
                    g.drawImage(white, i * 40, k * 40, null);
                }
            }
        }

    }
}
