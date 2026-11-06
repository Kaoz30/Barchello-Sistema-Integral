/**
 * ============================================================
 * MENU.JS — MÓDULO DE GESTIÓN DEL MENÚ (CRUD COMPLETO)
 * ============================================================
 * Este módulo se conecta directamente con los endpoints de 
 * la API REST definidos en api.js.
 */

const Menu = {
    //  ESTADO INTERNO DEL MÓDULO
    productos: [],              // Array con todos los productos cargados
    categoriaFiltro: 'todas',   // Categoría seleccionada actualmente

    /**
     *  FUNCIÓN PRINCIPAL: Inicializar el módulo
     * 
     * FLUJO:
     * 1. Muestra toast de "Cargando..."
     * 2. Carga productos desde JSON
     * 3. Configura todos los listeners
     * 4. Renderiza la vista
     */
    async inicializar() {
        try {
            UI.mostrarToast('Cargando menú...', 'info');
            await this.cargarProductos();
            this.configurarListeners();
            this.renderizar();
        } catch (error) {
            UI.mostrarToast('Error cargando el menú: ' + error.message, 'danger');
        }
    },

    /**
     *  CARGAR PRODUCTOS (API-Ready)
     * 
     */
    async cargarProductos() {
        // Consumo directo de la API real
        this.productos = await API.obtenerProductos();
    },

    /**
     *  CONFIGURAR TODOS LOS EVENT LISTENERS DEL MÓDULO
     * 
     * Aquí conecto todas las interacciones del usuario:
     * - Filtros de categoría
     * - Botón guardar producto
     * - Reset del formulario al abrir modal
     * - Modal de cambio de rol/turno
     */
    configurarListeners() {
        // ==========================================
        //  LISTENER 1: Filtros de categoría
        // ==========================================
        document.querySelectorAll('.filtro-categoria').forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Quitar active de todos los botones
                document.querySelectorAll('.filtro-categoria').forEach(b => b.classList.remove('active'));
                // Agregar active al botón clickeado
                e.currentTarget.classList.add('active');
                // Actualizar filtro y re-renderizar
                this.categoriaFiltro = e.currentTarget.dataset.categoria;
                this.renderizar();
            });
        });

        // ==========================================
        //  LISTENER 2: Botón "Guardar Producto"
        // ==========================================
        const btnGuardar = document.getElementById('btnGuardarProducto');
        if (btnGuardar) {
            btnGuardar.addEventListener('click', () => this.guardarProducto());
        }

        // ==========================================
        //  LISTENER 3: Resetear formulario al abrir modal
        // ==========================================
        const modalProducto = document.getElementById('modalProducto');
        if (modalProducto) {
            modalProducto.addEventListener('show.bs.modal', () => {
                document.getElementById('formProducto').reset();
                document.getElementById('productoId').value = '';
                document.getElementById('productoDisponible').checked = true;
                document.getElementById('modalProductoLabel').innerHTML =
                    '<i class="bi bi-plus-circle me-2"></i>Nuevo Producto';
            });
        }

        // =========================================
        //  LISTENER 4: Modal de cambio de rol/turno
        // ==========================================
        const modalTurno = document.getElementById('modalCambiarTurno');
        if (modalTurno) {
            modalTurno.addEventListener('show.bs.modal', () => this.renderizarModalRoles());
        }

        const btnConfirmarRol = document.getElementById('btnConfirmarCambioRol');
        if (btnConfirmarRol) {
            btnConfirmarRol.addEventListener('click', () => this.confirmarCambioRol());
        }
    },

    /**
     *  RENDERIZAR: Grid de productos + estadísticas
     * 
     * Esta función se llama cada vez que:
     * - Se carga el módulo por primera vez
     * - Se cambia el filtro de categoría
     * - Se crea/edita/elimina un producto
     * - Se cambia el estado disponible/agotado
     */
    renderizar() {
        const grid = document.getElementById('gridProductos');
        if (!grid) return;

        // Filtro adaptado a la relación de Java (categoria.nombre)
        const filtrados = this.categoriaFiltro === 'todas'
            ? this.productos
            : this.productos.filter(p => p.categoria && p.categoria.nombre === this.categoriaFiltro);

        // KPIs adaptados a 'estado'
        document.getElementById('statTotal').textContent = this.productos.length;
        document.getElementById('statDisponibles').textContent = this.productos.filter(p => p.estado === 'activo').length;
        document.getElementById('statAgotados').textContent = this.productos.filter(p => p.estado !== 'activo').length;
        const categorias = [...new Set(this.productos.map(p => p.categoria ? p.categoria.nombre : 'Sin categoría'))];
        document.getElementById('statCategorias').textContent = categorias.length;

        if (filtrados.length === 0) {
            grid.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="bi bi-inbox display-1 text-muted"></i>
                    <p class="mt-3 text-muted">No hay productos en esta categoría</p>
                </div>
            `;
            return;
        }

        grid.innerHTML = filtrados.map(p => this.crearCardProducto(p)).join('');

        // Listeners pasan el 'idProducto' (nombre en Java)
        grid.querySelectorAll('.btn-editar-producto').forEach(btn => {
            btn.addEventListener('click', () => this.editarProducto(parseInt(btn.dataset.id)));
        });
        grid.querySelectorAll('.btn-toggle-disponible').forEach(btn => {
            btn.addEventListener('click', () => this.toggleDisponible(parseInt(btn.dataset.id)));
        });
    },

    /**
     *  CREAR HTML DE UNA TARJETA DE PRODUCTO
     * 
     * @param {Object} p - Objeto producto con {id, nombre, precio, categoria, imagen, disponible, descripcion}
     * @returns {String} HTML de la tarjeta
     */
    /**

     /**
     *  CREAR HTML DE UNA TARJETA DE PRODUCTO (Diseño Premium Barchello)
     *  Aplicando sanitización XSS y estética de carta de restaurante.
     */
    crearCardProducto(p) {
        // 🛡️ Sanitizar datos dinámicos para prevenir XSS
        const nombre = UI.sanitizar(p.nombre);
        const descripcion = UI.sanitizar(p.descripcion || 'Sin descripción');
        // Adaptación a la relación ManyToOne de Java
        const categoria = UI.sanitizar(p.categoria ? p.categoria.nombre : 'Sin categoría');
        const imagen = UI.sanitizar(p.imagenUrl || `https://via.placeholder.com/300x200/8D6E63/FFFFFF?text=${encodeURIComponent(p.nombre)}`);

        // El backend usa 'estado' (activo/inactivo)
        const esDisponible = p.estado === 'activo';

        // Badge de disponibilidad
        const badgeDisponible = esDisponible
            ? '<span class="badge producto-badge bg-success"><i class="bi bi-check-circle-fill me-1"></i>Disponible</span>'
            : '<span class="badge producto-badge bg-secondary"><i class="bi bi-pause-circle-fill me-1"></i>Agotado</span>';

        // Formatear precio en pesos colombianos
        const precioFormateado = new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0
        }).format(p.precioVenta); // Cambio: precioVenta

        return `
            <div class="col-sm-6 col-md-4 col-lg-3">
                <article class="card shadow-sm h-100 producto-card border-0" aria-label="Producto ${nombre}">
                    <div class="producto-imagen-wrapper">
                        <img src="${imagen}" 
                             alt="${nombre}"
                             class="producto-imagen"
                             loading="lazy"
                             onerror="this.src='https://via.placeholder.com/300x200/8D6E63/FFFFFF?text=Error'">
                        <div class="producto-overlay"></div>
                        <div class="producto-badge-position">${badgeDisponible}</div>
                    </div>
                    
                    <div class="card-body d-flex flex-column p-3">
                        <div class="d-flex justify-content-between align-items-start mb-1">
                            <h5 class="card-title mb-0 text-cuero producto-nombre" title="${nombre}">${nombre}</h5>
                        </div>
                        <span class="badge bg-arena-light text-roble align-self-start mb-2 producto-categoria">${categoria}</span>
                        <p class="card-text small text-muted flex-grow-1 producto-descripcion">${descripcion}</p>
                        
                        <div class="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
                            <span class="fw-bold text-roble producto-precio">${precioFormateado}</span>
                            <div class="btn-group btn-group-sm" role="group" aria-label="Acciones del producto">
                                <button class="btn btn-outline-roble btn-editar-producto" 
                                        data-id="${p.idProducto}" 
                                        title="Editar ${nombre}" 
                                        aria-label="Editar ${nombre}">
                                    <i class="bi bi-pencil-square"></i>
                                </button>
                                <button class="btn ${esDisponible ? 'btn-outline-secondary' : 'btn-outline-success'} btn-toggle-disponible" 
                                        data-id="${p.idProducto}" 
                                        title="${esDisponible ? 'Marcar como agotado' : 'Marcar como disponible'}"
                                        aria-label="${esDisponible ? 'Marcar como agotado' : 'Marcar como disponible'}">
                                    <i class="bi ${esDisponible ? 'bi-eye-slash' : 'bi-eye'}"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </article>
            </div>
        `;
    },

    /**
     *  EDITAR PRODUCTO: Abre el modal con los datos cargados
     * 
     * @param {Number} id - ID del producto a editar
     */
    editarProducto(id) {
        const producto = this.productos.find(p => p.idProducto === id);
        if (!producto) return;

        // Llenar el formulario con los datos del producto
        document.getElementById('productoId').value = producto.idProducto;
        document.getElementById('productoNombre').value = producto.nombre;
        document.getElementById('productoPrecio').value = producto.precioVenta;
        document.getElementById('productoDescripcion').value = producto.descripcion || '';
        document.getElementById('productoCategoria').value = producto.categoria ? producto.categoria.nombre : '';
        document.getElementById('productoImagen').value = producto.imagenUrl || '';
        document.getElementById('productoDisponible').checked = (producto.estado === 'activo');

        // Cambiar título del modal
        document.getElementById('modalProductoLabel').innerHTML = '<i class="bi bi-pencil me-2"></i>Editar Producto';

        // Abrir el modal
        const modal = new bootstrap.Modal(document.getElementById('modalProducto'));
        modal.show();
    },

    /**
     *  GUARDAR PRODUCTO (Crear o Actualizar)
     * 
     * FLUJO:
     * 1. Lee los datos del formulario
     * 2. Valida campos obligatorios
     * 3. Si hay ID → actualiza producto existente
     * 4. Si no hay ID → crea producto nuevo con ID autoincremental
     * 5. Cierra el modal y re-renderiza
     * 
     * TODO: Implementar petición PUT para edición.
     */
    async guardarProducto() {
        const id = document.getElementById('productoId').value;

        const datos = {
            nombre: document.getElementById('productoNombre').value.trim(),
            precio: parseInt(document.getElementById('productoPrecio').value) || 0,
            descripcion: document.getElementById('productoDescripcion').value.trim(),
            categoria: document.getElementById('productoCategoria').value,
            imagen: document.getElementById('productoImagen').value.trim(),
            disponible: document.getElementById('productoDisponible').checked
        };

        if (!datos.nombre || !datos.precio || !datos.categoria) {
            UI.mostrarToast('⚠️ Completa los campos obligatorios', 'warning');
            return;
        }

        try {
            if (!id) {
                await API.guardarProducto(datos);
                UI.mostrarToast('✅ Producto guardado en la base de datos', 'success');
                await this.cargarProductos();
                bootstrap.Modal.getInstance(document.getElementById('modalProducto')).hide();
                this.renderizar();
            } else {
                UI.mostrarToast('Edición pendiente de conectar al backend', 'info');
            }
        } catch (error) {
            UI.mostrarToast('Error al guardar en el servidor', 'danger');
        }
    },

    /**
     *  TOGGLE DISPONIBLE/AGOTADO
     * 
     * Cambia el estado de un producto (disponible ↔ agotado)
     * 
     * @param {Number} id - ID del producto
     * 
     * TODO: Conectar con la API para persistir el estado.
     */
    toggleDisponible(id) {
        const producto = this.productos.find(p => p.idProducto === id);
        if (!producto) return;

        // Simulo el cambio de estado en el frontend (Pendiente conectar PATCH al backend)
        producto.estado = producto.estado === 'activo' ? 'inactivo' : 'activo';

        UI.mostrarToast(
            producto.estado === 'activo' ? `✅ ${producto.nombre} disponible` : `⏸️ ${producto.nombre} agotado`,
            producto.estado === 'activo' ? 'success' : 'warning'
        );
        this.renderizar();
    },

    /**
     *  RENDERIZAR MODAL DE CAMBIO DE ROL/TURNO
     * 
     * Muestra todos los roles que el usuario tiene asignados
     * (rol principal + roles secundarios) y permite elegir uno
     */
    renderizarModalRoles() {
        const usuario = Auth.obtenerUsuarioActual();
        if (!usuario) return;

        const contenedor = document.getElementById('contenedorRoles');
        const rolActivo = sessionStorage.getItem('barchello_rol_activo') || usuario.rol;

        // Roles disponibles = rol principal + secundarios
        const rolesDisponibles = [usuario.rol, ...(usuario.roles_secundarios || [])];

        // Info de cada rol (icono, nombre, descripción)
        const ROLES_INFO = {
            admin: { icono: 'bi-shield-lock', nombre: 'Administrador', desc: 'Acceso total al sistema' },
            gerente: { icono: 'bi-briefcase', nombre: 'Gerente', desc: 'Gestión general del local' },
            mesero: { icono: 'bi-cup-hot', nombre: 'Mesero', desc: 'Atención a clientes y pedidos' },
            cocinero: { icono: 'bi-fire', nombre: 'Cocinero', desc: 'Preparación de alimentos' },
            bartender: { icono: 'bi-cup-straw', nombre: 'Bartender', desc: 'Coctelería y bar' },
            cajero: { icono: 'bi-cash-coin', nombre: 'Cajero', desc: 'Facturación y caja' }
        };

        // Generar HTML de las opciones de rol
        contenedor.innerHTML = rolesDisponibles.map(rol => {
            const info = ROLES_INFO[rol] || { icono: 'bi-person', nombre: rol, desc: '' };
            const activo = rol === rolActivo ? 'active' : '';
            return `
                <div class="rol-option ${activo}" data-rol="${rol}">
                    <div class="rol-option-icon">
                        <i class="bi ${info.icono}"></i>
                    </div>
                    <div class="rol-option-info">
                        <h6>${info.nombre}</h6>
                        <small>${info.desc}</small>
                    </div>
                    ${rol === rolActivo ? '<i class="bi bi-check-circle-fill text-success ms-auto"></i>' : ''}
                </div>
            `;
        }).join('');

        // Listeners para seleccionar rol
        contenedor.querySelectorAll('.rol-option').forEach(opt => {
            opt.addEventListener('click', () => {
                contenedor.querySelectorAll('.rol-option').forEach(o => o.classList.remove('active'));
                opt.classList.add('active');
            });
        });
    },

    /**
     *  CONFIRMAR CAMBIO DE ROL
     * 
     * Lee el rol seleccionado y lo pasa a Auth.cambiarRolActivo()
     * que se encarga de guardar en sessionStorage y recargar la página
     */
    confirmarCambioRol() {
        const seleccion = document.querySelector('.rol-option.active');
        if (!seleccion) {
            UI.mostrarToast('Selecciona un rol', 'warning');
            return;
        }

        const nuevoRol = seleccion.dataset.rol;
        Auth.cambiarRolActivo(nuevoRol);

        // Cerrar el modal
        bootstrap.Modal.getInstance(document.getElementById('modalCambiarTurno')).hide();
    }
};