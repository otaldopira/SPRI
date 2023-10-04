/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import telas.Criar_ConexaoSocket_Page;

/**
 *
 * @author amtt
 */
public class Cliente {

    public static void main(String[] args) {
        try {
            new Criar_ConexaoSocket_Page().setVisible(true);
        } catch (Exception e) {
            System.exit(0);
        }
    }
}
