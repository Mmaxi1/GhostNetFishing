# Ghost Net Fishing - Webanwendung

## 📌 Projektbeschreibung

Ghost Net Fishing ist eine Webanwendung zur Erfassung und Bergung von Geisternetzen. Benutzer können Geisternetze melden, freiwillige Bergungspersonen registrieren und den Status der Bergung verfolgen. Ziel der Anwendung ist es, die Verwaltung und Organisation der Bergung von Geisternetzen effizienter zu gestalten und Transparenz über den Fortschritt zu schaffen. Das Projekt wurde mit **Java EE**, **JSF (PrimeFaces)** und **Hibernate (JPA)** umgesetzt und folgt einer mehrschichtigen Architektur.

## ⚙️ Technologien

- **Java EE** (Jakarta EE) für die Geschäftslogik
- **JSF (PrimeFaces 13.0.0)** für die Benutzeroberfläche
- **Hibernate ORM** für die Datenbankanbindung
- **MySQL** als relationale Datenbank
- **Apache Tomcat 10.1.34** als Application Server
- **Maven** als Build-Management-Tool

## 🚀 Installationsanleitung

### Voraussetzungen:

- **Java 17 (Corretto oder OpenJDK)**
- **Apache Tomcat 10**
- **MySQL 8+**
- **Maven 3+**

### Schritte zur Installation:

1. **Datenbank einrichten:**

   - Eine MySQL-Datenbank namens `ghostnet_fishing` erstellen:
     ```sql
     CREATE DATABASE ghostnet_fishing;
     USE ghostnet_fishing;
     ```
   - Sicherstellen, dass die MySQL-Serververbindung in `persistence.xml` korrekt konfiguriert ist.

2. **Projekt klonen und bauen:**

   ```bash
   git clone https://github.com/Mmaxi1/GhostNetFishing
   cd ghostnetfishing
   mvn clean install
   ```

3. **War-Datei deployen:**

   - Die generierte `.war`-Datei (`target/ghostnetfishing.war`) in den `webapps`-Ordner von Tomcat kopieren.
   - Tomcat starten:
     ```bash
     ./catalina.sh run
     ```

4. **Webanwendung öffnen:**

   - Standardmäßig ist die Anwendung unter folgender URL erreichbar:
     ```
     http://localhost:8080/ghostnetfishing/index.xhtml
     ```

## ✨ Features

- **Melden von Geisternetzen (auch anonym möglich)**
- **Zuordnung einer bergenden Person**
- **Statusänderungen von "gemeldet" zu "Bergung bevorstehend" oder "geborgen"**
- **CRUD-Funktionalitäten für Geisternetz-Datenbankeinträge**
- **Datenbankanbindung mit JPA (Hibernate)**
- **JSF (PrimeFaces) für eine moderne UI**
- **Erfolgsmeldungen und Fehlermeldungen für eine bessere Benutzerführung**



## 📜 Lizenz

Dieses Projekt steht unter der MIT-Lizenz. Weitere Informationen siehe `LICENSE`-Datei.

