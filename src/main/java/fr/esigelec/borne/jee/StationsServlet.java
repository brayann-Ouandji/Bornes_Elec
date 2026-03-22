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
//@WebServlet("/StationsServlet")
public class StationsServlet extends HttpServlet {

	// Informations de connexion (à retirer)
	private static final String URL = "jdbc:mysql://mysql:3306/bornes";;
	private static final String USER = "root";
	private static final String PASS = "root";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		// Récupération des paramètres envoyés par le Front-End
		String minPuissanceParam = request.getParameter("minPuissance");
		String gratuitParam = request.getParameter("gratuit");
		String priseEfParam = request.getParameter("priseEf");
		String priseT2Param = request.getParameter("priseT2");
		String priseCcsParam = request.getParameter("priseCcs");
		String priseChademoParam = request.getParameter("priseChademo");
		String accesParam = request.getParameter("acces");
		String paiementCbParam = request.getParameter("paiementCb");

		// a requête SQL (Technique du WHERE 1=1)
		StringBuilder sqlBuilder = new StringBuilder("SELECT* FROM bornes WHERE 1=1");
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
		if ("true".equals(priseEfParam)) {
			sqlBuilder.append(" AND prise_type_ef = 1");
		}
		if ("true".equals(priseT2Param)) {
			sqlBuilder.append(" AND prise_type_2 = 1");
		}
		if ("true".equals(priseCcsParam)) {
			sqlBuilder.append(" AND prise_type_combo_ccs = 1");
		}
		if ("true".equals(priseChademoParam)) {
			sqlBuilder.append(" AND prise_type_chademo = 1");
		}
		if (accesParam != null && !accesParam.isEmpty()) {
			sqlBuilder.append(" AND condition_acces = ?");
			parameters.add(accesParam);
		}
		if ("true".equals(paiementCbParam)) {
			sqlBuilder.append(" AND paiement_cb = 1");
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
						if (!first)
							jsonBuilder.append(",");

						String nom = rs.getString("nom_station");
						if (nom == null)
							nom = "";
						String adresse = rs.getString("adresse_station");
						if (adresse == null)
							adresse = "";
						String commune = rs.getString("consolidated_commune");
						if (commune == null)
							commune = "";
						String tarif = rs.getString("tarification");
						if (tarif == null)
							tarif = "";
						String acces = rs.getString("condition_acces");
						if (acces == null)
							acces = "";
						String horaires = rs.getString("horaires");
						if (horaires == null)
							horaires = "";

						jsonBuilder.append("{").append("\"id\":").append(rs.getInt("id")).append(",")
								.append("\"nom_station\":\"").append(nom.replace("\"", "\\\"")).append("\",")
								.append("\"adresse\":\"").append(adresse.replace("\"", "\\\"")).append("\",")
								.append("\"commune\":\"").append(commune.replace("\"", "\\\"")).append("\",")
								.append("\"lat\":").append(rs.getDouble("consolidated_latitude")).append(",")
								.append("\"lon\":").append(rs.getDouble("consolidated_longitude")).append(",")
								.append("\"puissance\":").append(rs.getDouble("puissance_nominale")).append(",")
								.append("\"nb_prises\":").append(rs.getInt("nbre_pdc")).append(",")
								.append("\"gratuit\":").append(rs.getBoolean("gratuit")).append(",")
								.append("\"paiement_cb\":").append(rs.getBoolean("paiement_cb")).append(",")
								.append("\"tarif\":\"").append(tarif.replace("\"", "\\\"")).append("\",")
								.append("\"acces\":\"").append(acces.replace("\"", "\\\"")).append("\",")
								.append("\"horaires\":\"").append(horaires.replace("\"", "\\\"")).append("\",")
								.append("\"prise_ef\":").append(rs.getBoolean("prise_type_ef")).append(",")
								.append("\"prise_t2\":").append(rs.getBoolean("prise_type_2")).append(",")
								.append("\"prise_ccs\":").append(rs.getBoolean("prise_type_combo_ccs")).append(",")
								.append("\"prise_chademo\":").append(rs.getBoolean("prise_type_chademo")).append("}");

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