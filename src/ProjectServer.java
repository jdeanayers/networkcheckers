
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.ByteBuffer;

public class ProjectServer {

    public static void main(String[] args) throws IOException {
        Socket playerone = null;
        Socket playertwo = null;
        ServerSocket ss = new ServerSocket(6010, 2);
        
        playerone = ss.accept();
        ObjectOutputStream toClient = new ObjectOutputStream(playerone.getOutputStream());
        toClient.writeInt(1);
        toClient.flush();
        
        playertwo = ss.accept();
        toClient = new ObjectOutputStream(playertwo.getOutputStream());
        toClient.writeInt(2);
        toClient.flush();
        
        Socket activeplayer;
        Socket passiveplayer;
        int playerturn = 1;
        try {
            System.out.println("Running!");
            while (true) {
                if (playerturn == 1) {
                    activeplayer = playerone;
                    passiveplayer = playertwo;
                } else {
                    activeplayer = playertwo;
                    passiveplayer = playerone;
                }
                Action boink = null;
                while (true) {
                    ObjectInputStream fromClient = new ObjectInputStream(activeplayer.getInputStream());
                    toClient = new ObjectOutputStream(passiveplayer.getOutputStream());
                    try {
                        boink = (Action) fromClient.readObject();
                        System.out.println(boink.blah);
                        toClient.writeObject(boink);
                        toClient.flush();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        } finally {
            if (playerone != null) {
                playerone.close();
            }
            if (playertwo != null) {
                playertwo.close();
            }
        }
    }
}
