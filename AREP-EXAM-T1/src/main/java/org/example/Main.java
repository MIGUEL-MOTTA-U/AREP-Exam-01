package org.example;

import org.example.framework.services.implementations.HTTPServerImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.out.println("Starting server...");
        HTTPServerImpl.start(9001);
    }
}