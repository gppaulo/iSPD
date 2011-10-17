/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ispd.motor.metricas;

import ispd.motor.filas.RedeDeFilas;
import ispd.motor.filas.Tarefa;
import ispd.motor.filas.servidores.CS_Comunicacao;
import ispd.motor.filas.servidores.CS_Processamento;
import java.util.List;

/**
 *
 * @author denison_usuario
 */
public class MetricasGlobais {
    private double tempoSimulacao;
    private double satisfacaoMedia;
    private double ociosidadeCompuacao;
    private double ociosidadeComunicacao;
    private double eficiencia;
    
    public MetricasGlobais(RedeDeFilas redeDeFilas, double tempoSimulacao, List<Tarefa> tarefas){
        this.tempoSimulacao = tempoSimulacao;
        this.satisfacaoMedia = 100;
        this.ociosidadeCompuacao = getOciosidadeComputacao(redeDeFilas);
        this.ociosidadeComunicacao = getOciosidadeComunicacao(redeDeFilas);
        this.eficiencia = getEficiencia(tarefas);
    }

    public double getEficiencia() {
        return eficiencia;
    }

    public double getOciosidadeCompuacao() {
        return ociosidadeCompuacao;
    }

    public double getOciosidadeComunicacao() {
        return ociosidadeComunicacao;
    }
    
    public double getSatisfacaoMedia() {
        return satisfacaoMedia;
    }

    public double getTempoSimulacao() {
        return tempoSimulacao;
    }

    private double getOciosidadeComputacao(RedeDeFilas redeDeFilas) {
        double tempoLivreMedio = 0.0;
        for (CS_Processamento maquina : redeDeFilas.getMaquinas()) {
            double aux = maquina.getMetrica().getSegundosDeProcessamento();
            aux = (this.getTempoSimulacao() - aux);
            tempoLivreMedio += aux;//tempo livre
            aux = maquina.getOcupacao() * aux;
            tempoLivreMedio -= aux;
        }
        tempoLivreMedio = tempoLivreMedio / redeDeFilas.getMaquinas().size();
        return (tempoLivreMedio * 100) / getTempoSimulacao();
    }
    
    private double getOciosidadeComunicacao(RedeDeFilas redeDeFilas) {
        double tempoLivreMedio = 0.0;
        for (CS_Comunicacao link : redeDeFilas.getLinks()) {
            double aux = link.getMetrica().getSegundosDeTransmissao();
            aux = (this.getTempoSimulacao() - aux);
            tempoLivreMedio += aux; //tempo livre
            aux = link.getOcupacao() * aux;
            tempoLivreMedio -= aux;
        }
        tempoLivreMedio = tempoLivreMedio / redeDeFilas.getLinks().size();
        return (tempoLivreMedio * 100) / getTempoSimulacao();
    }

    private double getEficiencia(List<Tarefa> tarefas) {
        double somaEfic = 0;
        for(Tarefa tar : tarefas){
            somaEfic += tar.getMetricas().getEficiencia();
        }
        return somaEfic / tarefas.size();
        /*
        double tempoUtil = 0.0;
        double tempoMedio = 0.0;
        for (CS_Processamento maquina : redeDeFilas.getMaquinas()) {
            double aux = maquina.getMetrica().getSegundosDeProcessamento();
            aux = (this.getTempoSimulacao() - aux);//tempo livre
            aux = maquina.getOcupacao() * aux;//tempo processando sem ser tarefa
            tempoUtil = aux + maquina.getMetrica().getSegundosDeProcessamento();
            tempoMedio += tempoUtil / this.getTempoSimulacao();
        }
        tempoMedio = tempoMedio / redeDeFilas.getMaquinas().size();
        return tempoMedio; 
         */
    }
    
}
