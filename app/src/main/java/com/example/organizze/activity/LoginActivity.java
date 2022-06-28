package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    //Firebase
    FirebaseAuth autenticacao;
    //Usuario
    Usuario usuario = new Usuario();
    //Compontnes da Activity
    private EditText editTextEmail, editTextSenha;
    private Button buttonEntrar;
    private TextView redefinirSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email_login);
        editTextSenha = findViewById(R.id.edit_text_senha_login);
        buttonEntrar = findViewById(R.id.button_entrar_login);
        redefinirSenha = findViewById(R.id.resetarSenha);

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });

    }

    private void validarLogin() {

        String textoEmail = editTextEmail.getText().toString();
        String textoSenha = editTextSenha.getText().toString();

        if (!textoEmail.isEmpty()) {
            if (!textoSenha.isEmpty()) {

                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                validarLogin(usuario);

            } else {
                Toast.makeText(this, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Digite o email", Toast.LENGTH_SHORT).show();
        }
    }

    private void validarLogin(Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            abrirTelaPrincipal();

                        } else {
                            String excecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                excecao = "Usuario não cadastrado";

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                resetarSenha();
                                excecao = "Usuário ou senha está errado";

                            } catch (Exception e) {
                                resetarSenha();
                                excecao = "Várias tentativas o usuário pode ser bloqueado";
                            }

                            Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void resetarSenha() {
        redefinirSenha.setVisibility(View.VISIBLE);

        redefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usuario.getEmail().isEmpty()) {
                    autenticacao.sendPasswordResetEmail(usuario.getEmail())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,
                                                "Email enviado " + usuario.getEmail(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            throw task.getException();

                                        } catch (Exception e) {
                                            Toast.makeText(LoginActivity.this,
                                                    "Erro ao enviar email " + e.getMessage(),
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                }
                            });
                } else {

                    Toast.makeText(LoginActivity.this,
                            "Digite o email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this,Content_Principal.class));
        finish();
    }
}