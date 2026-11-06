/**
 * ============================================================
 * AUTH.JS — Autenticación y Control de Acceso (RBAC)
 * ============================================================
 * 
 * MI NOTA: Este archivo maneja TODO lo relacionado con:
 * - Login / Logout
 * - Protección de rutas (Guardia de Ruta)
 * - Permisos visuales (ocultar/mostrar por rol)
 * - Dropdown de usuario en el navbar
 * - Cambio de rol activo (mesero ↔ bartender)
 * 
 * ESTRATEGIA DE SEGURIDAD: "Hidden by Default"
 * Los elementos con data-role nacen OCULTOS por CSS.
 * Este archivo solo AGREGA la clase .visible-por-rol si hay permiso.
 * ============================================================
 */

const Auth = {

    /**
     * ============================================================
     *  LOGIN
     * ============================================================
     * Se llama desde el formulario de index.html
     * Recibe usuario y password, los envía a API.login()
     * y guarda la respuesta completa en sessionStorage
     */
    async intentarLogin(usuario, password) {
        try {
            UI.mostrarLoading('#btnIngresar', 'Ingresando...');
            const respuesta = await API.login({ usuario, password });

            // ✅ Guardar el usuario COMPLETO (incluye roles_secundarios)
            sessionStorage.setItem('barchello_session', JSON.stringify(respuesta.usuario));
            sessionStorage.setItem('barchello_token', respuesta.token);

            UI.mostrarToast(`Bienvenido, ${respuesta.usuario.nombre}`, 'success');

            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 800);

        } catch (error) {
            UI.mostrarToast(error.message, 'danger');
        } finally {
            UI.ocultarLoading('#btnIngresar');
        }
    },

    /**
     * ============================================================
     *  GUARDIA DE RUTA + PERMISOS VISUALES (RBAC)
     * ============================================================
     * Se ejecuta en CADA carga de página desde app.js
     * 
     * FLUJO:
     * 1. ¿Hay sesión? → Si NO, redirige a index.html
     * 2. ¿La página exige permisos? → Valida contra rol activo
     * 3. Si no tiene permiso → Toast + redirige a dashboard
     * 4. Si tiene permiso → Renderiza dropdown + muestra elementos
     */
    protegerRutaYAplicarRoles: function() {
        const sessionStr = sessionStorage.getItem('barchello_session');
        const paginaActual = window.location.pathname.split('/').pop() || 'index.html';

        // ==========================================
        // CASO 1: NO HAY SESIÓN
        // ==========================================
        if (!sessionStr) {
            if (paginaActual !== 'index.html' && paginaActual !== 'registro.html' && paginaActual !== 'errores.html') {
                window.location.href = 'index.html';
                return;
            } else {
                document.body.classList.remove('validando-sesion');
                document.body.classList.add('sesion-validada');
                return;
            }
        }

        // ==========================================
        // CASO 2: HAY SESIÓN ACTIVA
        // ==========================================
        const usuario = JSON.parse(sessionStr);
        document.body.classList.add('sesion-activa');

        // ==========================================
        // GUARDIA DE RUTA (Route Guard)
        // ==========================================
        const PERMISOS_POR_PAGINA = {
            'dashboard.html':  ['admin', 'gerente', 'mesero', 'cocinero', 'cajero', 'bartender'],
            'reservas.html':   ['admin', 'gerente', 'mesero', 'bartender'],
            'cocina.html':     ['admin', 'gerente', 'cocinero', 'bartender'],
            'ventas.html':     ['admin', 'gerente', 'cajero', 'mesero'],
            'inventario.html': ['admin', 'gerente'],
            'compras.html':    ['admin', 'gerente'],
            'caja.html':       ['admin', 'gerente', 'cajero'],
            'reportes.html':   ['admin', 'gerente'],
            'usuarios.html':   ['admin'],
            'menu.html':       ['admin', 'gerente']
        };

        if (PERMISOS_POR_PAGINA[paginaActual]) {
            const rolesPermitidos = PERMISOS_POR_PAGINA[paginaActual];
            const rolActivo = sessionStorage.getItem('barchello_rol_activo') || usuario.rol;

            if (!rolesPermitidos.includes(rolActivo)) {
                if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                    UI.mostrarToast(`🚫 Acceso Denegado. Tu rol activo (${rolActivo}) no tiene permisos para este módulo.`, 'danger');
                }
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 1500);
                return;
            }
        }

        // ==========================================
        // RENDERIZAR DROPDOWN DE USUARIO
        // ==========================================
        this.renderizarInfoUsuario(usuario);

        // ==========================================
        // MOSTRAR SOLO ELEMENTOS AUTORIZADOS
        // ==========================================
        const rolActivo = sessionStorage.getItem('barchello_rol_activo') || usuario.rol;

        document.querySelectorAll('[data-role]').forEach(el => {
            const rolesPermitidos = el.getAttribute('data-role')
                .split(',')
                .map(r => r.trim());

            if (rolesPermitidos.includes(rolActivo)) {
                el.classList.add('visible-por-rol');
            }
        });

        document.body.classList.remove('validando-sesion');
        document.body.classList.add('sesion-validada');
    },

    /**
     * ================================================================
     *  RENDERIZAR INFO DEL USUARIO EN EL DROPDOWN - offcanvas - navbar
     * ================================================================
     * Llena los elementos del navbar y del dropdown con los datos
     * del usuario actual: nombre, rol activo, avatar con inicial
     */
        renderizarInfoUsuario: function (usuario) {
        const inicial = (usuario.nombre || 'U').charAt(0).toUpperCase();
        const rolActivo = sessionStorage.getItem('barchello_rol_activo') || usuario.rol;

        // Navbar (zona derecha)
        const nombreEl = document.getElementById('nombre-usuario-nav');
        const rolEl = document.getElementById('rol-usuario-nav');
        const avatarNav = document.getElementById('userAvatarNav');
        if (nombreEl) nombreEl.textContent = usuario.nombre;
        if (rolEl) rolEl.textContent = rolActivo;
        if (avatarNav) avatarNav.textContent = inicial;

        // Dropdown (menú desplegable)
        const userNameDropdown = document.getElementById('userNameDropdown');
        const userRoleDropdown = document.getElementById('userRoleDropdown');
        const avatarDropdown = document.getElementById('userAvatarDropdown');
        if (userNameDropdown) userNameDropdown.textContent = usuario.nombre;
        if (userRoleDropdown) userRoleDropdown.textContent = rolActivo;
        if (avatarDropdown) avatarDropdown.textContent = inicial;

        // Offcanvas (menú lateral)
        const avatarOffcanvas = document.getElementById('userAvatarOffcanvas');
        const nameOffcanvas = document.getElementById('userNameOffcanvas');
        const roleOffcanvas = document.getElementById('userRoleOffcanvas');
        if (avatarOffcanvas) avatarOffcanvas.textContent = inicial;
        if (nameOffcanvas) nameOffcanvas.textContent = usuario.nombre;
        if (roleOffcanvas) roleOffcanvas.textContent = rolActivo;
    },

    /**
     * ============================================================
     *  CAMBIAR ROL ACTIVO (mesero ↔ bartender)
     * ============================================================
     * ESTE ERA EL ESLABÓN ROTO #2. Ya funciona porque api.js
     * ahora devuelve roles_secundarios en el objeto usuario.
     * 
     * FLUJO:
     * 1. Verifica que el usuario tenga ese rol asignado
     * 2. Guarda el nuevo rol en sessionStorage
     * 3. Muestra toast de confirmación
     * 4. Recarga la página para aplicar nuevos permisos
     */
    cambiarRolActivo: function(nuevoRol) {
        const usuario = this.obtenerUsuarioActual();
        if (!usuario) return false;

        // Roles disponibles = rol principal + secundarios
        const rolesDisponibles = [usuario.rol, ...(usuario.roles_secundarios || [])];

        if (!rolesDisponibles.includes(nuevoRol)) {
            if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                UI.mostrarToast(`No tienes asignado el rol de ${nuevoRol}`, 'danger');
            }
            return false;
        }

        sessionStorage.setItem('barchello_rol_activo', nuevoRol);

        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
            UI.mostrarToast(`✅ Rol activo cambiado a: ${nuevoRol}`, 'success');
        }

        // Recargar la página para aplicar los nuevos permisos
        setTimeout(() => window.location.reload(), 800);
        return true;
    },

    /**
     * ============================================================
     *  CERRAR SESIÓN
     * ============================================================
     * Limpia TODO de sessionStorage y redirige al login
     */
    cerrarSesion: function() {
        sessionStorage.removeItem('barchello_session');
        sessionStorage.removeItem('barchello_token');
        sessionStorage.removeItem('barchello_rol_activo');

        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
            UI.mostrarToast('Sesión cerrada correctamente', 'success');
        }

        setTimeout(() => {
            window.location.href = 'index.html';
        }, 500);
    },

    /**
     * ============================================================
     *  OBTENER USUARIO ACTUAL
     * ============================================================
     * Lee el usuario desde sessionStorage
     * Devuelve el objeto completo (con roles_secundarios)
     */
    obtenerUsuarioActual: function() {
        const sessionStr = sessionStorage.getItem('barchello_session');
        return sessionStr ? JSON.parse(sessionStr) : null;
    },

    /**
     * ============================================================
     *  VERIFICAR PERMISO DE MÓDULO
     * ============================================================
     * Útil para ocultar botones internos dentro de una página
     * Ejemplo: solo admin ve el botón "Eliminar usuario"
     */
    tienePermiso: function(modulo) {
        const usuario = this.obtenerUsuarioActual();
        if (!usuario) return false;

        const permisos = {
            admin:    ['dashboard', 'reservas', 'cocina', 'ventas', 'inventario', 'compras', 'caja', 'reportes', 'usuarios', 'menu'],
            gerente:  ['dashboard', 'reservas', 'cocina', 'ventas', 'inventario', 'compras', 'caja', 'reportes', 'menu'],
            mesero:   ['dashboard', 'reservas', 'menu'],
            cocinero: ['dashboard', 'cocina', 'menu'],
            cajero:   ['dashboard', 'ventas', 'caja'],
            bartender:['dashboard', 'cocina', 'menu']
        };

        return (permisos[usuario.rol] || []).includes(modulo);
    }
};

// ✅ AUTO-EJECUCIÓN INMEDIATA DE AUTH
// No espera a DOMContentLoaded: se ejecuta en cuanto auth.js se carga
// El navbar ya existe en el HTML (está arriba de los scripts)
(function() {
    // Solo ejecutar si estamos en una página que necesita protección
    const paginaActual = window.location.pathname.split('/').pop() || 'index.html';
    const paginasPublicas = ['index.html', 'registro.html', 'errores.html'];
    
    if (!paginasPublicas.includes(paginaActual)) {
        // Ejecutar protección inmediatamente
        Auth.protegerRutaYAplicarRoles();
    } else if (paginaActual === 'index.html') {
        // En el login, solo quitar el estado de validación
        document.body.classList.remove('validando-sesion');
        document.body.classList.add('sesion-validada');
    }
})();