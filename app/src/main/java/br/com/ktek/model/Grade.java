package br.com.ktek.model;

public class Grade {
    private String Cor;
    private double Estoque;
    private String Codigo;
    private double Qtde;


    private String convertToString(double d){
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    public double getQtde() {
        return Qtde;
    }

    public void setQtde(double qtde) throws Exception {
        if(qtde <= Estoque)
            Qtde = qtde;
        else
            throw new Exception("Quantidade invalida");
    }

    public String getCor() {
        return Cor;
    }

    public void setCor(String cor) {
        Cor = cor;
    }

    public double getEstoque() {
        return Estoque;
    }

    public String getEstoqueString(String UnidadeVenda) {
        return convertToString(Estoque)+ " " + UnidadeVenda ;
    }

    public void setEstoque(double estoque) {
        Estoque = estoque;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getQtdeToString() {
        return convertToString(Qtde);
    }
}
