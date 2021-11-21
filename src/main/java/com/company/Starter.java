package com.company;

import java.util.Timer;

public class Starter {
    static long period = 1000 * 60; //milliseconds

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new Task(), 0, period);
        //new Task().run();
        //dbLayer.shutdown();
    }
}