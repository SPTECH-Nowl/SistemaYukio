package sistemaCaptura.telasSistema;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
import sistemaCaptura.DadosCaptura;
import sistemaCaptura.conexao.Conexao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JFrame.*;

public class TelaMonitorDeRecursos {
    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();

    java.util.Timer timerTela = new Timer();
    java.util.Timer timerTela2 = new Timer();
    JFrame frame;
    JFrame frame2;
    public void gerarTelaMonitor(Integer idMaquina){

        frame = new JFrame("Monitor de Recursos");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        tableModel.addColumn("Consumo CPU (%)");
        tableModel.addColumn("Consumo Disco");
        tableModel.addColumn("Consumo RAM");
        tableModel.addColumn("Janelas Abertas");
        tableModel.addColumn("Data hora");
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel();

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        timerTela.schedule(new TimerTask() {
            @Override
            public void run() {

                List<DadosCaptura> dadosConsumoCpu = con.query("select dataHora,  consumo , qtdJanelasAbertas from historico where fkHardware =1 and fkMaquina = ? order by idHistorico desc LIMIT 0, 20",
                        new BeanPropertyRowMapper<>(DadosCaptura.class),idMaquina);
                List<DadosCaptura> dadosConsumoRam = con.query("select dataHora,  consumo , qtdJanelasAbertas from historico where fkHardware =2 and fkMaquina = ? order by idHistorico desc LIMIT 0, 20",
                        new BeanPropertyRowMapper<>(DadosCaptura.class),idMaquina);
                List<DadosCaptura> dadosConsumoDisco = con.query("select dataHora,  consumo , qtdJanelasAbertas from historico where fkHardware =3 and fkMaquina = ? order by idHistorico desc LIMIT 0, 20",
                        new BeanPropertyRowMapper<>(DadosCaptura.class),idMaquina);



                tableModel.setRowCount(0);
                for (int i =0 ; i < dadosConsumoCpu.size();i++) {
                    tableModel.addRow(new Object[]{dadosConsumoCpu.get(i).getConsumo(),dadosConsumoDisco.get(i).getConsumo(),dadosConsumoRam.get(i).getConsumo(),dadosConsumoCpu.get(i).getQtdJanelasAbertas(),dadosConsumoCpu.get(i).getDataHora()});

                }
            }
        },0,2000);

        frame.setVisible(true);

    }

    private ArrayList<Bar> bars;

    public void BarChart(Integer idMaquina) {
        timerTela2.schedule(new TimerTask() {
            @Override
            public void run() {


        List<DadosCaptura> dadosConsumoCpu = con.query("select dataHora,  consumo , qtdJanelasAbertas from historico where fkHardware =1 and fkMaquina = ? order by idHistorico desc LIMIT 0, 20",
                new BeanPropertyRowMapper<>(DadosCaptura.class),idMaquina);

        int cpu = dadosConsumoCpu.get(0).getConsumo();
        int cpu2= dadosConsumoCpu.get(1).getConsumo();
        int cpu3 = dadosConsumoCpu.get(2).getConsumo();
        bars = new ArrayList<>();
        bars.add(new Bar("CPU", cpu));
        bars.add(new Bar("CPU", cpu2));
        bars.add(new Bar("CPU", cpu3));

        frame2 = new JFrame("Monitor de Recursos");
        frame2.setSize(400, 300);
        frame2.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame2.setLocationRelativeTo(null);
        frame2.add(new BarChartPanel());
        frame2.setVisible(true);

            }
        },0,2000);
    }

    class Bar {
        String label;
        int value;

        public Bar(String label, int value) {
            this.label = label;
            this.value = value;
        }
    }

    class BarChartPanel extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();
            int barWidth = width / bars.size();
            int maxValue = bars.stream().map(bar -> bar.value).max(Integer::compareTo).orElse(0);

            for (int i = 0; i < bars.size(); i++) {
                Bar bar = bars.get(i);
                int barHeight = (int) ((double) bar.value / maxValue * (height - 20));
                int x = i * barWidth;
                int y = height - barHeight;

                g.setColor(Color.blue);
                g.fillRect(x, y, barWidth, barHeight);

                g.setColor(Color.black);
                g.drawRect(x, y, barWidth, barHeight);

                g.drawString(bar.label, x + barWidth / 2 - 10, height - 5);
            }
        }




    }




}

