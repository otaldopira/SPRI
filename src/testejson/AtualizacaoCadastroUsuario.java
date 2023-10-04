package testejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author Eric
 */
public class AtualizacaoCadastroUsuario {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(dadosServer.ip, dadosServer.porta);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            // Cadastrar Incidente
            JSONObject objetoAtualizarCadastro = new JSONObject();
//            objetoAtualizarCadastro.put("token", dadosServer.token);
//            objetoAtualizarCadastro.put("id", dadosServer.id);
//            objetoAtualizarCadastro.put("email", "lulu@gmail.com");
//            objetoAtualizarCadastro.put("nome", "lulu");
//            objetoAtualizarCadastro.put("senha", "34567");
//            objetoAtualizarCadastro.put("operacao", 3);
            
            objetoAtualizarCadastro.put("token", dadosServer.token);
            objetoAtualizarCadastro.put("id", dadosServer.id);
            objetoAtualizarCadastro.put("email", "adsadsa@fdsfdsfds");
            objetoAtualizarCadastro.put("nome", "dsadsadsa");
            objetoAtualizarCadastro.put("senha", "dasdsadsaddasdsadass");
            objetoAtualizarCadastro.put("operacao", 3);

            System.out.println("Enviado: " + objetoAtualizarCadastro.toJSONString());
            saida.println(objetoAtualizarCadastro.toJSONString());

            System.out.println("Recebido: " + entrada.readLine());

            saida.close();
            entrada.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro conexão");
        }

    }

}
