import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

public class Server {

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> ids = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();
    private static final String[] VALID_IDS = {"id111", "id112"};

    public Server() {
        System.out.println("The chat server is running...");
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {
        private String id;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private Account account;
        private double counter;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITID");
                    id = in.nextLine();
                    boolean isValid = false;
                    for (String id : VALID_IDS) {
                        if (this.id.equals(id)) {
                            isValid = true;
                            break;
                        }
                    }
                    if (!isValid) {
                        System.out.println("Error: Invalid ID");
                        return;
                    }
                    synchronized (ids) {
                        if (!id.isBlank() && !ids.contains(id)) {
                            ids.add(id);
                            break;
                        }
                    }
                }

                out.println("IDACCEPTED " + id);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + id + " has joined");
                }
                writers.add(out);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + id + ": " + input);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (id != null) {
                    System.out.println(id + " is leaving");
                    ids.remove(id);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + id + " has left");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}