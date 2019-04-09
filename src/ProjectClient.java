
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;

public class ProjectClient {

    public static void main(String[] args) {
        boolean listening;
        try {
            Socket sock = new Socket("localhost", 6010);
            //BufferedOutputStream toServer = new BufferedOutputStream(sock.getOutputStream());
            
            Scanner input = new Scanner(System.in);
            System.out.println("Enter 1 for listener, 2 for typer");
            if (input.nextInt() == 1) {
                listening = true;
            } else {
                listening = false;
            }
            while (true) {
                if (listening) {
                    //InputStream inputstream = sock.getInputStream();
                    ObjectInputStream inputstream = new ObjectInputStream(sock.getInputStream());
                    try {
                        Action obj = (Action) inputstream.readObject();
                        System.out.println(obj.blah);
                    } catch (Exception e) {
                    }
                    //int num = 0;
                    //num = inputstream.read();
                } else {
                    ObjectOutputStream toServer = new ObjectOutputStream(sock.getOutputStream());
                    System.out.println("Enter a string to send");
                    Action obj = new Action(input.next());
                    toServer.writeObject(obj);
                    toServer.flush();
                }
            }
        } catch (IOException ioe) {
        }

    }
}
