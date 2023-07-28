package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
  public static void main(String[] args) {

    final var server = new Server();

    server.addHandler(HttpMethod.GET, "/classic.html", (request, responseStream) -> {
      try {
        final var filePath = Path.of(".", "public", "/classic.html");
        final var mimeType = Files.probeContentType(filePath);
        final var template = Files.readString(filePath);
        final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(content);
        responseStream.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    server.addHandler(HttpMethod.POST, "/links.html", (request, responseStream) -> {
      try {
        final var filePath = Path.of(".", "public", "/links.html");
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, responseStream);
        responseStream.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    server.listen(9999);
  }
}


