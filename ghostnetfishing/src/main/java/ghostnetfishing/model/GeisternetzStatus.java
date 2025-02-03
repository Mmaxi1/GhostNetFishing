package ghostnetfishing.model;


public enum GeisternetzStatus {
    GEMELDET, // Eine meldende Person hat das Geisternetz im System erfasst.
    BERGUNG_BEVORSTEHEND, // Eine bergende Person hat die Bergung angekündigt.
    GEBORGEN, // Eine bergende Person hat das Geisternetz erfolgreich geborgen.
    VERSCHOLLEN // Eine beliebige Person hat festgestellt, dass das Geisternetz am gemeldeten Standort nicht auffindbar ist.
}
