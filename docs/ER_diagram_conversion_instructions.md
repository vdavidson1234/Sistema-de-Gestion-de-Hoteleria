Para convertir `er_diagram.svg` a PDF localmente:

- Usando Inkscape:

```
inkscape docs/er_diagram.svg --export-type=pdf --export-filename=docs/er_diagram.pdf
```

- Usando rsvg-convert (librsvg):

```
rsvg-convert -f pdf -o docs/er_diagram.pdf docs/er_diagram.svg
```

- Abrir `er_diagram.svg` en un navegador y "Imprimir" -> "Guardar como PDF".

Si querés, puedo intentar generar el PDF aquí, pero mi entorno podría no tener herramientas de conversión instaladas. Por eso incluí el SVG en el repo para que lo transforme quien administre la base de datos.
