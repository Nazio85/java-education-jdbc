import java.awt.*;
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement ps;


    public static void main(String[] args) {

        try {
            connect();
            createTable();
//            insertProduct();
            console();
//            clearTable();
//            deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void console() {
        try (Scanner scanner = new Scanner(System.in)) {
            String order = scanner.nextLine();
            ResultSet resultSet;
            if (order.startsWith("/цена")) {
                String productName = order.substring(6);
                resultSet = statement.executeQuery("SELECT cost FROM product WHERE title == '" + productName + "';");
                if (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                } else System.out.println("Такого товара нет");
            } else if (order.startsWith("/сменитьцену")) {
                String[] changCost = order.split(" ", 3);
                statement.executeUpdate("UPDATE product SET cost='"+ changCost[2] +"' WHERE title ='"+ changCost[1]+"'");
            } else if (order.startsWith("/товарыпоцене")) {
                String[] between = order.split(" ", 3);

                resultSet = statement.executeQuery("SELECT title, cost FROM  product WHERE cost >= "+
                Integer.parseInt(between[1]) + " AND cost <= "
                 + Integer.parseInt(between[2])
                );

                while (resultSet.next()){
                    System.out.println("Товар -" + resultSet.getString(1) + " по цене - "
                            + resultSet.getString(2));
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private static void insertProduct() throws SQLException {
        try { //если товар добавлен то ничего не делать
            connection.setAutoCommit(false);
            for (int i = 1; i < 10001; i++) {
                statement.executeUpdate("INSERT INTO product (prodid, title, cost) " +
                        "VALUES ('id_товара " + i + "','" + "товар" + i + "','" + i * 10 + "')");
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {

        }
    }

    private static void createTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                "prodid TEXT UNIQUE, " +
                "title  TEXT, " +
                "cost   INTEGER);");
    }

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:base.db");
        statement = connection.createStatement();
    }

    private static void disconnect() {
//        try {
//            ps.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteTable() throws SQLException {
        statement.executeUpdate("DROP TABLE das");
    }

    private static void clearTable() throws SQLException {
        statement.executeUpdate("DELETE FROM product");
    }
}
