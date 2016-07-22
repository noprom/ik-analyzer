package com.huntdreams.ik;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil
 * 连接mysql
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/22/16 9:59 AM.
 */
public class DBUtil {

    private static String dirverClassName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://192.168.104.101:3306/testdb?useUnicode=true&characterEncoding=utf8";
    private static String user = "root";
    private static String password = "leizhimin";

    /**
     * 获得数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(dirverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}