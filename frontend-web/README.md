# Barchello Bávaro Pub — Panel Administrativo (EV03)

Evidencia GA6-220501096-AA4-EV03 — Diseño front-end (HTML, CSS y JavaScript).

## Cómo probarlo

1. Descomprime el ZIP y abre `index.html` en el navegador (no necesita servidor).
2. Inicia sesión con uno de estos usuarios de demostración:

   | Usuario     | Contraseña | Rol       | Qué verás en el navbar |
   |-------------|-----------|-----------|--------------------------|
   | `admin`     | `1234`    | Administrador | Todos los módulos |
   | `mesero`| `1234`    | Mesero    | Solo Dashboard y Reservas |
   | `cocinero`     | `1234`    | Cocinero  | Solo Panel de Cocina |

3. Prueba el módulo **Menú** (nuevo en esta evidencia): agrega, edita, elimina
   o marca como agotado un producto — los cambios quedan guardados en el
   navegador (localStorage) mientras no exista un backend real.

## Estructura del proyecto

```
index.html            Login (con validación jQuery Validate)
dashboard.html         Panel principal
reservas.html          Gestión de reservas (con validación)
cocina.html             Panel de cocina (cambio de estado de pedidos)
menu.html               Gestión de menú — NUEVO en EV03 (CRUD simulado)
ventas.html             Historial de ventas
inventario.html         Control de inventario
compras.html            Órdenes de compra (con validación)
caja.html                Apertura/cierre de turno
reportes.html           Accesos a informes
usuarios.html           Personal del restaurante
registro.html           Registro de clientes (uso interno del personal)
errores.html            Catálogo de estados de error (referencia de diseño)

css/styles.css          Hoja de estilos compartida (paleta e identidad visual)

js/
  auth.js               Autenticación y control de roles (simulados)
  api.js                 Capa de datos — patrón "API-Ready"
  ui.js                   Notificaciones, buscador de tablas, helpers
  validaciones.js        Reglas de validación (jQuery Validate + Regex)

LEEME_Justificacion_Diseño.txt   Justificación por evidencia AA3-EV02 no habilitada
```

## El patrón "API-Ready"

Todo dato que hoy se simula (reservas, compras, productos, cierre de caja)
pasa por una función aislada en `js/api.js` (por ejemplo `guardarReserva()`
u `obtenerProductos()`). El día que exista un backend real, solo se
reemplaza el *interior* de esas funciones por una llamada `fetch()` — el
HTML, el CSS y el resto del JavaScript no se tocan. El comentario `FUTURO:`
dentro de cada función documenta el endpoint que la reemplazará.

## Alcance de esta evidencia

Esta entrega es exclusivamente front-end. No incluye backend, base de
datos, ni conexión real con la app móvil de clientes — eso corresponde a
evidencias futuras de mi proyecto formativo.
