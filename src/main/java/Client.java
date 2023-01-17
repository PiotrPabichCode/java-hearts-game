import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Main class for Client
 */
public class Client {
    /**
     * Variable that stores PORT number
     */
    private static int PORT = 1410;
    /**
     * Variable that stores scanner for input
     */
    public static Scanner scanner = new Scanner(System.in);
    /**
     * Variable that stores socket
     */
    private Socket socket;
    /**
     * Variable that stores BufferedReader
     */
    private BufferedReader bufferedReader;
    /**
     * Variable that stores BufferedWriter
     */
    private BufferedWriter bufferedWriter;

    /**
     *  Client constructor
     */

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args){
        try {
            startClientService();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    /**
     *  Method that starts connection with server
     */

    private static void startClientService() throws IOException {
        Socket socket = new Socket("localhost", PORT);
        Client client = new Client(socket);

        client.listenForMessage();
        client.sendMessage();
    }

    /**
     *  Method that gets message and write it to bufferedWriter and flush it
     */

    public void sendMessage() {
        try {
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     *  Thread that listen for message and displays it
     */
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     *  Method that closes all opened streams
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connection has been lost");
        System.exit(1);
    }
}
