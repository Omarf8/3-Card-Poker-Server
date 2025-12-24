import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.net.Socket;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty; // Helps with binding the count for # of clients active
import javafx.beans.property.SimpleIntegerProperty;

public class Server {
    IntegerProperty count = new SimpleIntegerProperty(0);
    int id = 1;
    int port;
    ArrayList<ClientThread> clients = new ArrayList<>();
    ServerListen listen;

    private Consumer<Serializable> callback;

    Server(int portInput, Consumer<Serializable> call) {
        port = portInput;
        callback = call;
        listen = new ServerListen();
        listen.start();
    }

    // Updates the server GUI for clients currently connected
    public void setCount(int countInput) {
        Platform.runLater(() -> {
            count.set(countInput);
        });
    }

    public void stopThreads() {
        // Stops the server listen thread
        listen.stopListen();
        // Stops the client threads
        for (ClientThread client : clients) {
            client.stopClientThread();
        }
        callback.accept("All clients disconnected from the server");
    }

    public class ServerListen extends Thread {
        ServerSocket serSocket;

        @Override
        public void run() {
            try {
                serSocket = new ServerSocket(port);
                // Let the user know that the server has started
                callback.accept("Server has started");

                while(true) {
                    ClientThread client = new ClientThread(serSocket.accept(), id);
                    setCount(count.get() + 1);
                    callback.accept("Client " + id + " has connected to server");
                    clients.add(client);
                    client.start();

                    id++;
                }
            }
            catch(Exception e) {
                callback.accept("Server socket did not launch. Try again.");
            }
        }

        public void stopListen() {
            if(serSocket != null) {
                try {
                    serSocket.close();
                } catch (Exception e) {}
            }
        }
    }

    public class ClientThread extends Thread {
        Socket connection;
        int id;
        ObjectInputStream in;
        ObjectOutputStream out;
        PokerInfo info;

        ClientThread(Socket s, int id) {
            this.connection = s;
            this.id = id;
            this.info = new PokerInfo();
        }

        @Override
        public void run() {
            try {
                this.in = new ObjectInputStream(connection.getInputStream());
                this.out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                callback.accept("Client input and output streams for client " + id + " could not be created");
            }

            while(true) {
                try {
                    PokerInfo inPI = (PokerInfo) in.readObject();
                    if(inPI.message.equals("FRESH")) {
                        info = new PokerInfo(); // Create a fresh instance of the Poker Info
                    }
                    else {
                        info.playerTotal = inPI.playerTotal; // We should always get the total to work with
                        info.message = inPI.message; // This tells us what to do
                        if(inPI.message.equals("PLAY") || inPI.message.equals("FOLD") || inPI.message.equals("PUSHPLAY")) {
                            info.playerAnteBet = inPI.playerAnteBet;
                            info.playerPPBet = inPI.playerPPBet;
                        }
                        send(); // Modifies the info for whatever the client asks for
                    }
                }
                catch(SocketException e) {
                    callback.accept("Server socket closed");
                    break;
                }
                catch(Exception e) {
                    callback.accept(e.getMessage());
                    callback.accept("Client " + id + " disconnected from the server");
                    clients.remove(this);
                    setCount(count.get() - 1);
                    break;
                }
            }
        }

        public void send() {
            try {
                PokerInfo outPI = new PokerInfo();
                switch(info.message) {
                    case "DEAL":
                        // Sets everything we will send back in the PokerInfo object
                        Deck deck = new Deck();
                        deck.shuffleDeck(); // Shuffles the sorted deck
                        ArrayList<Card> playerHand = deck.deal3Cards(); // Deal 3 cards for the player
                        ArrayList<Card> dealerHand = deck.deal3Cards(); // Deal 3 cards for the dealer
                        // Set everything in our own PokerInfo object to make sure we have the data
                        info.setPlayerHand(playerHand); // 1
                        info.setDealerHand(dealerHand); // 2
                        outPI.setPlayerHand(playerHand);
                        outPI.setDealerHand(dealerHand);
                        char comparingHands = ThreeCardLogic.compareHands(info.playerHand, info.dealerHand);
                        info.won = comparingHands == 'W'; // 3
                        outPI.won = comparingHands == 'W';
                        boolean push = ThreeCardLogic.pushAnte(dealerHand) || comparingHands == 'P'; // If the dealer doesn't at least have a Queen High or the hands are a draw, push the ante
                        outPI.pushAnte = push;
                        info.pushAnte = push; // 4
                        outPI.setMessage("DEALDONE");
                        break;
                    case "PLAY":
                        callback.accept("Client " + id + " is going to PLAY");
                        int pp = ThreeCardLogic.evalPPWinnings(info.playerHand, info.playerPPBet);
                        outPI.ppEarnings = pp != 0 ? pp : -info.playerPPBet; // Calculate pair plus earnings
                        char charHand = ThreeCardLogic.compareHands(info.playerHand, info.dealerHand); // Get the char from comparing hands
                        int sum; // We have to consider whether we won, lost or need to push (add, subtract, or add nothing)
                        if(charHand == 'W') { sum = info.playerAnteBet * 4; }
                        else if(charHand == 'L') { sum = info.playerAnteBet * -2; }
                        else { sum = 0; }
                        outPI.roundEarnings = outPI.ppEarnings + sum; // Add here
                        outPI.playerTotal = info.playerTotal + outPI.roundEarnings;
                        info.playerTotal = outPI.playerTotal;
                        outPI.setMessage("PLAYDONE");
                        break;
                    case "FOLD":
                        callback.accept("Client " + id + " is going to FOLD");
                        outPI.ppEarnings = -info.playerPPBet; // Folding gives up the pair plus winnings
                        outPI.roundEarnings = -info.playerAnteBet + outPI.ppEarnings;
                        outPI.playerTotal = info.playerTotal + outPI.roundEarnings;
                        info.playerTotal = outPI.playerTotal;
                        outPI.setMessage("FOLDDONE");
                        break;
                    case "PUSHPLAY":
                        callback.accept("Client " + id + " played but had to PUSH the ante");
                        // Evaluate the pair plus winnings using the old draw since the player did not fold
                        int pp2 = ThreeCardLogic.evalPPWinnings(info.playerHand, info.playerPPBet);
                        outPI.ppEarnings = pp2 != 0 ? pp2 : -info.playerPPBet;
                        outPI.playerTotal = info.playerTotal + outPI.ppEarnings;
                        info.playerTotal = outPI.playerTotal;
                        // Sets everything we will send back in the PokerInfo object
                        Deck deck2 = new Deck();
                        deck2.shuffleDeck(); // Shuffles the sorted deck
                        ArrayList<Card> playerHand2 = deck2.deal3Cards(); // Deal 3 cards for the player
                        ArrayList<Card> dealerHand2 = deck2.deal3Cards(); // Deal 3 cards for the dealer
                        // Set everything in our own PokerInfo object to make sure we have the data
                        info.setPlayerHand(playerHand2); // 1
                        info.setDealerHand(dealerHand2); // 2
                        outPI.setPlayerHand(playerHand2);
                        outPI.setDealerHand(dealerHand2);
                        char comparingHands2 = ThreeCardLogic.compareHands(info.playerHand, info.dealerHand);
                        info.won = comparingHands2 == 'W'; // 3
                        outPI.won = comparingHands2 == 'W';
                        boolean push2 = ThreeCardLogic.pushAnte(dealerHand2) || comparingHands2 == 'P'; // If the dealer doesn't at least have a Queen High or the hands are a draw, push the ante
                        info.pushAnte = push2; // 4
                        outPI.pushAnte = push2;
                        outPI.setMessage("PUSHPLAYDONE");
                        break;
                    default: // The default case will be used to send status messages of what the user is doing
                        callback.accept("Client " + id + " " + info.message);
                        return;
                }
                out.writeObject(outPI);
            } catch (IOException e) {
                callback.accept("Something went wrong sending info back to the client");
                e.printStackTrace();
            }
        }

        public void stopClientThread() {
            try {
                if(in != null) {
                    in.close();
                }
            }
            catch(Exception e) {}

            try {
                if(out != null) {
                    out.close();
                }
            }
            catch(Exception e) {}

            try {
                if(connection != null) {
                    connection.close();
                }
            }
            catch(Exception e) {}
        }
    }
}
