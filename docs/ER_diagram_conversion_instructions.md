Para convertir `er_diagram.svg` a PDF localmente:

- Usando Inkscape:

```bash
inkscape docs/er_diagram.svg --export-type=pdf --export-filename=docs/er_diagram.pdf
```

- Usando rsvg-convert (librsvg):

```bash
rsvg-convert -f pdf -o docs/er_diagram.pdf docs/er_diagram.svg
```

- Abrir `docs/er_diagram.svg` en un navegador y usar "Imprimir" -> "Guardar como PDF".

El diagrama SVG incluido en el repositorio representa el esquema PostgreSQL actual:
reservas agrupadas por `grupo_codigo`, ocupantes por habitación en
`reserva_ocupantes`, baja lógica de habitaciones con `activa`, seña
requerida/pagada para confirmar reservas, estadías con política/descuento
persistidos y servicios con cantidad y precio unitario.
