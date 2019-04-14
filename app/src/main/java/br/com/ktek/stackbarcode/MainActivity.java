package br.com.ktek.stackbarcode;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import br.com.ktek.connection.HttpGetRequest;
import br.com.ktek.connection.TcpClient;
import br.com.ktek.model.BarCode;
import br.com.ktek.model.Produto;
import br.com.ktek.model.ProdutoDAO;


public class MainActivity extends FragmentActivity implements IScanResultHandler {

    BarcodeFragment fragment;
    ListView lvBarcode;
    ProductAdapter adapter;
    TextView txtAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        lvBarcode = (ListView)findViewById(R.id.lvBarcodes);
        lvBarcode.setOnItemClickListener(onItemClicklvBarcode());
        txtAdd = (TextView)findViewById(R.id.txtAdd);
        adapter = new ProductAdapter(this, ProdutoDAO.Banco);
        lvBarcode.setAdapter(adapter);
        fragment = (BarcodeFragment)getSupportFragmentManager().findFragmentById(R.id.sample);
        fragment.setScanResultHandler(this);
    }

    @Override
    protected void onResume (){
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void scanResult(ScanResult scanResult) {
        String code = scanResult.getRawResult().getText();
        if(scanResult.getRawResult().getBarcodeFormat() == BarcodeFormat.QR_CODE) {
            JSONObject json;
            try {


                json = new JSONObject(scanResult.getRawResult().getText());

                new TcpClient(json.getString("ip"),json.getInt("port"),new Gson().toJson(new BarCode().ConvertToBarcode(ProdutoDAO.Banco)));

            } catch (JSONException e) {
                Log.d("js",e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }
        }
        else {
            try {
                addProduto(code);
            } catch (Exception e) {
                Log.d("js", e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        scanAgain();
    }

    public void onClickBtnAdd(View view){
        String code = txtAdd.getText().toString();

        try{
            addProduto(code);
            txtAdd.setText("");
        }
        catch (Exception e){
            Log.d("js", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickBtnLimparLista (){
        new AlertDialog.Builder(this)
                .setTitle("Deletar lista")
                .setMessage("Voce quer realmente deletar a lista?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ProdutoDAO.Banco.clear();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void onClickBtnAtualizar (){
        TextView txtLink = new TextView(this);
        txtLink.setText(Html.fromHtml("<a href=\"http://192.168.0.50/update/stackbarcode.apk\">atualizar </a> "));
        txtLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtLink.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        txtLink.setGravity(Gravity.CENTER_VERTICAL);
        new AlertDialog.Builder(this)
                .setTitle("Atualizar")
                .setView(txtLink)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public AdapterView.OnItemClickListener onItemClicklvBarcode(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),ProductActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        };
    }

    public void addProduto(String code) throws Exception{
        if(code.trim().isEmpty())
            throw new Exception("Codigo inválido");
        if(isProdutoAdd(code))
            throw new Exception("Produto já adicionado");


        HttpGetRequest httpGetRequest = new HttpGetRequest();
        String execute ;
        Produto produto ;
        try {
            execute = httpGetRequest.execute("http://192.168.0.50/webservice/api/barcode/getnomeproduto/" + code).get(15000, TimeUnit.MILLISECONDS);
            // httpGetRequest.get(15000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            addCodeBar(code);
            throw new Exception("Servidor não emcontrado " + e.getMessage());
        }
        if(execute.isEmpty()) {
            addCodeBar(code);
            throw new Exception("Produto não encontrado");
        }
        if(!execute.substring(0,1).equals("{")) {
            addCodeBar(code);
            throw new Exception("Erro no servidor");
        }
        produto = new Gson().fromJson(execute, Produto.class);


        if (produto == null)
            throw new Exception("Erro no servidor");

            try {
                if (produto.getTipo() == 'N')
                    produto.setQtde(1);
            }catch (Exception e){
                throw new Exception(e.getMessage());
            }finally {
                addListProduto(produto);
            }

    }


    public void addCodeBar(String code){
        Produto produto = new Produto(code, 0);
        produto.setNome("Produto não encontrado");
        addListProduto(produto);
    }
    public void addListProduto (Produto produto){
        ProdutoDAO.Banco.add(produto);
        adapter.notifyDataSetChanged();
    }
    public boolean isProdutoAdd(String code){
        for (Produto item : ProdutoDAO.Banco){
            if ( item.getCodigo().equals(code))
                return true;
        }
        return false;
    }

    public void scanAgain(){
        fragment.restart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnLimparLista) {
            onClickBtnLimparLista();
        } else if (id == R.id.btnAtualizar) {
            onClickBtnAtualizar();
        }

        return super.onOptionsItemSelected(item);
    }


}
