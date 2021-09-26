package com.company;

import java.sql.SQLException;
import java.util.Timer;

public class Starter {
    public static DBLayer dbLayer;
    static long period = 1000 * 60; //milliseconds

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        dbLayer = new DBLayer();
        //Timer timer = new Timer();
        //timer.schedule(new Task(), 0, period);
        new Task().run();
        dbLayer.shutdown();
    }
}