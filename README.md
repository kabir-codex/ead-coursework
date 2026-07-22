# TourEase — Hotel & Tourism Management System

A Hotel and Tourism Management System built in **Java Swing** with an **ANT** build system and a **MySQL** backend, following an **MVC** architecture with **DAO**, **Singleton**, and **Factory** design patterns.

---

## What It Does

TourEase manages the core operations of a hotel and tourism business:

- Customer registration and management
- Hotel room inventory and availability tracking
- Tour package creation and management
- Booking creation (linking customers, rooms, and packages)
- Payment recording and receipt generation
- Dashboard with live stats and a monthly revenue chart
- Management reports — **Booking Summary** and **Tour Package Revenue** — generated as real JasperReports documents (with charts) and shown in a print/export-ready viewer

---

## How It Works

**Architecture — MVC + DAO:**

```
VIEW (Swing panels)
   │  calls
CONTROLLER (validation + business logic)
   │  calls
DAO (SQL queries)
   │  uses
DATABASE (MySQL: tourism_management_db)
```

Each Swing panel (Customer, Room, Package, Booking, Payment, Dashboard, Report) talks only to its own Controller. Controllers validate input and enforce business rules (e.g. checkout date must be after check-in, room must be available), then call the matching DAO. DAOs are the only classes that touch SQL, all going through a single shared `DatabaseConnection` (Singleton).

**Reports specifically:** `ReportFactory` (Factory + Singleton) compiles and fills the `.jrxml` report templates in `resources/reports/` using live data from the database. Each report's trend chart is built directly with JFreeChart and passed into the report as an image parameter, then the filled report is opened in `JasperViewer` and also exported to a PDF.

**Typical flow (Booking example):**
1. User picks a customer, room, and dates in `BookingPanel`.
2. `BookingController` validates the input and calculates the total.
3. `BookingController` calls `BookingDAO.insert()` and `RoomDAO.setAvailability(BOOKED)`.
4. The table refreshes with the new booking.

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| UI Framework | Java Swing (NetBeans Form Builder) |
| Build Tool | Apache ANT |
| Database | MySQL |
| JDBC Driver | mysql-connector-j |
| Reporting | JasperReports 7.0.6 + JFreeChart |

---

## How to Run

**From NetBeans:** Open the project → Run.

**From Terminal:**
```bash
ant -f build.xml -Dnb.internal.action.name=run run
```

Database connection settings are in `src/util/DatabaseConnection.java`. Import `sql/tourism_management_db.sql` into a MySQL database named `tourism_management_db` before running.

---

## Login Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Receptionist | `receptionist` | `recept123` |
