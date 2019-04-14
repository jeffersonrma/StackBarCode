package br.com.ktek.stackbarcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.ktek.model.Grade;
import br.com.ktek.model.Produto;

public class GradeAdapter extends BaseAdapter {
    private Produto produto;
    private ArrayList<Grade> list;
    private Context context;

    public GradeAdapter(Context context, Produto produto){
        this.produto = produto;
        this.list = produto.getGrades();
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
        final Grade grade = list.get(position);
        View layout;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.item_grade, null);
        }else{
            layout = convertView;
        }

        final ImageButton btnItemGradeAdd = (ImageButton) layout.findViewById(R.id.btnItemGradeAdd);
        btnItemGradeAdd.setOnClickListener(new View.OnClickListener() {
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
                                    grade.setQtde(qtde);
                                    produto.notifyDataChange();
                                    notifyDataSetChanged();

                                }catch (Exception e){
                                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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


        LinearLayout lyItemGradeBack = (LinearLayout) layout.findViewById(R.id.lyItemGradeBack);
        if(grade.getQtde() > 0)
            lyItemGradeBack.setBackgroundColor(0xffffffff);
        else
            lyItemGradeBack.setBackgroundColor(0xffe0e0ee);


        TextView txtItemGradeQtde = (TextView) layout.findViewById(R.id.txtItemGradeQtde);
        txtItemGradeQtde.setText(grade.getQtdeToString());

        TextView txtItemGradeEstoque = (TextView) layout.findViewById(R.id.txtItemGradeEstoque);
        txtItemGradeEstoque.setText(grade.getEstoqueString(produto.getUnidadeVenda()));

        TextView txtItemGradeNome = (TextView) layout.findViewById(R.id.txtItemGradeNome);
        txtItemGradeNome.setText(grade.getCor());
        return layout;
    }
}
