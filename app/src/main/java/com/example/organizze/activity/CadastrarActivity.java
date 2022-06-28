package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastrarActivity extends AppCompatActivity {

    //FireBase
    FirebaseAuth autenticacao;

    //Compontnes da Activity
    private EditText editTextNome, editTextEmail, editTextSenha;
    private AppCompatButton buttonCadastar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editTextNome = findViewById(R.id.edit_text_nome_cadastrar);
        editTextEmail = findViewById(R.id.edit_text_email_cadastrar);
        editTextSenha = findViewById(R.id.edit_text_senha_cadastrar);
        buttonCadastar = findViewById(R.id.button_cadastrar);

        buttonCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validar campos preenchidos
                validarCampo();
            }
        });

        getSupportActionBar().setTitle("Cadastro");
    }

    private void validarCampo() {

        String textoNome = editTextNome.getText().toString();
        String textoEmail = editTextEmail.getText().toString();
        String textoSenha = editTextSenha.getText().toString();

        if (!textoNome.isEmpty()) {
            if (!textoEmail.isEmpty()) {
                if (!textoSenha.isEmpty()) {

                    Usuario usuario = new Usuario();

                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    usuario.setNome(textoNome);

                    cadastrarUsuario(usuario);

                } else {
                    Toast.makeText(CadastrarActivity.this,
                            "Digite sua senha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastrarActivity.this,
                        "Digite seu email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CadastrarActivity.this,
                    "Digite seu nome", Toast.LENGTH_SHORT).show();
        }
    }

    private void cadastrarUsuario(Usuario usuario) {

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                            usuario.setIdUsuario(idUsuario);
                            usuario.salvarUsuario();
                            finish();

                        } else {
                            String excecao = "";
                            try {
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {
                                excecao = "Senha Fraca, Digite uma senha mais forte!";

                            }catch (FirebaseAuthInvalidCredentialsException e){
                                excecao = "Email invalido, digite um email válido";
                            }
                            catch (FirebaseAuthUserCollisionException e){
                                excecao = "Usuário já cadastrado";
                            }
                            catch (Exception e){
                                excecao = "Erro ao cadastrar usuario" + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastrarActivity.this,
                                    excecao, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}