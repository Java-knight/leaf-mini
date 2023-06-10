package com.knight.leaf;

public class Main {
    public static void main(String[] args) {
        int nextStep = 5;

        nextStep = nextStep << 1 > 11 ? nextStep : nextStep << 1;
        System.out.println(nextStep);
    }
}