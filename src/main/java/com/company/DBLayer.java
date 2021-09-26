package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DBLayer {
    private static Connection connection;
    private static final String tableName = "Name";
    private static final String tablePrice = "Prices";
    private static final String dbName = "test.db";

    public DBLayer() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
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

    private static ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private static ResultSet execute(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
        return statement.getResultSet();
    }

    public static int addNameIfAbsent(String key, int expPrice) throws SQLException {
        if (getCountNames(key) == 0)
            execute("INSERT INTO " + tableName + "(Name,expPrice) VALUES('" + key + "','" + expPrice + "')");
        return selectId(key).getInt("Id");
    }

    public static ResultSet selectId(String name) throws SQLException {
        return executeQuery("SELECT * FROM " + tableName + " WHERE Name = '" + name + "'");
    }

    private static int getCountNames(String name) throws SQLException {
        return executeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE Name = '" + name + "'").getInt("COUNT(*)");
    }

    public void putPrice(int id, Integer value, long currentTimeMillis) throws SQLException {
        execute(String.format("INSERT INTO %s (Id,Price,DateTime) VALUES(%s,%s,%s)", tablePrice, id, value, currentTimeMillis));
    }

    public List<Integer> selectAllIds() throws SQLException {
        List<Integer> response = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM " + tableName);
        while (resultSet.next())
            response.add(resultSet.getInt("Id"));
        return response;
    }

    public int selectPrice(Integer id) throws SQLException {
        ResultSet resultSet = executeQuery(String.format("SELECT * FROM %s WHERE Id = %s ORDER BY DateTime DESC", tablePrice, id));
        return resultSet.getInt("Price");
    }

    public String selectName(Integer id) throws SQLException {
        ResultSet resultSet = executeQuery(String.format("SELECT * FROM %s WHERE Id = %s", tableName, id));
        return resultSet.getString("Name");
    }
}
