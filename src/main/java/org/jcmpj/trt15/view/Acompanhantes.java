package org.jcmpj.trt15.view;

import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.JOptionPane;
import org.jcmpj.trt15.controller.PreencherForm;
import org.jcmpj.trt15.model.DBManager;
import org.jcmpj.trt15.resources.WindowManager;

/**
 *
 * @author jcmpj
 */
public class Acompanhantes extends javax.swing.JInternalFrame {

    private WindowManager windowManager;
    private String id;
    private static Acompanhantes acompanhantes;

    public static Acompanhantes getInstance() {
        if (acompanhantes == null) {
            acompanhantes = new Acompanhantes();
        } else {
            acompanhantes.loadInitialData();
        }
        return acompanhantes;
    }

    /**
     * Creates new form Atividades
     */
    public Acompanhantes() {
        initComponents();
        loadInitialData();
    }

    private void loadInitialData() {
        Map<String, String> dl = PreencherForm.getLaudoIdNumProcesso();

        id = dl.get("id");
        String numProcessos = dl.get("numProcesso");
        String nomeReclamante = dl.get("nomeReclamante");
        String nomeReclamada = dl.get("nomeReclamada");

        // txtReclamada.setText("Reclamada: " + nomeReclamada);
        // txtReclamante.setText("Reclamante: " + nomeReclamante);
        String titReclamante;
        titReclamante = "Acompanhantes reclamante: " + nomeReclamante;
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(
                new java.awt.Color(0, 153, 153)), titReclamante,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Liberation Sans", 3, 15),
                new java.awt.Color(0, 153, 153)));

        String titReclamada;
        titReclamada = "Acompanhantes reclamada: " + nomeReclamada;
        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(
                new java.awt.Color(0, 153, 153)), titReclamada,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Liberation Sans", 3, 15),
                new java.awt.Color(0, 153, 153)));

        Map<String, String> txt = DBManager.getSafeguard(id);
        txtAreaReclamante.setText(txt.get("acompanhantesReclamante"));
        txtAreaReclamada.setText(txt.get("acompanhantesReclamada"));
    }
    
    private void addLineBreak(int c, String anterior, String txtArea) {
        String txt = anterior.trim() + "\n";
        //txt = txt.trim();
        if (c == KeyEvent.VK_ENTER) {
            if (txtArea.equalsIgnoreCase("txtAreaReclamante")) {
                txtAreaReclamante.setText(txt);
            } else if (txtArea.equalsIgnoreCase("txtAreaReclamada")) {
                txtAreaReclamada.setText(txt);
            }
        }
    }
    
    private void saveText() {
        boolean up;
        up = DBManager.updateSafeguard(txtAreaReclamante.getText(), txtAreaReclamada.getText(), Integer.parseInt(id));
        if (up) {
            JOptionPane.showMessageDialog(null, "Texto Atualizados\nno banco de dados", "Update", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Erro\nbanco de dados", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnSalvarAtividades = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaReclamante = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAreaReclamada = new javax.swing.JTextArea();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Lista de Acompanhantes / Testemunhas");
        setPreferredSize(new java.awt.Dimension(1024, 603));

        jPanel1.setLayout(new java.awt.BorderLayout());

        btnSalvarAtividades.setText("Salvar Alterações");
        btnSalvarAtividades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarAtividadesActionPerformed(evt);
            }
        });
        jPanel1.add(btnSalvarAtividades, java.awt.BorderLayout.CENTER);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)), "Acompanhantes Reclamante", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Liberation Sans", 3, 18), new java.awt.Color(0, 153, 153))); // NOI18N

        txtAreaReclamante.setColumns(20);
        txtAreaReclamante.setRows(5);
        txtAreaReclamante.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAreaReclamanteKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(txtAreaReclamante);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)), "Acompanhantes Reclamada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Liberation Sans", 3, 18), new java.awt.Color(0, 153, 153))); // NOI18N

        txtAreaReclamada.setColumns(20);
        txtAreaReclamada.setRows(5);
        txtAreaReclamada.setBorder(null);
        txtAreaReclamada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAreaReclamadaKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(txtAreaReclamada);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarAtividadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarAtividadesActionPerformed
        saveText();
    }//GEN-LAST:event_btnSalvarAtividadesActionPerformed

    private void txtAreaReclamanteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAreaReclamanteKeyReleased
        addLineBreak(evt.getKeyCode(), txtAreaReclamante.getText(), "txtAreaReclamante");
    }//GEN-LAST:event_txtAreaReclamanteKeyReleased

    private void txtAreaReclamadaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAreaReclamadaKeyReleased
        addLineBreak(evt.getKeyCode(), txtAreaReclamada.getText(), "txtAreaReclamda");
    }//GEN-LAST:event_txtAreaReclamadaKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvarAtividades;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea txtAreaReclamada;
    private javax.swing.JTextArea txtAreaReclamante;
    // End of variables declaration//GEN-END:variables
}
