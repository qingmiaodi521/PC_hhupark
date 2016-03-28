package com.hhupark.util;
import java.sql.*;
public class Connectomysql {
	private static Connection con;
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/easypark?useUnicode=true&characterEncoding=UTF-8";
	private static String user = "root";
	private static String password = "52811314";

	// 链接数据库
	public static Connection getConnection() {
		try {
			// 加载驱动
			Class.forName(driver);
			// 获得连接
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	public static void main(String args[])
	{
		try {
			Statement sql = getConnection().createStatement();
			ResultSet rs  = sql.executeQuery("select testprice from tempfile where testid=1");
			while (rs.next())
			{
				System.out.println(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
     }
}