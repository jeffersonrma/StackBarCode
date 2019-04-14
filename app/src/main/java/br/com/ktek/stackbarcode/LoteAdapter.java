package br.com.ktek.stackbarcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.ktek.model.Lote;
import br.com.ktek.model.Produto;
import br.com.ktek.model.ProdutoDAO;


public class LoteAdapter extends BaseAdapter {
    Produto produto;
    private ArrayList<Lote> list;
    private Context context;

    public LoteAdapter(Context context, Produto produto){
        this.produto = produto;
        this.list = produto.getLotes();
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
        final Lote lote = list.get(position);
        View layout;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.item_lote, null);
        }else{
            layout = convertView;
        }

        final ImageButton btnItemLoteAdd = (ImageButton) layout.findViewById(R.id.btnItemloteAdd);
        btnItemLoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText txtAdd = new EditText(v.getContext());
                txtAdd.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Qtde")
                        .setMessage("Selecione a quantidade")
                        .setView(txtAdd)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                double qtde =Double.parseDouble(txtAdd.getText().toString());
                                try{
                                    lote.setQtde(qtde);
                                    produto.notifyDataChange();
                                    notifyDataSetChanged();

                                }catch (Exception e){
                                    Toast.makeText(v.getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        LinearLayout lyItemLoteBack = (LinearLayout) layout.findViewById(R.id.lyItemLoteBack);
            if(lote.getQtde() > 0)
                lyItemLoteBack.setBackgroundColor(0xffffffff);
            else
                lyItemLoteBack.setBackgroundColor(0xffe0e0ee);


        TextView txtItemLoteQtde = (TextView) layout.findViewById(R.id.txtItemLoteQtde);
        txtItemLoteQtde.setText(lote.getQtdeToString());

        TextView txtItemLoteEstoque = (TextView) layout.findViewById(R.id.txtItemLoteEstoque);
        txtItemLoteEstoque.setText(lote.getEstoqueString(produto.getUnidadeVenda()));

        TextView txtItemLoteNome = (TextView) layout.findViewById(R.id.txtItemLoteNome);
        txtItemLoteNome.setText(lote.getNome());



        return layout;
    }
}
