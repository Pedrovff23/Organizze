package com.example.organizze.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
import com.example.organizze.adapter.Adapter;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Content_Principal extends AppCompatActivity {

    private final FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private final DatabaseReference reference = ConfiguracaoFirebase.getdatabase();
    private final List<Movimentacao> movimentacaoList = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacao;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private FloatingActionButton floatingReceita, floatingDespesa;
    private FloatingActionMenu floatingActionMenu;
    private TextView textoSaudacao, textoSaldo;
    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private double valorTotal = 0.0;
    private double valorDespesas = 0.0;
    private double valorReceita = 0.0;
    private String nome = "";
    private String mesAno;
    private Adapter adapter;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        floatingReceita = findViewById(R.id.menu_receita);
        floatingDespesa = findViewById(R.id.menu_despesa);
        calendarView = findViewById(R.id.calendarView);
        textoSaudacao = findViewById(R.id.textSaudacao);
        textoSaldo = findViewById(R.id.textSaldo);
        recyclerView = findViewById(R.id.recyclerMovimentos);
        floatingActionMenu = findViewById(R.id.menu);

        calendarioView();
        swipe();

        adapter = new Adapter(movimentacaoList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        floatingReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Content_Principal.this, ReceitasActivity.class));
            }
        });
        floatingDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Content_Principal.this, DespesasActivity.class));
            }
        });

    }


    public void swipe() {

        simpleCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    excluirMovimentacao(viewHolder);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                    float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive)

                        .addBackgroundColor(ContextCompat
                                .getColor(Content_Principal.this,
                                        android.R.color.holo_red_dark))
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder) {

        int position = viewHolder.getAbsoluteAdapterPosition();
        String categoria = "Movimentaçao da conta excluido";

        Movimentacao movimentacao = movimentacaoList.get(position);
        movimentacaoList.remove(position);

        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        movimentacaoRef = reference.child("movimentacao")
                .child(idUsuario)
                .child(mesAno);

        movimentacaoRef.child(movimentacao.getKey()).removeValue();
        atualizarSaldosExcluir(movimentacao);
        adapter.notifyItemRemoved(position);

        Snackbar.make(recyclerView, categoria, Snackbar.LENGTH_LONG)
                .setAction("Desfazer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        movimentacaoList.add(position, movimentacao);

                        usuarioRef = reference;
                        usuarioRef.child("movimentacao").child(idUsuario)
                                  .child(mesAno)
                                .child(movimentacao.getKey())
                                .setValue(movimentacao);
                                atualizarSaldos(movimentacao);

                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    public void atualizarSaldosExcluir(Movimentacao movimentacao){

        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        usuarioRef = reference.child("usuarios")
                .child(idUsuario);

        if(movimentacao.getTipo().equals("r")){
            double receita = movimentacao.getValor();
            double valorTotal = valorReceita-receita;

            usuarioRef.child("receitaTotal")
                    .setValue(valorTotal);
        }

        if(movimentacao.getTipo().equals("d")){
            double despesas = movimentacao.getValor();
            double valorTotal = valorDespesas-despesas;
            usuarioRef.child("despesaTotal")
                    .setValue(valorTotal);
        }
    }

    public void atualizarSaldos(Movimentacao movimentacao){

        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        usuarioRef = reference.child("usuarios")
                .child(idUsuario);

        if(movimentacao.getTipo().equals("r")){
            double receita = movimentacao.getValor();
            usuarioRef.child("receitaTotal")
                    .setValue(valorReceita + receita);
            Log.i("Teste",""+valorReceita);
        }

        if(movimentacao.getTipo().equals("d")){
            double despesas = movimentacao.getValor();
            usuarioRef.child("despesaTotal")
                    .setValue(valorDespesas + despesas);
        }
    }


    @SuppressLint("DefaultLocale")
    public void calendarioView() {

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2020, 1, 1))
                .setMaximumDate(CalendarDay.from(2024, 12, 31)).commit();

        CalendarDay calendarDay = calendarView.getCurrentDate();

        String mesformatado1 = String.format("%02d", calendarDay.getMonth());
        mesAno = mesformatado1 + "" + calendarDay.getYear();

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String mesformatado2 = String.format("%02d", date.getMonth());
                mesAno = mesformatado2 + "" + date.getYear();

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
                recuperarMovimentacoes();
            }
        });
    }

    public void recupearResumo() {

        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        usuarioRef = reference.child("usuarios").child(idUsuario);

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);

                valorDespesas = usuario.getDespesaTotal();
                valorReceita = usuario.getReceitaTotal();
                valorTotal = (valorReceita - valorDespesas);
                nome = usuario.getNome();

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String valorFormatado = decimalFormat.format(valorTotal);

                textoSaudacao.setText("Olá, " + nome);
                textoSaldo.setText("R$ " + valorFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sair) {
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void recuperarMovimentacoes() {

        String emailUser = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUser);

        movimentacaoRef = reference.child("movimentacao")
                .child(idUsuario).child(mesAno);

        valueEventListenerMovimentacao = movimentacaoRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        movimentacaoList.clear();

                        for (DataSnapshot dados : snapshot.getChildren()) {
                            Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                            movimentacao.setKey(dados.getKey());
                            movimentacaoList.add(movimentacao);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    protected void onStart() {
        floatingActionMenu.close(true);
        recupearResumo();
        recuperarMovimentacoes();
        super.onStart();
    }

    @Override
    protected void onStop() {
        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
        super.onStop();
    }
}