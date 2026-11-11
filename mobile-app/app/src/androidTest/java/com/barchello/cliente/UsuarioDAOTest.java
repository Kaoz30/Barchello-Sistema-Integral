package com.barchello.cliente;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.barchello.cliente.dao.UsuarioDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// Esta anotación le dice a Android que use el emulador/celular
@RunWith(AndroidJUnit4.class)
public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @Before
    public void setUp() {
        // Obtenemos el contexto real del emulador
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        usuarioDAO = new UsuarioDAO(context);
    }

    @Test
    public void testValidarUsuarioCorrecto() {
        usuarioDAO.insertarUsuario("admin_test", "1234");
        boolean resultado = usuarioDAO.validarUsuario("admin_test", "1234");
        assertTrue("El usuario debe ser válido", resultado);
    }

    @Test
    public void testValidarUsuarioIncorrecto() {
        usuarioDAO.insertarUsuario("admin_test", "1234");
        boolean resultado = usuarioDAO.validarUsuario("admin_test", "wrong_pass");
        assertFalse("El usuario NO debe ser válido", resultado);
    }
}