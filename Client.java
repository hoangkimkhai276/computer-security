
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    String serverAddress;
    Scanner in;
    PrintWriter out;
    String id;

    public Client(String id) {
        this.id = id;
    }

    private String getId() {
        return this.id;
    }


    public void run() {
        try {
            var socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                var line = in.nextLine();
                if (line.startsWith("SUBMITID")) {
                    out.println(getId());
                } else if (line.startsWith("IDACCEPTED")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}