package sistemaCaptura;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import sistemaCaptura.conexao.Conexao;

import java.util.List;
import java.util.Scanner;

public class AppHistorico {
    public static void main(String[] args) {


        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        HistConsmRecurso histConsmRecurso = new HistConsmRecurso();
        Scanner in = new Scanner(System.in);
        Scanner leitor = new Scanner(System.in);
        Integer escolha;


        do {
            System.out.println("-".repeat(15));
            System.out.println("Bem vindo ao sistema Nowl");
            System.out.println("Escolha uma das opçções abaixo");
            System.out.println("1 - fazer login");
            System.out.println("2 - Sair");
            System.out.println("-".repeat(15));

            escolha = in.nextInt();
            switch (escolha) {
                case 1 -> {
                    System.out.println("Digite o seu email");
                    String email = leitor.nextLine();

                    System.out.println("Digite o sua senha");
                    String senha = leitor.nextLine();

                    List<Usuario> usuario = con.query("""  
                            select
                            i.idInstituicao,
                            u.fkInstituicao,
                            u.nome,
                            u.idUsuario
                             from instituicao i JOIN usuario u where  email=? AND senha =?;
                            """, new BeanPropertyRowMapper<>(Usuario.class), email, senha);
// teste@ 111 000000

                    if (usuario.size() > 0) {
                        System.out.println("Bem vindo " + usuario.get(0).getNome());
                        Integer opcaoUsuario;
                        do {
                            System.out.println("-".repeat(15));
                            System.out.println("Escolha uma das opçções abaixo");
                            System.out.println("1 - ativar maquina");
                            System.out.println("2 - Fechar sistema");
                            System.out.println("-".repeat(15));

                            opcaoUsuario = in.nextInt();
                            switch (opcaoUsuario) {
                                case 1 -> {
                                    List<Maquina> maquinas = con.query("""  
                                            select * from maquina where fkInstituicao=? AND emUso =0;
                                            """, new BeanPropertyRowMapper<>(Maquina.class), usuario.get(0).getIdInstituicao());
                                    System.out.println("-".repeat(15));

                                    System.out.println("Escolha uma maquina disponivel");
                                    for (int i = 0; i < maquinas.size(); i++) {
                                        System.out.println(String.format("""
                                                id: %d - nome: %s
                                                  """, maquinas.get(i).getIdMaquina(), maquinas.get(i).getNome()));
                                    }
                                    System.out.println("-".repeat(15));
                                    System.out.println("Digite o id da maquina que deseja utilizar");
                                    Integer numMaquina = in.nextInt();

                                    Integer escolhaTipo;
                                    do {
                                        System.out.println("-".repeat(15));
                                        System.out.println("""
                                                Escolha uma opção 
                                                1- Tela
                                                2- Dashboard - prototipo 70%
                                                3- Sem Visualização
                                                4- Sair""");
                                        System.out.println("-".repeat(15));

                                        escolhaTipo = in.nextInt();

                                        histConsmRecurso.mostarHistorico(escolhaTipo, numMaquina);
                                    } while (!escolhaTipo.equals(4));

                                }
                                case 2 -> histConsmRecurso.pararSistema();
                                default -> System.out.println("Opção invalida");

                            }

                        } while (opcaoUsuario != 2);

                    } else {
                        System.out.println("Dados da intituição ou de usuario invalidos");
                    }


                }

                default -> System.out.println("Opção invalida ");

            }

        } while (escolha != 2);


    }
}