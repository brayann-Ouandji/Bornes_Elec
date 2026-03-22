/**
 * 
 */
package fr.esigelec.borne.dao;

import java.sql.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 */
public class ConnectionDAO {
	private static final String URL = "jdbc:mysql://mysql:3306/bornes";
	private static final String USER = "root";
	private static final String PASS = "root";

	public Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(URL, USER, PASS);
	}

	public void testDAO(int id) {
		String sql = "SELECT * FROM bornes WHERE id = ?";
		try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				System.out.println(rs.getString("nom_station"));
				System.out.println(rs.getString("adresse_station"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		for(int i = 1; i<20; i++) {
			new ConnectionDAO().testDAO(i);
			System.out.print("\n");
		}
	}
}
