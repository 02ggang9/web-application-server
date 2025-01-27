package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.DefaultHttp;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private String path;
    private List<String> parameter = new ArrayList<>();


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder httpHeaderBuilder = new StringBuilder();

            while(true) {
                String line = bufferedReader.readLine();

                if (line.isEmpty()) {
                    httpHeaderBuilder.append("\n");
                    break;
                }

                httpHeaderBuilder
                    .append(line)
                    .append("\n");
            }

            String[] httpHeaders = httpHeaderBuilder.toString()
                .split("\n");

            DefaultHttp defaultHttp = new DefaultHttp(httpHeaders);
            DataOutputStream dos = new DataOutputStream(out);
            final byte[] body;
            if (Objects.equals(defaultHttp.getHttpStartLine().getPath(), "/index.html")) {
                body = Files.readAllBytes(new File("./webapp" + defaultHttp.getHttpStartLine().getPath()).toPath());
            } else if (
                Objects.equals(defaultHttp.getHttpStartLine().getPath(), "/user/create") &&
                    Objects.equals(defaultHttp.getHttpStartLine().getMethod(), "GET")
            ) {
                String userId = defaultHttp.getHttpStartLine().getQueries().get("userId");
                String password = defaultHttp.getHttpStartLine().getQueries().get("password");
                String name = defaultHttp.getHttpStartLine().getQueries().get("name");

                User user = new User(userId, password, name, null);
                body = "Hello World".getBytes();
                System.out.println("user = " + user);
            } else {
                body = "Hello World".getBytes();

            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
