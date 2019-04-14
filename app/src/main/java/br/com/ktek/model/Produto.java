package  br.com.ktek.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Produto {
    private  int Ordem;
    private String Codigo;
    private String Nome;
    private double Preco;
    private char Tipo;
    private String UnidadeVenda;
    private ArrayList<Grade> Grades;
    private ArrayList<Lote> Lotes;
    private double Estoque;
    private double Desconto;
    private double PrecoTotal;
    private double Promocao;

    private double Qtde;


    public Produto(String codigo, int qtde) {
        this.Codigo = codigo;
        this.Qtde = qtde;
        if (Tipo == 'N')
            Qtde = 1;


    }

    private String convertToString(double d){
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }




    public boolean isPromocao() {
        return Promocao != -1d && Promocao != 0;
    }

    public ArrayList<Grade> getGrades() {
        return Grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        Grades = grades;
        notifyDataChange();
    }

    public ArrayList<Lote> getLotes() {
        return Lotes;
    }

    public void setLotes(ArrayList<Lote> lotes) {
        Lotes = lotes;
        notifyDataChange();
    }

    public char getTipo() {
        return Tipo;
    }

    public void setTipo(char tipo) {
        Tipo = tipo;
        notifyDataChange();
    }

    public String getUnidadeVenda() {
        return UnidadeVenda;
    }

    public void setUnidadeVenda(String unidadeVenda) {
        UnidadeVenda = unidadeVenda;
        notifyDataChange();
    }



    public boolean isCodeBarOnly(){
        if(Nome.equals("Produto não encontrado"))
            return true;
        return false;
    }


    public int getOrdem() {
        return Ordem;
    }

    public void setOrdem(int ordem) {
        Ordem = ordem;
        notifyDataChange();
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        this.Codigo = codigo;
        notifyDataChange();
    }

    public double getQtde() {
        if(Tipo == 'L') {
            Qtde = 0;
            for (Lote l : Lotes) {
                Qtde += l.getQtde();
            }
        }else if(Tipo == 'G') {
            Qtde = 0;
            for (Grade g : Grades) {
                Qtde += g.getQtde();
            }
        }
        return Qtde;
    }

    public String getQtdeToString() {
        return convertToString(getQtde());
    }

    public void setQtde(double qtde) throws Exception {
        if(Tipo != 'N')
            throw new Exception("Produto Tipo Lote ou Grade");
        if(qtde <= Estoque)
            Qtde = qtde;
        else
            throw new Exception("Estoque insuficiente");
        notifyDataChange();
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        this.Nome = nome;

        notifyDataChange();
    }

    public double getEstoque() {
        return Estoque;
    }

    public String getEstoqueToString() {
        return convertToString(getEstoque()) ;
    }

    public void setEstoque(double estoque) {
        this.Estoque = estoque;
        notifyDataChange();
    }

    public double getPrecoOriginal() {
        return Preco;
    }

    public String getPrecoOriginalToString(){
        return String.format("%.2f",getPrecoOriginal());
    }

    public double getPreco() {
        if(isPromocao())
            return Promocao;
        else
            return Preco;
    }

    public String getPrecoToString(){
        return String.format("%.2f",getPreco());
    }

    public double getDesconto() {
        return Desconto;
    }

    public String getDescontoToString() {
        return  String.format("%.2f", getDesconto());
    }

    public void setDesconto(double desconto) throws Exception {
        if (isPromocao())
            throw new Exception("produto na promoção");
        Desconto = desconto;
        PrecoTotal = getPrecoTotal() - (getPrecoTotal() * (Desconto/100));

        notifyDataChange();
    }

    public double getPrecoTotal() {
        PrecoTotal = (getPreco() * getQtde()) - (getPreco() * getQtde() * (getDesconto() / 100));
        return PrecoTotal;
    }

    public String getPrecoTotalToString(){
        String st = new DecimalFormat("#,###0.00").format(getPrecoTotal());
        return st;
    }

    public void setPrecoTotal(double precoTotal) throws Exception {
        if (isPromocao())
            throw new Exception("produto na promoção");
        PrecoTotal = precoTotal;
        Desconto =  ( Preco - ( PrecoTotal / getQtde()) ) / Preco * 100;
        notifyDataChange();
    }



    private Set<OnProdutoItemChange> listners =new HashSet<OnProdutoItemChange>();
    public void onProdutoItemChange(OnProdutoItemChange listener) {
        if (listners == null)
            listners =new HashSet<OnProdutoItemChange>();
        this.listners.add(listener);
    }

    public void removeListener(OnProdutoItemChange listener) {
        if (listners == null)
            listners =new HashSet<OnProdutoItemChange>();
        this.listners.remove(listener);
    }

    public void notifyDataChange() {
        if (listners == null)
            listners =new HashSet<OnProdutoItemChange>();
        Iterator<OnProdutoItemChange> iterator = listners.iterator();

        while (iterator.hasNext()) {
            OnProdutoItemChange listener = (OnProdutoItemChange) iterator.next();
            listener.onValueChange();
        }
    }

}
