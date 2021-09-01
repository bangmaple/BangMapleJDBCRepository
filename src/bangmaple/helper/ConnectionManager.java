/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.helper;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author bangmaple
 */
public final class ConnectionManager {

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String protocol = "jdbc:sqlserver";
            String databaseName = "users_management";
            String host = "localhost";
            int port = 1433;
            String username = "sa";
            String password = "Nhatrang1";
            String connectionString = String.format("%s://%s:%s;databaseName=%s", protocol, host, port, databaseName);
            return DriverManager.getConnection(connectionString, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
