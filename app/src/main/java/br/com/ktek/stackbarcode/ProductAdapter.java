package br.com.ktek.stackbarcode;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.PrintWriter;
import java.util.ArrayList;

import br.com.ktek.model.Produto;

public class ProductAdapter extends BaseAdapter{
    private ArrayList<Produto> list;
    private Context context;

    public ProductAdapter(Context context, ArrayList<Produto> list){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Produto produto = list.get(position);
        View layout;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.item_product, null);
        }else{
            layout = convertView;
        }

        TextView txtPreco = (TextView) layout.findViewById(R.id.txtPreco);
        LinearLayout lyBack = (LinearLayout) layout.findViewById(R.id.lyBack);
        if(produto.isCodeBarOnly()) {
            lyBack.setBackgroundColor(0xff999999);
            txtPreco.setText("");
        } else {
            lyBack.setBackgroundColor(0xffffffff);
            txtPreco.setText("R$ " + produto.getPrecoToString());
        }

        if(produto.isPromocao())
            txtPreco.setTextColor(0xffff0000);
        else
            txtPreco.setTextColor(0xff000000);

        TextView txtCode = (TextView) layout.findViewById(R.id.txtCode);
        txtCode.setText(produto.getCodigo());

        TextView txtQtde = (TextView) layout.findViewById(R.id.txtQtde);
        txtQtde.setText(produto.getQtdeToString());

        TextView txtNome = (TextView) layout.findViewById(R.id.txtNome);
        txtNome.setText(produto.getNome());

        TextView txtEstoque = (TextView) layout.findViewById(R.id.txtItemEstoque);
        txtEstoque.setText(produto.getEstoqueToString() + " " + produto.getUnidadeVenda());




        return layout;
    }


}
