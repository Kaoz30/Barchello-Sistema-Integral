/**
 * ============================================================
 * INVENTARIO.JS — Control de Inventario
 * ============================================================
 * 
 * PATRÓN API-READY:
 * HOY: Lee desde data/productos.json
 * MAÑANA: fetch(`${API_BASE}/inventario`)
 * 
 * FUNCIONALIDADES:
 * 1. Renderizar tabla dinámica desde JSON
 * 2. Filtro por categoría
 * 3. Filtro "Solo stock bajo"
 * 4. Búsqueda en vivo
 * 5. Estadísticas (KPIs)
 * 6. Badge de estado (OK / Bajo) dinámico
 * ============================================================
 */
const Inventario = {
    productos: [],
    filtros: {
        categoria: 'todas',
        soloBajo: false,
        busqueda: ''
    },

    async inicializar() {
        try {
            await this.cargarDatos();
            this.configurarListeners();
            this.renderizar();
        } catch (error) {
            UI.mostrarToast('Error cargando inventario: ' + error.message, 'danger');
        }
    },

    /**
     * Cargar datos desde inventario.json o productos.json
     * Nota: Usamos productos.json porque el archivo de inventario
     * aún no existe. Mañana habrá un inventario.json dedicado.
     */
    async cargarDatos() {
        // Simular datos de inventario basados en productos
        const productos = await API.obtenerProductos();
        
        // Mapear productos a items de inventario con stock simulado
        this.productos = productos.map(p => ({
            id: p.id,
            nombre: p.nombre,
            categoria: this.mapearCategoria(p.categoria),
            stockActual: this.generarStockAleatorio(),
            stockMinimo: this.generarStockMinimo(),
            unidad: 'unidades',
            imagen: p.imagen
        }));
    },

    /**
     * Mapear categorías del menú a categorías de inventario
     */
    mapearCategoria(catMenu) {
        const mapa = {
            'Para Comer': 'Cocina',
            'Para Picar': 'Cocina',
            'Coctelería': 'Bar',
            'Cervezas y Licores': 'Bar',
            'Bebidas': 'Bebidas',
            'Cafetería': 'Cafetería'
        };
        return mapa[catMenu] || 'General';
    },

    generarStockAleatorio() {
        return Math.floor(Math.random() * 30) + 1;
    },

    generarStockMinimo() {
        return Math.floor(Math.random() * 10) + 3;
    },

    configurarListeners() {
        // Filtros de categoría
        document.querySelectorAll('.filtro-inventario').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('.filtro-inventario').forEach(b => b.classList.remove('active'));
                e.currentTarget.classList.add('active');
                this.filtros.categoria = e.currentTarget.dataset.categoria;
                this.renderizar();
            });
        });

        // Filtro "Solo stock bajo"
        const checkBajo = document.getElementById('checkSoloBajo');
        if (checkBajo) {
            checkBajo.addEventListener('change', (e) => {
                this.filtros.soloBajo = e.target.checked;
                this.renderizar();
            });
        }

        // Búsqueda en vivo
        const inputBusqueda = document.getElementById('busquedaInventario');
        if (inputBusqueda) {
            inputBusqueda.addEventListener('input', (e) => {
                this.filtros.busqueda = e.target.value.toLowerCase();
                this.renderizar();
            });
        }

        // Botón Registrar Entrada/Salida
        const btnRegistrar = document.getElementById('btnRegistrarMovimiento');
        if (btnRegistrar) {
            btnRegistrar.addEventListener('click', (e) => {
                e.preventDefault();
                UI.mostrarToast('📦 Función de movimientos disponible cuando se conecte al backend', 'info');
            });
        }

        // Botones de editar
        document.addEventListener('click', (e) => {
            if (e.target.closest('.btn-editar-inventario')) {
                const id = parseInt(e.target.closest('.btn-editar-inventario').dataset.id);
                UI.mostrarToast(`✏️ Edición del producto #${id} disponible con backend`, 'info');
            }
        });
    },

    renderizar() {
        const tbody = document.getElementById('tbodyInventario');
        if (!tbody) return;

        const filtrados = this.aplicarFiltros();
        this.renderizarEstadisticas(filtrados);

        if (filtrados.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center py-4 text-muted">
                        <i class="bi bi-inbox display-5 d-block mb-2 opacity-50"></i>
                        No se encontraron productos con los filtros aplicados
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = filtrados.map(item => {
            const esBajo = item.stockActual < item.stockMinimo;
            const estado = esBajo 
                ? `<span class="badge bg-danger">Bajo</span>`
                : `<span class="badge bg-success">OK</span>`;
            const claseFila = esBajo ? 'table-danger-light' : '';

            return `
                <tr class="${claseFila}">
                    <td class="fw-semibold text-cuero">${item.nombre}</td>
                    <td><span class="badge bg-arena text-cuero">${item.categoria}</span></td>
                    <td>
                        <span class="${esBajo ? 'fw-bold text-danger' : 'text-cuero'}">
                            ${item.stockActual}
                        </span>
                        <small class="text-muted"> ${item.unidad}</small>
                    </td>
                    <td class="text-muted">${item.stockMinimo}</td>
                    <td>${estado}</td>
                </tr>
            `;
        }).join('');
    },

    aplicarFiltros() {
        return this.productos.filter(p => {
            // Filtro categoría
            if (this.filtros.categoria !== 'todas' && p.categoria !== this.filtros.categoria) {
                return false;
            }
            // Filtro stock bajo
            if (this.filtros.soloBajo && p.stockActual >= p.stockMinimo) {
                return false;
            }
            // Filtro búsqueda
            if (this.filtros.busqueda && !p.nombre.toLowerCase().includes(this.filtros.busqueda)) {
                return false;
            }
            return true;
        });
    },

    renderizarEstadisticas(filtrados) {
        const total = filtrados.length;
        const bajoStock = filtrados.filter(p => p.stockActual < p.stockMinimo).length;
        const ok = total - bajoStock;
        const categorias = [...new Set(filtrados.map(p => p.categoria))].length;

        const el = (id) => document.getElementById(id);
        if (el('statInvTotal')) el('statInvTotal').textContent = total;
        if (el('statInvBajo')) el('statInvBajo').textContent = bajoStock;
        if (el('statInvOk')) el('statInvOk').textContent = ok;
        if (el('statInvCategorias')) el('statInvCategorias').textContent = categorias;
    }
};