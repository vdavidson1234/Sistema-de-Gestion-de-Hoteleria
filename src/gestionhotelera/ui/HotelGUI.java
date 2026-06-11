package gestionhotelera.ui;

import gestionhotelera.control.GestorEstadias;
import gestionhotelera.control.GestorHabitaciones;
import gestionhotelera.control.GestorPagos;
import gestionhotelera.control.GestorPersistenciaHotelera;
import gestionhotelera.control.GestorReservas;
import gestionhotelera.control.HotelSnapshot;
import gestionhotelera.control.ReglasCliente;
import gestionhotelera.control.ValidadorDatosPersonales;
import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.EstadoHabitacion;
import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Pago;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.ServicioAdicional;
import gestionhotelera.dominio.ServicioConsumido;
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
import gestionhotelera.strategy.DescuentoConvenioEmpresarial;
import gestionhotelera.strategy.DescuentoPromocionEspecial;
import gestionhotelera.strategy.DescuentoSinDescuento;
import gestionhotelera.strategy.DescuentoStrategy;
import gestionhotelera.strategy.PoliticaPrecio;
import gestionhotelera.strategy.PoliticaPrecioTemporadaAlta;
import gestionhotelera.strategy.PoliticaPrecioTemporadaBaja;
import gestionhotelera.strategy.PoliticaPrecioTemporadaMedia;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Interfaz grafica principal del sistema hotelero.
 */
public class HotelGUI extends JFrame {
    private static final String[] TIPOS_HUESPED = {"Regular", "Cliente frecuente", "Empresario", "Promocional", "Convenio empresarial"};
    private static final String[] METODOS_PAGO = {"Efectivo", "Tarjeta", "Transferencia", "Pago online simulado"};

    private static final ServicioCatalogo[] SERVICIOS = {
            new ServicioCatalogo("Desayuno", "Desayuno continental", 15000.0),
            new ServicioCatalogo("Spa", "Acceso al spa", 22000.0),
            new ServicioCatalogo("Lavanderia", "Lavado y planchado", 12000.0),
            new ServicioCatalogo("Cochera", "Cochera cubierta", 10000.0)
    };

    private final Hotel hotel;
    private final GestorHabitaciones gestorHabitaciones;
    private final GestorReservas gestorReservas;
    private final GestorEstadias gestorEstadias;
    private final GestorPagos gestorPagos;
    private final GestorPersistenciaHotelera gestorPersistencia;
    private final ReglasCliente reglasCliente;
    private final ValidadorDatosPersonales validadorDatosPersonales;
    private final Map<String, Reserva> reservasPorCodigo;
    private final Map<String, Estadia> estadiasPorReserva;

    private final DefaultTableModel habitacionesModel;
    private final DefaultTableModel reservasModel;
    private final DefaultTableModel estadiasModel;
    private final DefaultTableModel historialReservasModel;

    private final JLabel habitacionesCountLabel;
    private final JLabel reservasCountLabel;
    private final JLabel estadiasCountLabel;
    private final JTextArea logArea;

    private final JTextField habitacionNumeroField;
    private final JComboBox<Integer> habitacionCapacidadCombo;
    private final JComboBox<String> habitacionPrecioCombo;
    private final JComboBox<TipoHabitacion> habitacionTipoCombo;
    private final JComboBox<EstadoHabitacion> habitacionEstadoCombo;
    private final JComboBox<String> habitacionFiltroCapacidadCombo;
    private final JComboBox<String> habitacionFiltroTipoCombo;
    private final JComboBox<String> habitacionFiltroEstadoCombo;

    private final JTextField huespedNombreField;
    private final JTextField huespedApellidoField;
    private final JTextField huespedDniField;
    private final JTextField huespedTelefonoField;
    private final JTextField huespedEmailField;
    private final JComboBox<String> huespedTipoCombo;
    private final DefaultListModel<String> reservaHabitacionesModel;
    private final JList<String> reservaHabitacionesList;
    private final JTextField reservaIngresoField;
    private final JComboBox<Integer> reservaNochesCombo;
    private final JTextField reservaEgresoField;
    private final JCheckBox reservaTieneAcompanantesCheck;
    private final JComboBox<String> reservaMetodoSenaCombo;
    private final JLabel reservaOcupantesLabel;
    private final JLabel reservaCapacidadLabel;
    private final JLabel reservaSenaLabel;
    private final List<Huesped> acompanantesReservaPendientes;

    private final JComboBox<String> estadiaReservaCombo;
    private final JComboBox<String> servicioEstadiaCombo;
    private final JComboBox<String> pagoEstadiaCombo;
    private final JTextField estadiaIngresoField;
    private final JTextField estadiaEgresoField;
    private final JComboBox<String> servicioCombo;
    private final JComboBox<Integer> servicioCantidadCombo;
    private final JLabel servicioPrecioLabel;
    private final JComboBox<String> politicaPrecioCombo;
    private final JComboBox<String> descuentoCombo;
    private final JTextField montoPagoField;
    private final JComboBox<String> metodoPagoCombo;
    private final JLabel totalCalculadoLabel;
    private final JLabel saldoEstimadoLabel;
    private final JLabel totalPagadoLabel;
    private final JLabel politicaPrecioLabel;
    private final JLabel descuentoPorcentajeLabel;
    private final JTextArea resumenPagoArea;

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
        this.reglasCliente = new ReglasCliente();
        this.validadorDatosPersonales = new ValidadorDatosPersonales();
        this.reservasPorCodigo = new LinkedHashMap<>();
        for (Reserva reserva : hotel.getReservas()) {
            this.reservasPorCodigo.put(reserva.getCodigo(), reserva);
        }
        this.estadiasPorReserva = snapshot.getEstadiasPorReserva();

        setTitle("Sistema de Gestion Hotelera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 880));
        setLocationRelativeTo(null);

        habitacionesModel = new DefaultTableModel(new Object[] {"Numero", "Capacidad", "Precio base", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservasModel = new DefaultTableModel(new Object[] {
                "Codigo", "Grupo", "Titular", "Habitacion", "Ocupantes", "Ingreso", "Noches", "Egreso",
                "Total habitación", "Seña pagada", "Estado"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        estadiasModel = new DefaultTableModel(new Object[] {
                "Reserva", "Habitación", "Huésped", "Ocupantes", "Noches reales", "Ingreso real", "Egreso real",
                "Servicios", "Seña", "Pagos extra", "Total abonado", "Estado"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historialReservasModel = new DefaultTableModel(new Object[] {
                "Codigo", "Grupo", "Habitacion", "Titular", "Ingreso", "Egreso", "Noches", "Ocupantes",
                "Estado", "Seña", "Estadía", "Política", "Descuento", "Pagos registrados", "Total abonado"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        habitacionesCountLabel = new JLabel();
        reservasCountLabel = new JLabel();
        estadiasCountLabel = new JLabel();
        logArea = new JTextArea(6, 80);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        habitacionNumeroField = new JTextField(10);
        habitacionCapacidadCombo = new JComboBox<>(capacidadesHabitacion());
        habitacionPrecioCombo = new JComboBox<>(preciosHabitacion());
        habitacionCapacidadCombo.setEnabled(false);
        habitacionPrecioCombo.setEnabled(false);
        habitacionTipoCombo = new JComboBox<>(TipoHabitacion.values());
        habitacionEstadoCombo = new JComboBox<>(EstadoHabitacion.values());
        habitacionFiltroCapacidadCombo = new JComboBox<>(filtroCapacidadesHabitacion());
        habitacionFiltroTipoCombo = new JComboBox<>(filtroTiposHabitacion());
        habitacionFiltroEstadoCombo = new JComboBox<>(filtroEstadosHabitacion());

        huespedNombreField = new JTextField(12);
        huespedApellidoField = new JTextField(12);
        huespedDniField = new JTextField(12);
        huespedTelefonoField = new JTextField(12);
        huespedEmailField = new JTextField(12);
        huespedTipoCombo = new JComboBox<>(TIPOS_HUESPED);
        reservaHabitacionesModel = new DefaultListModel<>();
        reservaHabitacionesList = new JList<>(reservaHabitacionesModel);
        reservaHabitacionesList.setVisibleRowCount(8);
        reservaHabitacionesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        reservaIngresoField = new JTextField(10);
        reservaNochesCombo = new JComboBox<>(numeros(1, 30));
        reservaEgresoField = new JTextField(10);
        reservaEgresoField.setEditable(false);
        reservaTieneAcompanantesCheck = new JCheckBox("Tiene acompañantes");
        reservaMetodoSenaCombo = new JComboBox<>(METODOS_PAGO);
        reservaOcupantesLabel = new JLabel("Ocupantes cargados: 1 titular");
        reservaCapacidadLabel = new JLabel("Capacidad seleccionada: 0 / Ocupantes: 1");
        reservaSenaLabel = new JLabel("Seña requerida: -");
        acompanantesReservaPendientes = new ArrayList<>();

        estadiaReservaCombo = new JComboBox<>();
        servicioEstadiaCombo = new JComboBox<>();
        pagoEstadiaCombo = new JComboBox<>();
        estadiaIngresoField = new JTextField(10);
        estadiaEgresoField = new JTextField(10);
        servicioCombo = new JComboBox<>(servicioLabels());
        servicioCantidadCombo = new JComboBox<>(numeros(1, 16));
        servicioPrecioLabel = new JLabel();
        politicaPrecioCombo = new JComboBox<>(new String[] {"Temporada media (0%)", "Temporada alta (+20%)", "Temporada baja (-15%)"});
        descuentoCombo = new JComboBox<>(new String[] {
                "Ninguno (0%)", "Cliente frecuente (10%)", "Promoción especial (20%)", "Convenio empresarial (12%)"
        });
        montoPagoField = new JTextField(10);
        metodoPagoCombo = new JComboBox<>(METODOS_PAGO);
        totalCalculadoLabel = new JLabel("Total calculado: -");
        saldoEstimadoLabel = new JLabel("Saldo estimado: -");
        totalPagadoLabel = new JLabel("Total abonado: -");
        politicaPrecioLabel = new JLabel("Política tarifaria: Temporada media 0%");
        descuentoPorcentajeLabel = new JLabel("Descuento aplicado: 0%");
        resumenPagoArea = new JTextArea(12, 34);
        resumenPagoArea.setEditable(false);
        resumenPagoArea.setLineWrap(true);
        resumenPagoArea.setWrapStyleWord(true);

        servicioCombo.addActionListener(e -> actualizarPrecioServicio());
        servicioEstadiaCombo.addActionListener(e -> actualizarPrecioServicio());
        descuentoCombo.addActionListener(e -> actualizarEtiquetaDescuento());
        politicaPrecioCombo.addActionListener(e -> actualizarEtiquetaPoliticaPrecio());
        estadiaReservaCombo.addActionListener(e -> autocompletarFechasEstadia());
        habitacionTipoCombo.addActionListener(e -> sugerirPrecioPorTipo());
        habitacionFiltroCapacidadCombo.addActionListener(e -> refrescarTablaHabitaciones());
        habitacionFiltroTipoCombo.addActionListener(e -> refrescarTablaHabitaciones());
        habitacionFiltroEstadoCombo.addActionListener(e -> refrescarTablaHabitaciones());
        reservaNochesCombo.addActionListener(e -> actualizarFechasYHabitacionesReserva());
        reservaTieneAcompanantesCheck.addActionListener(e -> manejarToggleAcompanantes());
        reservaHabitacionesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarResumenCapacidadReserva();
            }
        });
        reservaIngresoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarFechasYHabitacionesReserva();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarFechasYHabitacionesReserva();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarFechasYHabitacionesReserva();
            }
        });
        actualizarPrecioServicio();
        actualizarEtiquetaDescuento();
        actualizarEtiquetaPoliticaPrecio();
        sugerirPrecioPorTipo();

        setLayout(new BorderLayout(12, 12));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenidoPrincipal(), BorderLayout.CENTER);
        add(crearPanelLog(), BorderLayout.SOUTH);

        log(this.gestorPersistencia.getUltimoMensaje());
        cargarDatosIniciales();
        persistirSnapshot();
        refrescarTodaLaVista();
    }

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
        panel.add(resumenPanel, BorderLayout.EAST);
        return panel;
    }

    private JComponent crearContenidoPrincipal() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Habitaciones", crearTabHabitaciones());
        tabs.addTab("Reservas", crearTabReservas());
        tabs.addTab("Check-in / Estadías", crearTabEstadias());
        tabs.addTab("Servicios", crearTabServicios());
        tabs.addTab("Check-out / pagos", crearTabPagos());
        tabs.addTab("Histórico de reservas", crearTabHistoricoReservas());
        return tabs;
    }

    private JComponent crearTabHabitaciones() {
        return crearPanelConFormularioYTabla(crearFormularioHabitacion(), crearTablaHabitaciones());
    }

    private JComponent crearTabReservas() {
        return crearPanelConFormularioYTabla(crearFormularioReserva(), crearTablaReservas());
    }

    private JComponent crearTabEstadias() {
        return crearPanelConFormularioYTabla(crearFormularioCheckIn(), crearTablaEstadias());
    }

    private JComponent crearTabServicios() {
        return crearPanelConFormularioYTabla(crearFormularioServicios(), crearTablaEstadias());
    }

    private JComponent crearTabPagos() {
        return crearPanelConFormularioYTabla(crearFormularioPagosYCierre(), crearTablaEstadias());
    }

    private JComponent crearTabHistoricoReservas() {
        JTable tabla = new JTable(historialReservasModel);
        tabla.setFillsViewportHeight(true);
        agregarAperturaDetalleReserva(tabla, historialReservasModel, 0);
        return wrapConTitulo("Seguimiento histórico de reservas", tabla);
    }

    private JComponent crearPanelConFormularioYTabla(JComponent formulario, JComponent tabla) {
        JPanel contenedor = new JPanel(new BorderLayout(12, 12));
        contenedor.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane formularioScroll = new JScrollPane(formulario);
        formularioScroll.setBorder(BorderFactory.createEmptyBorder());
        formularioScroll.setPreferredSize(new Dimension(520, 640));
        formularioScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formularioScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        formularioScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formularioScroll, tabla);
        splitPane.setResizeWeight(0.34);
        splitPane.setDividerLocation(540);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contenedor.add(splitPane, BorderLayout.CENTER);
        return contenedor;
    }

    private JComponent crearTablaHabitaciones() {
        JTable tabla = new JTable(habitacionesModel);
        tabla.setFillsViewportHeight(true);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof TipoHabitacion) {
                    setText(((TipoHabitacion) value).getNombreVisible());
                    return;
                }
                super.setValue(value);
            }
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarHabitacionSeleccionada(tabla);
            }
        });
        return wrapConTitulo("Habitaciones registradas", tabla);
    }

    private JComponent crearTablaReservas() {
        JTable tabla = new JTable(reservasModel);
        tabla.setFillsViewportHeight(true);
        agregarAperturaDetalleReserva(tabla, reservasModel, 0);
        return wrapConTitulo("Reservas y ocupantes por habitación", tabla);
    }

    private JComponent crearTablaEstadias() {
        JTable tabla = new JTable(estadiasModel);
        tabla.setFillsViewportHeight(true);
        agregarAperturaDetalleReserva(tabla, estadiasModel, 0);
        return wrapConTitulo("Estadías, servicios y pagos", tabla);
    }

    private JPanel crearFormularioHabitacion() {
        JPanel panel = crearFormularioBase("Alta / estado de habitaciones");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Numero", habitacionNumeroField);
        fila = agregarCampo(panel, fila, "Capacidad", habitacionCapacidadCombo);
        fila = agregarCampo(panel, fila, "Precio base", habitacionPrecioCombo);
        fila = agregarCampo(panel, fila, "Tipo", habitacionTipoCombo);
        fila = agregarCampo(panel, fila, "Estado", habitacionEstadoCombo);
        fila = agregarComponenteCompleto(panel, fila, new JLabel("Búsqueda de habitaciones"));
        fila = agregarCampo(panel, fila, "Capacidad mínima", habitacionFiltroCapacidadCombo);
        fila = agregarCampo(panel, fila, "Tipo buscado", habitacionFiltroTipoCombo);
        fila = agregarCampo(panel, fila, "Disponibilidad/estado", habitacionFiltroEstadoCombo);

        JButton crearButton = new JButton("Registrar habitación");
        crearButton.addActionListener(e -> registrarHabitacion());

        JButton estadoButton = new JButton("Aplicar estado");
        estadoButton.addActionListener(e -> cambiarEstadoHabitacion());

        JButton eliminarButton = new JButton("Eliminar habitación");
        eliminarButton.addActionListener(e -> eliminarHabitacion());

        JButton limpiarButton = new JButton("Limpiar seleccion");
        limpiarButton.addActionListener(e -> limpiarFormularioHabitacion());

        JButton limpiarFiltrosButton = new JButton("Limpiar filtros");
        limpiarFiltrosButton.addActionListener(e -> limpiarFiltrosHabitaciones());

        agregarBotonera(panel, fila, crearButton, estadoButton, eliminarButton, limpiarButton, limpiarFiltrosButton);
        return panel;
    }

    private JPanel crearFormularioReserva() {
        JPanel panel = crearFormularioBase("Nueva reserva grupal");
        int fila = 0;
        JButton buscarButton = new JButton("Buscar DNI");
        buscarButton.addActionListener(e -> buscarHuespedTitular());
        fila = agregarCampo(panel, fila, "DNI titular", crearCampoConBoton(huespedDniField, buscarButton));

        fila = agregarCampo(panel, fila, "Nombre", huespedNombreField);
        fila = agregarCampo(panel, fila, "Apellido", huespedApellidoField);
        fila = agregarCampo(panel, fila, "Telefono", huespedTelefonoField);
        fila = agregarCampo(panel, fila, "Email", huespedEmailField);
        fila = agregarCampo(panel, fila, "Tipo de huesped", huespedTipoCombo);
        fila = agregarCampo(panel, fila, "Habitaciones", new JScrollPane(reservaHabitacionesList));
        fila = agregarCampo(panel, fila, "Ingreso (yyyy-MM-dd)", reservaIngresoField);
        fila = agregarCampo(panel, fila, "Noches", reservaNochesCombo);
        fila = agregarCampo(panel, fila, "Egreso calculado", reservaEgresoField);
        fila = agregarCampo(panel, fila, "Acompañantes", reservaTieneAcompanantesCheck);
        fila = agregarComponenteCompleto(panel, fila, reservaOcupantesLabel);
        fila = agregarComponenteCompleto(panel, fila, reservaCapacidadLabel);
        fila = agregarCampo(panel, fila, "Método seña", reservaMetodoSenaCombo);
        fila = agregarComponenteCompleto(panel, fila, reservaSenaLabel);

        JButton reservarButton = new JButton("Crear y confirmar con seña");
        reservarButton.addActionListener(e -> crearYConfirmarReserva());

        JButton cancelarButton = new JButton("Cancelar seleccionada");
        cancelarButton.addActionListener(e -> cancelarReservaSeleccionada());

        JButton extenderButton = new JButton("Extender reserva");
        extenderButton.addActionListener(e -> extenderReservaSeleccionada());

        agregarBotonera(panel, fila, reservarButton, cancelarButton, extenderButton);
        return panel;
    }

    private JPanel crearFormularioCheckIn() {
        JPanel panel = crearFormularioBase("Iniciar estadía");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Reserva", estadiaReservaCombo);
        fila = agregarCampo(panel, fila, "Ingreso real", estadiaIngresoField);
        fila = agregarCampo(panel, fila, "Egreso real", estadiaEgresoField);

        JButton fechasReservaButton = new JButton("Usar fechas de reserva");
        fechasReservaButton.addActionListener(e -> autocompletarFechasEstadia());

        JButton registrarEstadiaButton = new JButton("Iniciar estadía");
        registrarEstadiaButton.addActionListener(e -> registrarEstadiaSeleccionada());

        agregarBotonera(panel, fila, fechasReservaButton, registrarEstadiaButton);
        return panel;
    }

    private JPanel crearFormularioServicios() {
        JPanel panel = crearFormularioBase("Servicios de la estadía");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Estadía", servicioEstadiaCombo);
        fila = agregarCampo(panel, fila, "Servicio", servicioCombo);
        fila = agregarCampo(panel, fila, "Cantidad", servicioCantidadCombo);
        fila = agregarComponenteCompleto(panel, fila, servicioPrecioLabel);

        JButton agregarServicioButton = new JButton("Agregar servicio a estadía");
        agregarServicioButton.addActionListener(e -> agregarServicioSeleccionado());

        JButton cantidadOcupantesButton = new JButton("Cantidad = ocupantes");
        cantidadOcupantesButton.addActionListener(e -> usarCantidadOcupantes());

        agregarBotonera(panel, fila, agregarServicioButton, cantidadOcupantesButton);
        return panel;
    }

    private JPanel crearFormularioPagosYCierre() {
        JPanel panel = crearFormularioBase("Check-out, pagos y liberación");
        int fila = 0;
        fila = agregarCampo(panel, fila, "Estadía", pagoEstadiaCombo);
        fila = agregarCampo(panel, fila, "Política tarifaria", politicaPrecioCombo);
        fila = agregarComponenteCompleto(panel, fila, politicaPrecioLabel);
        fila = agregarCampo(panel, fila, "Descuento", descuentoCombo);
        fila = agregarComponenteCompleto(panel, fila, descuentoPorcentajeLabel);

        JPanel resultado = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        resultado.add(totalCalculadoLabel, gbc);
        gbc.gridy = 1;
        resultado.add(totalPagadoLabel, gbc);
        gbc.gridy = 2;
        resultado.add(saldoEstimadoLabel, gbc);
        fila = agregarComponenteCompleto(panel, fila, resultado);
        fila = agregarComponenteCompleto(panel, fila, new JScrollPane(resumenPagoArea));

        fila = agregarCampo(panel, fila, "Monto pago", montoPagoField);
        fila = agregarCampo(panel, fila, "Metodo de pago", metodoPagoCombo);

        JButton calcularButton = new JButton("Calcular saldo");
        calcularButton.addActionListener(e -> calcularTotalSeleccionado());

        JButton pagarButton = new JButton("Registrar pago");
        pagarButton.addActionListener(e -> registrarPagoSeleccionado());

        JButton cerrarButton = new JButton("Finalizar pago y liberar habitación");
        cerrarButton.addActionListener(e -> cerrarEstadiaSeleccionada());

        agregarBotonera(panel, fila, calcularButton, pagarButton, cerrarButton);
        return panel;
    }

    private JPanel crearFormularioBase(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMinimumSize(new Dimension(420, 0));
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

    private int agregarComponenteCompleto(JPanel panel, int fila, JComponent componente) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 6, 4, 6);
        panel.add(componente, gbc);
        return fila + 1;
    }

    private void agregarBotonera(JPanel panel, int fila, JButton... botones) {
        JPanel botonera = new JPanel(new GridLayout(0, 1, 0, 6));
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
        scrollPane.setPreferredSize(new Dimension(880, 240));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void agregarAperturaDetalleReserva(JTable tabla, DefaultTableModel modelo, int columnaCodigo) {
        tabla.setToolTipText("Doble clic sobre una fila para ver el detalle completo de la reserva");
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() < 2) {
                    return;
                }
                int filaVista = tabla.rowAtPoint(event.getPoint());
                if (filaVista < 0) {
                    return;
                }
                tabla.setRowSelectionInterval(filaVista, filaVista);
                mostrarDetalleReservaDesdeTabla(tabla, modelo, columnaCodigo);
            }
        });
    }

    private void mostrarDetalleReservaDesdeTabla(JTable tabla, DefaultTableModel modelo, int columnaCodigo) {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        String codigo = modelo.getValueAt(filaModelo, columnaCodigo).toString();
        Reserva reserva = reservasPorCodigo.get(codigo);
        if (reserva == null) {
            mostrarError(new IllegalStateException("No se encontró la reserva " + codigo + " en memoria."));
            return;
        }

        JTextArea detalleArea = new JTextArea(construirDetalleReserva(reserva), 28, 78);
        detalleArea.setEditable(false);
        detalleArea.setLineWrap(true);
        detalleArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(detalleArea);
        scroll.setPreferredSize(new Dimension(820, 560));
        JOptionPane.showMessageDialog(this, scroll, "Detalle de reserva " + reserva.getCodigo(),
                JOptionPane.INFORMATION_MESSAGE);
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
        if (!hotel.getTodasLasHabitaciones().isEmpty()) {
            log("Se cargó historial existente del hotel. No hay habitaciones activas en catálogo.");
            return;
        }

        gestorHabitaciones.crearYRegistrarHabitacion(101, TipoHabitacion.SIMPLE.getCapacidadEstandar(),
                TipoHabitacion.SIMPLE.getPrecioBase(), TipoHabitacion.SIMPLE);
        gestorHabitaciones.crearYRegistrarHabitacion(202, TipoHabitacion.DOBLE.getCapacidadEstandar(),
                TipoHabitacion.DOBLE.getPrecioBase(), TipoHabitacion.DOBLE);
        gestorHabitaciones.crearYRegistrarHabitacion(303, TipoHabitacion.SUITE.getCapacidadEstandar(),
                TipoHabitacion.SUITE.getPrecioBase(), TipoHabitacion.SUITE);
        gestorHabitaciones.crearYRegistrarHabitacion(404, TipoHabitacion.PENTHOUSE.getCapacidadEstandar(),
                TipoHabitacion.PENTHOUSE.getPrecioBase(), TipoHabitacion.PENTHOUSE);

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
            int numero = parseEntero(habitacionNumeroField.getText(), "número de habitación");
            TipoHabitacion tipo = (TipoHabitacion) habitacionTipoCombo.getSelectedItem();
            int capacidad = tipo.getCapacidadEstandar();
            double precioBase = tipo.getPrecioBase();

            if (numero <= 0) {
                throw new IllegalArgumentException("El número de habitación debe ser mayor a cero.");
            }
            if (precioBase <= 0) {
                throw new IllegalArgumentException("El precio base debe ser mayor a cero.");
            }
            Habitacion existente = hotel.buscarHabitacion(numero);
            if (existente != null && !existente.estaActiva() && existente.getTipo() != tipo) {
                throw new IllegalStateException("La habitación " + numero + " existe dada de baja como "
                        + existente.getTipo().getNombreVisible()
                        + ". Para reactivarla debe conservar el mismo tipo.");
            }
            gestorHabitaciones.crearYRegistrarHabitacion(numero, capacidad, precioBase, tipo);
            if (existente != null && !existente.estaActiva()) {
                log("Habitación " + numero + " reactivada en el catálogo operativo.");
            } else {
                log("Habitación " + numero + " registrada correctamente.");
            }
            limpiarFormularioHabitacion();
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cambiarEstadoHabitacion() {
        try {
            int numero = parseEntero(habitacionNumeroField.getText(), "número de habitación");
            Habitacion habitacion = hotel.buscarHabitacion(numero);
            if (habitacion == null) {
                throw new IllegalStateException("No se encontró la habitación indicada.");
            }

            EstadoHabitacion estado = (EstadoHabitacion) habitacionEstadoCombo.getSelectedItem();
            gestorHabitaciones.cambiarEstado(numero, estado);
            log("La habitación " + numero + " pasó a estado " + estado + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarHabitacion() {
        try {
            int numero = parseEntero(habitacionNumeroField.getText(), "número de habitación");
            Habitacion habitacion = hotel.buscarHabitacion(numero);
            if (habitacion == null) {
                throw new IllegalStateException("No se encontró la habitación indicada.");
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "Eliminar la habitación " + numero + "? Si tiene solo reservas finalizadas o canceladas, quedará dada de baja y se conservará el histórico.",
                    "Eliminar habitación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            gestorHabitaciones.eliminarHabitacion(numero);
            log("Habitación " + numero + " eliminada del catálogo operativo.");
            limpiarFormularioHabitacion();
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void crearYConfirmarReserva() {
        try {
            Huesped titular = resolverTitularDesdeCampos();
            List<Integer> habitaciones = habitacionesSeleccionadas();
            LocalDate ingreso = parseFecha(reservaIngresoField.getText(), "fecha de ingreso");
            int noches = (Integer) reservaNochesCombo.getSelectedItem();
            int personas = totalOcupantesReserva();

            if (noches <= 0) {
                throw new IllegalArgumentException("La cantidad de noches debe ser mayor a cero.");
            }
            if (reservaTieneAcompanantesCheck.isSelected() && acompanantesReservaPendientes.isEmpty()) {
                throw new IllegalArgumentException("Marcaste acompañantes, pero no cargaste ningún acompañante.");
            }
            if (personas < habitaciones.size()) {
                throw new IllegalArgumentException("Debe haber al menos un ocupante por habitación reservada.");
            }
            int capacidadSeleccionada = capacidadTotalHabitaciones(habitaciones);
            if (personas > capacidadSeleccionada) {
                throw new IllegalArgumentException("La cantidad de ocupantes supera la capacidad total de las habitaciones seleccionadas.");
            }

            reservaEgresoField.setText(ingreso.plusDays(noches).toString());
            Map<Integer, List<Huesped>> ocupantesPorHabitacion = solicitarOcupantes(titular, habitaciones);
            double senaRequerida = calcularSenaRequerida(habitaciones, noches);
            reservaSenaLabel.setText(String.format("Seña requerida: $ %.2f", senaRequerida));
            SenaConfirmacion sena = solicitarSena(senaRequerida);
            if (sena == null) {
                return;
            }

            List<Reserva> reservas = gestorReservas.crearReservasGrupo(titular, ocupantesPorHabitacion, ingreso, noches);
            gestorReservas.confirmarReservasConSena(reservas, sena.monto, sena.metodo);
            for (Reserva reserva : reservas) {
                reservasPorCodigo.put(reserva.getCodigo(), reserva);
            }

            log("Reserva grupal confirmada: " + reservas.get(0).getGrupoCodigo() + " | habitaciones: " + reservas.size()
                    + " | seña: $ " + String.format("%.2f", sena.monto));
            limpiarCampos(huespedNombreField, huespedApellidoField, huespedDniField, huespedTelefonoField, huespedEmailField,
                    reservaIngresoField, reservaEgresoField);
            huespedTipoCombo.setSelectedIndex(0);
            reservaHabitacionesList.clearSelection();
            reservaNochesCombo.setSelectedIndex(0);
            reservaTieneAcompanantesCheck.setSelected(false);
            acompanantesReservaPendientes.clear();
            actualizarResumenAcompanantes();
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void cancelarReservaSeleccionada() {
        try {
            String codigo = solicitarCodigoReserva("Cancelar reserva", false);
            if (codigo == null) {
                return;
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

    private void extenderReservaSeleccionada() {
        try {
            String codigo = solicitarCodigoReserva("Extender reserva", true);
            if (codigo == null) {
                return;
            }

            Reserva reserva = reservasPorCodigo.get(codigo);
            if (reserva == null) {
                throw new IllegalStateException("La reserva seleccionada no existe.");
            }

            JTextField nuevaFechaField = new JTextField(reserva.getFechaEgreso().plusDays(1).toString(), 12);
            JPanel panel = new JPanel(new GridBagLayout());
            int fila = 0;
            fila = agregarCampo(panel, fila, "Reserva", new JLabel(textoReservaCorto(reserva)));
            fila = agregarCampo(panel, fila, "Egreso actual", new JLabel(reserva.getFechaEgreso().toString()));
            agregarCampo(panel, fila, "Nuevo egreso", nuevaFechaField);

            int resultado = JOptionPane.showConfirmDialog(this, panel, "Extender reserva", JOptionPane.OK_CANCEL_OPTION);
            if (resultado != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate nuevaFecha = parseFecha(nuevaFechaField.getText(), "nuevo egreso");
            gestorReservas.extenderReserva(reserva, nuevaFecha);
            log("Reserva " + codigo + " extendida hasta " + nuevaFecha + ".");
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
                throw new IllegalStateException("No hay reservas disponibles para registrar una estadía.");
            }

            Reserva reserva = reservasPorCodigo.get(codigo);
            if (reserva == null) {
                throw new IllegalStateException("La reserva seleccionada no existe.");
            }
            if (estadiasPorReserva.containsKey(codigo)) {
                throw new IllegalStateException("La estadía de esta reserva ya fue iniciada.");
            }

            LocalDate ingreso = parseFecha(estadiaIngresoField.getText(), "ingreso real");
            LocalDate egreso = parseFecha(estadiaEgresoField.getText(), "egreso real");
            StaySummary resumen = gestorEstadias.registrarEstadia(reserva, ingreso, egreso);
            estadiasPorReserva.put(codigo, resumen.getEstadia());
            montoPagoField.setText(montoPagoField.getText().isBlank() ? "0" : montoPagoField.getText());

            log("Estadía registrada para la reserva " + codigo + ".");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (RuntimeException ex) {
            mostrarError(ex);
        }
    }

    private void agregarServicioSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaDesdeCombo(servicioEstadiaCombo);
            if (estadia.getReserva().getEstado() != gestionhotelera.dominio.EstadoReserva.CONFIRMADA) {
                throw new IllegalStateException("No se pueden agregar servicios a una estadía cerrada.");
            }
            int cantidad = (Integer) servicioCantidadCombo.getSelectedItem();
            ServicioEstadia servicio = crearServicioDesdeSeleccion(estadia, cantidad);
            gestorEstadias.agregarServicio(estadia, servicio);
            log("Servicio agregado: " + servicio.getNombre() + " x" + servicio.getCantidad()
                    + " | total $ " + String.format("%.2f", servicio.getPrecio()));
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void usarCantidadOcupantes() {
        try {
            Estadia estadia = obtenerEstadiaDesdeCombo(servicioEstadiaCombo);
            seleccionarComboInteger(servicioCantidadCombo, estadia.getReserva().getCantidadPersonas());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void calcularTotalSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaDesdeCombo(pagoEstadiaCombo);
            PoliticaPrecio politicaPrecio = crearPoliticaDesdeSeleccion();
            DescuentoStrategy descuento = crearDescuentoDesdeSeleccion();
            validarDescuentoAplicable(estadia, descuento);
            gestorEstadias.aplicarCondicionesComerciales(estadia, politicaPrecio, descuento);
            double total = gestorEstadias.calcularCostoTotal(estadia, politicaPrecio, descuento);
            double saldo = estadia.calcularSaldoPendiente(total);

            totalCalculadoLabel.setText(String.format("Total calculado: $ %.2f", total));
            totalPagadoLabel.setText(String.format("Total abonado: $ %.2f (seña incluida)", estadia.calcularTotalPagado()));
            saldoEstimadoLabel.setText(String.format("Saldo estimado: $ %.2f", saldo));
            descuentoPorcentajeLabel.setText(String.format("Descuento aplicado: %s %.0f%%%s",
                    descuento.getNombre(), descuento.getPorcentaje(), requisitoDescuento(descuento)));
            resumenPagoArea.setText(construirResumenPago(estadia, politicaPrecio, descuento));
            montoPagoField.setText(String.format("%.2f", saldo));
            log("Total calculado para la estadía " + estadia.getReserva().getCodigo() + ": "
                    + String.format("$ %.2f", total) + " | descuento " + String.format("%.0f%%", descuento.getPorcentaje()));
            persistirSnapshot();
            refrescarTablaEstadias();
            refrescarHistorialReservas();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void registrarPagoSeleccionado() {
        try {
            Estadia estadia = obtenerEstadiaDesdeCombo(pagoEstadiaCombo);
            if (estadia.getReserva().getEstado() != gestionhotelera.dominio.EstadoReserva.CONFIRMADA) {
                throw new IllegalStateException("No se pueden registrar pagos en una estadía cerrada.");
            }
            PoliticaPrecio politicaPrecio = crearPoliticaDesdeSeleccion();
            DescuentoStrategy descuento = crearDescuentoDesdeSeleccion();
            validarDescuentoAplicable(estadia, descuento);
            gestorEstadias.aplicarCondicionesComerciales(estadia, politicaPrecio, descuento);
            double monto = parseDecimal(montoPagoField.getText(), "monto del pago");
            if (monto <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
            }
            MetodoPago metodoPago = crearMetodoPagoDesdeSeleccion();
            gestorPagos.registrarPago(estadia, monto, metodoPago);
            log("Pago registrado por " + metodoPago.getNombre() + ": $ " + String.format("%.2f", monto));
            persistirSnapshot();
            refrescarTodaLaVista();
            calcularTotalSeleccionado();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cerrarEstadiaSeleccionada() {
        try {
            Estadia estadia = obtenerEstadiaDesdeCombo(pagoEstadiaCombo);
            PoliticaPrecio politicaPrecio = crearPoliticaDesdeSeleccion();
            DescuentoStrategy descuento = crearDescuentoDesdeSeleccion();
            validarDescuentoAplicable(estadia, descuento);
            gestorEstadias.aplicarCondicionesComerciales(estadia, politicaPrecio, descuento);
            double total = gestorEstadias.calcularCostoTotal(estadia, politicaPrecio, descuento);
            double saldo = estadia.calcularSaldoPendiente(total);
            if (saldo > 0.01) {
                throw new IllegalStateException("No se puede cerrar la estadía con saldo pendiente.");
            }

            gestorEstadias.finalizarEstadia(estadia);
            log("Estadía cerrada para la reserva " + estadia.getReserva().getCodigo()
                    + ". Habitación " + estadia.getReserva().getHabitacion().getNumero() + " disponible.");
            persistirSnapshot();
            refrescarTodaLaVista();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private Huesped resolverTitularDesdeCampos() {
        String dni = huespedDniField.getText().trim();
        if (dni.isBlank()) {
            throw new IllegalArgumentException("El DNI del titular es obligatorio.");
        }
        Huesped existente = hotel.buscarHuespedPorDni(dni);
        if (existente != null) {
            return existente;
        }

        String nombre = huespedNombreField.getText().trim();
        String apellido = huespedApellidoField.getText().trim();
        validadorDatosPersonales.validarHuesped(nombre, apellido, dni,
                huespedTelefonoField.getText().trim(), huespedEmailField.getText().trim());
        Huesped nuevo = new Huesped(nombre, apellido, dni, huespedTelefonoField.getText().trim(),
                huespedEmailField.getText().trim(), huespedTipoCombo.getSelectedItem().toString());
        hotel.registrarHuesped(nuevo);
        return nuevo;
    }

    private void buscarHuespedTitular() {
        String dni = huespedDniField.getText().trim();
        try {
            validadorDatosPersonales.validarDni(dni);
        } catch (RuntimeException ex) {
            mostrarError(ex);
            return;
        }
        Huesped huesped = hotel.buscarHuespedPorDni(dni);
        if (huesped == null) {
            log("No se encontro huesped registrado para DNI " + dni + ".");
            return;
        }
        completarCamposHuesped(huesped);
        log("Huesped registrado cargado: " + huesped.getNombreCompleto() + ".");
    }

    private void completarCamposHuesped(Huesped huesped) {
        huespedNombreField.setText(huesped.getNombre());
        huespedApellidoField.setText(huesped.getApellido());
        huespedTelefonoField.setText(huesped.getTelefono());
        huespedEmailField.setText(huesped.getEmail());
        seleccionarComboString(huespedTipoCombo, huesped.getTipoHuesped());
    }

    private void manejarToggleAcompanantes() {
        if (!reservaTieneAcompanantesCheck.isSelected()) {
            acompanantesReservaPendientes.clear();
            actualizarResumenAcompanantes();
            refrescarOpcionesHabitacionesReserva();
            return;
        }

        try {
            solicitarAcompanantesReserva();
            actualizarResumenAcompanantes();
            refrescarOpcionesHabitacionesReserva();
        } catch (RuntimeException ex) {
            acompanantesReservaPendientes.clear();
            reservaTieneAcompanantesCheck.setSelected(false);
            actualizarResumenAcompanantes();
            mostrarError(ex);
        }
    }

    private void solicitarAcompanantesReserva() {
        JComboBox<Integer> cantidadCombo = new JComboBox<>(numeros(1, 15));
        if (!acompanantesReservaPendientes.isEmpty()) {
            seleccionarComboInteger(cantidadCombo, acompanantesReservaPendientes.size());
        }

        int cantidadResultado = JOptionPane.showConfirmDialog(this, cantidadCombo,
                "Cantidad de acompañantes", JOptionPane.OK_CANCEL_OPTION);
        if (cantidadResultado != JOptionPane.OK_OPTION) {
            throw new IllegalStateException("Se canceló la carga de acompañantes.");
        }

        int cantidad = (Integer) cantidadCombo.getSelectedItem();
        DefaultTableModel model = new DefaultTableModel(new Object[] {
                "DNI", "Nombre", "Apellido", "Telefono", "Email", "Tipo"
        }, cantidad);
        for (int i = 0; i < Math.min(cantidad, acompanantesReservaPendientes.size()); i++) {
            Huesped acompanante = acompanantesReservaPendientes.get(i);
            model.setValueAt(acompanante.getDni(), i, 0);
            model.setValueAt(acompanante.getNombre(), i, 1);
            model.setValueAt(acompanante.getApellido(), i, 2);
            model.setValueAt(acompanante.getTelefono(), i, 3);
            model.setValueAt(acompanante.getEmail(), i, 4);
            model.setValueAt(acompanante.getTipoHuesped(), i, 5);
        }
        for (int i = 0; i < cantidad; i++) {
            if (valorTabla(model, i, 5).isBlank()) {
                model.setValueAt(TIPOS_HUESPED[0], i, 5);
            }
        }

        JTable tabla = new JTable(model);
        TableColumn tipoColumn = tabla.getColumnModel().getColumn(5);
        tipoColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(TIPOS_HUESPED)));

        JButton completarButton = new JButton("Completar por DNI registrado");
        completarButton.addActionListener(e -> completarOcupantesRegistrados(model));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Registrar acompañantes de la reserva"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(completarButton, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(850, 320));

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Acompañantes", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) {
            throw new IllegalStateException("Se canceló la carga de acompañantes.");
        }

        List<Huesped> nuevosAcompanantes = new ArrayList<>();
        Set<String> dnis = new HashSet<>();
        String dniTitular = huespedDniField.getText().trim();
        for (int fila = 0; fila < model.getRowCount(); fila++) {
            Huesped acompanante = huespedDesdeFila(model, fila, null);
            if (acompanante.getDni().equalsIgnoreCase(dniTitular)) {
                throw new IllegalArgumentException("Un acompañante no puede tener el mismo DNI que el titular.");
            }
            if (!dnis.add(acompanante.getDni())) {
                throw new IllegalArgumentException("Hay acompañantes con DNI duplicado.");
            }
            nuevosAcompanantes.add(acompanante);
        }
        acompanantesReservaPendientes.clear();
        acompanantesReservaPendientes.addAll(nuevosAcompanantes);
    }

    private void actualizarResumenAcompanantes() {
        int total = totalOcupantesReserva();
        reservaOcupantesLabel.setText("Ocupantes cargados: " + total + " (titular + "
                + acompanantesReservaPendientes.size() + " acompañantes)");
        actualizarResumenCapacidadReserva();
    }

    private Map<Integer, List<Huesped>> solicitarOcupantes(Huesped titular, List<Integer> habitaciones) {
        List<Huesped> ocupantes = ocupantesReserva(titular);
        if (ocupantes.size() == 1) {
            Map<Integer, List<Huesped>> mapa = new LinkedHashMap<>();
            mapa.put(habitaciones.get(0), new ArrayList<>());
            mapa.get(habitaciones.get(0)).add(titular);
            return mapa;
        }

        DefaultTableModel model = new DefaultTableModel(new Object[] {
                "Ocupante", "DNI", "Tipo de cliente", "Habitación"
        }, ocupantes.size());
        for (int i = 0; i < ocupantes.size(); i++) {
            Huesped ocupante = ocupantes.get(i);
            model.setValueAt(ocupante.getNombreCompleto(), i, 0);
            model.setValueAt(ocupante.getDni(), i, 1);
            model.setValueAt(ocupante.getTipoHuesped(), i, 2);
            model.setValueAt(habitaciones.get(Math.min(i, habitaciones.size() - 1)), i, 3);
        }

        JTable tabla = new JTable(model);
        JComboBox<Integer> habitacionesCombo = new JComboBox<>(habitaciones.toArray(new Integer[0]));
        TableColumn habitacionColumn = tabla.getColumnModel().getColumn(3);
        habitacionColumn.setCellEditor(new DefaultCellEditor(habitacionesCombo));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Asignar titular y acompañantes a cada habitación"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(900, 320));

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Ocupantes de la reserva", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) {
            throw new IllegalStateException("Se cancelo la carga de ocupantes.");
        }

        Map<Integer, List<Huesped>> mapa = new LinkedHashMap<>();
        for (Integer habitacion : habitaciones) {
            mapa.put(habitacion, new ArrayList<>());
        }
        for (int fila = 0; fila < model.getRowCount(); fila++) {
            Huesped ocupante = ocupantes.get(fila);
            int habitacion = parseEntero(valorTabla(model, fila, 3), "habitación del ocupante");
            if (!mapa.containsKey(habitacion)) {
                throw new IllegalArgumentException("La habitación " + habitacion + " no está en la reserva.");
            }
            mapa.get(habitacion).add(ocupante);
        }
        for (Map.Entry<Integer, List<Huesped>> entry : mapa.entrySet()) {
            Habitacion habitacion = hotel.buscarHabitacion(entry.getKey());
            if (entry.getValue().isEmpty()) {
                throw new IllegalArgumentException("La habitación " + entry.getKey() + " no tiene ocupantes asignados.");
            }
            if (habitacion != null && entry.getValue().size() > habitacion.getCapacidad()) {
                throw new IllegalArgumentException("La habitación " + entry.getKey() + " supera su capacidad.");
            }
        }
        return mapa;
    }

    private void completarOcupantesRegistrados(DefaultTableModel model) {
        for (int fila = 0; fila < model.getRowCount(); fila++) {
            String dni = valorTabla(model, fila, 0).trim();
            Huesped registrado = hotel.buscarHuespedPorDni(dni);
            if (registrado != null) {
                model.setValueAt(registrado.getNombre(), fila, 1);
                model.setValueAt(registrado.getApellido(), fila, 2);
                model.setValueAt(registrado.getTelefono(), fila, 3);
                model.setValueAt(registrado.getEmail(), fila, 4);
                model.setValueAt(registrado.getTipoHuesped(), fila, 5);
            }
        }
    }

    private Huesped huespedDesdeFila(DefaultTableModel model, int fila, Huesped fallback) {
        String dni = valorTabla(model, fila, 0).trim();
        Huesped registrado = hotel.buscarHuespedPorDni(dni);
        if (registrado != null) {
            return registrado;
        }
        if (fallback != null && fallback.getDni().equalsIgnoreCase(dni)) {
            return fallback;
        }

        String nombre = valorTabla(model, fila, 1).trim();
        String apellido = valorTabla(model, fila, 2).trim();
        String telefono = valorTabla(model, fila, 3).trim();
        String email = valorTabla(model, fila, 4).trim();
        validadorDatosPersonales.validarHuesped(nombre, apellido, dni, telefono, email);
        return new Huesped(nombre, apellido, dni, telefono, email, valorTabla(model, fila, 5).trim());
    }

    private String valorTabla(DefaultTableModel model, int fila, int columna) {
        Object valor = model.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private SenaConfirmacion solicitarSena(double requerido) {
        JTextField montoField = new JTextField(String.format("%.2f", requerido), 12);
        JComboBox<String> metodoCombo = new JComboBox<>(METODOS_PAGO);
        metodoCombo.setSelectedItem(reservaMetodoSenaCombo.getSelectedItem());

        JPanel panel = new JPanel(new GridBagLayout());
        int fila = 0;
        fila = agregarCampo(panel, fila, "Minimo 25%", new JLabel(String.format("$ %.2f", requerido)));
        fila = agregarCampo(panel, fila, "Monto recibido", montoField);
        agregarCampo(panel, fila, "Metodo", metodoCombo);

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Registrar seña para confirmar", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) {
            return null;
        }

        double monto = parseDecimal(montoField.getText(), "monto de seña");
        if (monto < requerido) {
            throw new IllegalArgumentException("La seña debe cubrir al menos el 25% de las habitaciones.");
        }
        return new SenaConfirmacion(monto, metodoCombo.getSelectedItem().toString());
    }

    private double calcularSenaRequerida(List<Integer> habitaciones, int noches) {
        double requerida = 0.0;
        for (Integer numero : habitaciones) {
            Habitacion habitacion = hotel.buscarHabitacion(numero);
            if (habitacion == null) {
                throw new IllegalArgumentException("No existe la habitación " + numero + ".");
            }
            requerida += habitacion.getPrecioBase() * noches * Reserva.PORCENTAJE_SENA;
        }
        return requerida;
    }

    private List<Integer> habitacionesSeleccionadas() {
        List<String> seleccionadas = reservaHabitacionesList.getSelectedValuesList();
        if (seleccionadas.isEmpty()) {
            throw new IllegalArgumentException("Debe indicarse al menos una habitación.");
        }
        List<Integer> numeros = new ArrayList<>();
        for (String seleccionada : seleccionadas) {
            numeros.add(numeroHabitacionDesdeEtiqueta(seleccionada));
        }
        return numeros;
    }

    private int numeroHabitacionDesdeEtiqueta(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("La habitación seleccionada no es válida.");
        }
        String numero = etiqueta.split("\\|")[0].replace("Hab.", "").trim();
        return parseEntero(numero, "número de habitación");
    }

    private int totalOcupantesReserva() {
        return 1 + acompanantesReservaPendientes.size();
    }

    private List<Huesped> ocupantesReserva(Huesped titular) {
        List<Huesped> ocupantes = new ArrayList<>();
        ocupantes.add(titular);
        ocupantes.addAll(acompanantesReservaPendientes);
        return ocupantes;
    }

    private int capacidadTotalHabitaciones(List<Integer> habitaciones) {
        int total = 0;
        for (Integer numero : habitaciones) {
            Habitacion habitacion = hotel.buscarHabitacion(numero);
            if (habitacion == null) {
                throw new IllegalArgumentException("No existe la habitación " + numero + ".");
            }
            total += habitacion.getCapacidad();
        }
        return total;
    }

    private Estadia obtenerEstadiaDesdeCombo(JComboBox<String> combo) {
        String codigo = seleccionarCodigo(combo);
        if (codigo == null) {
            throw new IllegalStateException("No hay una estadía seleccionada.");
        }

        Estadia estadia = estadiasPorReserva.get(codigo);
        if (estadia == null) {
            throw new IllegalStateException("Primero inicia la estadía de la reserva seleccionada.");
        }
        return estadia;
    }

    private Estadia obtenerEstadiaDesdeComboOpcional(JComboBox<String> combo) {
        String codigo = seleccionarCodigo(combo);
        return codigo == null ? null : estadiasPorReserva.get(codigo);
    }

    private String seleccionarReservaActual() {
        return seleccionarCodigo(estadiaReservaCombo);
    }

    private String seleccionarCodigo(JComboBox<String> combo) {
        Object seleccionado = combo.getSelectedItem();
        if (seleccionado == null) {
            return null;
        }
        String texto = seleccionado.toString();
        int separador = texto.indexOf(" | ");
        return separador > 0 ? texto.substring(0, separador) : texto;
    }

    private ServicioEstadia crearServicioDesdeSeleccion(Estadia estadia, int cantidad) {
        ServicioCatalogo servicio = SERVICIOS[servicioCombo.getSelectedIndex()];
        double precioUnitario = precioUnitarioServicio(estadia, servicio);
        String descripcion = servicio.descripcion;
        String bonificacion = motivoBonificacionServicio(estadia, servicio);
        if (bonificacion != null) {
            descripcion += " | Bonificado por " + bonificacion;
        }
        return new ServicioAdicional(servicio.nombre, descripcion, precioUnitario, cantidad);
    }

    private double precioUnitarioServicio(Estadia estadia, ServicioCatalogo servicio) {
        if (estadia == null || servicio == null) {
            return servicio == null ? 0.0 : servicio.precioUnitario;
        }
        return reglasCliente.precioUnitarioServicio(estadia.getReserva().getHuesped(), servicio.nombre, servicio.precioUnitario);
    }

    private String motivoBonificacionServicio(Estadia estadia, ServicioCatalogo servicio) {
        if (estadia == null || servicio == null) {
            return null;
        }
        return reglasCliente.motivoBonificacionServicio(estadia.getReserva().getHuesped(), servicio.nombre);
    }

    private void validarDescuentoAplicable(Estadia estadia, DescuentoStrategy descuento) {
        reglasCliente.validarDescuentoAplicable(estadia.getReserva().getHuesped(), descuento);
    }

    private PoliticaPrecio crearPoliticaDesdeSeleccion() {
        int politica = politicaPrecioCombo.getSelectedIndex();
        if (politica == 1) {
            return new PoliticaPrecioTemporadaAlta();
        }
        if (politica == 2) {
            return new PoliticaPrecioTemporadaBaja();
        }
        return new PoliticaPrecioTemporadaMedia();
    }

    private DescuentoStrategy crearDescuentoDesdeSeleccion() {
        int descuento = descuentoCombo.getSelectedIndex();
        if (descuento == 1) {
            return new DescuentoClienteFrecuente();
        }
        if (descuento == 2) {
            return new DescuentoPromocionEspecial();
        }
        if (descuento == 3) {
            return new DescuentoConvenioEmpresarial();
        }
        return new DescuentoSinDescuento();
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

    private String construirDetalleReserva(Reserva reserva) {
        Huesped titular = reserva.getHuesped();
        Habitacion habitacion = reserva.getHabitacion();
        Estadia estadia = estadiasPorReserva.get(reserva.getCodigo());

        StringBuilder detalle = new StringBuilder();
        detalle.append("Reserva").append(System.lineSeparator());
        detalle.append("Código: ").append(reserva.getCodigo()).append(System.lineSeparator());
        detalle.append("Grupo: ").append(reserva.getGrupoCodigo()).append(System.lineSeparator());
        detalle.append("Estado: ").append(reserva.getEstado()).append(System.lineSeparator());
        detalle.append("Ingreso/Egreso reservado: ").append(reserva.getFechaIngreso())
                .append(" -> ").append(reserva.getFechaEgreso())
                .append(" | Noches: ").append(reserva.calcularNoches()).append(System.lineSeparator());
        detalle.append(String.format("Seña abonada (%s): $ %.2f",
                reserva.getMetodoSena(), reserva.getSenaPagada())).append(System.lineSeparator());
        detalle.append(String.format("Total habitación reservado: $ %.2f", reserva.calcularTotalHabitacion()))
                .append(System.lineSeparator()).append(System.lineSeparator());

        detalle.append("Titular").append(System.lineSeparator());
        detalle.append(resumenHuespedDetalle(titular)).append(System.lineSeparator());
        detalle.append("Tipo de cliente: ").append(titular.getTipoHuesped()).append(System.lineSeparator());
        detalle.append("Beneficios activos: ").append(describirBeneficiosCliente(reserva)).append(System.lineSeparator())
                .append(System.lineSeparator());

        detalle.append("Habitación").append(System.lineSeparator());
        detalle.append("Número: ").append(habitacion.getNumero())
                .append(" | Tipo: ").append(habitacion.getTipo().getNombreVisible())
                .append(" | Capacidad: ").append(habitacion.getCapacidad())
                .append(" | Precio base: $ ").append(String.format("%.2f", habitacion.getPrecioBase()))
                .append(" | Estado: ").append(habitacion.getEstado()).append(System.lineSeparator())
                .append(System.lineSeparator());

        detalle.append("Ocupantes de la habitación").append(System.lineSeparator());
        for (Huesped ocupante : reserva.getOcupantes()) {
            String rol = ocupante.getDni().equalsIgnoreCase(titular.getDni()) ? "Titular" : "Acompañante";
            detalle.append("- ").append(rol).append(": ").append(resumenHuespedDetalle(ocupante))
                    .append(" | Tipo: ").append(ocupante.getTipoHuesped())
                    .append(System.lineSeparator());
        }
        detalle.append(System.lineSeparator());

        detalle.append("Estadía").append(System.lineSeparator());
        if (estadia == null) {
            detalle.append("Sin check-in registrado.").append(System.lineSeparator());
            return detalle.toString();
        }

        detalle.append("Ingreso/Egreso real: ").append(estadia.getFechaIngresoReal())
                .append(" -> ").append(estadia.getFechaEgresoReal())
                .append(" | Noches reales: ").append(estadia.calcularNoches()).append(System.lineSeparator());
        detalle.append("Política tarifaria aplicada: ").append(estadia.getPoliticaPrecioNombre())
                .append(" (").append(String.format("%+.0f%%", estadia.getPoliticaPrecioPorcentaje())).append(")")
                .append(System.lineSeparator());
        detalle.append("Descuento aplicado: ").append(estadia.getDescuentoNombre())
                .append(" (").append(String.format("%.0f%%", estadia.getDescuentoPorcentaje())).append(")")
                .append(System.lineSeparator());
        detalle.append("Servicios").append(System.lineSeparator());
        if (estadia.getServicios().isEmpty()) {
            detalle.append("- Sin servicios registrados").append(System.lineSeparator());
        } else {
            for (ServicioConsumido servicio : estadia.getServicios()) {
                detalle.append(String.format("- %s x%d | Unitario: $ %.2f | Total: $ %.2f | %s",
                        servicio.getNombre(), servicio.getCantidad(), servicio.getPrecioUnitario(),
                        servicio.getPrecio(), servicio.getDescripcion())).append(System.lineSeparator());
            }
        }
        detalle.append("Pagos").append(System.lineSeparator());
        if (estadia.getPagos().isEmpty()) {
            detalle.append("- Sin pagos adicionales registrados").append(System.lineSeparator());
        } else {
            for (Pago pago : estadia.getPagos()) {
                detalle.append(String.format("- %s | %s | $ %.2f | %s",
                        pago.getFechaPago(), pago.getMetodoPago().getNombre(), pago.getMonto(),
                        pago.getEstadoPago())).append(System.lineSeparator());
            }
        }
        detalle.append(String.format("Pagos adicionales: $ %.2f", estadia.calcularPagosRegistrados()))
                .append(System.lineSeparator());
        detalle.append(String.format("Total abonado con seña: $ %.2f", estadia.calcularTotalPagado()));
        return detalle.toString();
    }

    private String resumenHuespedDetalle(Huesped huesped) {
        return huesped.getNombreCompleto() + " | DNI: " + huesped.getDni()
                + " | Tel: " + huesped.getTelefono()
                + " | Email: " + huesped.getEmail();
    }

    private String describirBeneficiosCliente(Reserva reserva) {
        return reglasCliente.describirBeneficios(reserva.getHuesped());
    }

    private String requisitoDescuento(DescuentoStrategy descuento) {
        return reglasCliente.requisitoDescuento(descuento);
    }

    private String construirResumenPago(Estadia estadia, PoliticaPrecio politicaPrecio, DescuentoStrategy descuento) {
        double baseHabitacion = estadia.getReserva().getHabitacion().getPrecioBase() * estadia.calcularNoches();
        double habitacionAjustada = politicaPrecio.calcularPrecio(baseHabitacion);
        double ajusteTemporada = habitacionAjustada - baseHabitacion;
        double totalServicios = estadia.calcularTotalServicios();
        double subtotal = habitacionAjustada + totalServicios;
        double totalFinal = descuento.aplicar(subtotal);
        double descuentoMonto = subtotal - totalFinal;
        double totalAbonado = estadia.calcularTotalPagado();
        double saldo = estadia.calcularSaldoPendiente(totalFinal);

        StringBuilder resumen = new StringBuilder();
        resumen.append("Reserva: ").append(estadia.getReserva().getCodigo())
                .append(" | Habitación ").append(estadia.getReserva().getHabitacion().getNumero())
                .append(System.lineSeparator());
        resumen.append("Tipo de cliente: ").append(estadia.getReserva().getHuesped().getTipoHuesped())
                .append(" | Beneficios: ").append(describirBeneficiosCliente(estadia.getReserva()))
                .append(System.lineSeparator());
        resumen.append("Noches reales: ").append(estadia.calcularNoches()).append(System.lineSeparator());
        resumen.append(String.format("Habitación: $ %.2f x %d noches = $ %.2f",
                estadia.getReserva().getHabitacion().getPrecioBase(), estadia.calcularNoches(), baseHabitacion))
                .append(System.lineSeparator());
        resumen.append(String.format("%s: %+.0f%% = $ %.2f",
                politicaPrecio.getNombre(), politicaPrecio.getPorcentajeAjuste(), ajusteTemporada))
                .append(System.lineSeparator());
        resumen.append(String.format("Subtotal habitación: $ %.2f", habitacionAjustada))
                .append(System.lineSeparator());
        resumen.append("Adicionales:").append(System.lineSeparator());
        if (estadia.getServicios().isEmpty()) {
            resumen.append("  Sin adicionales").append(System.lineSeparator());
        } else {
            for (ServicioConsumido servicio : estadia.getServicios()) {
                resumen.append(String.format("  %s x%d ($ %.2f c/u): $ %.2f",
                        servicio.getNombre(), servicio.getCantidad(), servicio.getPrecioUnitario(), servicio.getPrecio()));
                if (servicio.getDescripcion() != null && !servicio.getDescripcion().isBlank()) {
                    resumen.append(" | ").append(servicio.getDescripcion());
                }
                resumen
                        .append(System.lineSeparator());
            }
        }
        resumen.append(String.format("Total adicionales: $ %.2f", totalServicios)).append(System.lineSeparator());
        resumen.append(String.format("Subtotal antes de descuento: $ %.2f", subtotal)).append(System.lineSeparator());
        resumen.append(String.format("Descuento %s (%.0f%%): -$ %.2f%s",
                descuento.getNombre(), descuento.getPorcentaje(), descuentoMonto, requisitoDescuento(descuento)))
                .append(System.lineSeparator());
        resumen.append(String.format("Total final: $ %.2f", totalFinal)).append(System.lineSeparator());
        resumen.append(String.format("Seña abonada (%s): $ %.2f",
                estadia.getReserva().getMetodoSena(), estadia.getReserva().getSenaPagada())).append(System.lineSeparator());
        resumen.append(String.format("Pagos adicionales: $ %.2f", estadia.calcularPagosRegistrados())).append(System.lineSeparator());
        resumen.append(String.format("Total abonado: $ %.2f", totalAbonado)).append(System.lineSeparator());
        resumen.append(String.format("Saldo pendiente: $ %.2f", saldo));
        return resumen.toString();
    }

    private void refrescarTodaLaVista() {
        refrescarTablaHabitaciones();
        refrescarTablaReservas();
        refrescarTablaEstadias();
        refrescarHistorialReservas();
        refrescarCombos();
        actualizarResumenHotel();
    }

    private void refrescarTablaHabitaciones() {
        habitacionesModel.setRowCount(0);
        for (Habitacion habitacion : hotel.getHabitaciones()) {
            if (!habitacionPasaFiltros(habitacion)) {
                continue;
            }
            habitacionesModel.addRow(new Object[] {
                    habitacion.getNumero(),
                    habitacion.getCapacidad(),
                    String.format("%.2f", habitacion.getPrecioBase()),
                    habitacion.getTipo(),
                    habitacion.getEstado()
            });
        }
    }

    private boolean habitacionPasaFiltros(Habitacion habitacion) {
        String capacidadFiltro = (String) habitacionFiltroCapacidadCombo.getSelectedItem();
        if (capacidadFiltro != null && !"Todas".equals(capacidadFiltro)
                && habitacion.getCapacidad() < Integer.parseInt(capacidadFiltro)) {
            return false;
        }

        String tipoFiltro = (String) habitacionFiltroTipoCombo.getSelectedItem();
        if (tipoFiltro != null && !"Todos".equals(tipoFiltro)
                && !habitacion.getTipo().name().equals(tipoFiltro)) {
            return false;
        }

        String estadoFiltro = (String) habitacionFiltroEstadoCombo.getSelectedItem();
        return estadoFiltro == null || "Todos".equals(estadoFiltro)
                || habitacion.getEstado().name().equals(estadoFiltro);
    }

    private void refrescarTablaReservas() {
        reservasModel.setRowCount(0);
        for (Reserva reserva : hotel.getReservas()) {
            reservasModel.addRow(new Object[] {
                    reserva.getCodigo(),
                    reserva.getGrupoCodigo(),
                    reserva.getHuesped().getNombreCompleto(),
                    reserva.getHabitacion().getNumero(),
                    reserva.ocupantesResumen(),
                    reserva.getFechaIngreso(),
                    reserva.calcularNoches(),
                    reserva.getFechaEgreso(),
                    String.format("%.2f", reserva.calcularTotalHabitacion()),
                    String.format("%.2f", reserva.getSenaPagada()),
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
                    estadia.getReserva().getHabitacion().getNumero(),
                    estadia.getReserva().getHuesped().getNombreCompleto(),
                    estadia.getReserva().ocupantesResumen(),
                    estadia.calcularNoches(),
                    estadia.getFechaIngresoReal(),
                    estadia.getFechaEgresoReal(),
                    estadia.getServicios().size(),
                    String.format("%.2f", estadia.getReserva().getSenaPagada()),
                    String.format("%.2f", estadia.calcularPagosRegistrados()),
                    String.format("%.2f", estadia.calcularTotalPagado()),
                    estadia.getReserva().getEstado()
            });
        }
    }

    private void refrescarHistorialReservas() {
        historialReservasModel.setRowCount(0);
        for (Reserva reserva : hotel.getReservas()) {
            Estadia estadia = estadiasPorReserva.get(reserva.getCodigo());
            historialReservasModel.addRow(new Object[] {
                    reserva.getCodigo(),
                    reserva.getGrupoCodigo(),
                    reserva.getHabitacion().getNumero(),
                    reserva.getHuesped().getNombreCompleto(),
                    reserva.getFechaIngreso(),
                    reserva.getFechaEgreso(),
                    reserva.calcularNoches(),
                    reserva.ocupantesResumen(),
                    reserva.getEstado(),
                    String.format("%.2f", reserva.getSenaPagada()),
                    estadia == null ? "Sin iniciar" : estadia.getFechaIngresoReal() + " -> " + estadia.getFechaEgresoReal(),
                    estadia == null ? "-" : estadia.getPoliticaPrecioNombre()
                            + " (" + String.format("%+.0f%%", estadia.getPoliticaPrecioPorcentaje()) + ")",
                    estadia == null ? "-" : estadia.getDescuentoNombre()
                            + " (" + String.format("%.0f%%", estadia.getDescuentoPorcentaje()) + ")",
                    estadia == null ? "0.00" : String.format("%.2f", estadia.calcularPagosRegistrados()),
                    estadia == null ? String.format("%.2f", reserva.getSenaPagada()) : String.format("%.2f", estadia.calcularTotalPagado())
            });
        }
    }

    private void refrescarCombos() {
        String reservaSeleccionada = seleccionarReservaActual();
        String servicioSeleccionado = seleccionarCodigo(servicioEstadiaCombo);
        String pagoSeleccionado = seleccionarCodigo(pagoEstadiaCombo);

        DefaultComboBoxModel<String> modeloReservas = new DefaultComboBoxModel<>();
        for (Reserva reserva : hotel.getReservas()) {
            if (reserva.getEstado() == gestionhotelera.dominio.EstadoReserva.CONFIRMADA
                    && !estadiasPorReserva.containsKey(reserva.getCodigo())) {
                modeloReservas.addElement(textoReservaCorto(reserva));
            }
        }
        estadiaReservaCombo.setModel(modeloReservas);
        seleccionarComboPorCodigo(estadiaReservaCombo, reservaSeleccionada);

        DefaultComboBoxModel<String> modeloEstadias = new DefaultComboBoxModel<>();
        for (Estadia estadia : estadiasPorReserva.values()) {
            if (estadia.getReserva().getEstado() == gestionhotelera.dominio.EstadoReserva.CONFIRMADA) {
                modeloEstadias.addElement(textoReservaCorto(estadia.getReserva()));
            }
        }
        servicioEstadiaCombo.setModel(modeloEstadias);
        seleccionarComboPorCodigo(servicioEstadiaCombo, servicioSeleccionado);

        DefaultComboBoxModel<String> modeloPagos = new DefaultComboBoxModel<>();
        for (Estadia estadia : estadiasPorReserva.values()) {
            if (estadiaSeleccionableParaPago(estadia)) {
                modeloPagos.addElement(textoReservaCorto(estadia.getReserva()));
            }
        }
        pagoEstadiaCombo.setModel(modeloPagos);
        seleccionarComboPorCodigo(pagoEstadiaCombo, pagoSeleccionado);

        refrescarOpcionesHabitacionesReserva();
        autocompletarFechasEstadia();
        actualizarPrecioServicio();
    }

    private boolean estadiaSeleccionableParaPago(Estadia estadia) {
        return estadia.getReserva().getEstado() == gestionhotelera.dominio.EstadoReserva.CONFIRMADA;
    }

    private void actualizarResumenHotel() {
        habitacionesCountLabel.setText("Habitaciones: " + hotel.getHabitaciones().size());
        reservasCountLabel.setText("Reservas: " + hotel.getReservas().size());
        estadiasCountLabel.setText("Estadías registradas: " + estadiasPorReserva.size());
    }

    private void actualizarPrecioServicio() {
        ServicioCatalogo servicio = SERVICIOS[servicioCombo.getSelectedIndex()];
        Estadia estadia = obtenerEstadiaDesdeComboOpcional(servicioEstadiaCombo);
        double precioUnitario = precioUnitarioServicio(estadia, servicio);
        String bonificacion = motivoBonificacionServicio(estadia, servicio);
        if (bonificacion == null) {
            servicioPrecioLabel.setText(String.format("Precio unitario: $ %.2f", precioUnitario));
        } else {
            servicioPrecioLabel.setText(String.format(
                    "Precio unitario: $ %.2f | Bonificado para %s (precio lista $ %.2f)",
                    precioUnitario, bonificacion, servicio.precioUnitario));
        }
    }

    private void refrescarOpcionesHabitacionesReserva() {
        Set<Integer> seleccionadas = new HashSet<>();
        for (String seleccionada : reservaHabitacionesList.getSelectedValuesList()) {
            seleccionadas.add(numeroHabitacionDesdeEtiqueta(seleccionada));
        }
        reservaHabitacionesModel.clear();

        List<Integer> indices = new ArrayList<>();
        int indice = 0;
        for (Habitacion habitacion : habitacionesDisponiblesParaReserva()) {
            reservaHabitacionesModel.addElement(etiquetaHabitacionReserva(habitacion));
            if (seleccionadas.contains(habitacion.getNumero())) {
                indices.add(indice);
            }
            indice++;
        }

        int[] seleccion = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            seleccion[i] = indices.get(i);
        }
        reservaHabitacionesList.setSelectedIndices(seleccion);
        actualizarResumenCapacidadReserva();
    }

    private List<Habitacion> habitacionesDisponiblesParaReserva() {
        try {
            LocalDate ingreso = parseFecha(reservaIngresoField.getText(), "fecha de ingreso");
            int noches = (Integer) reservaNochesCombo.getSelectedItem();
            LocalDate egreso = ingreso.plusDays(noches);
            reservaEgresoField.setText(egreso.toString());
            return hotel.consultarDisponibilidad(ingreso, egreso, 1);
        } catch (RuntimeException ex) {
            List<Habitacion> candidatas = new ArrayList<>();
            for (Habitacion habitacion : hotel.getHabitaciones()) {
                if (habitacion.admiteReservas()) {
                    candidatas.add(habitacion);
                }
            }
            return candidatas;
        }
    }

    private String etiquetaHabitacionReserva(Habitacion habitacion) {
        return "Hab. " + habitacion.getNumero()
                + " | " + habitacion.getTipo().getNombreVisible()
                + " | Capacidad " + habitacion.getCapacidad()
                + " | " + formatearMonedaSinCentavos(habitacion.getPrecioBase()) + "/noche"
                + " | " + habitacion.getEstado();
    }

    private void actualizarFechasYHabitacionesReserva() {
        try {
            LocalDate ingreso = parseFecha(reservaIngresoField.getText(), "fecha de ingreso");
            int noches = (Integer) reservaNochesCombo.getSelectedItem();
            reservaEgresoField.setText(ingreso.plusDays(noches).toString());
        } catch (RuntimeException ex) {
            reservaEgresoField.setText("");
        }
        refrescarOpcionesHabitacionesReserva();
        actualizarSenaReservaSeleccionada();
    }

    private void actualizarResumenCapacidadReserva() {
        int capacidad = 0;
        for (String seleccionada : reservaHabitacionesList.getSelectedValuesList()) {
            Habitacion habitacion = hotel.buscarHabitacion(numeroHabitacionDesdeEtiqueta(seleccionada));
            if (habitacion != null) {
                capacidad += habitacion.getCapacidad();
            }
        }
        reservaCapacidadLabel.setText("Capacidad seleccionada: " + capacidad
                + " / Ocupantes: " + totalOcupantesReserva());
        actualizarSenaReservaSeleccionada();
    }

    private void actualizarSenaReservaSeleccionada() {
        try {
            List<Integer> habitaciones = habitacionesSeleccionadas();
            int noches = (Integer) reservaNochesCombo.getSelectedItem();
            reservaSenaLabel.setText(String.format("Seña requerida: $ %.2f", calcularSenaRequerida(habitaciones, noches)));
        } catch (RuntimeException ex) {
            reservaSenaLabel.setText("Seña requerida: -");
        }
    }

    private void sugerirPrecioPorTipo() {
        TipoHabitacion tipo = (TipoHabitacion) habitacionTipoCombo.getSelectedItem();
        if (tipo != null) {
            seleccionarComboInteger(habitacionCapacidadCombo, tipo.getCapacidadEstandar());
            seleccionarComboString(habitacionPrecioCombo, String.format("%.2f", tipo.getPrecioBase()));
        }
    }

    private void actualizarEtiquetaDescuento() {
        DescuentoStrategy descuento = crearDescuentoDesdeSeleccion();
        descuentoPorcentajeLabel.setText(String.format("Descuento aplicado: %s %.0f%%%s",
                descuento.getNombre(), descuento.getPorcentaje(), requisitoDescuento(descuento)));
    }

    private void actualizarEtiquetaPoliticaPrecio() {
        PoliticaPrecio politicaPrecio = crearPoliticaDesdeSeleccion();
        politicaPrecioLabel.setText(String.format("Política tarifaria: %s %.0f%%",
                politicaPrecio.getNombre(),
                politicaPrecio.getPorcentajeAjuste()));
    }

    private void autocompletarFechasEstadia() {
        String codigo = seleccionarReservaActual();
        Reserva reserva = codigo == null ? null : reservasPorCodigo.get(codigo);
        if (reserva == null) {
            estadiaIngresoField.setText("");
            estadiaEgresoField.setText("");
            return;
        }
        estadiaIngresoField.setText(reserva.getFechaIngreso().toString());
        estadiaEgresoField.setText(reserva.getFechaEgreso().toString());
    }

    private String textoReservaCorto(Reserva reserva) {
        return reserva.getCodigo() + " | Hab " + reserva.getHabitacion().getNumero();
    }

    private String[] elementosCombo(DefaultComboBoxModel<String> modelo) {
        String[] elementos = new String[modelo.getSize()];
        for (int i = 0; i < modelo.getSize(); i++) {
            elementos[i] = modelo.getElementAt(i);
        }
        return elementos;
    }

    private void seleccionarComboPorCodigo(JComboBox<String> combo, String codigo) {
        if (combo.getItemCount() == 0) {
            return;
        }
        if (codigo != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).startsWith(codigo + " |")) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
        combo.setSelectedIndex(0);
    }

    private String solicitarCodigoReserva(String titulo, boolean soloConfirmadas) {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        for (Reserva reserva : hotel.getReservas()) {
            if (soloConfirmadas && reserva.getEstado() != gestionhotelera.dominio.EstadoReserva.CONFIRMADA) {
                continue;
            }
            modelo.addElement(textoReservaCorto(reserva) + " | " + reserva.getEstado());
        }
        if (modelo.getSize() == 0) {
            throw new IllegalStateException("No hay reservas disponibles para esta acción.");
        }

        JComboBox<String> combo = new JComboBox<>(modelo);
        int resultado = JOptionPane.showConfirmDialog(this, combo, titulo, JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) {
            return null;
        }
        return seleccionarCodigo(combo);
    }

    private String[] servicioLabels() {
        String[] labels = new String[SERVICIOS.length];
        for (int i = 0; i < SERVICIOS.length; i++) {
            labels[i] = SERVICIOS[i].nombre + " - $ " + String.format("%.2f", SERVICIOS[i].precioUnitario);
        }
        return labels;
    }

    private Integer[] numeros(int desde, int hasta) {
        Integer[] valores = new Integer[hasta - desde + 1];
        for (int i = 0; i < valores.length; i++) {
            valores[i] = desde + i;
        }
        return valores;
    }

    private Integer[] capacidadesHabitacion() {
        Integer[] capacidades = new Integer[TipoHabitacion.values().length];
        for (int i = 0; i < TipoHabitacion.values().length; i++) {
            capacidades[i] = TipoHabitacion.values()[i].getCapacidadEstandar();
        }
        return capacidades;
    }

    private String[] preciosHabitacion() {
        String[] precios = new String[TipoHabitacion.values().length];
        for (int i = 0; i < TipoHabitacion.values().length; i++) {
            precios[i] = String.format("%.2f", TipoHabitacion.values()[i].getPrecioBase());
        }
        return precios;
    }

    private String[] filtroCapacidadesHabitacion() {
        Integer[] capacidades = capacidadesHabitacion();
        String[] filtros = new String[capacidades.length + 1];
        filtros[0] = "Todas";
        for (int i = 0; i < capacidades.length; i++) {
            filtros[i + 1] = capacidades[i].toString();
        }
        return filtros;
    }

    private String[] filtroTiposHabitacion() {
        TipoHabitacion[] tipos = TipoHabitacion.values();
        String[] filtros = new String[tipos.length + 1];
        filtros[0] = "Todos";
        for (int i = 0; i < tipos.length; i++) {
            filtros[i + 1] = tipos[i].name();
        }
        return filtros;
    }

    private String[] filtroEstadosHabitacion() {
        EstadoHabitacion[] estados = EstadoHabitacion.values();
        String[] filtros = new String[estados.length + 1];
        filtros[0] = "Todos";
        for (int i = 0; i < estados.length; i++) {
            filtros[i + 1] = estados[i].name();
        }
        return filtros;
    }

    private String formatearMonedaSinCentavos(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        formato.setMaximumFractionDigits(0);
        formato.setMinimumFractionDigits(0);
        return formato.format(monto).replace("\u00a0", " ");
    }

    private JPanel crearCampoConBoton(JTextField campo, JButton boton) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.add(campo, BorderLayout.CENTER);
        panel.add(boton, BorderLayout.EAST);
        return panel;
    }

    private void cargarHabitacionSeleccionada(JTable tabla) {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        habitacionNumeroField.setText(habitacionesModel.getValueAt(filaModelo, 0).toString());
        seleccionarComboInteger(habitacionCapacidadCombo, Integer.parseInt(habitacionesModel.getValueAt(filaModelo, 1).toString()));
        Object tipo = habitacionesModel.getValueAt(filaModelo, 3);
        if (tipo instanceof TipoHabitacion) {
            habitacionTipoCombo.setSelectedItem(tipo);
        }
        seleccionarComboString(habitacionPrecioCombo, habitacionesModel.getValueAt(filaModelo, 2).toString());
        habitacionEstadoCombo.setSelectedItem(EstadoHabitacion.valueOf(habitacionesModel.getValueAt(filaModelo, 4).toString()));
    }

    private void limpiarFormularioHabitacion() {
        habitacionNumeroField.setText("");
        habitacionCapacidadCombo.setSelectedIndex(0);
        habitacionTipoCombo.setSelectedIndex(0);
        habitacionEstadoCombo.setSelectedItem(EstadoHabitacion.DISPONIBLE);
        sugerirPrecioPorTipo();
    }

    private void limpiarFiltrosHabitaciones() {
        habitacionFiltroCapacidadCombo.setSelectedIndex(0);
        habitacionFiltroTipoCombo.setSelectedIndex(0);
        habitacionFiltroEstadoCombo.setSelectedIndex(0);
        refrescarTablaHabitaciones();
    }

    private void seleccionarComboString(JComboBox<String> combo, String valor) {
        if (valor == null || valor.isBlank()) {
            combo.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (valor.equalsIgnoreCase(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(valor);
        combo.setSelectedItem(valor);
    }

    private void seleccionarComboInteger(JComboBox<Integer> combo, int valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i) == valor) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(valor);
        combo.setSelectedItem(valor);
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

    private static class ServicioCatalogo {
        private final String nombre;
        private final String descripcion;
        private final double precioUnitario;

        ServicioCatalogo(String nombre, String descripcion, double precioUnitario) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precioUnitario = precioUnitario;
        }
    }

    private static class SenaConfirmacion {
        private final double monto;
        private final String metodo;

        SenaConfirmacion(double monto, String metodo) {
            this.monto = monto;
            this.metodo = metodo;
        }
    }
}
