package sistemaCaptura;

public class DadosCaptura {
    private String dataHora;
    private Integer consumo;
    private Integer qtdJanelasAbertas;

    public  DadosCaptura(){}
    public DadosCaptura(String dataHora, Integer consumo, Integer qtdJanelasAbertas) {
        this.dataHora = dataHora;
        this.consumo = consumo;
        this.qtdJanelasAbertas = qtdJanelasAbertas;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getConsumo() {
        return consumo;
    }

    public void setConsumo(Integer consumo) {
        this.consumo = consumo;
    }

    public Integer getQtdJanelasAbertas() {
        return qtdJanelasAbertas;
    }

    public void setQtdJanelasAbertas(Integer qtdJanelasAbertas) {
        this.qtdJanelasAbertas = qtdJanelasAbertas;
    }

    @Override
    public String toString() {
        return "DadosCaptura{" +
                "dataHora='" + dataHora + '\'' +
                ", consumo=" + consumo +
                ", qtdJanelasAbertas=" + qtdJanelasAbertas +
                '}';
    }
}
