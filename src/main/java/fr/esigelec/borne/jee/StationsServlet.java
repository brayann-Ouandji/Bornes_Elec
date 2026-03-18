package fr.esigelec.borne.jee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/stations")
public class StationsServlet extends HttpServlet {

    // Informations de connexion
    private static final String URL = "jdbc:mysql://localhost:3306/bornes";
    private static final String USER = "root";
    private static final String PASS = "Esi_Lec-28-29INGE";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
       
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
     //Récupération des paramètres envoyés par le Front-End
        String minPuissanceParam = request.getParameter("minPuissance");
        String gratuitParam = request.getParameter("gratuit");
        
     // a requête SQL (Technique du WHERE 1=1)
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM bornes WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (minPuissanceParam != null && !minPuissanceParam.isEmpty()) {
            sqlBuilder.append(" AND puissance_nominale >= ?");
            // Conversion sécurisée en double
            parameters.add(Double.parseDouble(minPuissanceParam)); 
        }

        if (gratuitParam != null && gratuitParam.equals("true")) {
            sqlBuilder.append(" AND gratuit = ?");
            parameters.add(true);
        }
        
        sqlBuilder.append(" LIMIT 500"); // Limite de sécurité(pour pas ramer)

        PrintWriter out = response.getWriter();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            		PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }
                
                try (ResultSet rs = pstmt.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        jsonBuilder.append(",");
                    }
                    // Construction JSON 
                    jsonBuilder.append("{")
                               .append("\"id\":").append(rs.getInt("id")).append(",")
                               .append("\"nom_station\":\"").append(rs.getString("nom_station").replace("\"", "\\\"")).append("\",")
                               .append("\"lat\":").append(rs.getDouble("consolidated_latitude")).append(",")
                               .append("\"lon\":").append(rs.getDouble("consolidated_longitude")).append(",")
                               .append("\"puissance\":").append(rs.getDouble("puissance_nominale"))
                               .append("}");
                    first = false;
                }
            }
          }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Erreur serveur : " + e.getMessage() + "\"}");
            return;
        }

        jsonBuilder.append("]");
        out.print(jsonBuilder.toString());
        out.flush();
    }
}