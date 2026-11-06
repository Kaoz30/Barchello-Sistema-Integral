package com.barchello.cliente.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.barchello.cliente.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsuario, etPassword;
    private TextInputLayout tilUsuario, tilPassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ 1. Enlazar vistas con los ID correctos de tu XML
        etUsuario = findViewById(R.id.et_usuario);
        tilUsuario = findViewById(R.id.til_usuario);
        etPassword = findViewById(R.id.et_password);
        tilPassword = findViewById(R.id.til_password);

        // ✅ 2. Botones y enlaces
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnGoogle = findViewById(R.id.btn_google);
        TextView tvOlvidaste = findViewById(R.id.tv_olvidaste);
        TextView tvInvitado = findViewById(R.id.tv_invitado);
        TextView tvRegistrate = findViewById(R.id.tv_registrate);

        // subrayado para "Continuar como invitado"
        tvInvitado.setPaintFlags(tvInvitado.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // ✅ 3. Toggle de contraseña
        tilPassword.setEndIconOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tilPassword.setEndIconDrawable(R.drawable.ic_eye_slash);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                tilPassword.setEndIconDrawable(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            etPassword.setSelection(Objects.requireNonNull(etPassword.getText()).length());
        });

        // ✅ 4. LIMPIAR Errores AL ESCRIBIR (NUEVO)
        etUsuario.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilUsuario.getError() != null) {
                    tilUsuario.setError(null);
                    tilUsuario.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPassword.getError() != null) {
                    tilPassword.setError(null);
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // ✅ 5. Botón Login
        btnLogin.setOnClickListener(v -> {
            String usuario = Objects.requireNonNull(etUsuario.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

            tilUsuario.setError(null);
            tilUsuario.setErrorEnabled(false);
            tilPassword.setError(null);
            tilPassword.setErrorEnabled(false);

            if (TextUtils.isEmpty(usuario)) {
                tilUsuario.setError(getString(R.string.login_error_vacio));
                tilUsuario.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError(getString(R.string.login_error_vacio));
                tilPassword.requestFocus();
                return;
            }
            if (!usuario.equals("admin") && !usuario.equals("juanperez")) {
                tilUsuario.setError(getString(R.string.login_error_usuario));
                tilUsuario.requestFocus();
                return;
            }
            if (!password.equals("1234")) {
                tilPassword.setError(getString(R.string.login_error_password));
                tilPassword.requestFocus();
                return;
            }

            Toast.makeText(this, "Bienvenido " + usuario, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("modo", "registrado");
            startActivity(intent);
            finish();
        });

        // ✅ 6. Botón Google (simulado)
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Continuar con Google (próximamente)", Toast.LENGTH_SHORT).show()
        );

        // ✅ 7. Enlace "¿Olvidaste tu contraseña?"
        tvOlvidaste.setOnClickListener(v ->
                Toast.makeText(this, "Enlace de recuperación enviado (simulado)", Toast.LENGTH_SHORT).show()
        );

        // ✅ 8. Enlace "Continuar como invitado"
        tvInvitado.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("modo", "invitado");
            Toast.makeText(this, "Bienvenido modo invitado", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        });

        // ✅ 9. Enlace "Regístrate" → Navega a RegistroActivity
        tvRegistrate.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class))
        );
    }
}