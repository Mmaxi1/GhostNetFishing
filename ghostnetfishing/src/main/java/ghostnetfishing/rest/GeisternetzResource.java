package ghostnetfishing.rest;

import ghostnetfishing.dao.GeisternetzDAO;
import ghostnetfishing.model.Geisternetz;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/geisternetze")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeisternetzResource {

    private GeisternetzDAO geisternetzDAO = new GeisternetzDAO(); // 🔥 Instanziierung direkt

    @GET
    public List<Geisternetz> getGeisternetzData() {
        return geisternetzDAO.findAll();  // 🔥 Direkter Aufruf der Methode aus GeisternetzDAO
    }
}
