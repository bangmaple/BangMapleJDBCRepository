/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.jdbc.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author bangmaple
 */
public final class ConnectionManager {

    private static final String JAVA_EE_ENVIRONMENT = "java:comp/env";
    public static String DATASOURCE_NAME = "JDBCRepository";

    private static DataSource getJNDIDataSource() throws NamingException {
        Context ctx = new InitialContext();
        Context envCtx = (Context) ctx.lookup(JAVA_EE_ENVIRONMENT);
        return (DataSource) envCtx.lookup(DATASOURCE_NAME);
    }

    public static Connection getConnection() {
        try {
            return getJNDIDataSource().getConnection();
        } catch (Exception e) {
            try {
                String protocol = "jdbc:sqlserver";
                String databaseName = "users_management";
                String host = "localhost";
                int port = 1433;
                String username = "sa";
                String password = "Nhatrang1";
                String connectionString = String.format("%s://%s:%s", protocol, host, port);
                return DriverManager.getConnection(connectionString, username, password);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
