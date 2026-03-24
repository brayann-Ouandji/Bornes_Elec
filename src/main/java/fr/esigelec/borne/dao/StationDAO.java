package fr.esigelec.borne.dao;

import fr.esigelec.borne.model.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAO {

	public List<Station> getFilteredStations(String minPuissance, String gratuit, String priseEf, String priseT2,
			String priseCcs, String priseChademo, String acces, String paiementCb) throws SQLException {

		List<Station> stations = new ArrayList<>();
		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM bornes WHERE 1=1");
		List<Object> parameters = new ArrayList<>();

		// on fait la requête
		if (minPuissance != null && !minPuissance.isEmpty()) {
			sqlBuilder.append(" AND puissance_nominale >= ?");
			parameters.add(Double.parseDouble(minPuissance));
		}
		if ("true".equals(gratuit)) {
			sqlBuilder.append(" AND gratuit = ?");
			parameters.add(true);
		}
		if ("true".equals(priseEf))
			sqlBuilder.append(" AND prise_type_ef = 1");
		if ("true".equals(priseT2))
			sqlBuilder.append(" AND prise_type_2 = 1");
		if ("true".equals(priseCcs))
			sqlBuilder.append(" AND prise_type_combo_ccs = 1");
		if ("true".equals(priseChademo))
			sqlBuilder.append(" AND prise_type_chademo = 1");

		if (acces != null && !acces.isEmpty()) {
			sqlBuilder.append(" AND condition_acces = ?");
			parameters.add(acces);
		}
		if ("true".equals(paiementCb)) {
			sqlBuilder.append(" AND paiement_cb = 1");
		}
		//sqlBuilder.append(" LIMIT 500");

		ConnectionDAO connectionDAO = new ConnectionDAO();

		try (Connection conn = connectionDAO.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

			for (int i = 0; i < parameters.size(); i++) {
				pstmt.setObject(i + 1, parameters.get(i));
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Station station = new Station();

					// Onrecupère les données de la bdd
					station.setId(rs.getInt("id"));
					station.setNom_station(rs.getString("nom_station") != null ? rs.getString("nom_station") : "");
					station.setAdresse(rs.getString("adresse_station") != null ? rs.getString("adresse_station") : "");
					station.setCommune(
							rs.getString("consolidated_commune") != null ? rs.getString("consolidated_commune") : "");
					station.setLat(rs.getDouble("consolidated_latitude"));
					station.setLon(rs.getDouble("consolidated_longitude"));
					station.setPuissance(rs.getDouble("puissance_nominale"));
					station.setNb_prises(rs.getInt("nbre_pdc"));
					station.setGratuit(rs.getBoolean("gratuit"));
					station.setPaiement_cb(rs.getBoolean("paiement_cb"));
					station.setTarif(rs.getString("tarification") != null ? rs.getString("tarification") : "");
					station.setAcces(rs.getString("condition_acces") != null ? rs.getString("condition_acces") : "");
					station.setHoraires(rs.getString("horaires") != null ? rs.getString("horaires") : "");
					station.setPrise_ef(rs.getBoolean("prise_type_ef"));
					station.setPrise_t2(rs.getBoolean("prise_type_2"));
					station.setPrise_ccs(rs.getBoolean("prise_type_combo_ccs"));
					station.setPrise_chademo(rs.getBoolean("prise_type_chademo"));

					stations.add(station);
				}
			}
		}
		return stations;
	}
}
