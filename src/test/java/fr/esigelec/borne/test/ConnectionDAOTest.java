package fr.esigelec.borne.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.esigelec.borne.dao.ConnectionDAO;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionDAOTest {

    private ConnectionDAO dao;
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        dao = new ConnectionDAO(); // on crée le DAO avant chaque test
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close(); // on ferme la connexion après chaque test
        }
    }

    @Test
    @DisplayName("La connexion ne doit pas être nulle")
    void testConnexionNonNulle() throws SQLException {
        conn = dao.getConnection();
        assertNotNull(conn);
    }

    @Test
    @DisplayName("La connexion doit être ouverte")
    void testConnexionOuverte() throws SQLException {
        conn = dao.getConnection();
        assertFalse(conn.isClosed());
    }

    @Test
    @DisplayName("La connexion doit se fermer correctement")
    void testConnexionFermeture() throws SQLException {
        conn = dao.getConnection();
        conn.close();
        assertTrue(conn.isClosed());
    }
}