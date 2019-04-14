package br.com.ktek.stackbarcode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.zip.DeflaterOutputStream;

import br.com.ktek.stackbarcode.R;
import br.com.ktek.model.OnProdutoItemChange;
import br.com.ktek.model.Produto;
import br.com.ktek.model.ProdutoDAO;


public class ProductActivity extends Activity {

    private int id;
    private Produto produto;
    private TextView txtProdutoNome;
    private TextView txtProdutoPreco;
    private TextView txtProdutoEstoque;
    private EditText txtProdutoQtde;
    private EditText txtProdutoDesconto;
    private EditText txtProdutoPrecoTotal;
    private ListView lvProdutoGradeLote;
    private BaseAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        produto = ProdutoDAO.Banco.get(id);
        setTitle(produto.getCodigo());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        txtProdutoEstoque = (TextView) findViewById(R.id.txtProdutoEstoque);
        txtProdutoPreco = (TextView) findViewById(R.id.txtProdutoPreco);
        txtProdutoNome = (TextView) findViewById(R.id.txtProdutoNome);
        txtProdutoQtde = (EditText) findViewById(R.id.txtProdutoQtde);
        txtProdutoDesconto = (EditText) findViewById(R.id.txtProdutoDesconto);
        txtProdutoPrecoTotal = (EditText) findViewById(R.id.txtProdutoPrecoTotal);
        lvProdutoGradeLote = (ListView) findViewById(R.id.lvProdutoGradeLote);
        preencherTela();

        if(produto.isPromocao())
            txtProdutoPreco.setTextColor(0xffff0000);
        else
            txtProdutoPreco.setTextColor(0xff000000);

        produto.onProdutoItemChange(new OnProdutoItemChange() {
            @Override
            public void onValueChange() {
                preencherTela();
            }
        });

        txtProdutoQtde.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveTxtQtde(s.toString());
            }
        });
        txtProdutoDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveTxtDesconto(s.toString());
            }
        });
        txtProdutoPrecoTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveTxtTotal(s.toString());
            }
        });

        txtProdutoQtde.setEnabled(false);
        txtProdutoQtde.setFocusable(false);
        preencherUnidadeVenda();

        preencherLista();
        setListViewHeightBasedOnChildren(lvProdutoGradeLote);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onResume (){
        super.onResume();

    }

    private void saveTxtQtde(String s){
        try {
            Double.parseDouble(s);
        }catch (Exception e){
            txtProdutoQtde.setText("0");
            txtProdutoQtde.selectAll();
            return;
        }
        if(Double.parseDouble(s) == produto.getQtde())
            return;
        if(produto.getTipo() != 'N')
            return;
        if(!txtProdutoQtde.hasFocus())
            return;
            try {
                produto.setQtde(Double.parseDouble(txtProdutoQtde.getText().toString()));
            } catch (Exception e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
    }
    private void saveTxtDesconto(String s){
        double d;
        try {
             d = Double.parseDouble(s.replace(',','.'));
        }catch (Exception e){
            txtProdutoDesconto.setText("0");
            txtProdutoDesconto.selectAll();
            return;
        }
        if(d == produto.getDesconto())
            return;
        if(!txtProdutoDesconto.hasFocus())
            return;
        try {
            produto.setDesconto(d);
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void saveTxtTotal(String s){
        double d;
        try {
            d = Double.parseDouble(s.replace(',','.'));
        }catch (Exception e){
            txtProdutoPrecoTotal.setText("0");
            txtProdutoPrecoTotal.selectAll();
            return;
        }
        if(d == produto.getPrecoTotal())
            return;
        if(!txtProdutoPrecoTotal.hasFocus())
            return;
        try {
            produto.setPrecoTotal(d);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    public  void preencherLista(){
        if (produto.getTipo() == 'G') {
            adapter = new GradeAdapter(getBaseContext(), produto);
            lvProdutoGradeLote.addHeaderView(View.inflate(this, R.layout.item_grade_header, null));
            lvProdutoGradeLote.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if(produto.getTipo() == 'L') {
            adapter = new LoteAdapter(getBaseContext(), produto);
            lvProdutoGradeLote.addHeaderView(View.inflate(this,R.layout.item_lote_header,null));
            lvProdutoGradeLote.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            txtProdutoQtde.setEnabled(true);
            txtProdutoQtde.setFocusable(true);
            txtProdutoQtde.setFocusableInTouchMode(true);
        }
    }


    public void preencherTela(){
        txtProdutoEstoque.setText(produto.getEstoqueToString());
        txtProdutoPreco.setText(produto.getPrecoToString());
        txtProdutoNome.setText((produto.getNome()+""));
        if(!txtProdutoQtde.hasFocus())
            txtProdutoQtde.setText((produto.getQtde()+""));
        if(!txtProdutoDesconto.hasFocus())
            txtProdutoDesconto.setText(produto.getDescontoToString());
        if(!txtProdutoPrecoTotal.hasFocus())
            txtProdutoPrecoTotal.setText(produto.getPrecoTotalToString());
    }

    private void preencherUnidadeVenda(){
        TextView txtProdutoQtdeLabel = (TextView) findViewById(R.id.txtProdutoQtdeLabel);
        txtProdutoQtdeLabel.setText(txtProdutoQtdeLabel.getText() + "(" + produto.getUnidadeVenda()+ ")" );

        TextView txtProdutoEstoqueLabel = (TextView) findViewById(R.id.txtProdutoEstoqueLabel);
        txtProdutoEstoqueLabel.setText(txtProdutoEstoqueLabel.getText() + "(" + produto.getUnidadeVenda() + ")");
    }



    public void onClick_btnProdutoSalvar (){
        ProdutoDAO.Banco.set(id, produto);
        finish();
    }


    public void onClick_btnProdutoApagar (){

        ProdutoDAO.Banco.remove(id);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(br.com.ktek.stackbarcode.R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == br.com.ktek.stackbarcode.R.id.actionProdutoSalvar) {
            onClick_btnProdutoSalvar();
        }else if (id == R.id.actionProdutoApagar) {
            onClick_btnProdutoApagar();
        }

        return super.onOptionsItemSelected(item);
    }
}
