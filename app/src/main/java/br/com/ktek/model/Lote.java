package br.com.ktek.model;

public class Lote {
    private String Nome;
    private double Estoque;
    private double Qtde;




    private String convertToString(double d){
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }


    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
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

    public double getQtde() {
        return Qtde;
    }

    public void setQtde(double qtde) throws Exception {
        if(qtde <= Estoque)
            Qtde = qtde;
        else
            throw new Exception("Quantidade invalida");
    }

    public String getQtdeToString() {
        return convertToString(Qtde);
    }
}
