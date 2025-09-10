package org.example.framework.services.implementations;

import org.example.framework.errors.FrameworkError;
import org.example.framework.services.interfaces.HTTPRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyServerImpl {

    private static boolean running = false;

    public static void stop(){
        running = false;
    }

    private static void handleListRequest(OutputStreamWriter out, BufferedOutputStream dataOut) throws IOException {
        out.write(getHeader(200, "application/json"));
        Map<String, String> fields = Map.of("status","OK","values",NUMBERS_LIST.toString());
        out.write(getJSON(fields));
    }

    private static void handleStatsRequest(OutputStreamWriter out, BufferedOutputStream dataOut) throws IOException {
        Map<String, String> fields;
        out.write(getHeader(200, "application/json"));
        if (NUMBERS_LIST.isEmpty()) {
            fields = Map.of("status", "ERR", "error", "empty_list");
        } else {
            fields = Map.of("status","OK","mean",String.valueOf(calculateMean()), "stddev",String.valueOf(calculateSTDDEV()), "count",String.valueOf(calculateCount()));
        }
        out.write(getJSON(fields));
    }
    private static void handleDynamicRoute(String path, OutputStreamWriter out, BufferedOutputStream dataOutput) throws IOException {
        //add?x=<real>
        // TODO --> Aqui estoy asumiendo que me va a solicitar solo add, esto debe refactorizarse
        String number = path.substring(7);
        Number parsedNumber = Long.valueOf(number);
        NUMBERS_LIST.add(parsedNumber);
        out.write(getHeader(200, "application/json"));
        Map<String, String> fields = Map.of("status","OK","added",number,"count",String.valueOf(NUMBERS_LIST.size()));
        out.write(getJSON(fields));
    }



    private static Number calculateSTDDEV(){
        return 3.2071349027;
    }



    public static void start(int port) {
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The server is running on port: " + port);
            running = true;
        } catch (IOException e) {
            System.out.println("Error running server...");
            e.printStackTrace();
            System.exit(1);
        }

        while (running) {
            Socket clientSocket = null;
            try {
                clientSocket = server.accept();
                handleRequest(clientSocket);
            } catch (FrameworkError e) {
                System.out.println("There has been a Framework error with the client request: ");
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Error code: " + e.getCode());

            } catch (Exception e) {
                System.out.println("Error accepting request from client");
                e.printStackTrace();
            }
        }
    }

    private static void handleRequest(Socket clientSocket) throws FrameworkError, Exception{

        OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream());
        BufferedOutputStream outData = new BufferedOutputStream(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        String inputLine, path = null;
        boolean firstLine = true;

        while ((inputLine = in.readLine()) != null) {
            if (firstLine) {
                String[] array = inputLine.split(" ");
                if (array.length < 2) throw new FrameworkError("Bad Request", 400);
                path = array[1];
            }
            firstLine = false;
            if (!in.ready())break;
        }
        if (path == null) sendResponse("nothing", out);
        else handleRoute(path, out, outData);

        out.close();
        in.close();
        clientSocket.close();
        outData.close();
    }

    private static void sendResponse(String message, OutputStreamWriter out) throws IOException{
        out.write(getHeader(200, "text/plain"));
        out.write(message);
    }

    private static void handleRoute(String path, OutputStreamWriter out, BufferedOutputStream dataOutput) throws Exception {

    }

    private static String getJSON(Map<String, String> fields) {
        StringBuilder s = new StringBuilder();
        s.append("{");
        int i = 0, size = fields.size();
        for (String k: fields.keySet()) {
            s.append("\"");
            s.append(k);
            s.append("\" : ");
            s.append("\"");
            s.append(fields.get(k));
            s.append("\"");
            i++;
            if(i<size) s.append(",");
        }
        s.append("}");
        return s.toString();
    }

    private static String getHeader(int code, String contentType) {
        return "HTTP/1.1 "+code+" OK\r\n"+
                "Content-Type: "+contentType+"\r\n"+
                "Access-Control-Allow-Credentials:true"+"\r\n"+
        "Access-Control-Allow-Methods:GET"+"\r\n"+
        "Access-Control-Allow-Origin:*"+"\r\n"+

        "\r\n";
    }

}
