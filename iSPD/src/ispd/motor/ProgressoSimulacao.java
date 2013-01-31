/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ispd.motor;

import InterpretadorInterno.ModeloIconico.InterpretadorIconico;
import InterpretadorInterno.ModeloSimulavel.InterpretadorSimulavel;
import ispd.gui.AreaDesenho;
import ispd.gui.Icone;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de conexão entre interface de usuario e motor de simulação
 *
 * @author denison
 */
public abstract class ProgressoSimulacao {

    public void println(String text, Color cor) {
        this.print(text, cor);
        this.print("\n", cor);
    }

    public void println(String text) {
        this.print(text, Color.black);
        this.print("\n", Color.black);
    }

    public void print(String text) {
        this.print(text, Color.black);
    }

    public abstract void incProgresso(int n);

    public abstract void print(String text, Color cor);

    /**
     * Verifica a partir dos icones graficos se a simulação pode ser iniciada
     *
     * @param aDesenho AreaDesenho contendo o modelo que será simulado
     * @throws IllegalArgumentException Exceção retornada caso encontre
     * elementos não configurados
     */
    public void validarInicioSimulacao(AreaDesenho aDesenho) throws IllegalArgumentException {
        this.print("Verifying configuration of the icons.");
        this.print(" -> ");
        if (aDesenho == null || aDesenho.getIcones().isEmpty()) {
            this.println("Error!", Color.red);
            throw new IllegalArgumentException("The model has no icons.");
        }
        for (Icone I : aDesenho.getIcones()) {
            if (I.getConfigurado() == false) {
                this.println("Error!", Color.red);
                throw new IllegalArgumentException("One or more parameters have not been configured.");
            }
        }
        this.incProgresso(4);
        this.println("OK", Color.green);
        this.print("Verifying configuration of the tasks.");
        this.print(" -> ");
        if (aDesenho.getCargasConfiguracao() == null) {
            this.println("Error!", Color.red);
            throw new IllegalArgumentException("One or more  workloads have not been configured.");
        }
        this.incProgresso(1);
        this.println("OK", Color.green);
    }

    /**
     * Escreve os arquivos com os modelos icônicos e simuláveis, e realiza a
     * analise e validação dos mesmos
     *
     * @param ModeloIconico Texto contendo o modelo icônico que será analisado
     */
    public void AnalisarModelos(String ModeloIconico) {
        //escreve modelo iconico
        this.print("Writing iconic model.");
        this.print(" -> ");
        File arquivo = new File("modeloiconico");
        try {
            FileWriter writer = new FileWriter(arquivo);
            PrintWriter saida = new PrintWriter(writer, true);
            saida.print(ModeloIconico);
            saida.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ProgressoSimulacao.class.getName()).log(Level.SEVERE, null, ex);
        }
        incProgresso(5);//[5%] --> 10%
        this.println("OK", Color.green);
        //interpreta modelo iconico
        this.print("Interpreting iconic model.");
        this.print(" -> ");
        InterpretadorIconico parser = new InterpretadorIconico();
        parser.leArquivo(arquivo);
        incProgresso(5);//[5%] --> 15%
        this.println("OK", Color.green);
        this.print("Writing simulation model.");
        this.print(" -> ");
        parser.escreveArquivo();
        incProgresso(5);//[5%] --> 20%
        this.println("OK", Color.green);
        this.print("Interpreting simulation model.");
        this.print(" -> ");
        InterpretadorSimulavel parser2 = new InterpretadorSimulavel();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        parser2.leArquivo(new File("modelosimulavel"));
        System.setOut(stdout);
        incProgresso(5);//[5%] --> 25%
        this.println("OK", Color.green);
    }
}
