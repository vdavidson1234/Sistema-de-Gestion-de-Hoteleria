package gestionhotelera.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple SQLite connection manager and schema initializer.
 * Note: requires the sqlite-jdbc driver on the classpath at runtime.
 */
public class Database {
    private final String url;

    public Database(String filePath) {
        this.url = "jdbc:sqlite:" + filePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initializeSchema() throws SQLException {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");

            s.execute("CREATE TABLE IF NOT EXISTS habitaciones ("
                    + "numero INTEGER PRIMARY KEY,"
                    + "capacidad INTEGER NOT NULL,"
                    + "precio_base REAL NOT NULL,"
                    + "tipo TEXT NOT NULL,"
                    + "estado TEXT NOT NULL"
                    + ")");

            s.execute("CREATE TABLE IF NOT EXISTS huespedes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT,"
                    + "apellido TEXT,"
                    + "dni TEXT,"
                    + "telefono TEXT,"
                    + "email TEXT,"
                    + "tipo TEXT"
                    + ")");

            s.execute("CREATE TABLE IF NOT EXISTS reservas ("
                    + "codigo TEXT PRIMARY KEY,"
                    + "huesped_id INTEGER NOT NULL,"
                    + "habitacion_num INTEGER NOT NULL,"
                    + "fecha_ingreso TEXT NOT NULL,"
                    + "fecha_egreso TEXT NOT NULL,"
                    + "estado TEXT NOT NULL,"
                    + "FOREIGN KEY(huesped_id) REFERENCES huespedes(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY(habitacion_num) REFERENCES habitaciones(numero) ON DELETE CASCADE"
                    + ")");

            s.execute("CREATE TABLE IF NOT EXISTS estadias ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "reserva_codigo TEXT NOT NULL,"
                    + "fecha_ingreso_real TEXT NOT NULL,"
                    + "fecha_egreso_real TEXT NOT NULL,"
                    + "FOREIGN KEY(reserva_codigo) REFERENCES reservas(codigo) ON DELETE CASCADE"
                    + ")");

            s.execute("CREATE TABLE IF NOT EXISTS servicios ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "estadia_id INTEGER NOT NULL,"
                    + "nombre TEXT NOT NULL,"
                    + "precio REAL NOT NULL,"
                    + "FOREIGN KEY(estadia_id) REFERENCES estadias(id) ON DELETE CASCADE"
                    + ")");

            s.execute("CREATE TABLE IF NOT EXISTS pagos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "estadia_id INTEGER NOT NULL,"
                    + "monto REAL NOT NULL,"
                    + "fecha TEXT NOT NULL,"
                    + "metodo TEXT NOT NULL,"
                    + "estado TEXT NOT NULL,"
                    + "FOREIGN KEY(estadia_id) REFERENCES estadias(id) ON DELETE CASCADE"
                    + ")");
        }
    }
}
