/**
 * 
 */
package fr.esigelec.borne.dao;

import java.sql.*;


/**
 * 
 */
public class ConnectionDAO {


	/**
	 * @param args
	 */
    private static final String URL = "jdbc:mysql://localhost:3306/bornes";
    private static final String USER = "root";
    private static final String PASS = "root";

    // 1. Connection Method
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
	public static void main(String[] args) {
		String sql = "SELECT* FROM bornes WHERE id = 66";
		 try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                System.out.println(rs.getString("nom_station"));
	                System.out.println(rs.getString("adresse_station"));
	                System.out.println(rs.getString("nom_operateur"));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


