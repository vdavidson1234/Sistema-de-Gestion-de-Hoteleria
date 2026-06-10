package gestionhotelera.ui;

import gestionhotelera.control.GestorEstadias;
import gestionhotelera.control.GestorHabitaciones;
import gestionhotelera.control.GestorPagos;
import gestionhotelera.control.GestorPersistenciaHotelera;
import gestionhotelera.control.GestorReservas;
import gestionhotelera.control.HotelSnapshot;
import gestionhotelera.decorator.CocheraDecorator;
import gestionhotelera.decorator.DesayunoDecorator;
import gestionhotelera.decorator.LavanderiaDecorator;
import gestionhotelera.decorator.ServicioBase;
import gestionhotelera.decorator.SpaDecorator;
import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.EstadoHabitacion;
import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.ServicioEstadia;
import gestionhotelera.dominio.StaySummary;
import gestionhotelera.dominio.TipoHabitacion;
import gestionhotelera.factory.HabitacionFactory;
import gestionhotelera.pagos.MetodoPago;
import gestionhotelera.pagos.PagoEfectivo;
import gestionhotelera.pagos.PagoOnlineSimulado;
import gestionhotelera.pagos.PagoTarjeta;
import gestionhotelera.pagos.PagoTransferencia;
import gestionhotelera.strategy.DescuentoClienteFrecuente;
import gestionhotelera.strategy.DescuentoPromocionEspecial;
import gestionhotelera.strategy.DescuentoStrategy;
import gestionhotelera.strategy.DescuentoTemporadaBaja;
import gestionhotelera.strategy.PoliticaPrecio;
import gestionhotelera.strategy.PoliticaPrecioNormal;
import gestionhotelera.strategy.PoliticaPrecioPromocional;
import gestionhotelera.strategy.PoliticaPrecioTemporadaAlta;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz grafica principal del sistema hotelero.
 * Mantiene la logica de negocio en los controladores y solo coordina la entrada y salida visual.
 */
public class HotelGUI extends JFrame {
    private final Hotel hotel;
    private final GestorHabitaciones gestorHabitaciones;
    private final GestorReservas gestorReservas;
    private final GestorEstadias gestorEstadias;
    private final GestorPagos gestorPagos;
    private final GestorPersistenciaHotelera gestorPersistencia;
    private final Map<String, Reserva> reservasPorCodigo;
    private final Map<String, Estadia> estadiasPorReserva;

    private final DefaultTableModel habitacionesModel;
    private final DefaultTableModel reservasModel;
    private final DefaultTableModel estadiasModel;

    private final JLabel resumenHotelLabel;
    private final JLabel habitacionesCountLabel;
    private final JLabel reservasCountLabel;
    private final JLabel estadiasCountLabel;
    private final JTextArea logArea;

    private final JTextField habitacionNumeroField;
    private final JTextField habitacionCapacidadField;
    private final JTextField habitacionPrecioField;
    private final JComboBox<TipoHabitacion> habitacionTipoCombo;
    private final JComboBox<EstadoHabitacion> habitacionEstadoCombo;

    private final JTextField huespedNombreField;
    private final JTextField huespedApellidoField;
    private final JTextField huespedDniField;
    private final JTextField huespedTelefonoField;
    private final JTextField huespedEmailField;
    private final JTextField huespedTipoField;
    private final JTextField reservaHabitacionField;
    private final JTextField reservaIngresoField;
    private final JTextField reservaEgresoField;
    private final JTextField reservaPersonasField;

    private final JComboBox<String> estadiaReservaCombo;
    private final JTextField estadiaIngresoField;
    private final JTextField estadiaEgresoField;
    private final JComboBox<String> servicioCombo;
    private final JComboBox<String> politicaPrecioCombo;
    private final JComboBox<String> descuentoCombo;
    private final JTextField montoPagoField;
    private final JComboBox<String> metodoPagoCombo;
    private final JLabel totalCalculadoLabel;
    private final JLabel saldoEstimadoLabel;

    /**
     * Construye la ventana principal con datos de ejemplo iniciales.
     */
    public HotelGUI() {
        this(new GestorPersistenciaHotelera());
    }

    public HotelGUI(GestorPersistenciaHotelera gestorPersistencia) {
        this.gestorPersistencia = gestorPersistencia;
        HotelSnapshot snapshot = this.gestorPersistencia.cargarEstadoInicial();

        this.hotel = snapshot.getHotel();
        this.gestorHabitaciones = new GestorHabitaciones(hotel, new HabitacionFactory());
        this.gestorReservas = new GestorReservas(hotel);
        this.gestorEstadias = new GestorEstadias();
        this.gestorPagos = new GestorPagos();
        this.reservasPorCodigo = new LinkedHashMap<>();
        for (Reserva reserva : hotel.getReservas()) {
            this.reservasPorCodigo.put(reserva.getCodigo(), reserva);
        }
        this.estadiasPorReserva = snapshot.getEstadiasPorReserva();

        setTitle("Sistema de Gestion Hotelera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 840));
        setLocationRelativeTo(null);

        habitacionesModel = new DefaultTableModel(new Object[] {"Numero", "Capacidad", "Precio base", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservasModel = new DefaultTableModel(new Object[] {"Codigo", "Huesped", "Habitacion", "Ingreso", "Egreso", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        estadiasModel = new DefaultTableModel(new Object[] {"Reserva", "Huesped", "Noches", "Servicios", "Pagos", "Ingreso real", "Egreso real", "Total servicios", "Total pagado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resumenHotelLabel = new JLabel();
        resumenHotelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        habitacionesCountLabel = new JLabel();
        reservasCountLabel = new JLabel();
        estadiasCountLabel = new JLabel();
        logArea = new JTextArea(12, 80);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        habitacionNumeroField = new JTextField(10);
        habitacionCapacidadField = new JTextField(10);
        habitacionPrecioField = new JTextField(10);
        habitacionTipoCombo = new JComboBox<>(TipoHabitacion.values());
        habitacionEstadoCombo = new JComboBox<>(EstadoHabitacion.values());

        huespedNombreField = new JTextField(12);
        huespedApellidoField = new JTextField(12);
        huespedDniField = new JTextField(12);
        huespedTelefonoField = new JTextField(12);
        huespedEmailField = new JTextField(12);
        huespedTipoField = new JTextField(12);
        reservaHabitacionField = new JTextField(10);
        reservaIngresoField = new JTextField(10);
        reservaEgresoField = new JTextField(10);
        reservaPersonasField = new JTextField(10);

        estadiaReservaCombo = new JComboBox<>();
        estadiaIngresoField = new JTextField(10);
        estadiaEgresoField = new JTextField(10);
        servicioCombo = new JComboBox<>(new String[] {"Desayuno", "Spa", "Lavanderia", "Cochera"});
        politicaPrecioCombo = new JComboBox<>(new String[] {"Normal", "Temporada alta", "Promocional"});
        descuentoCombo = new JComboBox<>(new String[] {"Ninguno", "Cliente frecuente", "Promocion especial", "Temporada baja"});
        montoPagoField = new JTextField(10);
        metodoPagoCombo = new JComboBox<>(new String[] {"Efectivo", "Tarjeta", "Transferencia", "Pago online simulado"});
        totalCalculadoLabel = new JLabel("Total calculado: -");
        saldoEstimadoLabel = new JLabel("Saldo estimado: -");

        setLayout(new BorderLayout(12, 12));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenidoPrincipal(), BorderLayout.CENTER);
        add(crearPanelLog(), BorderLayout.SOUTH);

        log(this.gestorPersistencia.getUltimoMensaje());
        cargarDatosIniciales();
        persistirSnapshot();
        refrescarTodaLaVista();
    }

    /**
     * Inicia la interfaz en el hilo de eventos.
     */
    public static void mostrar() {
        SwingUtilities.invokeLater(() -> new HotelGUI().setVisible(true));
    }

    public static void mostrar(GestorPersistenciaHotelera gestorPersistencia) {
        SwingUtilities.invokeLater(() -> new HotelGUI(gestorPersistencia).setVisible(true));
    }

    private JComponent crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 0, 14));

        JLabel titulo = new JLabel("Sistema de Gestion Hotelera", SwingConstants.LEFT);
        titulo.setFont(titulo.getFont().deriveFont(24f));

        JPanel resumenPanel = new JPanel(new GridBagLayout());
        resumenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Estado general"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        resumenPanel.add(habitacionesCountLabel, gbc);
        gbc.gridy = 1;
        resumenPanel.add(reservasCountLabel, gbc);
        gbc.gridy = 2;
        resumenPanel.add(estadiasCountLabel, gbc);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(resumenHotelLabel, BorderLayout.CENTER);
        panel.add(resumenPanel, BorderLayout.EAST);
        return panel;
    }

    private JComponent crearContenidoPrincipal() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Hotel", crearTabHotel());
        tabs.addTab("Habitaciones", crearTabHabitaciones());
        tabs.addTab("Reservas", crearTabReservas());
        tabs.addTab("Estadias y pagos", crearTabEstadias());
        return tabs;
    }

    private JComponent crearTabHotel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel descripcion = new JPanel(new GridBagLayout());
        descripcion.setBorder(BorderFactory.createTitledBorder("Resumen del hotel"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        descripcion.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        descripcion.add(new JLabel("Hotel Aurora"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        descripcion.add(new JLabel("Direccion:"), gbc);
        gbc.gridx = 1;
        descripcion.add(new JLabel("Av. Central 123"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        descripcion.add(new JLabel("Flujo principal:"), gbc);
        gbc.gridx = 1;
        descripcion.add(new JLabel("Habitaciones -> Reservas -> Estadias -> Pagos"), gbc);

        panel.add(descripcion, BorderLayout.NORTH);
        panel.add(crearPanelTablasHabitacionesReservas(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent crearTabHabitaciones() {
        return crearPanelConFormularioYTabla(crearFormularioHabitacion(), crearTablaHabitaciones());
    }

    private JComponent crearTabReservas() {
        return crearPanelConFormularioYTabla(crearFormularioReserva(), crearTablaReservas());
    }

    private JComponent crearTabEstadias() {
        return crearPanelConFormularioYTabla(crearFormularioEstadiaYPagos(), crearTablaEstadias());
    }

    private JComponent crearPanelConFormularioYTabla(JComponent formulario, JComponent tabla) {
        JPanel contenedor = new JPanel(new BorderLayout(12, 12));
        contenedor.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane formularioScroll = new JScrollPane(formulario);
        formularioScroll.setBorder(BorderFactory.createEmptyBorder());
        formularioScroll.setPreferredSize(new Dimension(420, 0));
        formularioScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formularioScroll, tabla);
        splitPane.setResizeWeight(0.34);
        splitPane.setDividerLocation(430);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contenedor.add(splitPane, BorderLayout.CENTER);
        return contenedor;
    }

    private JComponent crearPanelTablasHabitacionesReservas() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(crearTablaHabitaciones(), BorderLayout.CENTER);
        panel.add(crearTablaReservas(), BorderLayout.SOUTH);
        return panel;
    }

    private JComponent crearTablaHabitaciones() {
        JTable tabla = new JTable(habitacionesModel);
        tabla.setFillsViewportHeight(true);
        return wrapConTitulo("Habitaciones registradas", tabla);
    }

    private JComponent crearTablaReservas() {
        JTable tabla = new JTable(reservasModel);
        tabla.setFillsViewportHeight(true);
        return wrapConTitulo("Reservas activas", tabla);
    }

    private JComponent crearTablaEstadias() {
        JTable tabla = new JTable(estadiasModel);
        tabla.setFillsViewportHeight(true);
        return wrapConTitulo("Estadias y pagos", tabla);
    }

    private JPanel crearFormularioHabitacion() {
        JPanel panel = crearFormularioBase("Alta / estado de habitaciones");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Numero", habitacionNumeroField);
        fila = agregarCampo(panel, fila, "Capacidad", habitacionCapacidadField);
        fila = agregarCampo(panel, fila, "Precio base", habitacionPrecioField);
        fila = agregarCampo(panel, fila, "Tipo", habitacionTipoCombo);
        fila = agregarCampo(panel, fila, "Estado", habitacionEstadoCombo);

        JButton crearButton = new JButton("Registrar habitacion");
        crearButton.addActionListener(e -> registrarHabitacion());

        JButton estadoButton = new JButton("Aplicar estado");
        estadoButton.addActionListener(e -> cambiarEstadoHabitacion());

        agregarBotonera(panel, fila, crearButton, estadoButton);
        return panel;
    }

    private JPanel crearFormularioReserva() {
        JPanel panel = crearFormularioBase("Nueva reserva");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Nombre", huespedNombreField);
        fila = agregarCampo(panel, fila, "Apellido", huespedApellidoField);
        fila = agregarCampo(panel, fila, "DNI", huespedDniField);
        fila = agregarCampo(panel, fila, "Telefono", huespedTelefonoField);
        fila = agregarCampo(panel, fila, "Email", huespedEmailField);
        fila = agregarCampo(panel, fila, "Tipo de huesped", huespedTipoField);
        fila = agregarCampo(panel, fila, "Habitacion", reservaHabitacionField);
        fila = agregarCampo(panel, fila, "Ingreso (yyyy-MM-dd)", reservaIngresoField);
        fila = agregarCampo(panel, fila, "Egreso (yyyy-MM-dd)", reservaEgresoField);
        fila = agregarCampo(panel, fila, "Personas", reservaPersonasField);

        JButton reservarButton = new JButton("Crear y confirmar");
        reservarButton.addActionListener(e -> crearYConfirmarReserva());

        JButton cancelarButton = new JButton("Cancelar seleccionada");
        cancelarButton.addActionListener(e -> cancelarReservaSeleccionada());

        agregarBotonera(panel, fila, reservarButton, cancelarButton);
        return panel;
    }

    private JPanel crearFormularioEstadiaYPagos() {
        JPanel panel = crearFormularioBase("Estadia, servicios y pagos");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Reserva", estadiaReservaCombo);
        fila = agregarCampo(panel, fila, "Ingreso real", estadiaIngresoField);
        fila = agregarCampo(panel, fila, "Egreso real", estadiaEgresoField);
        fila = agregarCampo(panel, fila, "Servicio", servicioCombo);
        fila = agregarCampo(panel, fila, "Politica de precio", politicaPrecioCombo);
        fila = agregarCampo(panel, fila, "Descuento", descuentoCombo);
        fila = agregarCampo(panel, fila, "Monto pago", montoPagoField);
        fila = agregarCampo(panel, fila, "Metodo de pago", metodoPagoCombo);

        JPanel resultado = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        resultado.add(totalCalculadoLabel, gbc);
        gbc.gridy = 1;
        resultado.add(saldoEstimadoLabel, gbc);

        GridBagConstraints panelGbc = new GridBagConstraints();
        panelGbc.gridx = 0;
        panelGbc.gridy = fila;
        panelGbc.gridwidth = 2;
        panelGbc.fill = GridBagConstraints.HORIZONTAL;
        panelGbc.weightx = 1.0;
        panelGbc.insets = new Insets(6, 6, 6, 6);
        panel.add(resultado, panelGbc);

        JButton registrarEstadiaButton = new JButton("Registrar estadia");
        registrarEstadiaButton.addActionListener(e -> registrarEstadiaSeleccionada());

        JButton agregarServicioButton = new JButton("Agregar servicio");
        agregarServicioButton.addActionListener(e -> agregarServicioSeleccionado());

        JButton calcularButton = new JButton("Calcular total");
        calcularButton.addActionListener(e -> calcularTotalSeleccionado());

        JButton pagarButton = new JButton("Registrar pago");
        pagarButton.addActionListener(e -> registrarPagoSeleccionado());

        fila += 2;
        agregarBotonera(panel, fila, registrarEstadiaButton, agregarServicioButton, calcularButton, pagarButton);
        return panel;
    }

    private JPanel crearFormularioBase(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setPreferredSize(new Dimension(420, 0));
        return panel;
    }

    private int agregarCampo(JPanel panel, int fila, String etiqueta, JComponent componente) {
        GridBagConstraints etiquetaGbc = new GridBagConstraints();
        etiquetaGbc.gridx = 0;
        etiquetaGbc.gridy = fila;
        etiquetaGbc.anchor = GridBagConstraints.WEST;
        etiquetaGbc.insets = new Insets(4, 6, 4, 6);

        GridBagConstraints campoGbc = new GridBagConstraints();
        campoGbc.gridx = 1;
        campoGbc.gridy = fila;
        campoGbc.fill = GridBagConstraints.HORIZONTAL;
        campoGbc.weightx = 1.0;
        campoGbc.insets = new Insets(4, 6, 4, 6);

        panel.add(new JLabel(etiqueta + ":"), etiquetaGbc);
        panel.add(componente, campoGbc);
        return fila + 1;
    }

    private void agregarBotonera(JPanel panel, int fila, JButton... botones) {
        JPanel botonera = new JPanel();
        for (JButton boton : botones) {
            botonera.add(boton);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 6, 6, 6);
        panel.add(botonera, gbc);
    }

    private JComponent wrapConTitulo(String titulo, JTable tabla) {
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(800, 220));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent crearPanelLog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return panel;
    }

    private void cargarDatosIniciales() {
        if (!hotel.getHabitaciones().isEmpty()) {
            log("Se cargaron datos existentes del hotel.");
            return;
        }

        gestorHabitaciones.crearYRegistrarHabitacion(101, 2, 45000, TipoHabitacion.SIMPLE);
        gestorHabitaciones.crearYRegistrarHabitacion(202, 3, 68000, TipoHabitacion.DOBLE);
        gestorHabitaciones.crearYRegistrarHabitacion(303, 4, 95000, TipoHabitacion.SUITE);

        log("Se cargaron habitaciones de ejemplo para comenzar a trabajar.");
    }

    private void persistirSnapshot() {
        String mensaje = gestorPersistencia.guardarSnapshot(hotel, estadiasPorReserva);
        if (mensaje != null) {
            log(mensaje);
        }
    }

    private void registrarHabitacion() {
        try {
            int numero = parseEntero(habitacionNumeroField.getText(), "numero de habitacion");
            int capacidad = parseEntero(habitacionCapacidadField.getText(), "capacidad");
            double precioBase = parseDecimal(habitacionPrecioField.getText(), "precio base");
            TipoHabitacion tipo = (TipoHabitacion) habitacionTipoCombo.getSelectedItem();

            if (hotel.buscarHabitacion(numero) != null) {
                throw new IllegalStateException("Ya existe una habitacion con ese numero.");
            }

            gestorHabitaciones.crearYRegistrarHabitacion(numero, capacidad, precioBase, tipo);
            log("Habitacion " + numero + " registrada correctamente.");
            limpiarCampos(habitacionNumeroField, habitacionCapacidadField, habitacionPrecioField);
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cambiarEstadoHabitacion() {
        try {
            int numero = parseEntero(habitacionNumeroField.getText(), "numero de habitacion");
            Habitacion habitacion = hotel.buscarHabitacion(numero);
            if (habitacion == null) {
                throw new IllegalStateException("No se encontro la habitacion indicada.");
            }

            EstadoHabitacion estado = (EstadoHabitacion) habitacionEstadoCombo.getSelectedItem();
            habitacion.cambiarEstado(estado);
            log("La habitacion " + numero + " paso a estado " + estado + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void crearYConfirmarReserva() {
        try {
            Huesped huesped = new Huesped(
                    huespedNombreField.getText().trim(),
                    huespedApellidoField.getText().trim(),
                    huespedDniField.getText().trim(),
                    huespedTelefonoField.getText().trim(),
                    huespedEmailField.getText().trim(),
                    huespedTipoField.getText().trim());

            int numeroHabitacion = parseEntero(reservaHabitacionField.getText(), "numero de habitacion");
            LocalDate ingreso = parseFecha(reservaIngresoField.getText(), "fecha de ingreso");
            LocalDate egreso = parseFecha(reservaEgresoField.getText(), "fecha de egreso");
            int personas = parseEntero(reservaPersonasField.getText(), "cantidad de personas");

            Reserva reserva = gestorReservas.crearReserva(huesped, numeroHabitacion, ingreso, egreso, personas);
            gestorReservas.confirmarReserva(reserva);
            reservasPorCodigo.put(reserva.getCodigo(), reserva);

            log("Reserva creada y confirmada: " + reserva.getCodigo() + ".");
            limpiarCampos(huespedNombreField, huespedApellidoField, huespedDniField, huespedTelefonoField, huespedEmailField,
                    huespedTipoField, reservaHabitacionField, reservaIngresoField, reservaEgresoField, reservaPersonasField);
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cancelarReservaSeleccionada() {
        try {
            String codigo = seleccionarReservaActual();
            if (codigo == null) {
                throw new IllegalStateException("Primero registra o selecciona una reserva para cancelar.");
            }

            Reserva reserva = reservasPorCodigo.get(codigo);
            if (reserva == null) {
                throw new IllegalStateException("La reserva seleccionada no existe en memoria.");
            }

            gestorReservas.cancelarReserva(reserva);
            log("Reserva cancelada: " + codigo + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void registrarEstadiaSeleccionada() {
        try {
            String codigo = seleccionarReservaActual();
            if (codigo == null) {
                throw new IllegalStateException("No hay reservas disponibles para registrar una estadia.");
            }

            Reserva reserva = reservasPorCodigo.get(codigo);
            if (reserva == null) {
                throw new IllegalStateException("La reserva seleccionada no existe.");
            }

            LocalDate ingreso = parseFecha(estadiaIngresoField.getText(), "ingreso real");
            LocalDate egreso = parseFecha(estadiaEgresoField.getText(), "egreso real");
            StaySummary resumen = gestorEstadias.registrarEstadia(reserva, ingreso, egreso);
            estadiasPorReserva.put(codigo, resumen.getEstadia());
            montoPagoField.setText(montoPagoField.getText().isBlank() ? "0" : montoPagoField.getText());

            log("Estadia registrada para la reserva " + codigo + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void agregarServicioSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaSeleccionada();
            ServicioEstadia servicio = crearServicioDesdeSeleccion();
            gestorEstadias.agregarServicio(estadia, servicio);
            log("Servicio agregado: " + servicio.getNombre() + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void calcularTotalSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaSeleccionada();
            PoliticaPrecio politicaPrecio = crearPoliticaDesdeSeleccion();
            DescuentoStrategy descuento = crearDescuentoDesdeSeleccion();
            double total = gestorEstadias.calcularCostoTotal(estadia, politicaPrecio, descuento);
            double saldo = estadia.calcularSaldoPendiente(total);

            totalCalculadoLabel.setText(String.format("Total calculado: $ %.2f", total));
            saldoEstimadoLabel.setText(String.format("Saldo estimado: $ %.2f", saldo));
            montoPagoField.setText(String.format("%.2f", total));
            log("Total calculado para la estadia " + estadia.getReserva().getCodigo() + ": " + String.format("$ %.2f", total));
            refrescarTablaEstadias();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void registrarPagoSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaSeleccionada();
            double monto = parseDecimal(montoPagoField.getText(), "monto del pago");
            MetodoPago metodoPago = crearMetodoPagoDesdeSeleccion();
            gestorPagos.registrarPago(estadia, monto, metodoPago);
            log("Pago registrado por " + metodoPago.getNombre() + ": $ " + String.format("%.2f", monto));
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private Estadia obtenerEstadiaSeleccionada() {
        String codigo = seleccionarReservaActual();
        if (codigo == null) {
            throw new IllegalStateException("No hay una reserva seleccionada.");
        }

        Estadia estadia = estadiasPorReserva.get(codigo);
        if (estadia == null) {
            throw new IllegalStateException("Primero registra la estadia de la reserva seleccionada.");
        }
        return estadia;
    }

    private String seleccionarReservaActual() {
        Object seleccionado = estadiaReservaCombo.getSelectedItem();
        if (seleccionado == null) {
            return null;
        }
        String texto = seleccionado.toString();
        int separador = texto.indexOf(" - ");
        return separador > 0 ? texto.substring(0, separador) : texto;
    }

    private ServicioEstadia crearServicioDesdeSeleccion() {
        String servicio = (String) servicioCombo.getSelectedItem();
        ServicioEstadia base = new ServicioBase();
        if ("Desayuno".equals(servicio)) {
            return new DesayunoDecorator(base);
        }
        if ("Spa".equals(servicio)) {
            return new SpaDecorator(base);
        }
        if ("Lavanderia".equals(servicio)) {
            return new LavanderiaDecorator(base);
        }
        return new CocheraDecorator(base);
    }

    private PoliticaPrecio crearPoliticaDesdeSeleccion() {
        String politica = (String) politicaPrecioCombo.getSelectedItem();
        if ("Temporada alta".equals(politica)) {
            return new PoliticaPrecioTemporadaAlta();
        }
        if ("Promocional".equals(politica)) {
            return new PoliticaPrecioPromocional();
        }
        return new PoliticaPrecioNormal();
    }

    private DescuentoStrategy crearDescuentoDesdeSeleccion() {
        String descuento = (String) descuentoCombo.getSelectedItem();
        if ("Cliente frecuente".equals(descuento)) {
            return new DescuentoClienteFrecuente();
        }
        if ("Promocion especial".equals(descuento)) {
            return new DescuentoPromocionEspecial();
        }
        if ("Temporada baja".equals(descuento)) {
            return new DescuentoTemporadaBaja();
        }
        return total -> total;
    }

    private MetodoPago crearMetodoPagoDesdeSeleccion() {
        String metodo = (String) metodoPagoCombo.getSelectedItem();
        if ("Efectivo".equals(metodo)) {
            return new PagoEfectivo();
        }
        if ("Tarjeta".equals(metodo)) {
            return new PagoTarjeta();
        }
        if ("Transferencia".equals(metodo)) {
            return new PagoTransferencia();
        }
        return new PagoOnlineSimulado();
    }

    private void refrescarTodaLaVista() {
        refrescarTablaHabitaciones();
        refrescarTablaReservas();
        refrescarTablaEstadias();
        refrescarCombos();
        actualizarResumenHotel();
    }

    private void refrescarTablaHabitaciones() {
        habitacionesModel.setRowCount(0);
        for (Habitacion habitacion : hotel.getHabitaciones()) {
            habitacionesModel.addRow(new Object[] {
                    habitacion.getNumero(),
                    habitacion.getCapacidad(),
                    String.format("%.2f", habitacion.getPrecioBase()),
                    habitacion.getTipo(),
                    habitacion.getEstado()
            });
        }
    }

    private void refrescarTablaReservas() {
        reservasModel.setRowCount(0);
        for (Reserva reserva : hotel.getReservas()) {
            reservasModel.addRow(new Object[] {
                    reserva.getCodigo(),
                    reserva.getHuesped().getNombreCompleto(),
                    reserva.getHabitacion().getNumero(),
                    reserva.getFechaIngreso(),
                    reserva.getFechaEgreso(),
                    reserva.getEstado()
            });
        }
    }

    private void refrescarTablaEstadias() {
        estadiasModel.setRowCount(0);
        for (Map.Entry<String, Estadia> entry : estadiasPorReserva.entrySet()) {
            Estadia estadia = entry.getValue();
            estadiasModel.addRow(new Object[] {
                    entry.getKey(),
                    estadia.getReserva().getHuesped().getNombreCompleto(),
                    estadia.calcularNoches(),
                    estadia.getServicios().size(),
                    estadia.getPagos().size(),
                    estadia.getFechaIngresoReal(),
                    estadia.getFechaEgresoReal(),
                    String.format("%.2f", estadia.calcularTotalServicios()),
                    String.format("%.2f", estadia.calcularTotalPagado())
            });
        }
    }

    private void refrescarCombos() {
        DefaultComboBoxModel<String> modeloReservas = new DefaultComboBoxModel<>();
        for (Reserva reserva : hotel.getReservas()) {
            modeloReservas.addElement(reserva.getCodigo() + " - " + reserva.getHuesped().getNombreCompleto() + " - " + reserva.getEstado());
        }
        estadiaReservaCombo.setModel(modeloReservas);
        if (modeloReservas.getSize() > 0 && estadiaReservaCombo.getSelectedIndex() == -1) {
            estadiaReservaCombo.setSelectedIndex(0);
        }
    }

    private void actualizarResumenHotel() {
        resumenHotelLabel.setText(hotel.resumen());
        habitacionesCountLabel.setText("Habitaciones: " + hotel.getHabitaciones().size());
        reservasCountLabel.setText("Reservas: " + hotel.getReservas().size());
        estadiasCountLabel.setText("Estadias registradas: " + estadiasPorReserva.size());
    }

    private void log(String mensaje) {
        logArea.append(mensaje + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        log("Error: " + ex.getMessage());
    }

    private int parseEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser numerico.");
        }
    }

    private double parseDecimal(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser numerico.");
        }
    }

    private LocalDate parseFecha(String texto, String campo) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe tener formato yyyy-MM-dd.");
        }
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }
}
