package com.company;

import java.sql.*;
import java.util.Objects;

public class DBLayer {
    private static Connection connection;
    private static final String tableName = "Name";
    private static final String tablePrice = "Prices";
    private static final String dbName = "test.db";

    public DBLayer() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String anotherPath = System.getProperty("user.dir");
        String path = System.getProperty("user.dir") + "/src/main/resources/";
        connection = DriverManager.getConnection("jdbc:sqlite:" + path + dbName);
    }

    public void connectToDb() throws ClassNotFoundException, SQLException {
        if (connection != null)
            return;
        Class.forName("org.sqlite.JDBC");
        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("test.db")).getPath();
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public void shutdown() throws SQLException {
        connection.close();
    }

    private ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private ResultSet execute(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
        return statement.getResultSet();
    }

    public int addNameIfAbsent(String key) throws SQLException {
        if (getCountNames(key) == 0)
            execute("INSERT INTO " + tableName + "(Name) VALUES('" + key + "')");
        return selectId(key).getInt("Id");
    }

    private ResultSet selectId(String name) throws SQLException {
        return executeQuery("SELECT * FROM " + tableName + " WHERE Name = '" + name + "'");
    }

    private int getCountNames(String name) throws SQLException {
        return executeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE Name = '" + name + "'").getInt("COUNT(*)");
    }

    public void putPrice(int id, Integer value, long currentTimeMillis) throws SQLException {
        execute(String.format("INSERT INTO %s (Id,Price,DateTime) VALUES(%s,%s,%s)",tablePrice,id,value,currentTimeMillis));
    }
}
