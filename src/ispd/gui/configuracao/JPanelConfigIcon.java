/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ispd.gui.configuracao;

import ispd.escalonador.ManipularArquivos;
import ispd.gui.iconico.grade.Cluster;
import ispd.gui.iconico.grade.Internet;
import ispd.gui.iconico.grade.ItemGrade;
import ispd.gui.iconico.grade.Link;
import ispd.gui.iconico.grade.Machine;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author denison
 */
public class JPanelConfigIcon extends javax.swing.JPanel {

    /**
     * Creates new form JPanelConfigIcon
     */
    private VariedRowTable Tmachine;
    private VariedRowTable Tcluster;
    private VariedRowTable Tlink;
    private ResourceBundle palavras;
    private ManipularArquivos escalonadores;

    public JPanelConfigIcon() {
        palavras = ResourceBundle.getBundle("ispd.idioma.Idioma", new Locale("en", "US"));
        Tmachine = new VariedRowTable();
        Tmachine.setModel(new MachineTable(palavras));
        Tmachine.setRowHeight(20);
        Tcluster = new VariedRowTable();
        Tcluster.setModel(new ClusterTable(palavras));
        Tcluster.setRowHeight(20);
        Tlink = new VariedRowTable();
        Tlink.setModel(new LinkTable(palavras));
        Tlink.setRowHeight(20);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLabelIconName = new javax.swing.JLabel();

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabelTitle.setText("Machine icon configuration");

        jLabelIconName.setText("Configuration for the icon # 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelIconName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelIconName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelIconName;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    public void setEscalonadores(ManipularArquivos escalonadores) {
        this.escalonadores = escalonadores;
        for (Object escal : escalonadores.listar()) {
            getTabelaMaquina().getEscalonadores().addItem(escal);
        }
    }

    public void setIcone(ItemGrade icone) {
        if (icone instanceof Link) {
            jLabelTitle.setText(palavras.getString("Network icon configuration"));
            System.out.println(palavras.getLocale() + " - " + palavras.getString("Network icon configuration"));
        } else if (icone instanceof Internet) {
            jLabelTitle.setText(palavras.getString("Internet icon configuration"));
        }
        jLabelIconName.setText(palavras.getString("Configuration for the icon") + "#: " + icone.getId().getIdGlobal());
        getTabelaLink().setLink(icone);
        jScrollPane1.setViewportView(Tlink);
    }

    public void setIcone(ItemGrade icone, HashSet<String> usuarios) {
        if (!escalonadores.listarRemovidos().isEmpty()) {
            for (Object escal : escalonadores.listarRemovidos()) {
                getTabelaMaquina().getEscalonadores().removeItem(escal);
            }
            escalonadores.listarRemovidos().clear();
        }
        if (!escalonadores.listarAdicionados().isEmpty()) {
            for (Object escal : escalonadores.listarAdicionados()) {
                getTabelaMaquina().getEscalonadores().addItem(escal);
            }
            escalonadores.listarAdicionados().clear();
        }
        jLabelIconName.setText(palavras.getString("Configuration for the icon") + "#: " + icone.getId().getIdGlobal());
        if (icone instanceof Machine) {
            jLabelTitle.setText(palavras.getString("Machine icon configuration"));
            getTabelaMaquina().setMaquina((Machine) icone, usuarios);
            jScrollPane1.setViewportView(Tmachine);
        } else if (icone instanceof Cluster) {
            jLabelTitle.setText(palavras.getString("Cluster icon configuration"));
            getTabelaCluster().setCluster((Cluster) icone, usuarios);
            jScrollPane1.setViewportView(Tcluster);
        }
    }

    public String getTitle() {
        return jLabelTitle.getText();
    }

    public MachineTable getTabelaMaquina() {
        return (MachineTable) Tmachine.getModel();
    }

    public ClusterTable getTabelaCluster() {
        return (ClusterTable) Tcluster.getModel();
    }

    public LinkTable getTabelaLink() {
        return (LinkTable) Tlink.getModel();
    }

    public void setPalavras(ResourceBundle palavras) {
        this.palavras = palavras;
        ((MachineTable) Tmachine.getModel()).setPalavras(palavras);
        ((ClusterTable) Tcluster.getModel()).setPalavras(palavras);
        ((LinkTable) Tlink.getModel()).setPalavras(palavras);
    }
}
