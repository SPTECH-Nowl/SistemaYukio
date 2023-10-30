package sistemaCaptura;

import com.github.britooo.looca.api.core.Looca;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import sistemaCaptura.conexao.Conexao;
import sistemaCaptura.telasSistema.TelaMonitorDeRecursos;
import org.json.JSONObject;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class HistConsmRecurso {

    private Integer idHistorico;
    private LocalDateTime dataHora = LocalDateTime.now();
    private Double consumo;
    private Integer qtd_janelas_abertas;
    private Integer fkMaquina;
    private Integer fkHardware;
    private Integer fkComponente;
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    TelaMonitorDeRecursos telaMonitorDeRecursos = new TelaMonitorDeRecursos();
    Looca looca = new Looca();
    Timer timer = new Timer();
    Timer timer02 = new Timer();


    public HistConsmRecurso() {
    }

    public HistConsmRecurso(Integer idHistorico, LocalDateTime dataHora, Double consumo, Integer qtdJanelasAbertas, Integer fkMaquina, Integer fkHardware, Integer fkComponente) {
        this.idHistorico = idHistorico;
        this.dataHora = dataHora;
        this.consumo = consumo;
        this.qtd_janelas_abertas = qtdJanelasAbertas;
        this.fkMaquina = fkMaquina;
        this.fkHardware = fkHardware;
        this.fkComponente = fkComponente;
    }

    public void mostarHistorico(Integer tipo, Integer idMaquina) {
        switch (tipo) {
            case 1 -> {
                telaMonitorDeRecursos.gerarTelaMonitor(idMaquina);
                insertHistorico(idMaquina);
                MonitorarSoftware(idMaquina);
            }

            case 2 -> {
                System.out.println("Dashboard - prototipo 70%");

                  telaMonitorDeRecursos.BarChart(idMaquina);
                insertHistorico(idMaquina);
                MonitorarSoftware(idMaquina);
            }
            case 3 -> {
                System.out.println("Sistema funcionando sem tela de monitoramento");
                insertHistorico(idMaquina);
                MonitorarSoftware(idMaquina);
            }
            case 4 -> {
                pararSistema();
            }
            default -> System.out.println("Opção invalida!");
        }


    }


    public void insertHistorico(Integer idMaquina) {


        System.out.println("começo a funcionar");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Integer consumoCpu = (looca.getProcessador().getUso()).intValue();
                Long consumoDisco = (long) (looca.getGrupoDeDiscos().getTamanhoTotal() / 8e+9);
                Integer qtdJanelasAbertas = looca.getGrupoDeJanelas().getTotalJanelas();
                long consumoRam = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                dataHora = LocalDateTime.now();


                con.update("INSERT INTO historico (dataHora, consumo, qtdJanelasAbertas, fkComponente, fkHardware, fkMaquina) VALUES(?, ?, ?, ?, ?, ?)", dataHora, consumoCpu, qtdJanelasAbertas, 1, 1, idMaquina);

                con.update("INSERT INTO historico (dataHora, consumo, qtdJanelasAbertas, fkComponente, fkHardware, fkMaquina) VALUES(?, ?, ?, ?, ?, ?)", dataHora, consumoRam, qtdJanelasAbertas, 2, 2, idMaquina);

                con.update("INSERT INTO historico (dataHora, consumo, qtdJanelasAbertas, fkComponente, fkHardware, fkMaquina) VALUES(?, ?, ?, ?, ?, ?)", dataHora, consumoDisco, qtdJanelasAbertas, 3, 3, idMaquina);

                con.update("INSERT INTO historico (dataHora, consumo, qtdJanelasAbertas, fkComponente, fkHardware, fkMaquina) VALUES(?, ?, ?, ?, ?, ?)", dataHora, consumoDisco, qtdJanelasAbertas, 3, 3, idMaquina);


            }

        }, 0, 2000);

    }


    public void MonitorarSoftware(Integer idMaquina) {

        timer02.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder;
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        processBuilder = new ProcessBuilder("tasklist");
                    } else {
                        processBuilder = new ProcessBuilder("ps", "aux");
                    }

                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    BufferedReader Busca = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String linhaBusca;
                    String processo = "MySQLWorkbench.exe";
                    String motivo = "O motivo foi pelo uso do: ";

                    while ((linhaBusca = Busca.readLine()) != null) {

                        if (linhaBusca.contains(processo)) {
                            dataHora = LocalDateTime.now();
                            motivo += processo;
                            con.update("INSERT INTO strike(dataHora,validade,motivo,fkMaquina) VALUES (?,?,?,?)", dataHora, 1, motivo, idMaquina);
                            enviarNotificacao(processo);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10000);
        // roda a cada 10 segundos
    }

    private void enviarNotificacao(String processo)throws IOException, InterruptedException {

            String mensagemAlerta = "O usuario esta com o processo  " + processo +" aberto !!";

        JSONObject json = new JSONObject();

        json.put("text",mensagemAlerta) ;

        BotSlack.sendMessage(json);

    }

    public void pararSistema() {
        System.out.println("Parando sistema...");
        System.exit(0);
    }


    public Integer getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(Integer idHistorico) {
        this.idHistorico = idHistorico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public Integer getQtd_janelas_abertas() {
        return qtd_janelas_abertas;
    }

    public void setQtd_janelas_abertas(Integer qtd_janelas_abertas) {
        this.qtd_janelas_abertas = qtd_janelas_abertas;
    }

    public Integer getFkMaquina() {
        return fkMaquina;
    }

    public void setFkMaquina(Integer fkMaquina) {
        this.fkMaquina = fkMaquina;
    }

    public Integer getFkHardware() {
        return fkHardware;
    }

    public void setFkHardware(Integer fkHardware) {
        this.fkHardware = fkHardware;
    }

    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
    }

    @Override
    public String toString() {
        return "HistConsmRecurso{" +
                "idHistorico=" + idHistorico +
                ", dataHora=" + dataHora +
                ", consumo=" + consumo +
                ", qtd_janelas_abertas=" + qtd_janelas_abertas +
                ", fkMaquina=" + fkMaquina +
                ", fkHardware=" + fkHardware +
                ", fkComponente=" + fkComponente +
                '}';
    }
}


