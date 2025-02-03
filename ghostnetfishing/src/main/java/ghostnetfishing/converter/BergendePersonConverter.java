package ghostnetfishing.converter;

import ghostnetfishing.dao.BergendePersonDAO;
import ghostnetfishing.model.BergendePerson;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "bergendePersonConverter")
public class BergendePersonConverter implements Converter<BergendePerson> {

    private final BergendePersonDAO bergendePersonDAO = new BergendePersonDAO(); // 🔥 Stellt sicher, dass DAO existiert

    @Override
    public BergendePerson getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            return bergendePersonDAO.findById(id);
        } catch (NumberFormatException e) {
            throw new ConverterException("Ungültige ID: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BergendePerson person) {
        if (person == null || person.getId() == null) {
            return "";
        }
        return String.valueOf(person.getId());
    }
}
