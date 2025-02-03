document.addEventListener("DOMContentLoaded", function () {
    console.log("🌍 Leaflet.js Map wird initialisiert...");

    var map = L.map("map").setView([48.8566, 2.3522], 6);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    var markerGroup = L.layerGroup().addTo(map);

    function ladeGeisternetze() {
        console.log("🔄 Lade Geisternetz-Daten...");

        fetch("http://localhost:8080/ghostnetfishing_war_exploded/rest/geisternetze")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`🚨 Fehler: Server antwortete mit Status ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("📌 Geisternetz-Daten empfangen:", data);

                markerGroup.clearLayers(); // 🔥 Entferne alte Marker, bevor neue hinzugefügt werden

                data.forEach(net => {
                    if (!net.latitude || !net.longitude) {
                        console.warn("⚠️ Geisternetz hat keine gültigen Koordinaten:", net);
                        return;
                    }

                    L.marker([net.latitude, net.longitude])
                        .addTo(markerGroup)
                        .bindPopup(`
                            <b>Beschreibung:</b> ${net.beschreibung || "Keine Beschreibung"}<br>
                            <b>Größe:</b> ${net.groesse} m²<br>
                            <b>Status:</b> ${net.status}<br>
                            <b>Bergende Person:</b> ${net.bergendePerson ? net.bergendePerson.name : "Unbekannt"}<br>
                            <b>Meldende Person:</b> ${net.meldendePerson ? net.meldendePerson.name : "Unbekannt"}
                        `);
                });

                console.log("✅ Karte erfolgreich aktualisiert!");
            })
            .catch(error => console.error("❌ Fehler beim Laden der Geisternetz-Daten:", error));
    }

    // ✅ Funktionen global machen (wichtig für Buttons in `karte.xhtml`)
    window.ladeGeisternetze = ladeGeisternetze;
    window.aktualisiereKarte = function () {
        console.log("🔄 Manuelle Aktualisierung gestartet...");
        ladeGeisternetze();
    };

    // ⏳ Automatische Aktualisierung alle 10 Sekunden
    setInterval(ladeGeisternetze, 10000);

    // 📌 Beim ersten Laden sofort Daten holen
    ladeGeisternetze();
});
