/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package telas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;
import model.ClienteTCP;
import model.Usuario;

/**
 *
 * @author a2384310
 */
public class Inicio_Page extends javax.swing.JFrame {

    /**
     * Creates new form Inicio_Page
     */
    public Inicio_Page() {
        initComponents();
        setLocationRelativeTo(null);
    }

    public void credenciais() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSair = new javax.swing.JButton();
        btnPerfil = new javax.swing.JButton();
        btnReportarIncidente = new javax.swing.JButton();
        btnListarTodosIncidentes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnSair.setText("SAIR");
        btnSair.setPreferredSize(new java.awt.Dimension(75, 23));
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnPerfil.setText("PERFIL");
        btnPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerfilActionPerformed(evt);
            }
        });

        btnReportarIncidente.setText("REPORTAR INCIDENTES");
        btnReportarIncidente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportarIncidenteActionPerformed(evt);
            }
        });

        btnListarTodosIncidentes.setText("LISTAR INCIDENTES");
        btnListarTodosIncidentes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarTodosIncidentesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(140, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnListarTodosIncidentes, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReportarIncidente, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(140, 140, 140))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .addComponent(btnListarTodosIncidentes)
                .addGap(18, 18, 18)
                .addComponent(btnReportarIncidente)
                .addGap(18, 18, 18)
                .addComponent(btnPerfil)
                .addGap(18, 18, 18)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        try {
            // Recebe a instância do usuário logado
            int result = JOptionPane.showConfirmDialog(null, "Você deseja sair ?", "Excluir Incidente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                Usuario user = Usuario.getInstancia();
                // Transforma em JSON
                String json = new Gson().toJson(user);
                // Transforma em Objeto
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                // Remove o nome do JSON
                jsonObject.remove("nome");
                // Adiciona a operação
                jsonObject.addProperty("operacao", "9");
                // Transforma em JSON novamente
                String userLogut = new Gson().toJson(jsonObject);
                // Cria uma conexão scoket nova com o servidor
                Socket socket = new Socket(ClienteTCP.getIpConexao(), ClienteTCP.getPortaConexao());
                // Cria um PrintWriter para enviar a string JSON para o servidor
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Manda para o ServerSocket
                System.out.println("Enviado: " + userLogut);
                out.println(userLogut);
                // Cria um BufferedReader para receber a resposta do servidor
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Recebe do ServerSocket
                String response = in.readLine();
                System.out.println("Recebido: " + response);
                // Transforma a resposta em JSON
                JsonObject responseJson = new Gson().fromJson(response, JsonObject.class);
                // Obtem o status
                String status = responseJson.get("status").getAsString();
                // Verifica se está OK para fechar as janelas
                out.close();
                in.close();
                if (status.equals("OK")) {
                    this.dispose();
                    new Login_Page().setVisible(true);
                    socket.close();

                }
                socket.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o servidor", "Erro", JOptionPane.ERROR_MESSAGE);
            System.out.println("Erro de conexão com o servidor");
        }

    }//GEN-LAST:event_btnSairActionPerformed

    private void btnPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerfilActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new Perfil_Usuario_Page().setVisible(true);

    }//GEN-LAST:event_btnPerfilActionPerformed

    private void btnReportarIncidenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportarIncidenteActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new Reportar_Incidentes_Page().setVisible(true);
    }//GEN-LAST:event_btnReportarIncidenteActionPerformed

    private void btnListarTodosIncidentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarTodosIncidentesActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new Listagem_Incidentes_Page().setVisible(true);
    }//GEN-LAST:event_btnListarTodosIncidentesActionPerformed

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
            java.util.logging.Logger.getLogger(Inicio_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inicio_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inicio_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inicio_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inicio_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnListarTodosIncidentes;
    private javax.swing.JButton btnPerfil;
    private javax.swing.JButton btnReportarIncidente;
    private javax.swing.JButton btnSair;
    // End of variables declaration//GEN-END:variables
}
