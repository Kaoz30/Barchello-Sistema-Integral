/**
 * validaciones.js - Reglas de negocio y Regex (jQuery Validate)
 * Arquitectura API-Ready: preparada para conectar con backend real
 */
const Validaciones = {
    inicializar: function() {
        if (typeof $.fn.validate === 'undefined') {
            console.warn('jQuery Validate no está cargado.');
            return;
        }

        // ========== 1. MÉTODOS PERSONALIZADOS (Regex Instructor Luis) ==========
        $.validator.addMethod('cedulaColombiana', function(value, element) {
            return this.optional(element) || /^\d{7,10}$/.test(value);
        }, 'Cédula inválida (7 a 10 dígitos numéricos).');

        $.validator.addMethod('telefonoColombiano', function(value, element) {
            return this.optional(element) || /^3\d{9}$/.test(value);
        }, 'Teléfono inválido (Debe iniciar con 3 y tener 10 dígitos).');

        $.validator.addMethod('emailValido', function(value, element) {
            return this.optional(element) || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
        }, 'Email inválido.');

        $.validator.addMethod('noFuturo', function(value, element) {
            return this.optional(element) || new Date(value) <= new Date();
        }, 'La fecha no puede ser futura.');

        // ========== 2. FORMULARIO DE LOGIN (Diseño Materialize-like) ==========
        if ($('#loginForm').length) {
            $('#loginForm').validate({
                rules: {
                    username: { required: true, minlength: 4 },
                    password: { required: true, minlength: 4 }
                },
                messages: {
                    username: { 
                        required: 'El usuario es obligatorio', 
                        minlength: 'Mínimo 4 caracteres' 
                    },
                    password: { 
                        required: 'La contraseña es obligatoria', 
                        minlength: 'Mínimo 4 caracteres' 
                    }
                },
                
                // No hacer focus automático (evita ocultar errores)
                focusInvalid: false,
                
                // Clase NEUTRA que no colisiona con ningún CSS visual
                errorClass: 'field-error',
                validClass: 'field-valid',
                errorElement: 'div',
                
                // Colocar el texto del error en nuestro div.error-msg del HTML
                errorPlacement: function(error, element) {
                    var container = element.closest('.input-field');
                    container.find('.error-msg span').text(error.text());
                    container.find('.error-msg').addClass('visible');
                },
                
                // Cuando el campo es inválido: borde rojo + mostrar mensaje
                highlight: function(element) {
                    var container = $(element).closest('.input-field');
                    $(element).addClass('error');
                    container.find('.error-msg').addClass('visible');
                },
                
                // Cuando el campo es válido: quitar borde rojo + ocultar mensaje
                unhighlight: function(element) {
                    var container = $(element).closest('.input-field');
                    $(element).removeClass('error');
                    container.find('.error-msg').removeClass('visible');
                },
                
                submitHandler: function(form) {
                    var user = $('#username').val().trim();
                    var pass = $('#password').val().trim();
                    
                    if (typeof Auth !== 'undefined' && typeof Auth.intentarLogin === 'function') {
                        Auth.intentarLogin(user, pass);
                    } else {
                        console.log('Login simulado:', { user, pass });
                        if (user === 'admin' && pass === '1234') {
                            window.location.href = 'dashboard.html';
                        } else {
                            alert('❌ Credenciales incorrectas (usa admin / 1234)');
                        }
                    }
                }
            });
            
            // Ocultar error al hacer focus (comportamiento original del login)
            $('#loginForm .input-field input').on('focus', function() {
                var container = $(this).closest('.input-field');
                container.find('.error-msg').removeClass('visible');
                $(this).removeClass('error');
            });
        }

        // ========== 3. FORMULARIO DE RESERVAS ==========
        if ($('#formReserva').length) {
            $('#formReserva').validate({
                rules: {
                    nombreCliente: { required: true, minlength: 3 },
                    fechaReserva: { required: true, date: true, noFuturo: false }, // permite fechas futuras en reservas
                    horaReserva: { required: true },
                    personas: { required: true, min: 1, max: 12 },
                    mesaReserva: { required: true }
                },
                messages: {
                    nombreCliente: { 
                        required: 'El nombre del cliente es obligatorio',
                        minlength: 'Mínimo 3 caracteres'
                    },
                    fechaReserva: { 
                        required: 'Seleccione una fecha',
                        date: 'Fecha inválida'
                    },
                    horaReserva: { required: 'Seleccione una hora' },
                    personas: { 
                        required: 'Indique cuántas personas',
                        min: 'Mínimo 1 persona',
                        max: 'Máximo 12 personas'
                    },
                    mesaReserva: { required: 'Seleccione una mesa' }
                },
                errorClass: 'is-invalid',
                validClass: 'is-valid',
                errorElement: 'div',
                errorPlacement: function(error, element) {
                    error.addClass('invalid-feedback');
                    element.closest('.mb-3, .col-md-6').append(error);
                },
                submitHandler: async function(form) {
                    const btn = '#btnGuardarReserva';
                    // Mostrar loading si UI está disponible
                    if (typeof UI !== 'undefined' && typeof UI.mostrarLoading === 'function') {
                        UI.mostrarLoading(btn, 'Guardando...');
                    } else {
                        $(btn).prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Guardando...');
                    }

                    try {
                        const datos = {
                            cliente: $('#nombreCliente').val(),
                            fecha: $('#fechaReserva').val(),
                            hora: $('#horaReserva').val(),
                            personas: $('#personas').val(),
                            mesa: $('#mesaReserva').val()
                        };

                        // Patrón API-Ready
                        let resp;
                        if (typeof API !== 'undefined' && typeof API.guardarReserva === 'function') {
                            resp = await API.guardarReserva(datos);
                        } else {
                            // Simulación mientras no hay backend
                            await new Promise(r => setTimeout(r, 800));
                            resp = { exito: true, mensaje: '✅ Reserva registrada correctamente' };
                            console.log('Reserva simulada:', datos);
                        }

                        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                            UI.mostrarToast(resp.mensaje, 'success');
                        } else {
                            alert(resp.mensaje);
                        }
                        form.reset();
                    } catch (e) {
                        const msgErr = 'Error al guardar: ' + e.message;
                        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                            UI.mostrarToast(msgErr, 'danger');
                        } else {
                            alert(msgErr);
                        }
                    } finally {
                        if (typeof UI !== 'undefined' && typeof UI.ocultarLoading === 'function') {
                            UI.ocultarLoading(btn);
                        } else {
                            $(btn).prop('disabled', false).html('<i class="bi bi-check-lg me-2"></i>Registrar Reserva');
                        }
                    }
                }
            });
        }

        // ========== 4. FORMULARIO DE COMPRAS ==========
        if ($('#formCompra').length) {
            $('#formCompra').validate({
                rules: {
                    proveedor: { required: true },
                    producto: { required: true },
                    cantidad: { required: true, min: 1 },
                    precioUnitario: { required: true, min: 0 },
                    fechaEntrega: { required: true, date: true, noFuturo: true }
                },
                messages: {
                    proveedor: { required: 'Seleccione un proveedor' },
                    producto: { required: 'Seleccione un producto' },
                    cantidad: { 
                        required: 'Indique la cantidad',
                        min: 'Mínimo 1 unidad'
                    },
                    precioUnitario: { 
                        required: 'Indique el precio',
                        min: 'El precio no puede ser negativo'
                    },
                    fechaEntrega: { 
                        required: 'Seleccione fecha de entrega',
                        date: 'Fecha inválida',
                        noFuturo: 'La fecha no puede ser futura'
                    }
                },
                errorClass: 'is-invalid',
                validClass: 'is-valid',
                errorElement: 'div',
                errorPlacement: function(error, element) {
                    error.addClass('invalid-feedback');
                    element.closest('.mb-3, .col-md-6').append(error);
                },
                submitHandler: async function(form) {
                    const btn = '#btnEmitirCompra';
                    if (typeof UI !== 'undefined' && typeof UI.mostrarLoading === 'function') {
                        UI.mostrarLoading(btn, 'Emitiendo...');
                    } else {
                        $(btn).prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Emitiendo...');
                    }

                    try {
                        const datos = $(form).serializeArray();

                        let resp;
                        if (typeof API !== 'undefined' && typeof API.guardarCompra === 'function') {
                            resp = await API.guardarCompra(datos);
                        } else {
                            await new Promise(r => setTimeout(r, 800));
                            resp = { exito: true, mensaje: '✅ Orden de compra emitida correctamente' };
                            console.log('Compra simulada:', datos);
                        }

                        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                            UI.mostrarToast(resp.mensaje, 'success');
                        } else {
                            alert(resp.mensaje);
                        }
                        form.reset();
                    } catch (e) {
                        const msgErr = 'Error al emitir: ' + e.message;
                        if (typeof UI !== 'undefined' && typeof UI.mostrarToast === 'function') {
                            UI.mostrarToast(msgErr, 'danger');
                        } else {
                            alert(msgErr);
                        }
                    } finally {
                        if (typeof UI !== 'undefined' && typeof UI.ocultarLoading === 'function') {
                            UI.ocultarLoading(btn);
                        } else {
                            $(btn).prop('disabled', false).html('<i class="bi bi-send-check me-2"></i>Emitir Orden de Compra');
                        }
                    }
                }
            });
        }
    }
};