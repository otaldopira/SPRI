package testejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;
import testejson.dadosServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author Eric
 */
public class SolicitarLogout {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(dadosServer.ip, dadosServer.porta);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            // Remover Usuario
            JSONObject objetoUsuario = new JSONObject();
            objetoUsuario.put("token", dadosServer.token);
            objetoUsuario.put("id", dadosServer.id);
            objetoUsuario.put("operacao", 9);

            System.out.println("Enviado: " + objetoUsuario.toJSONString());
            saida.println(objetoUsuario.toJSONString());

            System.out.println("Recebido: " + entrada.readLine());

            saida.close();
            entrada.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro conexão");
        }

    }

}
