package fr.esigelec.borne.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.esigelec.borne.dao.ConnectionDAO;

import java.sql.*;

class StationsIntegrationTest {

    private static final String TEST_URL = "jdbc:mysql://127.0.0.1:3306/bornes";
    private static final String TEST_USER = "root";
    private static final String TEST_PASSWORD = "root";
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        conn = new ConnectionDAO().getConnection();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    @DisplayName("La table bornes contient des données")
    void testTableNonVide() throws Exception {
        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM bornes");
        rs.next();
        assertTrue(rs.getInt(1) > 0, "La table doit contenir au moins une borne");
    }

    @Test
    @DisplayName("Filtre puissance >= 50 : résultats cohérents")
    void testFiltrePuissance() throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM bornes WHERE puissance_nominale >= ?");
        ps.setDouble(1, 50.0);
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertTrue(rs.getInt(1) > 0, "Il doit y avoir des bornes >= 50 kW");
    }

    @Test
    @DisplayName("Filtre accès libre : résultats cohérents")
    void testFiltreAccesLibre() throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM bornes WHERE condition_acces = ?");
        ps.setString(1, "Accès libre");
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertTrue(rs.getInt(1) > 0, "Il doit y avoir des bornes en accès libre");
    }

    @Test
    @DisplayName("Filtre prise Type 2 : résultats cohérents")
    void testFiltrePriseType2() throws Exception {
        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM bornes WHERE prise_type_2 = 1");
        rs.next();
        assertTrue(rs.getInt(1) > 0, "Il doit y avoir des bornes avec prise Type 2");
    }

    @Test
    @DisplayName("Les coordonnées GPS ne doivent pas être nulles")
    void testCoordonnéesNonNulles() throws Exception {
        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM bornes WHERE consolidated_latitude IS NULL OR consolidated_longitude IS NULL");
        rs.next();
        assertEquals(0, rs.getInt(1), "Aucune borne ne doit avoir des coordonnées nulles");
    }
}
