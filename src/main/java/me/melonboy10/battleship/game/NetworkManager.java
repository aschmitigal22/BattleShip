package me.melonboy10.battleship.game;

import javafx.application.Platform;
import me.melonboy10.battleship.BattleShipApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class NetworkManager implements Runnable {

    public enum PacketType {NAME_CHANGE, CONFIRM_PIECES, GUESS, HIT, MISS, END;

        public boolean allowed(BattleShipGame.GameState state) {
            boolean allowPacket = true;
            switch (this) {
                case NAME_CHANGE:
                    break;
                case CONFIRM_PIECES:
                    if (!state.equals(BattleShipGame.GameState.PLACING_SHIPS)) allowPacket = false;
                    break;
                case GUESS:
                case HIT:
                case MISS:
                    if (!state.equals(BattleShipGame.GameState.GUESSING)) allowPacket = false;
                    break;
            }
            return allowPacket;
        }
    }

    String ip = "localhost";
    int port = 22222;
    Scanner scanner = new Scanner(System.in);
    Thread thread;

    BattleShipGame game;
    volatile boolean running = true;
    public boolean accepted = false;
    boolean guest = true;
    boolean unableToCommunicate = false;
    int errors = 0;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public NetworkManager(BattleShipGame game) {
        this.game = game;
        if (BattleShipApplication.customIp) {
            System.out.println("Please enter the IP (localhost): ");
            ip = scanner.nextLine();
            if (!ip.equals("`")) {
                System.out.println("Please enter the port: ");
                port = scanner.nextInt();
                while (port < 1 || port > 65535) {
                    System.out.println("Sorry, the port you entered is not valid. Please try again (1 - 65534):");
                    port = scanner.nextInt();
                }
            } else {
                ip = "localhost";
                port = 22222;
            }
        }

        if (!connect()) initializeServer();

        thread = new Thread(this, "BattleShip");
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            tick();

            if (!guest && !accepted) {
                listenForServerJoinRequest();
            }
            if (unableToCommunicate) {
                System.out.println("Unable to communicate");
                running = false;
                try {
                    game.gameWindow.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        thread.interrupt();
    }

    private void tick() {
        if (errors >= 10) unableToCommunicate = true;

        if (accepted && !unableToCommunicate) {
            try {
                String receivedBytes = dataInputStream.readUTF();
                PacketType packetType = PacketType.valueOf(receivedBytes.substring(0, receivedBytes.indexOf("-")));
                System.out.println("packetType = " + packetType);
                String packetInfo = receivedBytes.substring(receivedBytes.indexOf("-") + 1);
                switch (packetType) {
                    case NAME_CHANGE -> {
                        game.gameInformationGrid.setOpponentName(packetInfo);
                    }
                    case CONFIRM_PIECES -> {
                        if (game.state.equals(BattleShipGame.GameState.PLACING_SHIPS)) {
                            game.opponentConfirmedPieces = true;
                            game.checkPieceConfirm();
                        }
                    }
                    case GUESS -> {
                        game.yourTurn = true;
                        int[] loc = Arrays.stream(packetInfo.split(",")).mapToInt(Integer::valueOf).toArray();
                        Platform.runLater(() -> {
                            if (game.shipGrid.isHit(loc[0], loc[1])) sendInfo(PacketType.HIT, packetInfo);
                            else sendInfo(PacketType.MISS, packetInfo);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            game.setGameState(BattleShipGame.GameState.GUESSING);
                        });
                    }
                    case HIT, MISS -> {
                        game.yourTurn = false;
                        int[] loc = Arrays.stream(packetInfo.split(",")).mapToInt(Integer::valueOf).toArray();
                        Platform.runLater(() -> game.guessGrid.guess(loc[0], loc[1], packetType.equals(PacketType.HIT)));
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        game.setGameState(BattleShipGame.GameState.GUESSING);
                    }
                    case END -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        game.setGameState(BattleShipGame.GameState.GAME_OVER);
                    }
                }
                game.gameInformationGrid.updateText();
            } catch (IOException e) {
                e.printStackTrace();
                errors++;
            }
        }
    }

    private void listenForServerJoinRequest() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("Client has requested to join and we have accepted.");
            game.setGameState(BattleShipGame.GameState.PLACING_SHIPS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(ip, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            accepted = true;
        } catch (IOException e) {
            System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.yourTurn = true;
        guest = false;
    }

    public void sendInfo(PacketType type, String data) {
        if (accepted) {
            if (type.allowed(game.state) && !unableToCommunicate) {

                try {
                    dataOutputStream.writeUTF(type.name() + "-" + data);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    errors++;
                }

                System.out.println("Sent packet with data: \"" + type.name() + "-" + data + "\"");
            }
        }
    }
}
