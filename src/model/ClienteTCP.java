/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.net.Socket;

/**
 *
 * @author Eric
 */
public class ClienteTCP {

    private static Socket socket;
    private static Integer portaConexao = null;
    private static String ipConexao = null;

    public ClienteTCP() {
    }

    public static void setIpConexao(String ipConexao) {
        ClienteTCP.ipConexao = ipConexao;
    }

    public static String getIpConexao() {
        return ipConexao;
    }

    public static void setPortaConexao(Integer portaConexao) {
        ClienteTCP.portaConexao = portaConexao;
    }

    public static int getPortaConexao() {
        return portaConexao;
    }
}
