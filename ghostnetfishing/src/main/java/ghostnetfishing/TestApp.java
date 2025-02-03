package ghostnetfishing;

import ghostnetfishing.dao.GeisternetzDAO;
import ghostnetfishing.model.Geisternetz;
import ghostnetfishing.model.GeisternetzStatus;

import java.math.BigDecimal;
import java.util.List;

public class TestApp {
    public static void main(String[] args) {
        // Initialize DAO
        GeisternetzDAO geisternetzDAO = new GeisternetzDAO();

        // Example usage: Create a new Geisternetz entry
        Geisternetz geisternetz = new Geisternetz();
        geisternetz.setId(1L); // Use Long type for ID
        geisternetz.setStatus(GeisternetzStatus.GEMELDET); // Initial status: GEMELDET
        geisternetz.setGroesse(50);
        geisternetz.setBeschreibung("Ein großes Geisternetz im nördlichen Meer.");
        geisternetz.setLatitude(new BigDecimal("57.1234")); // Set latitude as BigDecimal
        geisternetz.setLongitude(new BigDecimal("-1.2345")); // Set longitude as BigDecimal

        // Save Geisternetz to the database
        geisternetzDAO.save(geisternetz);

        // Retrieve all Geisternetze
        List<Geisternetz> geisternetze = geisternetzDAO.findAll();
        System.out.println("Retrieved Geisternetze:");
        for (Geisternetz net : geisternetze) {
            System.out.println(net);
        }

        // Update Geisternetz status
        geisternetz.setStatus(GeisternetzStatus.GEBORGEN); // Change status to GEBORGEN
        geisternetzDAO.update(geisternetz);

        // Delete a Geisternetz entry
        geisternetzDAO.delete(geisternetz);

        System.out.println("Geisternetz operations completed.");
    }
}
