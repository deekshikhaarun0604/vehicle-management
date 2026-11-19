# Overall System Architecture Diagram

## 1. System Overview
The **Vehicle Service Management and Maintenance Analysis System** adopts a modern tiered enterprise architecture connecting a rich single-page client interface, a high-performance Java SE backend, an ACID-compliant MySQL relational database, and an asynchronous Python analytics engine.

---

## 2. Mermaid System Architecture Diagram

```mermaid
graph TD
    subgraph ClientTier ["Presentation Tier (Web Browser)"]
        UI["Modern Dashboard Single-Page App"]
        HTML["HTML5 Semantic Markup"]
        CSS["Responsive Automotive Theme CSS"]
        JS["Modular JavaScript (Fetch API & Router)"]
        UI --- HTML
        UI --- CSS
        UI --- JS
    end

    subgraph ServerTier ["Application Tier (Java SE 9+)"]
        HTTPServer["Lightweight HttpServer (Port 8080)"]
        Router["REST API Router (/api/*)"]
        StaticServer["Static Web Asset Handler"]
        
        subgraph BusinessLogic ["Core Business Logic"]
            Svc_Appt["Appointment Service (Conflict Prevention)"]
            Svc_Bill["Billing Service (Total = Labour + Parts)"]
            Svc_Maint["Maintenance & Parts Service"]
            Svc_Rep["Reporting & Aggregation Service"]
        end

        subgraph DataAccess ["Data Access Layer (JDBC)"]
            DAO["PreparedStatements & Connection Pool"]
            Driver["MySQL Connector/J & Fallback Engine"]
        end

        HTTPServer --> Router
        HTTPServer --> StaticServer
        Router --> BusinessLogic
        BusinessLogic --> DataAccess
    end

    subgraph DataTier ["Data Tier (Relational Storage)"]
        MySQL[("MySQL 8.0 Server (vehicle_service_db)")]
        Tables["3NF Tables: customers, vehicles, appointments, spare_parts, maintenance_records, maintenance_parts, bills"]
        MySQL --- Tables
    end

    subgraph AnalyticsTier ["Data Science & Analytics Tier (Python 3.14)"]
        ETL["Data Extraction & Preprocessing (PyMySQL / CSV)"]
        Pandas["Pandas & NumPy Analysis Engine"]
        Seaborn["Matplotlib & Seaborn Visualizations"]
        Notebook["Jupyter Notebook (vehicle_service_analysis.ipynb)"]
        Charts["Generated High-Res PNG Visualizations"]
        
        ETL --> Pandas
        Pandas --> Seaborn
        Seaborn --> Charts
        Notebook --> Charts
    end

    ClientTier <==>|HTTP / JSON REST API| HTTPServer
    DataAccess <==>|JDBC TCP/IP Connection| MySQL
    MySQL <==>|SQL Data Extraction| ETL
    Charts -.->|Embedded & Rendered| UI
```

---

## 3. Tier Responsibilities

1. **Client Tier**:
   - Single-page dashboard application rendered directly by standard web browsers.
   - Handles client-side form validation, modal state management, dynamic table filtering, search, and dynamic invoice rendering.
2. **Application Tier (Java Backend)**:
   - Built on Java SE without requiring external application servers.
   - Enforces business rules: guarantees non-conflicting appointment bookings and exact financial calculations.
   - Provides thread-safe REST API endpoints and delivers static frontend assets.
3. **Data Tier (MySQL Relational Database)**:
   - Stores normalized relational data across 7 tables with strict foreign key constraints, unique indexes, and domain checks.
4. **Analytics Tier (Python / Jupyter)**:
   - Reads database tables, performs data cleansing, handles missing values, detects duplicates, calculates statistical metrics, and generates publication-grade visualizations.
