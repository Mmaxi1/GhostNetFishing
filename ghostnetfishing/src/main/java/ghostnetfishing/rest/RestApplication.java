package ghostnetfishing.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/rest")  // 👈 Basis-URL für die REST-API
public class RestApplication extends Application {
}
