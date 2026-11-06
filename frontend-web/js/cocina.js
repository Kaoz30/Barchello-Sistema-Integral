/**
 * ============================================================
 * COCINA.JS — Panel de Cocina & Bar (Kanban de Pedidos)
 * ============================================================
 * 
 * PATRÓN API-READY:
 * HOY: Lee desde data/pedidos.json (archivo local)
 * MAÑANA: Cambiar fetch local → fetch(`${API_BASE}/pedidos`)
 * 
 * FUNCIONALIDADES:
 * 1. Carga pedidos desde API y los agrupa por estado
 * 2. Renderiza cada pedido como tarjeta con sus items
 * 3. Permite cambiar el estado de cada item individual
 * 4. Filtrado por categoría: Todos / Solo Comida / Solo Bebida
 * 5. Muestra tiempo transcurrido desde la orden
 * 6. Badges de color según urgencia (tiempo)
 * ============================================================
 */
const Cocina = {
    // ESTADO INTERNO
    pedidos: [],
    filtroCategoria: 'todas', // 'todas' | 'comida' | 'bebida'
    timerInterval: null,       // Para actualizar tiempos cada minuto

    /**
     * FUNCIÓN PRINCIPAL: Inicializar el módulo
     */
    async inicializar() {
        try {
            UI.mostrarToast('🍳 Cargando panel de cocina...', 'info');
            await this.cargarPedidos();
            this.configurarListeners();
            this.renderizar();
            this.iniciarTimerTiempos();
        } catch (error) {
            UI.mostrarToast('Error cargando pedidos: ' + error.message, 'danger');
        }
    },

    /**
     * CARGAR PEDIDOS (API-Ready)
     * MAÑANA: const resp = await fetch(`${API_BASE}/pedidos/activos`);
     */
    async cargarPedidos() {
        const pedidos = await API.obtenerPedidosActivos();
        this.pedidos = pedidos;
    },

    /**
     * CONFIGURAR LISTENERS
     */
    configurarListeners() {
        // Filtros de categoría
        document.querySelectorAll('.filtro-cocina').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('.filtro-cocina').forEach(b => b.classList.remove('active'));
                e.currentTarget.classList.add('active');
                this.filtroCategoria = e.currentTarget.dataset.categoria;
                this.renderizar();
            });
        });

        // Refrescar datos
        const btnRefrescar = document.getElementById('btnRefrescarCocina');
        if (btnRefrescar) {
            btnRefrescar.addEventListener('click', async () => {
                UI.mostrarLoading('#btnRefrescarCocina', 'Actualizando...');
                await this.cargarPedidos();
                this.renderizar();
                UI.ocultarLoading('#btnRefrescarCocina');
                UI.mostrarToast('✅ Pedidos actualizados', 'success');
            });
        }
    },

    /**
     * INICIAR TIMER: actualiza tiempos transcurridos cada minuto
     */
    iniciarTimerTiempos() {
        if (this.timerInterval) clearInterval(this.timerInterval);
        this.timerInterval = setInterval(() => this.actualizarTiempos(), 60000);
    },

    /**
     * RENDERIZAR: Vista principal tipo Kanban
     */
    renderizar() {
        const contenedor = document.getElementById('gridCocina');
        if (!contenedor) return;

        // Agrupar pedidos por estado
        const grupos = {
            pendiente: [],
            en_preparacion: [],
            listo_para_servir: []
        };

        this.pedidos.forEach(p => {
            if (grupos[p.estado]) grupos[p.estado].push(p);
        });

        // Renderizar 3 columnas Kanban
        contenedor.innerHTML = `
            ${this.renderColumna('pendiente', 'Recibidos', 'bg-cuero', grupos.pendiente)}
            ${this.renderColumna('en_preparacion', 'En Preparación', 'bg-roble', grupos.en_preparacion)}
            ${this.renderColumna('listo_para_servir', 'Listos para Servir', 'bg-success', grupos.listo_para_servir)}
        `;

        // Conectar listeners de cambio de estado en items
        this.conectarListenersItems();
    },

    /**
     * RENDERIZAR UNA COLUMNA DEL KANBAN
     */
    renderColumna(estado, titulo, colorHeader, pedidos) {
        const pedidosFiltrados = this.filtrarPorCategoria(pedidos);
        const contador = pedidosFiltrados.length;

        return `
            <div class="col-12 col-lg-4">
                <div class="kanban-columna">
                    <div class="kanban-header ${colorHeader} text-white p-3 rounded-top">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="mb-0 fw-bold">
                                <i class="bi bi-${this.getIconoEstado(estado)} me-2"></i>${titulo}
                            </h5>
                            <span class="badge bg-white ${colorHeader === 'bg-success' ? 'text-success' : 'text-' + colorHeader.replace('bg-', '')} rounded-pill fs-6">
                                ${contador}
                            </span>
                        </div>
                    </div>
                    <div class="kanban-body p-3">
                        ${pedidosFiltrados.length === 0
                            ? `<div class="text-center text-muted py-5">
                                    <i class="bi bi-inbox display-4 d-block mb-2 opacity-50"></i>
                                    <small>Sin pedidos</small>
                               </div>`
                            : pedidosFiltrados.map(p => this.renderTarjetaPedido(p)).join('')
                        }
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * RENDERIZAR TARJETA DE PEDIDO (Ticket de cocina)
     */
    renderTarjetaPedido(pedido) {
        const tiempoTranscurrido = this.calcularTiempo(pedido.hora);
        const urgencia = this.getUrgencia(pedido.hora);
        const itemsFiltrados = this.filtrarItemsPorCategoria(pedido.items);

        if (itemsFiltrados.length === 0) return ''; // No mostrar si no hay items para el filtro

        return `
            <article class="card shadow-sm mb-3 tarjeta-pedido" data-pedido-id="${pedido.id}">
                <div class="card-header d-flex justify-content-between align-items-center bg-white border-bottom">
                    <div>
                        <span class="fw-bold text-cuero">Mesa ${pedido.mesa}</span>
                        <small class="text-muted d-block">#${pedido.id} · ${pedido.hora}</small>
                    </div>
                    <span class="badge ${urgencia.clase}">${tiempoTranscurrido}</span>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        ${itemsFiltrados.map(item => this.renderItem(item, pedido.id)).join('')}
                    </ul>
                </div>
            </article>
        `;
    },

    /**
     * RENDERIZAR UN ITEM DEL PEDIDO
     */
    renderItem(item, pedidoId) {
        // Determinar acción según estado
        let botonAccion = '';
        const usuario = Auth.obtenerUsuarioActual();
        const rolActivo = sessionStorage.getItem('barchello_rol_activo') || (usuario ? usuario.rol : '');

        // Cocinero solo ve comida, bartender solo ve bebida
        const puedeVer = (item.categoria === 'comida' && (rolActivo === 'cocinero' || rolActivo === 'admin' || rolActivo === 'gerente'))
                      || (item.categoria === 'bebida' && (rolActivo === 'bartender' || rolActivo === 'admin' || rolActivo === 'gerente'));

        if (item.estado === 'listo') {
            botonAccion = `<span class="badge bg-success-subtle text-success"><i class="bi bi-check-circle-fill"></i> Listo</span>`;
        } else if (puedeVer) {
            if (item.estado === 'pendiente') {
                botonAccion = `
                    <button class="btn btn-sm btn-roble btn-accion-item" 
                            data-pedido="${pedidoId}" data-item="${item.id_item}" data-accion="en_preparacion">
                        <i class="bi bi-hourglass-split"></i> Iniciar
                    </button>`;
            } else if (item.estado === 'en_preparacion') {
                botonAccion = `
                    <button class="btn btn-sm btn-success btn-accion-item" 
                            data-pedido="${pedidoId}" data-item="${item.id_item}" data-accion="listo">
                        <i class="bi bi-check-lg"></i> Listo
                    </button>`;
            }
        } else {
            botonAccion = `<span class="text-muted small"><i class="bi bi-person-lock"></i> Otro rol</span>`;
        }

        const iconoCategoria = item.categoria === 'comida' ? 'bi-egg-fried' : 'bi-cup-hot';
        const asignado = item.asignado_nombre 
            ? `<small class="text-muted d-block"><i class="bi bi-person-fill"></i> ${item.asignado_nombre}</small>` 
            : '';

        return `
            <li class="list-group-item d-flex justify-content-between align-items-center item-pedido">
                <div class="flex-grow-1">
                    <div class="d-flex align-items-start gap-2">
                        <i class="bi ${iconoCategoria} text-roble mt-1"></i>
                        <div>
                            <div class="fw-semibold text-cuero">${item.cantidad}x ${item.producto}</div>
                            ${asignado}
                        </div>
                    </div>
                </div>
                <div class="ms-2">
                    ${botonAccion}
                </div>
            </li>
        `;
    },

    /**
     * CONECTAR LISTENERS A BOTONES DE ITEMS
     */
    conectarListenersItems() {
        document.querySelectorAll('.btn-accion-item').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const pedidoId = parseInt(btn.dataset.pedido);
                const itemId = parseInt(btn.dataset.item);
                const nuevoEstado = btn.dataset.accion;

                UI.mostrarLoading(`[data-pedido="${pedidoId}"][data-item="${itemId}"]`, '...');

                const usuario = Auth.obtenerUsuarioActual();
                try {
                    await API.actualizarEstadoItem(
                        pedidoId, 
                        itemId, 
                        nuevoEstado,
                        usuario.id,
                        usuario.nombre
                    );

                    // Actualizar estado local
                    const pedido = this.pedidos.find(p => p.id === pedidoId);
                    const item = pedido.items.find(i => i.id_item === itemId);
                    item.estado = nuevoEstado;
                    item.asignado_nombre = usuario.nombre;
                    item.hora_fin = nuevoEstado === 'listo' ? new Date().toLocaleTimeString() : null;

                    UI.mostrarToast(`✅ Item actualizado: ${item.producto}`, 'success');
                    this.renderizar();
                } catch (error) {
                    UI.mostrarToast('Error: ' + error.message, 'danger');
                    UI.ocultarLoading(`[data-pedido="${pedidoId}"][data-item="${itemId}"]`);
                }
            });
        });
    },

    /**
     * FILTRAR PEDIDOS por categoría (mostrar solo si tiene items de esa categoría)
     */
    filtrarPorCategoria(pedidos) {
        if (this.filtroCategoria === 'todas') return pedidos;
        return pedidos.filter(p => 
            p.items.some(item => item.categoria === this.filtroCategoria)
        );
    },

    /**
     * FILTRAR ITEMS de un pedido específico
     */
    filtrarItemsPorCategoria(items) {
        if (this.filtroCategoria === 'todas') return items;
        return items.filter(item => item.categoria === this.filtroCategoria);
    },

    /**
     * CALCULAR TIEMPO TRANSCURRIDO desde la hora del pedido
     */
    calcularTiempo(horaPedido) {
        const [h, m] = horaPedido.split(':').map(Number);
        const ahora = new Date();
        const pedido = new Date();
        pedido.setHours(h, m, 0, 0);
        
        const diffMs = ahora - pedido;
        const minutos = Math.floor(diffMs / 60000);
        
        if (minutos < 0) return 'Recién';
        if (minutos < 60) return `${minutos} min`;
        const horas = Math.floor(minutos / 60);
        return `${horas}h ${minutos % 60}m`;
    },

    /**
     * DETERMINAR URGENCIA por tiempo (colores de badge)
     */
    getUrgencia(horaPedido) {
        const [h, m] = horaPedido.split(':').map(Number);
        const ahora = new Date();
        const pedido = new Date();
        pedido.setHours(h, m, 0, 0);
        const minutos = Math.floor((ahora - pedido) / 60000);

        if (minutos > 45) return { clase: 'bg-danger' };          // Crítico
        if (minutos > 20) return { clase: 'bg-warning text-dark' }; // Atención
        return { clase: 'bg-success' };                             // Normal
    },

    /**
     * ACTUALIZAR SOLO LOS TIEMPOS sin re-renderizar todo
     */
    actualizarTiempos() {
        document.querySelectorAll('.tarjeta-pedido').forEach(card => {
            const pedidoId = parseInt(card.dataset.pedidoId);
            const pedido = this.pedidos.find(p => p.id === pedidoId);
            if (pedido) {
                const badgeTiempo = card.querySelector('.badge[class*="bg-"]');
                if (badgeTiempo) {
                    const urgencia = this.getUrgencia(pedido.hora);
                    badgeTiempo.className = `badge ${urgencia.clase}`;
                    badgeTiempo.textContent = this.calcularTiempo(pedido.hora);
                }
            }
        });
    },

    getIconoEstado(estado) {
        const iconos = {
            'pendiente': 'clock',
            'en_preparacion': 'fire',
            'listo_para_servir': 'check-circle-fill'
        };
        return iconos[estado] || 'circle';
    }
};