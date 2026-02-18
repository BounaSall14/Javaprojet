package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.dao.VenteDAO;
import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.dao.ConnectionProvider;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VenteServiceTest {

    @Mock private VenteDAO venteDAO;
    @Mock private MedicamentDAO medicamentDAO;
    @Mock private ConnectionProvider connectionProvider;
    @Mock private Connection connection;

    private VenteService venteService;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(connectionProvider.getConnection()).thenReturn(connection);
        venteService = new VenteService(venteDAO, medicamentDAO, connectionProvider);
    }

    @Test
    void testVenteStockInsuffisant() throws Exception {
        Vente vente = new Vente();
        VenteLigne ligne = new VenteLigne();
        ligne.setMedicamentId(1);
        ligne.setQuantite(10);
        vente.getLignes().add(ligne);

        Medicament m = new Medicament();
        m.setId(1);
        m.setStock(5);
        m.setNomCommercial("Test Med");

        when(medicamentDAO.findById(eq(1), any(Connection.class))).thenReturn(Optional.of(m));

        Exception exception = assertThrows(Exception.class, () -> {
            venteService.effectuerVente(vente);
        });

        assertTrue(exception.getMessage().contains("Stock insuffisant"));
    }
}
