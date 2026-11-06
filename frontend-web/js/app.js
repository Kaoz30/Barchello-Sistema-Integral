/**
 * ============================================================
 * APP.JS — Controlador Principal (Inicialización)
 * ============================================================
 * 
 * MI NOTA: Este es el "director de orquesta".
 * Se ejecuta cuando carga CUALQUIER página del sistema.
 * 
 * ORDEN DE EJECUCIÓN (NO CAMBIAR):
 * 1. Proteger rutas y aplicar roles RBAC
 * 2. Inicializar validaciones de formularios
 * 3. Configurar listeners globales (logout, perfil, config, cambiar rol)
 * 4. Cargar datos dinámicos específicos por módulo
 * 5. Configurar interacciones UI (toggle password)
 * ============================================================
 */

document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // PASO 1: PROTEGER RUTAS Y APLICAR ROLES
    // ==========================================
    //Auth.protegerRutaYAplicarRoles();

    // ==========================================
    // PASO 2: INICIALIZAR VALIDACIONES
    // ==========================================
    if (typeof Validaciones !== 'undefined' && typeof Validaciones.inicializar === 'function') {
        Validaciones.inicializar();
    }

    // ==========================================
    // PASO 3: LISTENERS GLOBALES
    // ==========================================

    // 3.1 Botón "Cerrar Sesión"
    const btnLogout = document.getElementById('btnCerrarSesion');
    if (btnLogout) {
        btnLogout.addEventListener('click', (e) => {
            e.preventDefault();
            Auth.cerrarSesion();
        });
    }

    // 3.2 Botón "Mi Perfil" (preparado para backend)
    const btnMiPerfil = document.getElementById('btnMiPerfil');
    if (btnMiPerfil) {
        btnMiPerfil.addEventListener('click', (e) => {
            e.preventDefault();
            UI.mostrarToast('👤 Función "Mi Perfil" preparada para el backend', 'info');
        });
    }

    // 3.3 Botón "Configuración" (solo admin/gerente)
    const btnConfig = document.getElementById('btnConfiguracion');
    if (btnConfig) {
        btnConfig.addEventListener('click', (e) => {
            e.preventDefault();
            UI.mostrarToast('⚙️ Configuración disponible solo para Admin/Gerente', 'info');
        });
    }

    // 3.4  FIX: Botón "Cambiar Rol / Turno"
    // Este era el que NO funcionaba. Ahora llama al modal dinámico.
    const btnCambiarTurno = document.getElementById('btnCambiarTurno');
    if (btnCambiarTurno) {
        btnCambiarTurno.addEventListener('click', (e) => {
            e.preventDefault();
            const usuario = Auth.obtenerUsuarioActual();

            if (!usuario) {
                UI.mostrarToast('No hay sesión activa', 'warning');
                return;
            }

            // Verificar si tiene roles secundarios
            const secundarios = usuario.roles_secundarios || [];

            if (secundarios.length > 0) {
                // Tiene roles secundarios → mostrar modal
                UI.mostrarModalCambiarRol(usuario);
            } else {
                // No tiene roles secundarios → informar
                UI.mostrarToast('ℹ️ Tu cuenta no tiene roles secundarios asignados. Contacta al administrador.', 'info');
            }
        });
    }

    // ==========================================
    // PASO 3.5: OFFCANVAS — Interacciones
    // ==========================================

    // 3.5.1 Marcar el link activo del Offcanvas según la página actual
    const paginaActualOffcanvas = window.location.pathname.split('/').pop() || 'dashboard.html';
    document.querySelectorAll('.offcanvas-nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href === paginaActualOffcanvas) {
            link.classList.add('active');
        }
    });

    // 3.5.2 Cuando se abre el Offcanvas:
    //   - Cerrar cualquier dropdown abierto (evitar cruces)
    //   - Bloquear scroll del body (refuerzo)
    const offcanvasMenuEl = document.getElementById('offcanvasMenu');
    if (offcanvasMenuEl) {
        offcanvasMenuEl.addEventListener('show.bs.offcanvas', function () {
            // Cerrar TODOS los dropdowns abiertos (más agresivo)
            const dropdownsAbiertos = document.querySelectorAll('.dropdown-menu.show');
            dropdownsAbiertos.forEach(menu => {
                const dropdownInstance = bootstrap.Dropdown.getInstance(menu.previousElementSibling);
                if (dropdownInstance) {
                    dropdownInstance.hide();
                }
            });
            
            // Bloquear scroll
            document.body.classList.add('offcanvas-open');
        });

        offcanvasMenuEl.addEventListener('hidden.bs.offcanvas', function () {
            document.body.classList.remove('offcanvas-open');
        });
        
        // NUEVO: Cerrar offcanvas al hacer click en un enlace de navegación
        offcanvasMenuEl.querySelectorAll('.offcanvas-nav-link:not(.disabled-future)').forEach(link => {
            link.addEventListener('click', () => {
                const offcanvasInstance = bootstrap.Offcanvas.getInstance(offcanvasMenuEl);
                if (offcanvasInstance) {
                    offcanvasInstance.hide();
                }
            });
        });
    }

    // 3.5.2b MORPH DEL DISPARADOR (grid ↔ X)
    const iconoTrigger = document.getElementById('iconoMenuTrigger');
    if (offcanvasMenuEl && iconoTrigger) {
        const btnTrigger = iconoTrigger.closest('.btn-hamburger');
        offcanvasMenuEl.addEventListener('show.bs.offcanvas', () => {
            iconoTrigger.classList.replace('bi-grid-3x3-gap-fill', 'bi-x-lg');
            btnTrigger.classList.add('icono-abierto');
            btnTrigger.setAttribute('aria-expanded', 'true');
        });
        offcanvasMenuEl.addEventListener('hidden.bs.offcanvas', () => {
            iconoTrigger.classList.replace('bi-x-lg', 'bi-grid-3x3-gap-fill');
            btnTrigger.classList.remove('icono-abierto');
            btnTrigger.setAttribute('aria-expanded', 'false');
        });
    }

    // ==========================================
    // PASO 3.5.2c: SINCRONIZACIÓN OFFCANVAS <-> DROPDOWN
    // ==========================================
    const userDropdownEl = document.getElementById('userDropdown');

    // Si abro el Dropdown de usuario → Cierro el Offcanvas
    if (userDropdownEl && offcanvasMenuEl) {
        userDropdownEl.addEventListener('show.bs.dropdown', function () {
            const offcanvasInstance = bootstrap.Offcanvas.getInstance(offcanvasMenuEl);
            if (offcanvasInstance) {
                offcanvasInstance.hide();
            }
        });
    }

    // Si abro el Offcanvas → Cierro TODOS los Dropdowns (refuerzo de seguridad)
    if (offcanvasMenuEl) {
        offcanvasMenuEl.addEventListener('show.bs.offcanvas', function () {
            const dropdownsAbiertos = document.querySelectorAll('.dropdown-menu.show');
            dropdownsAbiertos.forEach(menu => {
                // Bootstrap 5 asocia la instancia al botón trigger, no al menú ul
                const trigger = menu.previousElementSibling || menu.parentElement;
                const dropdownInstance = bootstrap.Dropdown.getInstance(trigger);
                if (dropdownInstance) {
                    dropdownInstance.hide();
                }
            });
            document.body.classList.add('offcanvas-open');
        });
    }

    // 3.5.3 Cerrar sesión desde el Offcanvas
    const btnLogoutOffcanvas = document.getElementById('btnCerrarSesionOffcanvas');
    if (btnLogoutOffcanvas) {
        btnLogoutOffcanvas.addEventListener('click', (e) => {
            e.preventDefault();
            Auth.cerrarSesion();
        });
    }

    // 3.5.4 Actualizar info del usuario en el Offcanvas
    const usuarioOffcanvas = Auth.obtenerUsuarioActual();
    if (usuarioOffcanvas) {
        const inicial = (usuarioOffcanvas.nombre || 'U').charAt(0).toUpperCase();
        const elAvatar = document.getElementById('userAvatarOffcanvas');
        const elName = document.getElementById('userNameOffcanvas');
        const elRole = document.getElementById('userRoleOffcanvas');
        if (elAvatar) elAvatar.textContent = inicial;
        if (elName) elName.textContent = usuarioOffcanvas.nombre;
        if (elRole) elRole.textContent = sessionStorage.getItem('barchello_rol_activo') || usuarioOffcanvas.rol;
    }


    // ==========================================
    // PASO 4: CARGAR DATOS POR MÓDULO
    // ==========================================

    // 4.1 Dashboard → cargar KPIs
    if (window.location.pathname.endsWith('dashboard.html')) {
        cargarKPIDashboard();
    }

    // 4.2 Menú → inicializar CRUD
    if (window.location.pathname.endsWith('menu.html')) {
        if (typeof Menu !== 'undefined' && typeof Menu.inicializar === 'function') {
            Menu.inicializar();
        }
    }

    // 4.3 Cocina → inicializar panel
    if (window.location.pathname.endsWith('cocina.html')) {
        if (typeof Cocina !== 'undefined' && typeof Cocina.inicializar === 'function') {
            Cocina.inicializar();
        }
    }

      // 4.4 Inventario → inicializar CRUD
    if (window.location.pathname.endsWith('inventario.html')) {
        if (typeof Inventario !== 'undefined' && typeof Inventario.inicializar === 'function') {
            Inventario.inicializar();
        }
    }

    // ==========================================
    // PASO 5: INTERACCIONES DE UI
    // ==========================================

    // 5.1 Toggle de contraseña en el login
    const togglePassword = document.getElementById('togglePassword');
    if (togglePassword) {
        togglePassword.addEventListener('click', function() {
            const passwordInput = document.getElementById('password');
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                this.classList.remove('bi-eye-slash');
                this.classList.add('bi-eye');
            } else {
                passwordInput.type = 'password';
                this.classList.remove('bi-eye');
                this.classList.add('bi-eye-slash');
            }
        });
    }
});

/**
 *  Cargar KPIs del Dashboard (API-Ready)
 * HOY: Solo log en consola
 * MAÑANA: Llamar a API.obtenerKPIs() y renderizar tarjetas
 */
async function cargarKPIDashboard() {
    try {
        console.log('Dashboard: Cargando datos simulados...');
        // MAÑANA: const datos = await API.obtenerKPIDashboard();
    } catch (error) {
        UI.mostrarToast('Error cargando métricas', 'danger');
    }
}