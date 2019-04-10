
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
    private int side; //1 is Red, 2 is White
    private GameBoard board;
    private int clickX;
    private int clickY;
    private int currentPieceX;
    private int currentPieceY;

    private char[][] boardpieces;

    public GameFrame(String ip) {
        initComponents();
        /*
        try {
            sock = new Socket(ip, 6010);
            ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
            int turn = inputstream.readInt();
           
            if (turn == 1) {
                thisPlayerTurn = true;
                side = 1;
                statusLabel.setText("Opponent connected. You are Red. You go first!");
            } else {
                thisPlayerTurn = false;
                side = 2;
                statusLabel.setText("Opponent connected. You are White. Opponent's turn!");
            }

        } catch (IOException e) {
        }
         */
        thisPlayerTurn = true;
        side = 1;
        thread = new Thread(this);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        endTurnButton = new javax.swing.JButton();
        surrenderButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        endTurnButton.setText("End Turn");

        surrenderButton.setText("Surrender");

        statusLabel.setText("Waiting on opponent...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(endTurnButton)
                        .addGap(55, 55, 55)
                        .addComponent(surrenderButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(statusLabel)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                .addComponent(statusLabel)
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endTurnButton)
                    .addComponent(surrenderButton))
                .addGap(25, 25, 25))
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
        board = new GameBoard(boardpieces);
        board.setVisible(true);
        board.setBounds(0, 0, 320, 320);
        add(board);
        board.repaint();
        addMouseListener(this);

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

        clickX = me.getX();
        clickY = me.getY();

    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (clickX < 320 && clickY < 320) {
            int oldX = clickX / 40;
            int oldY = clickY / 40;
            int newX = me.getX() / 40;
            int newY = me.getY() / 40;
            System.out.println("Pressed on " + oldX + ", " + oldY);
            System.out.println("Released on " + newX + ", " + newY);
            if (checkIfLegitMove(oldX, oldY, newX, newY)) {
                System.out.println("Success");
                char movingPiece = boardpieces[oldY][oldX];
                boardpieces[oldY][oldX] = 0;
                boardpieces[newY][newX] = movingPiece;
                currentPieceX = newX;
                currentPieceY = newY;
                board.setBoard(boardpieces);
                board.repaint();
            } else {
                System.out.println("Failed");
            }
        }
        clickX = -1;
        clickY = -1;
    }

    public boolean checkIfLegitMove(int oldX, int oldY, int newX, int newY) {
        if (!thisPlayerTurn) {
            System.out.println("1");
            return false;
        }
        if (oldX == newX) {
            System.out.println("2");
            return false;
        }
        System.out.println(boardpieces[newY][newX]);
        if (boardpieces[newY][newX] != 0) {
            System.out.println("3");
            return false;

        }
        if (side == 1 && boardpieces[oldY][oldX] == 'r' && newY > oldY) {
            System.out.println("4");
            return false;
        }
        if (side == 2 && boardpieces[oldY][oldX] == 'w' && newY < oldY) {
            System.out.println("5");
            return false;
        }
        if (Math.abs(oldY - newY) == 1) {
            return true;
        } else if (Math.abs(oldY - newY) == 2) {
            int midX = (oldX + newX) / 2;
            int midY = (oldY + newY) / 2;
            if (side == 1) {
                if (boardpieces[midY][midX] == 'w' || boardpieces[midY][midX] == 'W') {
                    boardpieces[midY][midX] = 0;
                    return true;
                }
            } else {
                if (boardpieces[midY][midX] == 'r' || boardpieces[midY][midX] == 'R') {
                    boardpieces[midY][midX] = 0;
                    return true;
                }
            }
        }
        System.out.println("6");
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton endTurnButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton surrenderButton;
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
            redKing = ImageIO.read(new File("res\\redKing.png"));
            whiteKing = ImageIO.read(new File("res\\whiteKing.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setBoard(char[][] boardpieces) {
        this.boardpieces = boardpieces;
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
