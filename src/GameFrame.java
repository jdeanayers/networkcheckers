
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class GameFrame extends javax.swing.JFrame implements Runnable {

    private Thread thread;
    private boolean thisPlayerTurn;
    Socket sock;

    public GameFrame(String ip) {
        initComponents();
        try {
            sock = new Socket(ip, 6010);
        } catch (IOException e) {
        }
        thread = new Thread(this);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void run() {
        while (true) {
            tick();
        }
    }

    private void tick() {
        if (!thisPlayerTurn) {
            try {
                ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
                Action obj = (Action) inputstream.readObject();
                System.out.println(obj.blah);
            } catch (Exception e) {
            }
        }
    }

    private void sendTurnData() {
        /*
        ObjectOutputStream toServer = new ObjectOutputStream(sock.getOutputStream());
        System.out.println("Enter a string to send");
        Action obj = new Action(input.next());
        toServer.writeObject(obj);
        toServer.flush();
         */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
