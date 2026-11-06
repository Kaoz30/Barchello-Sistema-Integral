/**
 * ============================================================
 * UI.JS — Utilidades de Interfaz de Usuario
 * ============================================================
 * 
 * MI NOTA: Este archivo centraliza todo lo visual:
 * - Toasts (notificaciones tipo Bootstrap)
 * - Loading (estado de carga en botones)
 * - Modal dinámico de cambio de rol (se inyecta por JS)
 * 
 * VENTAJA del modal dinámico: No tengo que pegar HTML de modal
 * en los 10 archivos HTML. Se crea al vuelo y se destruye al cerrar.
 * ============================================================
 */

const UI = {

    /**
     * ============================================================
     *  MOSTRAR TOAST (Notificación)
     * ============================================================
     * Crea un contenedor de toasts si no existe, agrega el toast,
     * y lo elimina automáticamente después de 4 segundos.
     * 
     * @param {string} mensaje - Texto a mostrar
     * @param {string} tipo - 'success', 'danger', 'warning', 'info'
     */
    mostrarToast: function(mensaje, tipo = 'info') {
        // Crear contenedor si no existe
        let contenedor = document.getElementById('contenedor-toasts');
        if (!contenedor) {
            contenedor = document.createElement('div');
            contenedor.id = 'contenedor-toasts';
            contenedor.className = 'toast-container position-fixed top-0 end-0 p-3';
            contenedor.style.zIndex = '1100';
            document.body.appendChild(contenedor);
        }

        // Crear el toast
        const toastEl = document.createElement('div');
        toastEl.className = `toast align-items-center text-bg-${tipo} border-0 mb-2`;
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');
        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${mensaje}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
            </div>
        `;

        contenedor.appendChild(toastEl);

        // Mostrar con Bootstrap
        const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
        bsToast.show();

        // Limpiar del DOM cuando se oculte
        toastEl.addEventListener('hidden.bs.toast', function() {
            toastEl.remove();
        });
    },

    /**
     * ============================================================
     *  MOSTRAR LOADING EN BOTÓN
     * ============================================================
     * Deshabilita el botón y muestra un spinner + texto de carga.
     * Guarda el texto original para restaurarlo después.
     * 
     * @param {string} selector - Selector CSS del botón (ej: '#btnIngresar')
     * @param {string} texto - Texto a mostrar mientras carga
     */
    mostrarLoading: function(selector, texto = 'Procesando...') {
        const btn = document.querySelector(selector);
        if (!btn) return;

        btn.dataset.textoOriginal = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = `
            <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ${texto}
        `;
    },

    /**
     * ============================================================
     *  OCULTAR LOADING EN BOTÓN
     * ============================================================
     * Restaura el botón a su estado original.
     */
    ocultarLoading: function(selector) {
        const btn = document.querySelector(selector);
        if (!btn) return;

        btn.disabled = false;
        if (btn.dataset.textoOriginal) {
            btn.innerHTML = btn.dataset.textoOriginal;
            delete btn.dataset.textoOriginal;
        }
    },

        /**
     * ============================================================
     *  SANITIZAR TEXDO (Prevención de XSS)
     * ============================================================
     * Escapa caracteres HTML especiales para insertar texto
     * dinámico de forma segura en el DOM.
     * 
     * @param {string} str - Texto a sanitizar
     * @returns {string} - Texto seguro para innerHTML
     */
    sanitizar: function(str) {
        if (str === null || str === undefined) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return String(str).replace(/[&<>"']/g, m => map[m]);
    },

    /**
     * ============================================================
     *  MODAL DINÁMICO DE CAMBIO DE ROL
     * ... (el resto del código sigue igual)

    /**
     * ============================================================
     *  MODAL DINÁMICO DE CAMBIO DE ROL
     * ============================================================
     *  Antes el HTML buscaba un modal
     * #modalCambiarTurno que NO EXISTÍA → error en consola.
     * 
     * SOLUCIÓN: El modal se crea AQUÍ al vuelo, se muestra,
     * y se destruye al cerrarse. No necesito HTML de modal
     * en los 10 archivos.
     * 
     * @param {Object} usuario - Objeto usuario de sessionStorage
     */
    mostrarModalCambiarRol: function(usuario) {
        const modalId = 'modalCambiarRolDinamico';
        const rolActual = sessionStorage.getItem('barchello_rol_activo') || usuario.rol;

        // ==========================================
        // GENERAR OPCIONES DE ROL
        // ==========================================

        // Info visual de cada rol
        const ROLES_INFO = {
            admin:     { icono: 'bi-shield-lock',  nombre: 'Administrador', desc: 'Acceso total al sistema' },
            gerente:   { icono: 'bi-briefcase',    nombre: 'Gerente',       desc: 'Gestión general del local' },
            mesero:    { icono: 'bi-cup-hot',      nombre: 'Mesero',        desc: 'Atención a clientes y pedidos' },
            cocinero:  { icono: 'bi-fire',         nombre: 'Cocinero',      desc: 'Preparación de alimentos' },
            bartender: { icono: 'bi-cup-straw',    nombre: 'Bartender',     desc: 'Coctelería y bar' },
            cajero:    { icono: 'bi-cash-coin',    nombre: 'Cajero',        desc: 'Facturación y caja' }
        };

        // Rol principal (siempre aparece)
        const infoPrincipal = ROLES_INFO[usuario.rol] || { icono: 'bi-person', nombre: usuario.rol, desc: '' };

        let opcionesHTML = `
            <div class="rol-option ${usuario.rol === rolActual ? 'active' : ''}" 
                 data-rol="${usuario.rol}" 
                 style="display:flex;align-items:center;gap:1rem;padding:1rem;border:2px solid ${usuario.rol === rolActual ? '#8D6E63' : '#e0e0e0'};border-radius:12px;cursor:pointer;margin-bottom:0.75rem;background:${usuario.rol === rolActual ? 'rgba(239,235,233,0.5)' : '#fff'};transition:all 0.2s ease;">
                <div style="width:48px;height:48px;border-radius:50%;background:${usuario.rol === rolActual ? '#8D6E63' : '#EFEBE9'};color:${usuario.rol === rolActual ? '#fff' : '#3E2723'};display:flex;align-items:center;justify-content:center;font-size:1.5rem;flex-shrink:0;">
                    <i class="bi ${infoPrincipal.icono}"></i>
                </div>
                <div style="flex:1;">
                    <h6 style="margin:0 0 0.25rem;font-weight:600;color:#3E2723;">${infoPrincipal.nombre} <small style="color:#999;font-weight:400;">(Principal)</small></h6>
                    <small style="color:#666;">${infoPrincipal.desc}</small>
                </div>
                ${usuario.rol === rolActual ? '<i class="bi bi-check-circle-fill text-success"></i>' : ''}
            </div>
        `;

        // Roles secundarios (si existen)
        const secundarios = usuario.roles_secundarios || [];
        secundarios.forEach(rol => {
            const info = ROLES_INFO[rol] || { icono: 'bi-person', nombre: rol, desc: '' };
            const esActivo = rol === rolActual;

            opcionesHTML += `
                <div class="rol-option ${esActivo ? 'active' : ''}" 
                     data-rol="${rol}"
                     style="display:flex;align-items:center;gap:1rem;padding:1rem;border:2px solid ${esActivo ? '#8D6E63' : '#e0e0e0'};border-radius:12px;cursor:pointer;margin-bottom:0.75rem;background:${esActivo ? 'rgba(239,235,233,0.5)' : '#fff'};transition:all 0.2s ease;">
                    <div style="width:48px;height:48px;border-radius:50%;background:${esActivo ? '#8D6E63' : '#EFEBE9'};color:${esActivo ? '#fff' : '#3E2723'};display:flex;align-items:center;justify-content:center;font-size:1.5rem;flex-shrink:0;">
                        <i class="bi ${info.icono}"></i>
                    </div>
                    <div style="flex:1;">
                        <h6 style="margin:0 0 0.25rem;font-weight:600;color:#3E2723;">${info.nombre} <small style="color:#999;font-weight:400;">(Secundario)</small></h6>
                        <small style="color:#666;">${info.desc}</small>
                    </div>
                    ${esActivo ? '<i class="bi bi-check-circle-fill text-success"></i>' : ''}
                </div>
            `;
        });

        // ==========================================
        // CONSTRUIR EL MODAL COMPLETO
        // ==========================================
        const modalHTML = `
            <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content" style="border-radius:16px;border:none;">
                        <div class="modal-header" style="background:#3E2723;color:#fff;border-radius:16px 16px 0 0;">
                            <h5 class="modal-title" style="font-family:'Playfair Display',serif;font-weight:700;">
                                <i class="bi bi-arrow-repeat me-2"></i>Cambiar Rol / Turno
                            </h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                        </div>
                        <div class="modal-body">
                            <p class="text-muted mb-3">
                                Selecciona el rol que ejercerás durante este turno. 
                                Esto actualizará los módulos disponibles en el sistema.
                            </p>
                            <div id="contenedorRolesDinamico">
                                ${opcionesHTML}
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-roble" data-bs-dismiss="modal">Cancelar</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // ==========================================
        // INYECTAR EN EL DOM Y MOSTRAR
        // ==========================================
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        const modalElement = document.getElementById(modalId);
        const bsModal = new bootstrap.Modal(modalElement);
        bsModal.show();

        // ==========================================
        // LISTENERS PARA SELECCIONAR ROL
        // ==========================================
        modalElement.querySelectorAll('.rol-option').forEach(opt => {
            // Efecto hover
            opt.addEventListener('mouseenter', function() {
                if (!this.classList.contains('active')) {
                    this.style.borderColor = '#8D6E63';
                    this.style.background = 'rgba(239,235,233,0.3)';
                }
            });
            opt.addEventListener('mouseleave', function() {
                if (!this.classList.contains('active')) {
                    this.style.borderColor = '#e0e0e0';
                    this.style.background = '#fff';
                }
            });

            // Clic → cambiar rol
            opt.addEventListener('click', function() {
                const nuevoRol = this.getAttribute('data-rol');
                if (nuevoRol !== rolActual) {
                    Auth.cambiarRolActivo(nuevoRol);
                } else {
                    UI.mostrarToast('ℹ️ Ya estás actuando como ' + nuevoRol, 'info');
                }
                bsModal.hide();
            });
        });

        // ==========================================
        // LIMPIAR EL MODAL DEL DOM AL CERRARSE
        // ==========================================
        modalElement.addEventListener('hidden.bs.modal', function() {
            modalElement.remove();
        });
    }
};