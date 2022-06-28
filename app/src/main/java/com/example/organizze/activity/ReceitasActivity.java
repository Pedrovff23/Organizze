package com.example.organizze.activity;

import android.icu.text.Transliterator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoDescricao, campoCategoria;
    private EditText campoValor;
    private FloatingActionButton confirmar;

    private final DatabaseReference reference = ConfiguracaoFirebase.getdatabase();
    private final FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference getUsuario;
    private ValueEventListener valueEventListener;

    private double valorTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData_Receita);
        campoDescricao = findViewById(R.id.editDescricao_Receita);
        campoCategoria = findViewById(R.id.editCategoria_Receita);
        campoValor = findViewById(R.id.editValor_Receita);
        confirmar = findViewById(R.id.check_receita);

        campoData.setText(DateCustom.dataAtual());
        salvarReceita();
    }

    public void salvarReceita() {
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validarCampos()) {
                    Movimentacao movimentacao = new Movimentacao();
                    double valorDouble = Double.parseDouble(campoValor.getText().toString());
                    Log.i("Valor_Double",""+valorDouble);
                    movimentacao.setValor(valorDouble);
                    movimentacao.setDescricao(campoDescricao.getText().toString());
                    movimentacao.setCategoria(campoCategoria.getText().toString());
                    movimentacao.setData(campoData.getText().toString());
                    movimentacao.setTipo("r");
                    movimentacao.salvar();
                    recuperarReceita();
                    adicionarReceita(valorDouble);
                    finish();
                }
            }
        });

    }

    public boolean validarCampos() {
        if (!campoValor.getText().toString().isEmpty()) {

            if (!campoData.getText().toString().isEmpty()) {
                return true;
            } else {
                Toast.makeText(this, "Digite a data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Digite o valor", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void recuperarReceita() {
        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);
        getUsuario = reference.child("usuarios").child(idUsuario);

        valueEventListener = getUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                valorTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void adicionarReceita(Double valorAcresentado) {
        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        reference.child("usuarios").child(idUsuario)
                .child("receitaTotal").setValue(valorTotal + valorAcresentado);

    }
    @Override
    protected void onStart() {
        recuperarReceita();
        super.onStart();
    }

    @Override
    protected void onStop() {
        getUsuario.removeEventListener(valueEventListener);
        super.onStop();
    }
}