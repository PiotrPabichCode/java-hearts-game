import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *  Client handler for every Client that implements Runnable interface
 */

public class ClientHandler implements Runnable {

    /**
     * Variable that stores ArrayList of ClientHandler
     */
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    /**
     * Variable that stores all rooms
     */
    public static ArrayList<Room> rooms = new ArrayList<>();
    /**
     * Variable that stores Socket
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
     * Variable that stores userName
     */
    private String userName;
    /**
     * Flag that checks if client is logged
     */
    private boolean isLogged = false;
    /**
     * Constant if player doesn't have room
     */
    final int NO_ROOM = -1;
    /**
     * Variable that stores player room number
     */
    private int roomNumber = NO_ROOM;
    /**
     * Constant for picking register account option
     */
    final int REGISTER_ACCOUNT = 1;
    /**
     * Constant for picking login account option
     */
    final int LOGIN_ACCOUNT = 2;
    /**
     * Constant for maximum players of room
     */
    final int MAX_PLAYERS_PER_ROOM = 3;
    /**
     * Constant that stores error value for GetInteger
     */
    final int EXIT_VALUE = -10;

    /**
     * ClientHandler constructor
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if(clientHandlers.isEmpty()) {
                rooms.add(new Room(1,0, new Game(1)));
                rooms.get(rooms.size() - 1).startRoom();
            }
            clientHandlers.add(this);
        } catch (IOException e) {
            closeAll();
        }
    }

    /**
     * Method run that implements Runnable interface
     */

    @Override
    public void run() {
        validateUser();
        while(socket.isConnected()) {
            try {
                if(roomNumber == NO_ROOM) {
                    pickRoom();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                isLogged = false;
            }
        }
        System.out.println("NEW MESSAGE: " + userName + " has left the server");
        Room room = rooms.get(roomNumber - 1);
        room.deletePlayer(this);
        isLogged = false;
    }

    /**
     * Getter that gets room by its number
     */
    Room getRoom(int number) {
        for(int i = 0; i < rooms.size(); i++) {
            Room currRoom = rooms.get(i);
            if(currRoom.getNumber() == number) {
                return currRoom;
            }
        }
        return null;
    }

    /**
     * Method that refresh menu display
     */

    void refreshMenuDisplay() {
        sendMessage("Enter one of available options\n" +
                    "0. Create new room");
        displayRooms(false);
    }

    /**
     * Method that refresh menu for all clients who are on this display
     */

    void refreshAllMenuDisplays() {
        for(int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler clientHandler = clientHandlers.get(i);
            if(clientHandler.roomNumber == NO_ROOM && clientHandler.isLogged) {
                clientHandler.unblockReader();
            }
        }
    }

    /**
     * Method that picks room for client
     */

    void pickRoom() {
        boolean next = false;
        while(!next) {
            int option = EXIT_VALUE;
            while(option == EXIT_VALUE) {
                refreshMenuDisplay();
                int lowerBound = 0;
                int upperBound = rooms.size();
                option = getInteger(lowerBound,upperBound);
                if(option == 0) {
                    next = createNewRoom();
                } else if(option != EXIT_VALUE) {
                    next = checkPickedRoom(option);
                }
            }
        }
        sendMessage("You've picked room no." + roomNumber);
        System.out.println("NEW MESSAGE: " + "[Room " + roomNumber + "]: " + userName + " joined to the room");
        System.out.println("NEW MESSAGE: " + "[Room " + roomNumber + "]: Players [" + rooms.get(roomNumber - 1).getSize() + "/3]");
        refreshServerScreen();
        refreshAllMenuDisplays();
    }

    /**
     * Method that creates new room
     */
    boolean createNewRoom() {
        int number = rooms.get(rooms.size() - 1).getNumber() + 1;
        rooms.add(new Room(number,1, new Game(number)));
        Room room = rooms.get(rooms.size() - 1);
        room.startRoom();
        room.addPlayer(this);
        roomNumber = number;
        return true;
    }

    /**
     * Method that gets room by its room number and checks if its picked
     */
    boolean checkPickedRoom(int roomNumber) {
        Room room = rooms.get(roomNumber - 1);
        if(room.getSize() != MAX_PLAYERS_PER_ROOM) {
            this.roomNumber = room.getNumber();
            room.increaseSize();
            room.game.addPlayer(this);
            return true;
        } else {
            sendMessage("Picked room is full. Pick another one.");
        }
        return false;
    }

    /**
     * Method that displays all rooms
     */
    void displayRooms(boolean isServer) {
        for(int i = 0; i < rooms.size(); i++) {
            int pos = i + 1;
            Room room = rooms.get(i);
            String message = pos + ". Room no." + room.getNumber() + " | Players [" + room.getSize() + "/3]";
            if(isServer) {
                System.out.println(message);
            } else {
                sendMessage(message);
            }
        }
    }

    /**
     * Method that displays all players
     */

    void displayPlayers() {
        System.out.println("Active players on server");
        for(int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler clientHandler = clientHandlers.get(i);
            int position = i + 1;
            System.out.println(position + ". " +"[Room " + clientHandler.roomNumber + "]: " + clientHandler.userName);
        }
    }

    /**
     * Method that displays all full rooms
     */

    int displayFullRooms() {
        int count = 0;
        for(int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if(room.getSize() == MAX_PLAYERS_PER_ROOM) {
                if(count == 0) {
                    System.out.println("0. Exit");
                }
                count++;
                System.out.println(count + ". [room number " + room.getNumber() + "]: [CURRENTLY PLAYING]" );
            }
        }
        if(count == 0) {
            System.out.println("No rooms currently playing...");
        }
        return count;
    }

    /**
     * Method that refresh server screen
     */

    public static void refreshServerScreen() {
        System.out.println("---------------------------------------\n" +
                "Pick one of available options: \n" +
                "1. Display rooms\n" +
                "2. Display players\n" +
                "3. Display room status\n" +
                "4. End round\n" +
                "5. End game\n" +
                "Your choice: ");
    }

    /**
     * Method that validates user by login and password
     */

    void validateUser() {
        boolean correct = false;
        sendMessage("Enter one of available options\n" +
                    "1.Register new account\n" +
                    "2.Login to account");
        int lowerBound = 1;
        int upperBound = 2;
        int option = getInteger(lowerBound,upperBound);
        String login = "";
        while(!correct) {
            login = getData("Enter you username - it's your login: ");
            String password = getData("Enter you password: ");
            if(option == REGISTER_ACCOUNT) {
                correct = registerAccount(login, password);
            } else if(option == LOGIN_ACCOUNT) {
                correct = loginAccount(login, password);
            }
        }
        this.userName = login;
        isLogged = true;
        System.out.println("NEW MESSAGE: " + userName + " joined to the server");
        refreshServerScreen();
        sendMessage("Welcome!!!");
    }

    /**
     * Method that registers user by login and password
     */
    boolean registerAccount(String login, String password) {
        boolean exists = checkAccountExistence(login, password);
        if(exists) {
            sendMessage("Account already exists");
            return false;
        } else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
                String data = login + " " + password + "\n";
                writer.append(data);
                writer.close();
                isLogged = true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            sendMessage("Register completed successfully");
            return true;
        }
    }

    /**
     * Method that sign-in user by login and password
     */
    boolean loginAccount(String login, String password) {
        boolean exists = checkAccountExistence(login, password);
        if(exists) {
            for(int i = 0; i < clientHandlers.size(); i++) {
                String username = clientHandlers.get(i).getUserName();
                if(username != null && username.equals(login)) {
                    sendMessage("Account is logged in");
                    return false;
                }
            }
            sendMessage("Login completed successfully");
            return true;
        } else {
            sendMessage("Account doesn't exists");
            return false;
        }
    }

    /**
     * Method that checks if account is already in database
     */
    boolean checkAccountExistence(String login, String password) {
        try {
            File file = new File("users.txt");
            List<String> fileLines = FileUtils.readLines(file, "ISO-8859-1");
            String line = login + " " + password;
            if(fileLines.contains(line)) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Flag that checks if unblocking reader is necessary
     */
    boolean unblockReader = false;
    /**
     * Flag that checks if leaving GetInteger loop is necessary
     */
    boolean exit = false;

    /**
     * Method that unblock bufferedReader
     */
    public void unblockReader() {
        exit = true;
        unblockReader = true;
    }

    /**
     * Method that gets integer between minimum value and maximum value
     */
    public int getInteger(int minValue, int maxValue) {
        int choice = 0;
        boolean again = true;
        while(!exit){
            try{
                if(again) {
                    sendMessage("Your choice[" + minValue + '-' + maxValue + "]: ");
                    again = false;
                }
                if(bufferedReader.ready()) {
                    String buffer;
                    if((buffer = bufferedReader.readLine()) != null) {
                        choice = Integer.parseInt(buffer);
                        if(choice >= minValue && choice <= maxValue){
                            exit = true;
                        } else {
                            again = true;
                        }
                    }
                }
            }
            catch(Exception e){
                again = true;
                sendMessage("The given value is not a number! Try again.");
            }
        }
        exit = false;
        if(unblockReader) {
            unblockReader = false;
            return EXIT_VALUE;
        }
        return choice;
    }


    /**
     * Method that gets string with terminal message
     */
    String getData(String message) {
        boolean exit = false;
        String buffer = "";
        while(!exit) {
            try{
                sendMessage(message);

                buffer = bufferedReader.readLine();
                if(!buffer.equals("")) {
                    exit = true;
                } else {
                    sendMessage("Something went wrong.");
                }
            } catch(IOException e){
                e.getStackTrace();
            }
        }

        return buffer;
    }

    /**
     * Setter that sets room number
     */

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * Getter that gets username
     */

    public String getUserName() {
        return this.userName;
    }

    /**
     * Method that sends message to client
     */

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeAll();
        }
    }

    /**
     * Method that removes client handler from arraylist
     */


    public void removeClientHandler() {
        clientHandlers.remove(this);
    }


    /**
     * Method that removes client handler from arraylist and closes all streams
     */
    public void closeAll() {
        removeClientHandler();
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
    }
}
