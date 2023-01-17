import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class for Server
 */

public class Server {
    /**
     * Variable that stores Server PORT
     */
    private static int PORT = 1410;
    /**
     * Variable that stores ServerSocket
     */
    private final ServerSocket serverSocket;
    /**
     * Flag that checks is server is active
     */
    private boolean isActive = true;
    /**
     * Variable that stores ArrayList of ClientHandlers
     */
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    /**
     * Constant for display room option
     */
    final int DISPLAY_ROOMS = 1;
    /**
     * Constant for display player option
     */
    final int DISPLAY_PLAYERS = 2;
    /**
     * Constant for display results option
     */
    final int DISPLAY_RESULTS = 3;
    /**
     * Constant for end round option
     */
    final int END_ROUND = 4;
    /**
     * Constant for end game option
     */
    final int END_GAME = 5;

    /**
     * Server constructor
     */

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * Server main method
     */
    public static void main(String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            Server server = new Server(serverSocket);
            System.out.println("Server is listening at port: " + PORT);
            server.listenForServerInput();
            server.startServer();
        } catch(Exception e){
            System.exit(1);
        }
    }

    /**
     * Method thats starts server
     */

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                clientHandlers.add(clientHandler);
            }
        } catch(Exception e){
            closeServerSocket();
        }
    }

    /**
     * Method that display results option
     */
    private void displayResultsOption(ClientHandler clientHandler) {
        int count = clientHandler.displayFullRooms();
        if(count != 0) {
            int pickedRoom = getInteger(0, count);
            if(pickedRoom != 0) {
                clientHandler.getRoom(pickedRoom).game.displayPoints(true, false);
            }
        }
    }

    /**
     * Method that display end round option
     */

    private void endRoundOption(ClientHandler clientHandler) {
        int count = clientHandler.displayFullRooms();
        if(count != 0) {
            int pickedRoom = getInteger(0, count);
            if(pickedRoom != 0) {
                clientHandler.getRoom(pickedRoom).game.endRound(true, false);
            }
        }
    }

    /**
     * Method that display end game option
     */

    private void endGameOption(ClientHandler clientHandler) {
        int count = clientHandler.displayFullRooms();
        if(count != 0) {
            int pickedRoom = getInteger(0, count);
            if(pickedRoom != 0) {
                clientHandler.getRoom(pickedRoom).game.randomEndGame();
            }
        }
    }

    /**
     * Main loop where server can pick options
     */

    private void serverMainLoop() {
        ClientHandler clientHandler = clientHandlers.get(0);
        while (!serverSocket.isClosed()) {
            int lowerBound = 1;
            int upperBound = 5;
            int option = getInteger(lowerBound, upperBound);
            switch (option) {
                case DISPLAY_ROOMS:
                    clientHandler.displayRooms(true);
                    break;
                case DISPLAY_PLAYERS:
                    clientHandler.displayPlayers();
                    break;
                case DISPLAY_RESULTS:
                    displayResultsOption(clientHandler);
                    break;
                case END_ROUND:
                    endRoundOption(clientHandler);
                    break;
                case END_GAME:
                    endGameOption(clientHandler);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Thread that listens for server input
     */
    public void listenForServerInput() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isActive) {
                    if(clientHandlers.size() != 0) {
                        serverMainLoop();
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Method that get integer between lowerBound and upperBound
     */

    public int getInteger(int minValue, int maxValue) {
        int choice = 0;
        boolean exit = false;
        while(!exit){
            ClientHandler.refreshServerScreen();
            try{
                String buffer = Client.scanner.nextLine();
                choice = Integer.parseInt(buffer);
                if(choice >= minValue && choice <= maxValue){
                    exit = true;
                }
            }
            catch(Exception e){
                System.out.println("The given value is not a number! Try again.");
            }
        }
        return choice;
    }

    /**
     * Method that closes server socket
     */
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
