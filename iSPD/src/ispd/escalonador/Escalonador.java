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
import java.util.ArrayList;
import java.util.List;

public abstract class Escalonador {
    //Atributos
    protected List<CS_Processamento> escravos;
    protected List<Tarefa> tarefas;
    /**
     * Armazena os caminhos possiveis para alcançar cada escravo
     */
    protected List<List> caminhoEscravo;

    //Métodos

    public abstract void iniciar();

    public abstract void atualizar();

    public abstract Tarefa escalonarTarefa();

    public abstract CS_Processamento escalonarRecurso();

    public abstract List<CentroServico> escalonarRota(CentroServico destino);

    public abstract void escalonar(Mestre mestre);

    public abstract void adicionarTarefa(Tarefa tarefa);

    public abstract void adicionarFilaTarefa(ArrayList<Tarefa> tarefa);

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

    public List<Tarefa> getFilaTarefas() {
        return this.tarefas;
    }
}
