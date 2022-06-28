package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.organizze.R;
import com.example.organizze.activity.LoginActivity;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonNextVisible(false);
        setButtonBackVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slider_1)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slider_2)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slider_3)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slider_4)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slider_5_cadastro)
                .canGoForward(false)
                .build());


    }

    public void buttonCadastrarConta(View view){
        startActivity(new Intent(this, CadastrarActivity.class));
    }

    public void buttonContaExistente(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void verificarUsuarioLogado(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(MainActivity.this,Content_Principal.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }
}