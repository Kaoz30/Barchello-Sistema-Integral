package com.barchello.cliente.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.barchello.cliente.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    // Vistas
    private TextInputEditText etNombre, etEmail, etTelefono, etPassword, etConfirmar;
    private TextInputLayout tilNombre, tilEmail, tilTelefono, tilPassword, tilConfirmar;
    private CheckBox cbTerminos;

    // Estados de visibilidad para cada campo de contraseña
    private boolean isPasswordVisible = false;
    private boolean isConfirmarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // ============================================================
        // 1. ENLAZAR VISTAS
        // ============================================================
        etNombre = findViewById(R.id.et_registro_nombre);
        tilNombre = findViewById(R.id.til_registro_nombre);

        etEmail = findViewById(R.id.et_registro_email);
        tilEmail = findViewById(R.id.til_registro_email);

        etTelefono = findViewById(R.id.et_registro_telefono);
        tilTelefono = findViewById(R.id.til_registro_telefono);

        etPassword = findViewById(R.id.et_registro_password);
        tilPassword = findViewById(R.id.til_registro_password);

        etConfirmar = findViewById(R.id.et_registro_confirmar);
        tilConfirmar = findViewById(R.id.til_registro_confirmar);

        cbTerminos = findViewById(R.id.cb_terminos);

        Button btnRegistrar = findViewById(R.id.btn_registrar);
        TextView tvIrLogin = findViewById(R.id.tv_ir_login);

        // ============================================================
        // 2. TOGGLES DE VISIBILIDAD PARA CONTRASEÑA Y CONFIRMAR
        // ============================================================
        // Toggle para contraseña
        tilPassword.setEndIconOnClickListener(v -> {
            if (isPasswordVisible) {
                // Ocultar
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tilPassword.setEndIconDrawable(R.drawable.ic_eye_slash);
                isPasswordVisible = false;
            } else {
                // Mostrar
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                tilPassword.setEndIconDrawable(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            etPassword.setSelection(Objects.requireNonNull(etPassword.getText()).length());
        });

        // Toggle para confirmar contraseña
        tilConfirmar.setEndIconOnClickListener(v -> {
            if (isConfirmarVisible) {
                etConfirmar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tilConfirmar.setEndIconDrawable(R.drawable.ic_eye_slash);
                isConfirmarVisible = false;
            } else {
                etConfirmar.setInputType(InputType.TYPE_CLASS_TEXT);
                tilConfirmar.setEndIconDrawable(R.drawable.ic_eye);
                isConfirmarVisible = true;
            }
            etConfirmar.setSelection(Objects.requireNonNull(etConfirmar.getText()).length());
        });

        // ============================================================
        // 3. LIMPIAR ERRORES AL ESCRIBIR (TextWatchers)
        // ============================================================
        etNombre.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilNombre.getError() != null) {
                    tilNombre.setError(null);
                    tilNombre.setErrorEnabled(false);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilEmail.getError() != null) {
                    tilEmail.setError(null);
                    tilEmail.setErrorEnabled(false);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etTelefono.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilTelefono.getError() != null) {
                    tilTelefono.setError(null);
                    tilTelefono.setErrorEnabled(false);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPassword.getError() != null) {
                    tilPassword.setError(null);
                    tilPassword.setErrorEnabled(false);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etConfirmar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilConfirmar.getError() != null) {
                    tilConfirmar.setError(null);
                    tilConfirmar.setErrorEnabled(false);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // ============================================================
        // 4. BOTÓN REGISTRAR – VALIDACIONES COMPLETAS
        // ============================================================
        btnRegistrar.setOnClickListener(v -> {
            // Obtener textos
            String nombre = Objects.requireNonNull(etNombre.getText()).toString().trim();
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String telefono = Objects.requireNonNull(etTelefono.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
            String confirmar = Objects.requireNonNull(etConfirmar.getText()).toString().trim();

            // Limpiar errores anteriores
            tilNombre.setError(null);
            tilNombre.setErrorEnabled(false);
            tilEmail.setError(null);
            tilEmail.setErrorEnabled(false);
            tilTelefono.setError(null);
            tilTelefono.setErrorEnabled(false);
            tilPassword.setError(null);
            tilPassword.setErrorEnabled(false);
            tilConfirmar.setError(null);
            tilConfirmar.setErrorEnabled(false);

            // --- Validación 1: Campos vacíos ---
            if (TextUtils.isEmpty(nombre)) {
                tilNombre.setError(getString(R.string.registro_error_vacio));
                tilNombre.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                tilEmail.setError(getString(R.string.registro_error_vacio));
                tilEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(telefono)) {
                tilTelefono.setError(getString(R.string.registro_error_vacio));
                tilTelefono.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError(getString(R.string.registro_error_vacio));
                tilPassword.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(confirmar)) {
                tilConfirmar.setError(getString(R.string.registro_error_vacio));
                tilConfirmar.requestFocus();
                return;
            }

            // --- Validación 2: Formato de email ---
            if (!isEmailValido(email)) {
                tilEmail.setError(getString(R.string.registro_error_email_invalido));
                tilEmail.requestFocus();
                return;
            }

            // --- Validación 3: Teléfono (solo números, al menos 7 dígitos) ---
            if (!telefono.matches("\\d{7,}")) {
                tilTelefono.setError(getString(R.string.registro_error_telefono));
                tilTelefono.requestFocus();
                return;
            }

            // --- Validación 4: Contraseña (mínimo 4 caracteres) ---
            if (password.length() < 4) {
                tilPassword.setError(getString(R.string.registro_error_password_corta));
                tilPassword.requestFocus();
                return;
            }

            // --- Validación 5: Coincidencia de contraseñas ---
            if (!password.equals(confirmar)) {
                tilConfirmar.setError(getString(R.string.registro_error_no_coinciden));
                tilConfirmar.requestFocus();
                return;
            }

            // --- Validación 6: Términos y condiciones ---
            if (!cbTerminos.isChecked()) {
                Toast.makeText(this, R.string.registro_error_terminos, Toast.LENGTH_SHORT).show();
                cbTerminos.requestFocus();
                return;
            }

            // --- ¡Todo correcto! Registro exitoso ---
            Toast.makeText(this, R.string.registro_exitoso, Toast.LENGTH_SHORT).show();

            // Redirigir a MainActivity como usuario registrado
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            intent.putExtra("modo", "registrado");
            startActivity(intent);
            finish();
        });

        // ============================================================
        // 5. ENLACE "¿YA TIENES CUENTA? INICIA SESIÓN"
        // ============================================================
        tvIrLogin.setOnClickListener(v -> finish());
    }

    // ============================================================
    // MÉTODO AUXILIAR PARA VALIDAR EMAIL
    // ============================================================
    private boolean isEmailValido(String email) {
        // Patrón simple pero efectivo para validar email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return Pattern.matches(emailPattern, email);
    }
}