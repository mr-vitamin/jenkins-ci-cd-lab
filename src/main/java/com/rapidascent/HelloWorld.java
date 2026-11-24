package com.rapidascent;

public final class HelloWorld {
    private HelloWorld() {
    }

    public static String greeting() {
        return "Hello from the Jenkins CI/CD demo!";
    }

    public static void main(String[] args) {
        System.out.println(greeting());
    }
}
