package br.com.ktek.model;

import java.util.ArrayList;

public class BarCode {
    private String Codigo;
    private double Qtde;
    private String CodigoLote;

    public BarCode(){

    }

    public ArrayList<BarCode> ConvertToBarcode(ArrayList<Produto> produtoList){
        ArrayList<BarCode> lista = new ArrayList<>();
        BarCode barCode;
        for(Produto item : produtoList){
            if(item.getTipo() == 'N') {
                barCode = new BarCode();
                barCode.Codigo = item.getCodigo();
                barCode.Qtde = item.getQtde();
                lista.add(barCode);
            }else  if(item.getTipo() == 'G'){
                for(Grade grade :item.getGrades() ){
                    if(grade.getQtde() > 0 ){
                        barCode = new BarCode();
                        barCode.Codigo = grade.getCodigo();
                        barCode.Qtde = grade.getQtde();
                        lista.add(barCode);
                    }
                }
            }else if(item.getTipo() == 'L'){
                for(Lote lote : item.getLotes()){
                    if (lote.getQtde() > 0) {
                        barCode = new BarCode();
                        barCode.Codigo = item.getCodigo();
                        barCode.Qtde = lote.getQtde();
                        barCode.CodigoLote = lote.getNome();
                        lista.add(barCode);
                    }
                }
            }
        }
        return lista;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public double getQtde() {
        return Qtde;
    }

    public void setQtde(double qtde) {
        Qtde = qtde;
    }

    public String getCodigoLote() {
        return CodigoLote;
    }

    public void setCodigoLote(String lote) {
        CodigoLote = CodigoLote;
    }
}
