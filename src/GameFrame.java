
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

public class GameFrame extends JFrame implements Runnable {

    private Thread thread;
    volatile private boolean thisPlayerTurn;
    private boolean connected;
    private Socket sock;
    private int side; //1 is Red, 2 is White
    private GameBoard board;
    private int currentPieceX;
    private int currentPieceY;
    private boolean jumped;
    private boolean moved;

    private char[][] boardpieces;

    public GameFrame(String ip) {
        initComponents();

        try {
            sock = new Socket(ip, 6010);
            ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
            int turn = inputstream.readInt();

            if (turn == 1) {
                thisPlayerTurn = true;
                side = 1;
                statusLabel.setText("You are Red. You go first!");
            } else {
                thisPlayerTurn = false;
                side = 2;
                statusLabel.setText("You are White. Opponent's turn!");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
        readyGame();
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
        endTurnButton.setEnabled(false);
        endTurnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endTurnButtonActionPerformed(evt);
            }
        });

        surrenderButton.setText("Surrender");
        surrenderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surrenderButtonActionPerformed(evt);
            }
        });

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

    private void endTurnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endTurnButtonActionPerformed
        if (thisPlayerTurn && currentPieceX != -1 && currentPieceY != -1) {
            thisPlayerTurn = false;
            statusLabel.setText("Opponent's turn.");
            sendTurnData(new Action(true, false, 0, 0, 0, 0));
            endTurnButton.setEnabled(false);
        }
    }//GEN-LAST:event_endTurnButtonActionPerformed

    private void surrenderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_surrenderButtonActionPerformed
        sendTurnData(new Action(false, true, 0, 0, 0, 0));
        declareWinner(side - 1);
    }//GEN-LAST:event_surrenderButtonActionPerformed

    private void initializeBoard() {
        boardpieces = new char[8][8];
        //r represents red piece, R represents kinged red piece, 
        //w represents white piece, W represents kinged white piece
        for (int i = 1; i < 8; i += 2) {
            boardpieces[0][i] = 'w';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[1][i] = 'w';
        }
        for (int i = 1; i < 8; i += 2) {
            boardpieces[2][i] = 'w';
        }

        for (int i = 1; i < 8; i += 2) {
            boardpieces[6][i] = 'r';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[7][i] = 'r';
        }
        for (int i = 0; i < 8; i += 2) {
            boardpieces[5][i] = 'r';
        }
        currentPieceX = -1;
        currentPieceY = -1;
    }

    public void run() {
        while (true) {
            checkOpponentAction();
        }
    }

    public void readyGame() {
        initializeBoard();
        board = new GameBoard(boardpieces, this);
        board.setVisible(true);
        board.setBounds(0, 0, 320, 320);
        add(board);
        board.repaint();
    }

    private void checkOpponentAction() {
        if (!thisPlayerTurn) {
            try {
                ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
                Action obj = (Action) inputstream.readObject();
                if (obj.endOfTurn) {
                    currentPieceX = -1;
                    currentPieceY = -1;
                    jumped = false;
                    moved = false;
                    thisPlayerTurn = true;
                    statusLabel.setText("Your turn!");
                } else if (obj.surrender) {
                    declareWinner(side);
                } else {
                    moveOpponent(obj);
                }
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

    public void movementAction(int oldX, int oldY, int newX, int newY) {
        if (checkIfLegitMove(oldX, oldY, newX, newY)) {
            char movingPiece = boardpieces[oldY][oldX];
            boardpieces[oldY][oldX] = 0;
            boardpieces[newY][newX] = movingPiece;
            currentPieceX = newX;
            currentPieceY = newY;
            if (boardpieces[newY][newX] == 'w' && newY == 7) {
                boardpieces[newY][newX] = 'W';
            } else if (boardpieces[newY][newX] == 'r' && newY == 0) {
                boardpieces[newY][newX] = 'R';
            }
            moved = true;
            board.setBoard(boardpieces);
            board.repaint();
            sendTurnData(new Action(false, false, oldX, oldY, newX, newY));
            endTurnButton.setEnabled(true);
            victoryCheck();
        }
    }

    public void moveOpponent(Action action) {
        char movingPiece = boardpieces[action.oldY][action.oldX];
        boardpieces[action.oldY][action.oldX] = 0;
        boardpieces[action.newY][action.newX] = movingPiece;
        if (Math.abs(action.oldY - action.newY) == 2 && Math.abs(action.oldX - action.newX) == 2) {
            boardpieces[(action.oldY + action.newY) / 2][(action.oldX + action.newX) / 2] = 0;
        }
        if (boardpieces[action.newY][action.newX] == 'w' && action.newY == 7) {
            boardpieces[action.newY][action.newX] = 'W';
        } else if (boardpieces[action.newY][action.newX] == 'r' && action.newY == 0) {
            boardpieces[action.newY][action.newX] = 'R';
        }
        board.setBoard(boardpieces);
        board.repaint();
        victoryCheck();
    }

    public boolean checkIfLegitMove(int oldX, int oldY, int newX, int newY) {
        if (!thisPlayerTurn) {
            return false;
        }
        if (moved && !jumped) {
            return false;
        }
        if (currentPieceX != -1 && currentPieceY != -1 && currentPieceX == oldX && currentPieceY == oldY && !jumped) {
            return false;
        }
        if (side == 1) {
            if (boardpieces[oldY][oldX] == 'w' || boardpieces[oldY][oldX] == 'W') {
                return false;
            }
        } else {
            if (boardpieces[oldY][oldX] == 'r' || boardpieces[oldY][oldX] == 'R') {
                return false;
            }
        }
        if (oldX == newX) {
            return false;
        }
        if (boardpieces[oldY][oldX] == 0) {
            return false;

        }
        if (side == 1 && boardpieces[oldY][oldX] == 'r' && newY > oldY) {
            return false;
        }
        if (side == 2 && boardpieces[oldY][oldX] == 'w' && newY < oldY) {
            return false;
        }
        if (Math.abs(oldY - newY) == 1 && Math.abs(oldX - newX) == 1) {
            return !jumped;
        } else if (Math.abs(oldY - newY) == 2 && Math.abs(oldX - newX) == 2) {
            int midX = (oldX + newX) / 2;
            int midY = (oldY + newY) / 2;
            if (side == 1) {
                if (boardpieces[midY][midX] == 'w' || boardpieces[midY][midX] == 'W') {
                    boardpieces[midY][midX] = 0;
                    jumped = true;
                    return true;
                }
            } else {
                if (boardpieces[midY][midX] == 'r' || boardpieces[midY][midX] == 'R') {
                    boardpieces[midY][midX] = 0;
                    jumped = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void victoryCheck() {
        int rcount = 0;
        int wcount = 0;
        for (int i = 0; i < boardpieces.length; i++) {
            for (int k = 0; k < boardpieces.length; k++) {
                if (boardpieces[i][k] == 'r' || boardpieces[i][k] == 'R') {
                    rcount++;
                }
                if (boardpieces[i][k] == 'w' || boardpieces[i][k] == 'W') {
                    wcount++;
                }
            }
        }
        if (rcount == 0) {
            declareWinner(2);
        }
        if (wcount == 0) {
            declareWinner(1);
        }
    }

    public void declareWinner(int side) {
        endTurnButton.setEnabled(false);
        surrenderButton.setEnabled(false);
        if (side == 1) {
            statusLabel.setText("Red wins!");
        } else {
            statusLabel.setText("White wins!");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton endTurnButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton surrenderButton;
    // End of variables declaration//GEN-END:variables
}

class GameBoard extends JPanel implements MouseListener {

    char[][] boardpieces;
    BufferedImage board;
    BufferedImage red;
    BufferedImage white;
    BufferedImage redKing;
    BufferedImage whiteKing;
    int clickX;
    int clickY;
    GameFrame parent;

    public GameBoard(char[][] boardpieces, GameFrame parent) {
        this.boardpieces = boardpieces;
        this.parent = parent;
        try {
            board = ImageIO.read(new File("res\\checkerboard.png"));
            red = ImageIO.read(new File("res\\red.png"));
            white = ImageIO.read(new File("res\\white.png"));
            redKing = ImageIO.read(new File("res\\redKing.png"));
            whiteKing = ImageIO.read(new File("res\\whiteKing.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        addMouseListener(this);
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
                    g.drawImage(red, k * 40, i * 40, null);
                } else if (boardpieces[i][k] == 'w') {
                    g.drawImage(white, k * 40, i * 40, null);
                }
                if (boardpieces[i][k] == 'R') {
                    g.drawImage(redKing, k * 40, i * 40, null);
                } else if (boardpieces[i][k] == 'W') {
                    g.drawImage(whiteKing, k * 40, i * 40, null);
                }
            }
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
            parent.movementAction(oldX, oldY, newX, newY);
        }
    }
}
