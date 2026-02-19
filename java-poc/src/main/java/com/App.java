package com.demo;

public class App {
    public static void main(String[] args) {
        System.out.println("SonarQube POC Running");
    }

    public static int divide(int a, int b) {
        return a / b;   // intentional risk (for scan detection)
    }
}
