/**
 * ============================================================
 * API.JS — Capa de Abstracción de Datos (Cliente REST)
 * ============================================================
 * Este archivo centraliza las peticiones HTTP al backend.
 * Cualquier cambio en los endpoints se realiza únicamente aquí.
 */

const API = {
    // MAÑANA: 'http://localhost:8000/api' o 'https://api.barchello.com'
    BASE_URL: 'http://localhost:8080/api',

    // Helper: simula latencia de red para que la UI se sienta real
    _simularDelay: (ms = 600) => new Promise(resolve => setTimeout(resolve, ms)),

    /**
     * ============================================================
     *  AUTENTICACIÓN
     * ============================================================
     * MAÑANA: POST ${API_BASE}/auth/login
     * 
     *  FIX CRÍTICO: Ahora devuelve TODOS los campos del usuario,
     * incluyendo roles_secundarios. Antes solo devolvía {nombre, rol}
     * y por eso el cambio de rol no funcionaba.
     */
    async login(credenciales) {
        await this._simularDelay(800);

        // const response = await fetch(`${this.BASE_URL}/auth/login`, {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify(credenciales)
        // });
        // if (!response.ok) throw new Error('Credenciales inválidas');
        // return await response.json();

        // TODO: Conectar con endpoint de autenticación real.
        try {
            const resp = await fetch('data/usuarios.json');
            const usuarios = await resp.json();

            const encontrado = usuarios.find(u =>
                u.usuario === credenciales.usuario &&
                u.password === credenciales.password &&
                u.estado === 'activo'
            );

            if (!encontrado) {
                throw new Error('El usuario o la contraseña son incorrectos.');
            }

            //  DEVOLVER TODOS LOS CAMPOS NECESARIOS
            // Esto es lo que se guarda en sessionStorage
            return {
                exito: true,
                token: 'fake-jwt-' + encontrado.rol + '-' + Date.now(),
                usuario: {
                    id: encontrado.id,
                    nombre: encontrado.nombre_completo,
                    usuario: encontrado.usuario,
                    email: encontrado.email,
                    rol: encontrado.rol,
                    roles_secundarios: encontrado.roles_secundarios || [],
                    sucursal_id: encontrado.sucursal_id
                }
            };
        } catch (error) {
            throw new Error(error.message || 'Error al iniciar sesión');
        }
    },

    /**
     * ============================================================
     *  PRODUCTOS (Menú del restaurante)
     * ============================================================
     */
    async obtenerProductos() {
        try {
            const resp = await fetch(`${this.BASE_URL}/productos`);
            if (!resp.ok) throw new Error('Error al consultar la API');
            return await resp.json();
        } catch (e) {
            console.error('Error cargando productos:', e);
            return [];
        }
    },

    /**
     * Envía un nuevo producto al backend para ser guardado en MySQL.
    */
    async guardarProducto(producto) {
        try {
            // Adapto los datos que vienen del formulario al modelo de Java
            const payload = {
                nombre: producto.nombre,
                descripcion: producto.descripcion,
                precioVenta: producto.precio,
                estado: producto.disponible ? 'activo' : 'inactivo',
                imagenUrl: producto.imagen,
                // Nota: Categoría fija por ahora. Pendiente hacerla dinámica.
                categoria: { idCategoria: 1 }
            };

            const resp = await fetch(`${this.BASE_URL}/productos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) throw new Error('Error al guardar en el servidor');
            return await resp.json();
        } catch (e) {
            console.error('Error guardando producto:', e);
            throw e;
        }
    },

    /**
     * ============================================================
     *  RESERVAS
     * ============================================================
     * MAÑANA: GET ${API_BASE}/reservas
     */
    async obtenerReservas() {
        await this._simularDelay(500);
        try {
            const resp = await fetch('data/reservas.json');
            return await resp.json();
        } catch (e) {
            console.error('Error cargando reservas:', e);
            return [];
        }
    },

    /**
     * MAÑANA: POST ${API_BASE}/reservas
     */
    async guardarReserva(datos) {
        await this._simularDelay(700);
        console.log('Reserva guardada en BD (simulado):', datos);
        return { exito: true, mensaje: 'Reserva registrada correctamente', id: Date.now() };
    },

    /**
     * ============================================================
     *  PEDIDOS (Sistema de Cocina con tracking de items)
     * ============================================================
     * MAÑANA: GET ${API_BASE}/pedidos
     */
    async obtenerPedidos() {
        await this._simularDelay(500);
        try {
            const resp = await fetch('data/pedidos.json');
            return await resp.json();
        } catch (e) {
            console.error('Error cargando pedidos:', e);
            return [];
        }
    },

    /**
     * MAÑANA: GET ${API_BASE}/pedidos/activos
     * Devuelve solo pedidos que NO están completados ni cancelados
     */
    async obtenerPedidosActivos() {
        await this._simularDelay(400);
        const pedidos = await this.obtenerPedidos();
        return pedidos.filter(p => p.estado !== 'completado' && p.estado !== 'cancelado');
    },

    /**
     * MAÑANA: PATCH ${API_BASE}/pedidos/{pedidoId}/items/{itemId}
     * 
     * Actualiza el estado de UN item específico del pedido.
     * Esto es lo que permite que cocinero y bartender trabajen en paralelo.
     */
    async actualizarEstadoItem(pedidoId, itemId, nuevoEstado, usuarioId, usuarioNombre) {
        await this._simularDelay(400);

       // TODO: Implementar petición PATCH en el backend.

        // HOY (Simulación):
        const ahora = new Date().toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });

        console.log(`[SIMULADO] Pedido ${pedidoId} → Item ${itemId} → ${nuevoEstado} por ${usuarioNombre}`);

        return {
            exito: true,
            mensaje: `Estado actualizado a: ${nuevoEstado}`,
            timestamp: ahora
        };
    },

    /**
     * ============================================================
     *  COMPRAS (Órdenes a proveedores)
     * ============================================================
     * MAÑANA: POST ${API_BASE}/compras
     */
    async guardarCompra(datos) {
        await this._simularDelay(700);
        console.log('Orden de compra emitida (simulado):', datos);
        return { exito: true, mensaje: 'Orden de compra enviada al proveedor' };
    },

    /**
     * ============================================================
     *  CAJA (Cierre de turno)
     * ============================================================
     * MAÑANA: POST ${API_BASE}/caja/cierre
     */
    async cerrarCaja(datos) {
        await this._simularDelay(700);
        console.log('Caja cerrada (simulado):', datos);
        return { exito: true, mensaje: 'Cierre de caja registrado correctamente' };
    },

    /**
     * ============================================================
     *  USUARIOS (Personal del restaurante)
     * ============================================================
     * MAÑANA: GET ${API_BASE}/usuarios
     */
    async obtenerUsuarios() {
        await this._simularDelay(500);
        try {
            const resp = await fetch('data/usuarios.json');
            return await resp.json();
        } catch (e) {
            console.error('Error cargando usuarios:', e);
            return [];
        }
    }
};