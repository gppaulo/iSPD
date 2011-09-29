/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ispd.motor.filas.servidores;

import ispd.motor.Simulacao;
import ispd.motor.EventoFuturo;
import ispd.motor.filas.Tarefa;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author denison_usuario
 */
public class CS_Internet extends CS_Comunicacao {

    private List<CS_Link> conexoesEntrada;
    private List<CS_Link> conexoesSaida;

    public CS_Internet(String id, double LarguraBanda, double Ocupacao, double Latencia) {
        super(id, LarguraBanda, Ocupacao, Latencia);
        this.conexoesEntrada = new ArrayList<CS_Link>();
        this.conexoesSaida = new ArrayList<CS_Link>();
    }

    public List<CS_Link> getConexoesEntrada() {
        return conexoesEntrada;
    }

    public List<CS_Link> getConexoesSaida() {
        return conexoesSaida;
    }

    public void addConexoesEntrada(CS_Link conexao) {
        this.conexoesEntrada.add(conexao);
    }

    public void addConexoesSaida(CS_Link conexao) {
        this.conexoesSaida.add(conexao);
    }

    @Override
    public void chegadaDeCliente(Simulacao simulacao, Tarefa cliente) {
        //cria evento para iniciar o atendimento imediatamente
        EventoFuturo novoEvt = new EventoFuturo(
                simulacao.getTime(),
                EventoFuturo.ATENDIMENTO,
                this,
                cliente);
        simulacao.getEventos().offer(novoEvt);
    }

    @Override
    public void atendimento(Simulacao simulacao, Tarefa cliente) {
        cliente.iniciarAtendimentoComunicacao(simulacao.getTime());
        //Gera evento para atender proximo cliente da lista
        EventoFuturo evtFut = new EventoFuturo(
                simulacao.getTime() + tempoTransmitir(cliente.getTamComunicacao()),
                EventoFuturo.SAÍDA,
                this, cliente);
        //Event adicionado a lista de evntos futuros
        simulacao.getEventos().offer(evtFut);
    }

    @Override
    public void saidaDeCliente(Simulacao simulacao, Tarefa cliente) {
        //Incrementa o número de Mbits transmitido por este link
        this.getMetrica().incMbitsTransmitidos(cliente.getTamComunicacao());
        //Incrementa o tempo de transmissão
        double tempoTrans = this.tempoTransmitir(cliente.getTamComunicacao());
        this.getMetrica().incSegundosDeTransmissao(tempoTrans);
        //Incrementa o tempo de transmissão no pacote
        cliente.finalizarAtendimentoComunicacao(simulacao.getTime());
        //Gera evento para chegada da tarefa no proximo servidor
        EventoFuturo evtFut = new EventoFuturo(
                simulacao.getTime(),
                EventoFuturo.CHEGADA,
                cliente.getCaminho().remove(0), cliente);
        //Event adicionado a lista de evntos futuros
        simulacao.getEventos().offer(evtFut);
    }

    @Override
    public void requisicao(Simulacao simulacao, Tarefa cliente, int tipo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}