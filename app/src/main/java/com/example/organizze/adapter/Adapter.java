package com.example.organizze.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
import com.example.organizze.model.Movimentacao;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<Movimentacao> movimentacaoList;
    Context context;

    public Adapter(List<Movimentacao> movimentacaoList, Context context) {
        this.context = context;
        this.movimentacaoList = movimentacaoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_movimentacao, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.campoCategoria.setText(movimentacaoList.get(position).getCategoria());
        holder.campoDescricao.setText(movimentacaoList.get(position).getDescricao());

        if (movimentacaoList.get(position).getTipo().equals("d")) {
            String despesa = "-" + movimentacaoList.get(position).getValor();

            holder.campoSaldo.setTextColor(context.getResources()
                    .getColor(R.color.colorPrimaryDespesas));

            holder.campoSaldo.setText(despesa);
        }else{
            holder.campoSaldo.setTextColor(context.getResources()
                    .getColor(R.color.colorPrimaryReceita));
            holder.campoSaldo.setText(String.valueOf(movimentacaoList.get(position).getValor()));
        }
    }

    @Override
    public int getItemCount() {
        return movimentacaoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView campoCategoria;
        private final TextView campoDescricao;
        private final TextView campoSaldo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            campoCategoria = itemView.findViewById(R.id.campoCategoria);
            campoDescricao = itemView.findViewById(R.id.campoDescricao);
            campoSaldo = itemView.findViewById(R.id.campoSaldo);
        }
    }
}
