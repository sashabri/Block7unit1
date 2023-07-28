package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {
    final private static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html",
            "/forms.html", "/classic.html", "/events.html", "/events.js");

    private final Map<HttpMethod, Map<String,Handler>> handlers = new HashMap<>();

    public void listen(int port){

        try (final var serverSocket = new ServerSocket(port)) {
            ExecutorService executor = Executors.newFixedThreadPool(64);
            while (true) {
                final var socket = serverSocket.accept();

                executor.execute(() -> handle(socket));
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket){
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            String httpMethod = parts[0];

            HttpMethod method;

            switch (httpMethod.toUpperCase()) {
                case "GET":
                    method = HttpMethod.GET;
                    break;
                case "POST":
                    method = HttpMethod.POST;
                    break;
                default:
                    return;
            }

            Map<String, String> headers = new HashMap<>();

            while (true) {
                String readedLine = in.readLine();

                if (readedLine == null || !readedLine.equals("")) {
                    break;
                }

                String[] partsOfLine = readedLine.split(": ");
                headers.put(partsOfLine[0], partsOfLine[1].trim());
            }

            String body = in.lines().collect(Collectors.joining());

            Request request = new Request(method, headers, body);

            final var path = parts[1];

            if (!validPaths.contains(path)) {
               sendNotFound(out);
                return;
            }

            if (handlers.get(method) != null && handlers.get(method).get(path) != null) {
                Handler handler = handlers.get(method).get(path);

                handler.handle(request, out);
            } else {
                sendNotFound(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(HttpMethod method, String path, Handler handler) {
        if(handlers.get(method) == null) {
            Map<String, Handler> map = new HashMap<>();
            map.put(path, handler);
            handlers.put(method, map);
        } else {
            Map<String, Handler> map = handlers.get(method);
            map.put(path, handler);
        }
    }

    public void sendNotFound(OutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}
