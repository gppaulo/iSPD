/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ispd;

import ispd.arquivo.xml.IconicoXML;
import ispd.gui.JResultados;
import ispd.motor.ProgressoSimulacao;
import ispd.motor.Simulacao;
import ispd.motor.SimulacaoParalela;
import ispd.motor.SimulacaoSequencial;
import ispd.motor.filas.RedeDeFilas;
import ispd.motor.filas.Tarefa;
import ispd.motor.filas.servidores.CS_Comunicacao;
import ispd.motor.filas.servidores.CS_Processamento;
import ispd.motor.filas.servidores.implementacao.CS_Link;
import ispd.motor.filas.servidores.implementacao.CS_Maquina;
import ispd.motor.filas.servidores.implementacao.CS_Mestre;
import ispd.motor.metricas.Metricas;
import ispd.motor.metricas.MetricasGlobais;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author denison
 */
public class Terminal {

    private File arquivoIn = null;
    private File arquivoOut = null;
    private File configuracao = null;
    private int opcao;
    private int numExecucoes;
    private int numThreads;
    private ProgressoSimulacao progrSim;
    private boolean paralelo = false;
    private boolean visible = true;
    private int port = 2004;
    //Resultados
    private double tempoSimulacao = 0;
    private double satisfacaoMedia = 0;
    private double ociosidadeComputacao = 0;
    private double ociosidadeComunicacao = 0;
    private double eficiencia = 0;

    public Terminal(String[] args) {
        if (args[0].equals("help") || args[0].equals("-help") || args[0].equals("-h")) {
            opcao = 0;
        } else if (args[0].equals("-server")) {
            if (!args[1].equals("-th")) {
                port = Integer.parseInt(args[1]);
            } else {
                port = 2004;
            }
            opcao = 2;
            if (args.length > 2 && args[2].equals("-th")) {
                numThreads = Integer.parseInt(args[3]);
            } else {
                numThreads = 1;
            }
            numExecucoes = 1;
            progrSim = new ProgressoSimulacao() {
                @Override
                public void incProgresso(int n) {
                }

                @Override
                public void print(String text, Color cor) {
                }
            };
        } else if (args[0].equals("-client")) {
            opcao = 3;
            numThreads = 1;
            configuracao = new File(args[1]);
            arquivoIn = new File(args[2]);
            progrSim = new ProgressoSimulacao() {
                @Override
                public void incProgresso(int n) {
                }

                @Override
                public void print(String text, Color cor) {
                    if (visible) {
                        System.out.print(text);
                    }
                }
            };
        } else {
            int atual = 0;
            numThreads = 1;
            numExecucoes = 1;
            while (args[atual].charAt(0) == '-') {
                if (args[atual].equals("-n")) {
                    numExecucoes = Integer.parseInt(args[atual + 1]);
                    atual += 2;
                } else if (args[atual].equals("-th")) {
                    numThreads = Integer.parseInt(args[atual + 1]);
                    atual += 2;
                } else if (args[atual].equals("-o")) {
                    atual++;
                    String dirSaida = args[atual];
                    while (args[atual].charAt(args[atual].length() - 1) == '\\') {
                        atual++;
                        dirSaida += " " + args[atual];
                    }
                    arquivoOut = new File(dirSaida);
                    atual++;
                } else if (args[atual].equals("-p")) {
                    paralelo = true;
                    atual++;
                } else {
                    atual++;
                }
            }
            opcao = 1;
            String nomeArquivo = args[atual];
            for (int i = atual + 1; i < args.length; i++) {
                nomeArquivo = nomeArquivo + " " + args[i];
            }
            arquivoIn = new File(nomeArquivo);
            progrSim = new ProgressoSimulacao() {
                @Override
                public void incProgresso(int n) {
                }

                @Override
                public void print(String text, Color cor) {
                    if (visible) {
                        System.out.print(text);
                    }
                }
            };
        }
    }

    void executar() {
        switch (opcao) {
            case 0:
                System.out.println("Usage: java -jar iSPD.jar");
                System.out.println("\t\t(to execute the graphical interface of the iSPD)");
                System.out.println("\tjava -jar iSPD.jar [option] [model file.imsx]");
                System.out.println("\t\t(to execute a model in terminal)");
                System.out.println("where options include:");
                System.out.println("\t-n <number>\tnumber of simulation");
                System.out.println("\t-th <number>\tnumber of threads");
                System.out.println("\t-p \tOptimistic parallel simulation");
                System.out.println("\t-o <directory>\tdirectory to save html output");
                System.out.println("\t-server <port>");
                System.out.println("\t-client <> <model file.imsx>");
                System.out.println("\t-help\tprint this help message");
                break;
            case 1:
                if (arquivoIn.getName().endsWith(".imsx") && arquivoIn.exists()) {
                    if (numThreads > 1 && !paralelo) {
                        this.simularParalelo();
                    } else {
                        this.simularSequencial();
                    }
                } else {
                    System.out.println("iSPD can not open the file: " + arquivoIn.getName());
                }
                break;
            case 2:
                this.simularRedeServidor();
                break;
            case 3:
                Object conf[] = lerConfiguracao(configuracao);
                String server[] = new String[conf.length / 3];//{"localhost","localhost"};
                int ports[] = new int[conf.length / 3];//{2005,2006};
                int numSim[] = new int[conf.length / 3];//{15,15};
                for (int i = 0, j = 0; i < server.length; i++) {
                    server[i] = (String) conf[j];
                    j++;
                    ports[i] = Integer.parseInt(conf[j].toString());
                    j++;
                    numSim[i] = Integer.parseInt(conf[j].toString());
                    j++;
                }
                this.simularRedeCliente(server, ports, numSim);
                break;
        }
    }

    private void simularSequencial() {
        progrSim.println("Simulation Initiated.");
        try {
            progrSim.print("Opening iconic model.");
            progrSim.print(" -> ");
            Document modelo = IconicoXML.ler(arquivoIn);
            progrSim.println("OK", Color.green);
            //Verifica se foi construido modelo corretamente
            progrSim.validarInicioSimulacao(modelo);
            //Escrever Modelo
            //this.modelo(redeDeFilas);
            //criar tarefas

            Metricas metricas = new Metricas(IconicoXML.newListUsers(modelo));
            double total = 0;

            for (int i = 1; i <= numExecucoes; i++) {
                double t1 = System.currentTimeMillis();
                progrSim.println("* Simulation " + i);
                //criar grade
                progrSim.print("  Mounting network queue.");
                progrSim.print(" -> ");
                RedeDeFilas redeDeFilas = IconicoXML.newRedeDeFilas(modelo);
                progrSim.println("OK", Color.green);
                progrSim.print("  Creating tasks.");
                progrSim.print(" -> ");
                List<Tarefa> tarefas = IconicoXML.newGerarCarga(modelo).toTarefaList(redeDeFilas);
                progrSim.print("OK\n  ", Color.green);
                //Verifica recursos do modelo e define roteamento
                Simulacao sim;
                if (!paralelo) {
                    sim = new SimulacaoSequencial(progrSim, redeDeFilas, tarefas);//[10%] --> 55 %
                } else {
                    System.out.println("Execução paralela da simulação");
                    sim = new SimulacaoParalela(progrSim, redeDeFilas, tarefas, numThreads);
                }
                //Realiza asimulação
                progrSim.println("  Simulating.");
                //recebe instante de tempo em milissegundos ao iniciar a simulação
                sim.simular();//[30%] --> 85%
                if (arquivoOut == null) {
                    this.addResultadosGlobais(new MetricasGlobais(redeDeFilas, sim.getTime(null), tarefas));
                } else {
                    Metricas temp = sim.getMetricas();
                    metricas.addMetrica(temp);
                    this.addResultadosGlobais(temp.getMetricasGlobais());
                }
                //Recebe instante de tempo em milissegundos ao fim da execução da simulação
                double t2 = System.currentTimeMillis();
                //Calcula tempo de simulação em segundos
                double tempototal = (t2 - t1) / 1000;
                total += tempototal;
                progrSim.println("  Simulation Execution Time = " + tempototal + "seconds");
            }
            if (numExecucoes > 1 && arquivoOut != null) {
                metricas.calculaMedia();
            }
            progrSim.println("Results:");
            if (numExecucoes > 1) {
                progrSim.println("  Total Simulation Execution Time = " + total + "seconds");
            }
            if (arquivoOut != null) {
                double t1 = System.currentTimeMillis();
                JResultados result = new JResultados(metricas);
                result.salvarHTML(arquivoOut);
                double t2 = System.currentTimeMillis();
                //Calcula tempo de simulação em segundos
                double tempototal = (t2 - t1) / 1000;
                progrSim.println("  Time to create html = " + tempototal + "seconds");
            }
            progrSim.println(this.getResultadosGlobais());
        } catch (Exception erro) {
            progrSim.println(erro.getMessage(), Color.red);
            progrSim.print("Simulation Aborted", Color.red);
            progrSim.println("!", Color.red);
        }
    }

    private void simularParalelo() {
        progrSim.println("Simulation Initiated.");
        try {
            progrSim.print("Opening iconic model.");
            progrSim.print(" -> ");
            progrSim.incProgresso(5);//[5%] --> 5%
            progrSim.println("OK", Color.green);
            //Escrever Modelo
            //this.modelo(redeDeFilas);
            //criar tarefas

            if (numThreads > numExecucoes) {
                numThreads = numExecucoes;
            }
            Document[] modelo = IconicoXML.clone(arquivoIn, numThreads);
            Metricas metricas = new Metricas(IconicoXML.newListUsers(modelo[0]));
            //Verifica se foi construido modelo corretamente
            progrSim.validarInicioSimulacao(modelo[0]);
            int inicio = 0, incremento = numExecucoes / numThreads;
            RunnableImpl[] trabalhador = new RunnableImpl[numThreads];
            Thread[] thread = new Thread[numThreads];
            System.out.println("Será executado com " + numThreads + " threads");
            visible = false;
            double t1 = System.currentTimeMillis();
            for (int i = 0; i < numThreads - 1; i++) {
                trabalhador[i] = new RunnableImpl(modelo[i], inicio, incremento);
                thread[i] = new Thread(trabalhador[i]);
                thread[i].start();
                inicio += incremento;
            }
            trabalhador[numThreads - 1] = new RunnableImpl(modelo[numThreads - 1], inicio, numExecucoes - inicio);
            thread[numThreads - 1] = new Thread(trabalhador[numThreads - 1]);
            thread[numThreads - 1].start();
            for (int i = 0; i < numThreads; i++) {
                thread[i].join();
            }
            visible = true;
            double t2 = System.currentTimeMillis();
            //Calcula tempo de simulação em segundos
            double tempototal = (t2 - t1) / 1000;
            progrSim.println("  Total Simulation Execution Time = " + tempototal + "seconds");
            progrSim.print("Getting Results.");
            progrSim.print(" -> ");
            if (numExecucoes > 1 && arquivoOut != null) {
                for (int i = 0; i < numThreads; i++) {
                    metricas.addMetrica(trabalhador[i].getMetricas());
                    this.addResultadosGlobais(trabalhador[i].getMetricasGlobais());
                }
                metricas.calculaMedia();
            } else {
                for (int i = 0; i < numThreads; i++) {
                    this.addResultadosGlobais(trabalhador[i].getMetricasGlobais());
                }
            }
            progrSim.println("OK");
            progrSim.println("Results:");
            if (arquivoOut != null) {
                t1 = System.currentTimeMillis();
                JResultados result = new JResultados(metricas);
                result.salvarHTML(arquivoOut);
                t2 = System.currentTimeMillis();
                //Calcula tempo de simulação em segundos
                tempototal = (t2 - t1) / 1000;
                progrSim.println("  Time to create html = " + tempototal + "seconds");
            }
            progrSim.println(this.getResultadosGlobais());
        } catch (Exception erro) {
            progrSim.println(erro.getMessage(), Color.red);
            progrSim.print("Simulation Aborted", Color.red);
            progrSim.println("!", Color.red);
        }
    }

    private void simularRedeServidor() {
        Document modelo = null;
        Metricas metricas = new Metricas(null);
        String origem = null;
        //Recebendo Modelo
        try {
            System.out.println("Creating a server socket");
            ServerSocket providerSocket = new ServerSocket(port, 10);
            System.out.println("Waiting for connection");
            Socket connection = providerSocket.accept();
            origem = connection.getInetAddress().getHostName();
            System.out.println("Connection received from " + origem);
            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            System.out.println("Recebendo mensagem");
            numExecucoes = (Integer) in.readObject();
            System.out.println("Será feitas " + numExecucoes + " simulações");
            modelo = (Document) in.readObject();
            in.close();
            System.out.println("Closing connection");
            providerSocket.close();
        } catch (Exception ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Executando simulação
        if (numThreads <= 1) {
            System.out.println("Será realizado " + numExecucoes + " simulações.");
            double t1 = System.currentTimeMillis();
            for (int i = 1; i <= numExecucoes; i++) {
                RedeDeFilas redeDeFilas = IconicoXML.newRedeDeFilas(modelo);
                List<Tarefa> tarefas = IconicoXML.newGerarCarga(modelo).toTarefaList(redeDeFilas);
                Simulacao sim = new SimulacaoSequencial(progrSim, redeDeFilas, tarefas);//[10%] --> 55 %
                sim.simular();//[30%] --> 85%
                Metricas temp = sim.getMetricas();
                metricas.addMetrica(temp);
            }
            double t2 = System.currentTimeMillis();
            double tempototal = (t2 - t1) / 1000;
            System.out.println("  Simulation Execution Time = " + tempototal + "seconds");
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        //Enviando Resultados
        System.out.println("Devolvendo resultados...");
        try {
            System.out.println("Creating a server socket");
            Socket requestSocket = new Socket(origem, 2004);
            System.out.println("Connection received from " + requestSocket.getInetAddress().getHostName());
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            System.out.println("Mensagem... resultados obtidos: " + metricas);
            out.flush();
            out.writeObject(metricas);
            out.flush();
            out.close();
            System.out.println("Closing connection");
            requestSocket.close();
        } catch (Exception ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void simularRedeCliente(String servers[], int ports[], int numSim[]) {
        //Obtem modelo
        progrSim.print("Opening iconic model.");
        progrSim.print(" -> ");
        Document modelo = null;
        try {
            modelo = IconicoXML.ler(arquivoIn);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        progrSim.println("OK", Color.green);
        //Verifica se foi construido modelo corretamente
        progrSim.validarInicioSimulacao(modelo);
        //Enviar modelo...
        for (int i = 0; i < servers.length; i++) {
            try {
                System.out.println("Creating a server socket to " + servers[i] + " " + ports[i]);
                Socket requestSocket = new Socket(servers[i], ports[i]);
                System.out.println("Connection received from " + requestSocket.getInetAddress().getHostName());
                ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                System.out.println("Mensagem... número de execuções: " + numSim[i]);
                out.flush();
                out.writeObject(numSim[i]);
                out.flush();
                out.writeObject(modelo);
                out.flush();
                out.close();
                System.out.println("Closing connection");
                requestSocket.close();
            } catch (Exception ex) {
                Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Metricas metricas = new Metricas(IconicoXML.newListUsers(modelo));
        //Recebendo resultados
        try {
            System.out.println("Creating a server socket");
            ServerSocket providerSocket = new ServerSocket(2004, 10);
            for (int i = 0; i < servers.length; i++) {
                System.out.println("Waiting for connection");
                Socket connection = providerSocket.accept();
                System.out.println("Connection received from " + connection.getInetAddress().getHostName());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                System.out.println("Recebendo mensagem");
                metricas.addMetrica((Metricas) in.readObject());
                in.close();
            }
            System.out.println("Closing connection");
            providerSocket.close();
        } catch (Exception ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Apresentando Resultados
        System.out.println("Realizados " + metricas.getNumeroDeSimulacoes() + " simulações");
    }

    private void addResultadosGlobais(MetricasGlobais globais) {
        this.tempoSimulacao += globais.getTempoSimulacao();
        this.satisfacaoMedia += globais.getSatisfacaoMedia();
        this.ociosidadeComputacao += globais.getOciosidadeComputacao();
        this.ociosidadeComunicacao += globais.getOciosidadeComunicacao();
        this.eficiencia += globais.getEficiencia();
    }

    private String getResultadosGlobais() {
        String texto = "\t\tSimulation Results\n\n";
        texto += String.format("\tTotal Simulated Time = %g \n", tempoSimulacao / numExecucoes);
        texto += String.format("\tSatisfaction = %g %%\n", satisfacaoMedia / numExecucoes);
        texto += String.format("\tIdleness of processing resources = %g %%\n", ociosidadeComputacao / numExecucoes);
        texto += String.format("\tIdleness of communication resources = %g %%\n", ociosidadeComunicacao / numExecucoes);
        texto += String.format("\tEfficiency = %g %%\n", eficiencia / numExecucoes);
        if (eficiencia / numExecucoes > 70.0) {
            texto += "\tEfficiency GOOD\n ";
        } else if (eficiencia / numExecucoes > 40.0) {
            texto += "\tEfficiency MEDIA\n ";
        } else {
            texto += "\tEfficiency BAD\n ";
        }
        return texto;
    }

    private void modelo(RedeDeFilas redeDeFilas) {
        int cs_maq = 0, cs_link = 0, cs_mestre = 0;

        for (CS_Maquina maq : redeDeFilas.getMaquinas()) {
            if (maq instanceof CS_Maquina) {
                cs_maq++;
            }
        }
        for (CS_Comunicacao link : redeDeFilas.getLinks()) {
            if (link instanceof CS_Link) {
                cs_link++;
            }
        }
        for (CS_Processamento mestre : redeDeFilas.getMestres()) {
            if (mestre instanceof CS_Mestre) {
                cs_mestre++;
            }
        }
        progrSim.println("* Grid:");
        progrSim.println("  - Number of Masters: " + cs_mestre);
        progrSim.println("  - Number of Slaves: " + cs_maq);
        progrSim.println("  - Number of Links: " + cs_link);
        //progrSim.println("  - Number of Tasks: "+task);
    }

    private Object[] lerConfiguracao(File configuracao) {
        ArrayList config = new ArrayList();
        FileReader arq = null;
        try {
            arq = new FileReader(configuracao);
            BufferedReader lerArq = new BufferedReader(arq);
            String linha = lerArq.readLine();
            while (linha != null) {
                config.addAll(Arrays.asList(linha.split(" ")));
                System.out.printf("%s\n", linha);
                linha = lerArq.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                arq.close();
            } catch (IOException ex) {
                Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
            }
            return config.toArray();
        }
    }

    private class RunnableImpl implements Runnable {

        private final Document modelo;
        private final int numExecucaoThread;
        private final int inicio;
        private Metricas metricas;
        private MetricasGlobais metricasGlobais;

        public RunnableImpl(Document modelo, int inicio, int numExecucao) {
            this.modelo = modelo;
            this.numExecucaoThread = numExecucao;
            this.inicio = inicio;
            this.metricas = new Metricas(null);
            this.metricasGlobais = new MetricasGlobais();
        }

        public MetricasGlobais getMetricasGlobais() {
            return metricasGlobais;
        }

        public Metricas getMetricas() {
            System.out.println("Simulados: " + metricas.getNumeroDeSimulacoes());
            return metricas;
        }

        @Override
        public void run() {
            for (int i = 0; i < numExecucaoThread; i++) {
                double t1 = System.currentTimeMillis();
                //criar grade
                RedeDeFilas redeDeFilas;
                redeDeFilas = IconicoXML.newRedeDeFilas(modelo);
                List<Tarefa> tarefas = IconicoXML.newGerarCarga(modelo).toTarefaList(redeDeFilas);
                //Verifica recursos do modelo e define roteamento
                Simulacao sim = new SimulacaoSequencial(progrSim, redeDeFilas, tarefas);//[10%] --> 55 %
                //Realiza asimulação
                sim.simular();//[30%] --> 85%
                if (arquivoOut == null) {
                    MetricasGlobais global = new MetricasGlobais(redeDeFilas, sim.getTime(null), tarefas);
                    metricasGlobais.setTempoSimulacao(metricasGlobais.getTempoSimulacao() + global.getTempoSimulacao());
                    metricasGlobais.setSatisfacaoMedia(metricasGlobais.getSatisfacaoMedia() + global.getSatisfacaoMedia());
                    metricasGlobais.setOciosidadeComputacao(metricasGlobais.getOciosidadeComputacao() + global.getOciosidadeComputacao());
                    metricasGlobais.setOciosidadeComunicacao(metricasGlobais.getOciosidadeComunicacao() + global.getOciosidadeComunicacao());
                    metricasGlobais.setEficiencia(metricasGlobais.getEficiencia() + global.getEficiencia());
                } else {
                    Metricas temp = sim.getMetricas();
                    metricas.addMetrica(temp);
                    MetricasGlobais global = temp.getMetricasGlobais();
                    metricasGlobais.setTempoSimulacao(metricasGlobais.getTempoSimulacao() + global.getTempoSimulacao());
                    metricasGlobais.setSatisfacaoMedia(metricasGlobais.getSatisfacaoMedia() + global.getSatisfacaoMedia());
                    metricasGlobais.setOciosidadeComputacao(metricasGlobais.getOciosidadeComputacao() + global.getOciosidadeComputacao());
                    metricasGlobais.setOciosidadeComunicacao(metricasGlobais.getOciosidadeComunicacao() + global.getOciosidadeComunicacao());
                    metricasGlobais.setEficiencia(metricasGlobais.getEficiencia() + global.getEficiencia());
                }
                double t2 = System.currentTimeMillis();
                //Calcula tempo de simulação em segundos
                double tempototal = (t2 - t1) / 1000;
                System.out.println("* Simulation " + (inicio + 1 + i) + " Time = " + tempototal + "seconds");
            }
        }
    }
}
