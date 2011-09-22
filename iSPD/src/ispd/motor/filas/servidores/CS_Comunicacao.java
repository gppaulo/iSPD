/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ispd.motor.filas.servidores;

import ispd.motor.metricas.MetricasComunicacao;

/**
 * Classe abstrata que representa os servidores de comunicação do modelo de fila,
 * Esta classe possui atributos referente a este ripo de servidor, e indica como
 * calcular o tempo gasto para transmitir uma tarefa.
 * @author denison_usuario
 */
public abstract class CS_Comunicacao extends CentroServico {
    /**
     * Identificador do centro de serviço, deve ser o mesmo do modelo icônico
     */
    private String id;
    private double larguraBanda;
    private double ocupacao;
    private double latencia;
    private MetricasComunicacao metrica;
    private double larguraBandaDisponivel;

    public CS_Comunicacao(String id, double LarguraBanda, double Ocupacao, double Latencia) {
        this.id = id;
        this.larguraBanda = LarguraBanda;
        this.ocupacao = Ocupacao;
        this.latencia = Latencia;
        this.metrica = new MetricasComunicacao();
        this.larguraBandaDisponivel = this.larguraBanda - (this.larguraBanda * this.ocupacao);
    }

    public MetricasComunicacao getMetrica() {
        return metrica;
    }

    public String getId(){
        return id;
    }

    public double getLarguraBanda() {
        return larguraBanda;
    }

    public double getLatencia() {
        return latencia;
    }
    /**
     * Retorna o tempo gasto
     * @param Mbits
     */
    public double tempoTransmitir(double Mbits){
        return ( Mbits / larguraBandaDisponivel ) + latencia;
    }

}
