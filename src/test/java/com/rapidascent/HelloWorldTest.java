package com.rapidascent;

public final class HelloWorldTest {
    private HelloWorldTest() {
    }

    public static void main(String[] args) {
        String expected = "Hello from the Jenkins CI/CD demo!";
        String actual = HelloWorld.greeting();
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but got \"" + actual + "\"");
        }
        System.out.println("PASS: greeting returns the expected message.");
    }
}
