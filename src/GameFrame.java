
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameFrame extends javax.swing.JFrame implements Runnable, MouseListener {

    private Thread thread;
    private boolean thisPlayerTurn;
    private boolean connected;
    Socket sock;

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        jButton1.setText("Test");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jButton1)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(jLabel1)
                .addGap(53, 53, 53)
                .addComponent(jButton1)
                .addContainerGap(127, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (thisPlayerTurn) {
            Action action = new Action("blah");
            sendTurnData(action);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public void run() {
        while (true) {
            checkOpponentAction();
        }
    }

    private void checkOpponentAction() {
        if (!thisPlayerTurn) {
            try {
                ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
                Action obj = (Action) inputstream.readObject();
                jLabel1.setText(obj.blah);;
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
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
