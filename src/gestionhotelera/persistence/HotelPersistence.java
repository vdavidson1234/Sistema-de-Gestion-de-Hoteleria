package gestionhotelera.persistence;

import gestionhotelera.control.HotelRepository;
import gestionhotelera.control.HotelSnapshot;
import gestionhotelera.control.PersistenciaException;
import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.EstadoPago;
import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Pago;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.ServicioConsumido;
import gestionhotelera.dominio.TipoHabitacion;
import gestionhotelera.pagos.MetodoPago;
import gestionhotelera.pagos.PagoEfectivo;
import gestionhotelera.pagos.PagoOnlineSimulado;
import gestionhotelera.pagos.PagoTarjeta;
import gestionhotelera.pagos.PagoTransferencia;
import gestionhotelera.state.ReservaCanceladaState;
import gestionhotelera.state.ReservaConfirmadaState;
import gestionhotelera.state.ReservaFinalizadaState;
import gestionhotelera.state.ReservaPendienteState;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adaptador JDBC entre el dominio del hotel y PostgreSQL.
 * Maneja habitaciones, huespedes, reservas, estadias, servicios y pagos.
 */
public class HotelPersistence implements HotelRepository {
    private final Database db;

    public HotelPersistence(Database db) {
        this.db = db;
    }

    public void initialize() throws SQLException {
        db.initializeSchema();
    }

    @Override
    public HotelSnapshot loadSnapshot() throws PersistenciaException {
        try {
            initialize();
            Hotel hotel = loadHotel();
            return new HotelSnapshot(hotel, loadEstadias(hotel));
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo cargar el estado del hotel.", ex);
        }
    }

    public void saveHotel(Hotel hotel) throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                saveHotelData(c, hotel);
                c.commit();
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    @Override
    public void saveSnapshot(Hotel hotel, Map<String, Estadia> estadiasPorReserva) throws PersistenciaException {
        try {
            try (Connection c = db.getConnection()) {
                c.setAutoCommit(false);
                try {
                    saveHotelData(c, hotel);
                    saveEstadiasData(c, estadiasPorReserva);
                    c.commit();
                } catch (SQLException | RuntimeException ex) {
                    c.rollback();
                    throw ex;
                }
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo guardar el estado del hotel.", ex);
        }
    }

    public Hotel loadHotel() throws SQLException {
        Hotel hotel = new Hotel("Hotel Aurora", "Av. Central 123");

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT numero, capacidad, precio_base, tipo, estado FROM habitaciones ORDER BY numero")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int numero = rs.getInt("numero");
                    int capacidad = rs.getInt("capacidad");
                    double precio = rs.getDouble("precio_base");
                    TipoHabitacion tipo = TipoHabitacion.valueOf(rs.getString("tipo"));
                    Habitacion habitacion = new Habitacion(numero, capacidad, precio, tipo);
                    try {
                        habitacion.cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.valueOf(rs.getString("estado")));
                    } catch (IllegalArgumentException ex) {
                        // Se ignoran estados desconocidos para no bloquear la carga completa.
                    }
                    hotel.agregarHabitacion(habitacion);
                }
            }

            Map<Integer, Huesped> huespedes = new HashMap<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id, nombre, apellido, dni, telefono, email, tipo FROM huespedes ORDER BY id")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Huesped huesped = new Huesped(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("tipo"));
                    huespedes.put(id, huesped);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT codigo, huesped_id, habitacion_num, fecha_ingreso, fecha_egreso, estado "
                            + "FROM reservas ORDER BY fecha_ingreso, codigo")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String codigo = rs.getString("codigo");
                    Huesped huesped = huespedes.get(rs.getInt("huesped_id"));
                    Habitacion habitacion = hotel.buscarHabitacion(rs.getInt("habitacion_num"));
                    if (huesped == null || habitacion == null) {
                        continue;
                    }
                    LocalDate ingreso = rs.getDate("fecha_ingreso").toLocalDate();
                    LocalDate egreso = rs.getDate("fecha_egreso").toLocalDate();
                    Reserva reserva = new Reserva(codigo, huesped, habitacion, ingreso, egreso);
                    aplicarEstadoReserva(reserva, rs.getString("estado"));
                    hotel.registrarReserva(reserva);
                }
            }
        }

        return hotel;
    }

    public Map<String, Estadia> loadEstadias(Hotel hotel) throws SQLException {
        Map<String, Reserva> reservasPorCodigo = new HashMap<>();
        for (Reserva reserva : hotel.getReservas()) {
            reservasPorCodigo.put(reserva.getCodigo(), reserva);
        }

        Map<String, Estadia> estadiasPorReserva = new LinkedHashMap<>();
        Map<Integer, Estadia> estadiasPorId = new HashMap<>();

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id, reserva_codigo, fecha_ingreso_real, fecha_egreso_real FROM estadias ORDER BY id")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String codigoReserva = rs.getString("reserva_codigo");
                    Reserva reserva = reservasPorCodigo.get(codigoReserva);
                    if (reserva == null) {
                        continue;
                    }
                    Estadia estadia = new Estadia(
                            reserva,
                            rs.getDate("fecha_ingreso_real").toLocalDate(),
                            rs.getDate("fecha_egreso_real").toLocalDate());
                    estadiasPorReserva.put(codigoReserva, estadia);
                    estadiasPorId.put(rs.getInt("id"), estadia);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT estadia_id, nombre, descripcion, precio FROM servicios ORDER BY id")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Estadia estadia = estadiasPorId.get(rs.getInt("estadia_id"));
                    if (estadia != null) {
                        estadia.agregarServicio(new ServicioPersistido(
                                rs.getString("nombre"),
                                rs.getString("descripcion"),
                                rs.getDouble("precio")));
                    }
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT estadia_id, monto, fecha, metodo, estado FROM pagos ORDER BY id")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Estadia estadia = estadiasPorId.get(rs.getInt("estadia_id"));
                    if (estadia == null) {
                        continue;
                    }
                    Timestamp fecha = rs.getTimestamp("fecha");
                    LocalDateTime fechaPago = fecha == null ? LocalDateTime.now() : fecha.toLocalDateTime();
                    Pago pago = new Pago(
                            rs.getDouble("monto"),
                            metodoPagoDesdeNombre(rs.getString("metodo")),
                            fechaPago,
                            estadoPagoDesdeTexto(rs.getString("estado")));
                    estadia.registrarPago(pago);
                }
            }
        }

        return estadiasPorReserva;
    }

    private void saveHotelData(Connection c, Hotel hotel) throws SQLException {
        try (PreparedStatement upsertHabitacion = c.prepareStatement(
                "INSERT INTO habitaciones(numero, capacidad, precio_base, tipo, estado) VALUES (?, ?, ?, ?, ?) "
                        + "ON CONFLICT (numero) DO UPDATE SET "
                        + "capacidad = EXCLUDED.capacidad, "
                        + "precio_base = EXCLUDED.precio_base, "
                        + "tipo = EXCLUDED.tipo, "
                        + "estado = EXCLUDED.estado")) {
            for (Habitacion habitacion : hotel.getHabitaciones()) {
                upsertHabitacion.setInt(1, habitacion.getNumero());
                upsertHabitacion.setInt(2, habitacion.getCapacidad());
                upsertHabitacion.setDouble(3, habitacion.getPrecioBase());
                upsertHabitacion.setString(4, habitacion.getTipo().name());
                upsertHabitacion.setString(5, habitacion.getEstado().name());
                upsertHabitacion.addBatch();
            }
            upsertHabitacion.executeBatch();
        }

        try (PreparedStatement upsertHuesped = c.prepareStatement(
                "INSERT INTO huespedes(nombre, apellido, dni, telefono, email, tipo) VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (dni) DO UPDATE SET "
                        + "nombre = EXCLUDED.nombre, "
                        + "apellido = EXCLUDED.apellido, "
                        + "telefono = EXCLUDED.telefono, "
                        + "email = EXCLUDED.email, "
                        + "tipo = EXCLUDED.tipo "
                        + "RETURNING id");
                PreparedStatement upsertReserva = c.prepareStatement(
                        "INSERT INTO reservas(codigo, huesped_id, habitacion_num, fecha_ingreso, fecha_egreso, estado) "
                                + "VALUES (?, ?, ?, ?, ?, ?) "
                                + "ON CONFLICT (codigo) DO UPDATE SET "
                                + "huesped_id = EXCLUDED.huesped_id, "
                                + "habitacion_num = EXCLUDED.habitacion_num, "
                                + "fecha_ingreso = EXCLUDED.fecha_ingreso, "
                                + "fecha_egreso = EXCLUDED.fecha_egreso, "
                                + "estado = EXCLUDED.estado")) {
            for (Reserva reserva : hotel.getReservas()) {
                int huespedId = upsertHuesped(upsertHuesped, reserva.getHuesped());

                upsertReserva.setString(1, reserva.getCodigo());
                upsertReserva.setInt(2, huespedId);
                upsertReserva.setInt(3, reserva.getHabitacion().getNumero());
                upsertReserva.setDate(4, Date.valueOf(reserva.getFechaIngreso()));
                upsertReserva.setDate(5, Date.valueOf(reserva.getFechaEgreso()));
                upsertReserva.setString(6, reserva.getEstado().name());
                upsertReserva.addBatch();
            }
            upsertReserva.executeBatch();
        }
    }

    private int upsertHuesped(PreparedStatement upsertHuesped, Huesped huesped) throws SQLException {
        upsertHuesped.setString(1, safe(huesped.getNombre()));
        upsertHuesped.setString(2, safe(huesped.getApellido()));
        upsertHuesped.setString(3, safe(huesped.getDni()));
        upsertHuesped.setString(4, safe(huesped.getTelefono()));
        upsertHuesped.setString(5, safe(huesped.getEmail()));
        upsertHuesped.setString(6, safe(huesped.getTipoHuesped()));

        try (ResultSet rs = upsertHuesped.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        throw new SQLException("No se pudo obtener el id del huesped " + huesped.getDni());
    }

    private void saveEstadiasData(Connection c, Map<String, Estadia> estadiasPorReserva) throws SQLException {
        try (PreparedStatement deletePagos = c.prepareStatement(
                "DELETE FROM pagos WHERE estadia_id IN (SELECT id FROM estadias WHERE reserva_codigo = ?)");
                PreparedStatement deleteServicios = c.prepareStatement(
                        "DELETE FROM servicios WHERE estadia_id IN (SELECT id FROM estadias WHERE reserva_codigo = ?)");
                PreparedStatement deleteEstadia = c.prepareStatement("DELETE FROM estadias WHERE reserva_codigo = ?");
                PreparedStatement insertEstadia = c.prepareStatement(
                        "INSERT INTO estadias(reserva_codigo, fecha_ingreso_real, fecha_egreso_real) "
                                + "VALUES (?, ?, ?) RETURNING id");
                PreparedStatement insertServicio = c.prepareStatement(
                        "INSERT INTO servicios(estadia_id, nombre, descripcion, precio) VALUES (?, ?, ?, ?)");
                PreparedStatement insertPago = c.prepareStatement(
                        "INSERT INTO pagos(estadia_id, monto, fecha, metodo, estado) VALUES (?, ?, ?, ?, ?)")) {

            for (Map.Entry<String, Estadia> entry : estadiasPorReserva.entrySet()) {
                String codigoReserva = entry.getKey();
                Estadia estadia = entry.getValue();

                deletePagos.setString(1, codigoReserva);
                deletePagos.executeUpdate();
                deleteServicios.setString(1, codigoReserva);
                deleteServicios.executeUpdate();
                deleteEstadia.setString(1, codigoReserva);
                deleteEstadia.executeUpdate();

                insertEstadia.setString(1, codigoReserva);
                insertEstadia.setDate(2, Date.valueOf(estadia.getFechaIngresoReal()));
                insertEstadia.setDate(3, Date.valueOf(estadia.getFechaEgresoReal()));
                int estadiaId;
                try (ResultSet rs = insertEstadia.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el id de estadia para " + codigoReserva);
                    }
                    estadiaId = rs.getInt("id");
                }

                for (ServicioConsumido servicio : estadia.getServicios()) {
                    insertServicio.setInt(1, estadiaId);
                    insertServicio.setString(2, servicio.getNombre());
                    insertServicio.setString(3, servicio.getDescripcion());
                    insertServicio.setDouble(4, servicio.getPrecio());
                    insertServicio.addBatch();
                }
                insertServicio.executeBatch();

                for (Pago pago : estadia.getPagos()) {
                    insertPago.setInt(1, estadiaId);
                    insertPago.setDouble(2, pago.getMonto());
                    insertPago.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));
                    insertPago.setString(4, pago.getMetodoPago().getNombre());
                    insertPago.setString(5, pago.getEstadoPago().name());
                    insertPago.addBatch();
                }
                insertPago.executeBatch();
            }
        }
    }

    private void aplicarEstadoReserva(Reserva reserva, String estadoTexto) {
        EstadoReserva estado = EstadoReserva.PENDIENTE;
        try {
            estado = EstadoReserva.valueOf(estadoTexto);
        } catch (IllegalArgumentException | NullPointerException ex) {
            // Si el estado no es valido, se conserva PENDIENTE.
        }

        switch (estado) {
            case CONFIRMADA:
                reserva.setEstado(new ReservaConfirmadaState());
                break;
            case CANCELADA:
                reserva.setEstado(new ReservaCanceladaState());
                break;
            case FINALIZADA:
                reserva.setEstado(new ReservaFinalizadaState());
                break;
            case PENDIENTE:
            default:
                reserva.setEstado(new ReservaPendienteState());
                break;
        }
    }

    private MetodoPago metodoPagoDesdeNombre(String nombre) {
        if ("Tarjeta".equals(nombre)) {
            return new PagoTarjeta();
        }
        if ("Transferencia".equals(nombre)) {
            return new PagoTransferencia();
        }
        if ("Pago online simulado".equals(nombre)) {
            return new PagoOnlineSimulado();
        }
        return new PagoEfectivo();
    }

    private EstadoPago estadoPagoDesdeTexto(String texto) {
        try {
            return EstadoPago.valueOf(texto);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return EstadoPago.PENDIENTE;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static class ServicioPersistido implements ServicioConsumido {
        private final String nombre;
        private final String descripcion;
        private final double precio;

        ServicioPersistido(String nombre, String descripcion, double precio) {
            this.nombre = nombre == null ? "Servicio" : nombre;
            this.descripcion = descripcion == null ? "" : descripcion;
            this.precio = precio;
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        @Override
        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public double getPrecio() {
            return precio;
        }
    }
}
