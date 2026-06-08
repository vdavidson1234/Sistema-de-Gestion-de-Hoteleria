Uso de persistencia SQLite

Pasos rápidos:

1) Descargar el driver `sqlite-jdbc` (por ejemplo, https://github.com/xerial/sqlite-jdbc/releases) y colocar el JAR en algún lugar accesible.

2) Para ejecutar la aplicación y usar la BD `hotel.db`, lanzar Java añadiendo el JAR al classpath. Ejemplo (PowerShell):

```powershell
$jar = 'C:\path\to\sqlite-jdbc-<version>.jar'
java -cp "bin;${jar}" gestionhotelera.App
```

3) Iniciar el helper de persistencia desde la UI o desde `App` llamando a:

```java
Database db = new Database("hotel.db");
HotelPersistence p = new HotelPersistence(db);
p.initialize();
// p.saveHotel(hotel);
// Hotel loaded = p.loadHotel();
```

Archivos importantes:
- `src/gestionhotelera/persistence/Database.java` — conexión y creación de tablas
- `src/gestionhotelera/persistence/HotelPersistence.java` — guardado/carga básica de habitaciones y reservas
- `docs/schema.sql` — script SQL del esquema recomendado
- `docs/er_diagram.svg` — diagrama ER (abrir y exportar a PDF)

Notas:
- La implementación actual guarda habitaciones y reservas/huéspedes mínimos. Se pueden extender servicios, pagos y estadías con lógica similar.
- Para entornos de producción usar migraciones y control de transacciones más robusto.
