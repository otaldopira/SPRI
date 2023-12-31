/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package telas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ClienteTCP;
import model.Incidente;
import model.Usuario;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Eric
 */
public class Listagem_Incidentes_Page extends javax.swing.JFrame {

    /**
     * Creates new form Listagem_Incidentes_Page
     */
    public Listagem_Incidentes_Page() {
        initComponents();
        setLocationRelativeTo(null);
        AutoCompleteDecorator.decorate(cbEstado);
        try {
            if (Usuario.getInstancia() == null) {
                this.btnReportadosPorMim.hide();
                this.btnVoltar.setText("Sair");
            }
        } catch (NullPointerException e) {
            this.btnReportadosPorMim.hide();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableIncidentes = new javax.swing.JTable();
        txtData = new com.toedter.calendar.JDateChooser();
        cbEstado = new javax.swing.JComboBox<>();
        txtCidade = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JToggleButton();
        btnVoltar = new javax.swing.JButton();
        btnReportadosPorMim = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        tableIncidentes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id_incidente", "tipo_incidente", "estado", "cidade", "bairro", "rua", "data", "hora"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jScrollPane1.setViewportView(tableIncidentes);

        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        txtData.setDateFormatString(formatoData.toPattern());
        txtData.setDate(java.sql.Date.valueOf(java.time.LocalDate.now()));

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE" }));
        cbEstado.setSelectedIndex(-1);

        jLabel1.setText("Data:");

        jLabel2.setText("Estado:");

        jLabel3.setText("Cidade:");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        btnReportadosPorMim.setText("Reportados por mim");
        btnReportadosPorMim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportadosPorMimActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscar)
                                .addGap(18, 18, 18)
                                .addComponent(btnVoltar)
                                .addGap(18, 18, 18)
                                .addComponent(btnReportadosPorMim)))))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscar)
                        .addComponent(btnVoltar)
                        .addComponent(btnReportadosPorMim)))
                .addGap(51, 51, 51)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        try {
            Socket socket = new Socket(ClienteTCP.getIpConexao(), ClienteTCP.getPortaConexao());
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");

            JSONObject objetoBusca = new JSONObject();
            objetoBusca.put("data", formatoData.format(txtData.getDate()));
            objetoBusca.put("estado", cbEstado.getSelectedItem());
            objetoBusca.put("cidade", txtCidade.getText().toUpperCase());
            objetoBusca.put("operacao", 4);

            saida.println(objetoBusca.toJSONString());
            System.out.println("Enviado: " + objetoBusca.toJSONString());

            JSONParser parser = new JSONParser();
            String response = entrada.readLine();

            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            System.out.println("Recebido: " + jsonResponse.toJSONString());

            // Extrai os dados do JSON
            try {
                JSONArray incidentesJson = (JSONArray) jsonResponse.get("incidentes");
                DefaultTableModel dados = new DefaultTableModel(
                        new Object[incidentesJson.size()][8], // Número de linhas é o tamanho do array de incidentes
                        new String[]{"id_incidente", "tipo_incidente", "estado", "cidade", "bairro", "rua", "data", "hora"} // Nomes das colunas
                );

                for (int i = 0; i < incidentesJson.size(); i++) {
                    JSONObject incidenteJson = (JSONObject) incidentesJson.get(i);
                    try {
                        // incidenteJson retorna do tipo Long
                        Long tipoIncidenteLong = (Long) incidenteJson.get("tipo_incidente");
                        // após capturar tenho que fazer o parse para int
                        int tipoIncidente = tipoIncidenteLong.intValue();
                        dados.setValueAt(incidenteJson.get("id_incidente"), i, 0);
                        // envio o número que corresponde o incidente e me retorna o nome do incidente
                        dados.setValueAt(Incidente.obterTextoTipoIncidente(tipoIncidente), i, 1);
                        dados.setValueAt(incidenteJson.get("estado"), i, 2);
                        dados.setValueAt(incidenteJson.get("cidade"), i, 3);
                        dados.setValueAt(incidenteJson.get("bairro"), i, 4);
                        dados.setValueAt(incidenteJson.get("rua"), i, 5);
                        dados.setValueAt(incidenteJson.get("data"), i, 6);
                        dados.setValueAt(incidenteJson.get("hora"), i, 7);
                    } catch (NumberFormatException | ClassCastException e) {
                        JOptionPane.showMessageDialog(null, "NÃO FOI POSSÍVEL GERAR A TABELA", "Erro", JOptionPane.ERROR_MESSAGE);
                        saida.close();
                        entrada.close();
                        socket.close();
                    }

                }

                tableIncidentes.setModel(dados);
                tableIncidentes.setAutoCreateRowSorter(true);
                saida.close();
                entrada.close();
                socket.close();
            } catch (NullPointerException e) {
                saida.close();
                entrada.close();
                socket.close();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o servidor", "Erro", JOptionPane.ERROR_MESSAGE);

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conversão do JSON", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnBuscarActionPerformed
    
    
    
    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        // TODO add your handling code here:
        try {
            if (Usuario.getInstancia() == null) {
                this.btnReportadosPorMim.hide();
                System.exit(0);
            } else {
                this.dispose();
                new Inicio_Page().setVisible(true);
            }
        } catch (NullPointerException e) {
            this.btnReportadosPorMim.hide();
            System.exit(0);
        }

    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnReportadosPorMimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportadosPorMimActionPerformed
        // TODO add your handling code here:
        new Incidentes_Usuario_Page().setVisible(true);
    }//GEN-LAST:event_btnReportadosPorMimActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Listagem_Incidentes_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Listagem_Incidentes_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Listagem_Incidentes_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Listagem_Incidentes_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Listagem_Incidentes_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnBuscar;
    private javax.swing.JButton btnReportadosPorMim;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableIncidentes;
    private javax.swing.JTextField txtCidade;
    private com.toedter.calendar.JDateChooser txtData;
    // End of variables declaration//GEN-END:variables
}
