/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author denison_usuario
 */
package ispd.escalonador;

import ispd.motor.filas.Tarefa;
import ispd.motor.filas.servidores.CS_Processamento;
import ispd.motor.filas.servidores.CentroServico;
import ispd.motor.metricas.MetricasUsuarios;
import java.util.List;

public abstract class Escalonador {
    //Atributos
    protected List<CS_Processamento> escravos;
    protected List<Tarefa> tarefas;
    protected MetricasUsuarios metricaUsuarios;
    protected Mestre mestre;
    /**
     * Armazena os caminhos possiveis para alcançar cada escravo
     */
    protected List<List> caminhoEscravo;

    //Métodos

    public abstract void iniciar();

    public abstract Tarefa escalonarTarefa();

    public abstract CS_Processamento escalonarRecurso();

    public abstract List<CentroServico> escalonarRota(CentroServico destino);

    public abstract void escalonar();

    public void adicionarTarefa(Tarefa tarefa){
        if(tarefa.getOrigem().equals(mestre)){
            this.metricaUsuarios.incTarefasSubmetidas(tarefa);
        }
        this.tarefas.add(tarefa);
    }

    //Get e Set

    public List<CS_Processamento> getEscravos() {
        return escravos;
    }

    public void setCaminhoEscravo(List<List> caminhoEscravo) {
        this.caminhoEscravo = caminhoEscravo;
    }

    public void addEscravo(CS_Processamento maquina) {
        this.escravos.add(maquina);
    }
    
    public void addTarefaConcluida(Tarefa tarefa) {
        if(tarefa.getOrigem().equals(mestre)){
            this.metricaUsuarios.incTarefasConcluidas(tarefa);
        }
    }
    
    public List<Tarefa> getFilaTarefas() {
        return this.tarefas;
    }

    public MetricasUsuarios getMetricaUsuarios() {
        return metricaUsuarios;
    }

    public void setMetricaUsuarios(MetricasUsuarios metricaUsuarios) {
        this.metricaUsuarios = metricaUsuarios;
    }

    public void setMestre(Mestre mestre) {
        this.mestre = mestre;
    }

    public List<List> getCaminhoEscravo() {
        return caminhoEscravo;
    }
    
    public Double getTempoAtualizar(){
        return null;
    }
}
