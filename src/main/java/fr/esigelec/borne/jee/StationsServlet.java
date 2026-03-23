package fr.esigelec.borne.jee;

import fr.esigelec.borne.dao.StationDAO;
import fr.esigelec.borne.model.Station;
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

	// lien avec labdd
	private final StationDAO stationDAO = new StationDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		// Récupéres des paramètres du fFront-End
		String minPuissanceParam = request.getParameter("minPuissance");
		String gratuitParam = request.getParameter("gratuit");
		String priseEfParam = request.getParameter("priseEf");
		String priseT2Param = request.getParameter("priseT2");
		String priseCcsParam = request.getParameter("priseCcs");
		String priseChademoParam = request.getParameter("priseChademo");
		String accesParam = request.getParameter("acces");
		String paiementCbParam = request.getParameter("paiementCb");
		PrintWriter out = response.getWriter();

		try {
			// Récupe de la liste des objets Station via le DAO
			List<Station> stations = stationDAO.getFilteredStations(minPuissanceParam, gratuitParam, priseEfParam,
					priseT2Param, priseCcsParam, priseChademoParam, accesParam, paiementCbParam);
			// le JSON 
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("[");

			boolean first = true;
			for (Station station : stations) {
				if (!first) {
					jsonBuilder.append(",");
				}

				// On récupère les données de l'objet Station et on fait attention aavec les guillemets pour
				// les Strings
				jsonBuilder.append("{").append("\"id\":").append(station.getId()).append(",")
						.append("\"nom_station\":\"").append(station.getNom_station().replace("\"", "\\\""))
						.append("\",").append("\"adresse\":\"").append(station.getAdresse().replace("\"", "\\\""))
						.append("\",").append("\"commune\":\"").append(station.getCommune().replace("\"", "\\\""))
						.append("\",").append("\"lat\":").append(station.getLat()).append(",").append("\"lon\":")
						.append(station.getLon()).append(",").append("\"puissance\":").append(station.getPuissance())
						.append(",").append("\"nb_prises\":").append(station.getNb_prises()).append(",")
						.append("\"gratuit\":").append(station.isGratuit()).append(",").append("\"paiement_cb\":")
						.append(station.isPaiement_cb()).append(",").append("\"tarif\":\"")
						.append(station.getTarif().replace("\"", "\\\"")).append("\",").append("\"acces\":\"")
						.append(station.getAcces().replace("\"", "\\\"")).append("\",").append("\"horaires\":\"")
						.append(station.getHoraires().replace("\"", "\\\"")).append("\",").append("\"prise_ef\":")
						.append(station.isPrise_ef()).append(",").append("\"prise_t2\":").append(station.isPrise_t2())
						.append(",").append("\"prise_ccs\":").append(station.isPrise_ccs()).append(",")
						.append("\"prise_chademo\":").append(station.isPrise_chademo()).append("}");

				first = false;
			}

			jsonBuilder.append("]");
			out.print(jsonBuilder.toString());

		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\":\"Erreur base de données : " + e.getMessage() + "\"}");
		} finally {
			out.flush();
		}
	}
}