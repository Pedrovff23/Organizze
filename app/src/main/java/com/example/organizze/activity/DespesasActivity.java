package com.example.organizze.activity;

import android.os.Bundle;
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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private FloatingActionButton confirmar;

    private DatabaseReference fireBaseRef = ConfiguracaoFirebase.getdatabase();
    private FirebaseAuth fireBaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListener;

    private Double despesasTotal;
    private Double despesasGerada;
    private Double despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editData_Despesas);
        campoCategoria = findViewById(R.id.editCategoria_Despesas);
        campoDescricao = findViewById(R.id.editDescricao_Despesas);
        campoValor = findViewById(R.id.editValor_Despesas);
        confirmar = findViewById(R.id.check_despesas);

        campoData.setText(DateCustom.dataAtual());
        salvarDespesas();
    }

    public void salvarDespesas() {

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validarCampos()){
                    Movimentacao movimentacao = new Movimentacao();
                    movimentacao.setData(campoData.getText().toString());
                    movimentacao.setCategoria(campoCategoria.getText().toString());
                    movimentacao.setDescricao(campoDescricao.getText().toString());
                    movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
                    movimentacao.setTipo("d");
                    despesasGerada = Double.parseDouble(campoValor.getText().toString());
                    despesaAtualizada = despesasTotal + despesasGerada;
                    atualizarDespesas();
                    movimentacao.salvar();
                    finish();
                }
            }

        });
    }

    public boolean validarCampos() {

        if (!campoValor.getText().toString().isEmpty()) {
            if (!campoValor.getText().toString().isEmpty()) {
                return true;
            } else {
                Toast.makeText(DespesasActivity.this, "Digite a data da despesa",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DespesasActivity.this, "Digite o valor da despesa",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void recuperarDespesas(){
        String emailUsuario = fireBaseAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = fireBaseRef.child("usuarios").child(idUsuario);

        valueEventListener = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesasTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void atualizarDespesas(){
        String emailUsuario = fireBaseAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = fireBaseRef.child("usuarios").child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesaAtualizada);
    }

    @Override
    protected void onStart() {
        recuperarDespesas();
        super.onStart();
    }

    @Override
    protected void onStop() {
        usuarioRef.removeEventListener(valueEventListener);
        super.onStop();
    }
}
