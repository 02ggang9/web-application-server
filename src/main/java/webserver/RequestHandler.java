package webserver;

import static webserver.http.HttpStatus.POST;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;
import webserver.http.body.DefaultHttpBody;
import webserver.http.body.HttpBody;
import webserver.http.header.DefaultHttpHeader;
import webserver.http.requestLine.DefaultRequestLine;
import webserver.http.header.HttpHeader;
import webserver.http.HttpStatus;
import webserver.http.requestLine.RequestLine;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            RequestLine requestLine = createRequestLine(bufferedReader);
            HttpHeader httpHeader = createHttpHeader(bufferedReader);
            HttpBody httpBody = createHttpBody(
                bufferedReader,
                httpHeader.getValue("Content-Length") != null ? Integer.parseInt(httpHeader.getValue("Content-Length")
                    .trim()) : 0
            );

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;

            if (Objects.equals(requestLine.getUrl(), "/index.html")) {
                body = Files.readAllBytes(new File("./webapp" + requestLine.getUrl()).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (Objects.equals(requestLine.getUrl(), "/user/create") && requestLine.getMethod() == POST) {
                String userId = httpBody.value("userId");
                String password = httpBody.value("password");
                String name = httpBody.value("name");

                body = Files.readAllBytes(new File("./webapp" + "/index.html").toPath());
                User user = new User(userId, password, name, "");
                response302Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
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

    private RequestLine createRequestLine(BufferedReader bufferedReader) throws IOException {
        String[] httpRequestLines = bufferedReader.readLine().split(" ");
        String pathOrQuery = httpRequestLines[1];
        Map<String, String> queries = new HashMap<>();

        if (pathOrQuery.split("\\?").length == 1) {
            String path = pathOrQuery;
            return new DefaultRequestLine(
                HttpStatus.valueOf(httpRequestLines[0]),
                path,
                queries,
                httpRequestLines[2]
            );
        }

        String[] split = pathOrQuery.split("\\?");
        String path = split[0];
        String query = split[1];

        for (String q : query.split("&")) {
            String[] keyAndValue = q.split("=");
            queries.put(keyAndValue[0], keyAndValue[1]);
        }

        return new DefaultRequestLine(
            HttpStatus.valueOf(httpRequestLines[0]),
            path,
            queries,
            httpRequestLines[2]
        );
    }

    private HttpHeader createHttpHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder httpHeaderBuilder = new StringBuilder();
        while(true) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                httpHeaderBuilder.append("\n");
                break;
            }
            httpHeaderBuilder.append(line).append("\n");
        }

        Map<String, String> httpHeaderMap = new HashMap<>();
        String[] httpHeaders = httpHeaderBuilder.toString().split("\n");
        for (String httpHeader : httpHeaders) {
            String[] split = httpHeader.split(":");
            String key = split[0];
            String value = split[1];
            httpHeaderMap.put(key, value);
        }

        return new DefaultHttpHeader(httpHeaderMap);
    }

    private HttpBody createHttpBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        Map<String, String> bodyMap = new HashMap<>();
        if (contentLength > 0) {
            String httpBodies = IOUtils.readData(bufferedReader, contentLength);
            String[] queryStrings = httpBodies.split("&");
            for (String queryString : queryStrings) {
                String[] split = queryString.split("=");
                bodyMap.put(split[0], split[1]);
            }
        }
        return new DefaultHttpBody(bodyMap);
    }
}
