package api;

import api.controllers.CommandController;
import api.controllers.LoggerController;
import api.controllers.UsersController;
import api.fetch.CryptoAPIClient;
import api.fetch.CryptoAPIClientRunnable;
import api.models.users.Users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CWMServer {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 32_768;
    private static final int TIME_BETWEEN_REQUESTS = 30;

    private static final String INPUT_ERROR = "Error when reading user input!";
    private static final String LOGOUT_SERVER_RESPONSE = "You have successfully logged out!";
    private static final String SHUTDOWN_SERVER_RESPONSE = "Shutting down the server...";

    private boolean isRunning;
    private ByteBuffer buffer;

    private final CryptoAPIClient cryptoAPIClient;
    private final CommandController cmdExecutor;

    public CWMServer(String apiKey) {
        Users users = null;

        try {
            users = UsersController.readUsers();
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
        }

        this.cryptoAPIClient = new CryptoAPIClient(apiKey);
        this.cmdExecutor = new CommandController(users, this.cryptoAPIClient);
        this.isRunning = true;
    }

    public void start() {
        try (ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()) {
            scheduledExecutorService.scheduleAtFixedRate(
                new CryptoAPIClientRunnable(this.cryptoAPIClient),
                0,
                TIME_BETWEEN_REQUESTS,
                TimeUnit.MINUTES
            );

            startServer();
        } catch (Exception exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
        }
    }

    public void stop() {
        try {
            UsersController.writeUsers(this.cmdExecutor.getUsers());
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
        }
    }

    public void startServer() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            Selector selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);

            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.isRunning = true;

            while (this.isRunning) {
                handleRequests(selector);
            }
        } catch (Exception exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
        }
    }

    private void handleRequests(Selector selector) throws IOException {
        int readyChannels = selector.select();
        if (readyChannels == 0) {
            return;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isAcceptable()) {
                accept(selector, key);
            } else if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                handleKey(sc, key);
            }

            keyIterator.remove();
        }
    }

    private void handleKey(SocketChannel sc, SelectionKey key) {
        try {
            String clientInput = getClientInput(sc, key);

            if (clientInput == null) {
                handleLogout(sc, key);
                return;
            }

            System.out.println("Received: " + clientInput);

            String serverResponse = cmdExecutor.executeCommand(clientInput, key);
            sendResponseToClient(sc, serverResponse);

            if (LOGOUT_SERVER_RESPONSE.equals(serverResponse)) {
                handleLogout(sc, key);
            }
        } catch (IOException e) {
            handleLogout(sc, key);
        }
    }

    private String getClientInput(SocketChannel sc, SelectionKey key) throws IOException {
        this.buffer.clear();
        int r = sc.read(this.buffer);

        if (r < 0) {
            handleLogout(sc, key);
            return null;
        }

        this.buffer.flip();
        byte[] byteArray = new byte[this.buffer.remaining()];
        this.buffer.get(byteArray);

        return new String(byteArray, StandardCharsets.UTF_8).trim();
    }

    private void handleLogout(SocketChannel sc, SelectionKey key) {
        try {
            sc.close();
            key.cancel();
        } catch (IOException exc) {
            LoggerController.writeLogsErrors(exc.getMessage());
        }
    }

    private void sendResponseToClient(SocketChannel sc, String serverResponse) throws IOException {
        if (serverResponse == null) {
            serverResponse = INPUT_ERROR;
        }

        this.buffer.clear();
        this.buffer.put((serverResponse + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        this.buffer.flip();

        sc.write(this.buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public static void main(String[] args) throws IOException {
        String apiKey;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter API-KEY: ");
            apiKey = reader.readLine();
        }

        CWMServer server = new CWMServer(apiKey);

        Thread serverThread = new Thread(() -> {
            System.out.println("Starting server on " + SERVER_HOST + ":" + SERVER_PORT + "...");
            server.start();
        });

        serverThread.setDaemon(false);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(SHUTDOWN_SERVER_RESPONSE);
            server.stop();
        }));
    }
}
