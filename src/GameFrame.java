
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 320, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(446, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void run() {
        boardpieces = new char[8][8];
        //r represents red piece, R represents kinged red piece, 
        //b represents black piece, B represents kinged black piece
        
        
        GameBoard board = new GameBoard();
        board.setVisible(true);
        board.setBounds(0, 0, 320, 320);
        add(board);

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
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

class GameBoard extends JPanel {

    public GameBoard() {
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        try {
            BufferedImage image = ImageIO.read(new File("res\\checkerboard.png"));
            g.drawImage(image, 0, 0, null);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
