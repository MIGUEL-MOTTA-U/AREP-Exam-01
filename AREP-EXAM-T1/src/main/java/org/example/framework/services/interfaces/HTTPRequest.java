package org.example.framework.services.interfaces;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public interface HTTPRequest {
    void handleRequest(String path, OutputStreamWriter out, BufferedOutputStream outData) throws Exception;
}
