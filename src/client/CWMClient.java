package client;

import api.controllers.LoggerController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CWMClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private static final String LOGOUT_COMMAND = "logout";
    private static final String LOGOUT_COMMAND_SUCCESSFUL_OPERATION = "You have successfully logged out!";

    private static final String SUCCESSFUL_CONNECTION =
        "Connection with " + SERVER_HOST + ":" + SERVER_PORT + " has been established!";

    private static final String NETWORK_ERROR_MESSAGE = "Unable to connect to the server! " +
        "Try again later or contact administrator by providing the logs in errors/logs.log!";

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println(SUCCESSFUL_CONNECTION);

            while (true) {
                String message = scanner.nextLine();
                writer.println(message);

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (!reader.ready()) {
                        break;
                    }
                }

                if (LOGOUT_COMMAND.equals(message) && LOGOUT_COMMAND_SUCCESSFUL_OPERATION.equals(line)) {
                    break;
                }
            }
        } catch (IOException exc) {
            System.out.println(NETWORK_ERROR_MESSAGE);
            LoggerController.writeLogsErrors(exc.getMessage(), exc.getStackTrace());
        }
    }
}
