package com.demo;

public class App {

    public static void main(String[] args) {
        System.out.println("SonarQube POC Running");

        int result1 = calculateSum(10, 20);
        int result2 = calculateSumDuplicate(10, 20);   // duplicate logic
        int result3 = calculateSumDuplicate2(10, 20);  // duplicate logic

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);

        // Risky call (for sonar bug detection)
        // int risky = divide(10, 0);
    }

    // Original method
    public static int calculateSum(int a, int b) {
        int sum = a + b;
        int result = sum * 2;
        return result;
    }

    // Duplicate method 1
    public static int calculateSumDuplicate(int a, int b) {
        int sum = a + b;
        int result = sum * 2;
        return result;
    }

    // Duplicate method 2
    public static int calculateSumDuplicate2(int a, int b) {
        int sum = a + b;
        int result = sum * 2;
        return result;
    }

    // Risky method for sonar detection
    public static int divide(int a, int b) {
        return a / b;
    }
}