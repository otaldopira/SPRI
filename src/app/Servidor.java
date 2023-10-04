/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import model.Usuario;
import model.ConexaoBanco;
import telas.Criar_ServerSocket_Page;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JOptionPane;
import model.Incidente;

/**
 *
 * @author amtt
 */
public class Servidor extends Thread {

    protected Socket clientSocket;
    protected Criar_ServerSocket_Page chat;

    public static void main(String[] args) {

        Criar_ServerSocket_Page terminalServidor = new Criar_ServerSocket_Page();
        Criar_ServerSocket_Page.instancia = terminalServidor;
        terminalServidor.setVisible(true);

    }

    public Servidor(Socket clientSoc, Criar_ServerSocket_Page chat) {
        clientSocket = clientSoc;
        this.chat = chat;
        start();
    }

    @Override
    public void run() {
        System.out.println("Nova thread de comunicacao iniciada.");
        try {
            PrintWriter saida = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Lê a mensagem do cliente
            String mensagem;
            String resposta;
            mensagem = entrada.readLine();
            System.out.println(mensagem);
            // Coloca a mensagem no painel
            chat.setTextRecebido(mensagem);

            // Verifica se a mensagem está nula ou vazia
            if (mensagem == null || mensagem.isEmpty()) {
                resposta = "status: \"MENSAGEM NULA\"";
                saida.println(resposta);
                chat.setTextEnviado(resposta);
                clientSocket.close();
                return;
            } else if (!mensagem.contains("operacao")) {
                resposta = "status: \"NAO CONTEM OPERACAO\"";
                saida.println(resposta);
                chat.setTextEnviado(resposta);
                clientSocket.close();
                return;
            }
            // Instancia do painel para poder dar append na mensagem recebida.
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            try {
                jsonObject = gson.fromJson(mensagem, JsonObject.class);
            } catch (JsonSyntaxException e) {
                resposta = "status: \"FORMATO INVALIDO\"";
                saida.println(resposta);
                chat.setTextEnviado(resposta);
                clientSocket.close();
                return;
            } catch (UnsupportedOperationException e) {
                resposta = "status: \"JSON INVALIDO\"";
                saida.println(resposta);
                chat.setTextEnviado(resposta);
                clientSocket.close();
                return;
            }

            Integer operacao = 1;

            try {
                operacao = jsonObject.get("operacao").getAsInt(); // Pega o código da operacao para escolher o que fazer
                if (operacao == null) {
                    resposta = "status: \"NAO CONTEM OPERACAO\"";
                    saida.println(resposta);
                    chat.setTextEnviado(resposta);
                    clientSocket.close();
                    return;
                }
            } catch (NumberFormatException e) {
                resposta = "status: \"FORMATO INVALIDO\"";
                saida.println(resposta);
                chat.setTextEnviado(resposta);
                clientSocket.close();
                return;
            }

            // cria um objeto JsonObject
            JsonObject json = new JsonObject();
            ConexaoBanco conexaoBancoDados = new ConexaoBanco();
            Connection conexao = conexaoBancoDados.getConexao();
            PreparedStatement ps = null;
            ResultSet rs = null;

            switch (operacao) {
                // Cadastro de usuário
                case 1:
                    try {
                    String nomeCadastro = jsonObject.get("nome").getAsString();
                    String emailCadastro = jsonObject.get("email").getAsString();
                    String senhaCadastro = jsonObject.get("senha").getAsString();
                    if (nomeCadastro == null || nomeCadastro.isEmpty()) {
                        // adiciona as propriedades operacao e status ao objeto
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "NOME VAZIO");
                        // converte o objeto em uma string JSON
                        String jsonString = gson.toJson(json);
                        // envia a string JSON para o cliente
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (emailCadastro == null || emailCadastro.isEmpty()) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "EMAIL VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (senhaCadastro == null || senhaCadastro.isEmpty()) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "SENHA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.validaNome(nomeCadastro)) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "NOME DEVE SER NO MINIMO 3 A 50 CARACTERES");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.validarEmail(emailCadastro)) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "EMAIL INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (senhaCadastro.length() < 5 || senhaCadastro.length() > 10) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "SENHA DEVE SER DE 5 À 10 CARACTERES");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.descriptografar(senhaCadastro)) {
                        json.addProperty("operacao", 1);
                        json.addProperty("status", "SENHA INVALIDA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        // Consulta SQL para verificar se o email já está cadastrado
                        if (!Usuario.verificarEmailExistente(conexao, emailCadastro)) {
                            Usuario.inserirUsuario(conexao, nomeCadastro, emailCadastro, senhaCadastro);
                            json.addProperty("operacao", 1);
                            json.addProperty("status", "OK");
                            String jsonString = gson.toJson(json);
                            saida.println(jsonString);
                            chat.setTextEnviado(jsonString);
                        } else {
                            json.addProperty("operacao", 1);
                            json.addProperty("status", "EMAIL JA EXISTE");
                            String jsonString = gson.toJson(json);
                            saida.println(jsonString);
                            chat.setTextEnviado(jsonString);
                            clientSocket.close();
                            return;
                        }
                        if (ps != null) {
                            try {
                                ps.close();
                            } catch (SQLException e) {
                                System.out.println("Erro ao fechar o PreparedStatement: " + e.getMessage());
                                clientSocket.close();
                                return;
                            }
                        }
                        conexaoBancoDados.fecharConexao();
                    }
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 1);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Login
                case 2:
                    try {
                    String emailLogin = jsonObject.get("email").getAsString();
                    String senhaLogin = jsonObject.get("senha").getAsString();

                    if (emailLogin == null || emailLogin.isEmpty()) {
                        json.addProperty("operacao", 2);
                        json.addProperty("status", "EMAIL VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (senhaLogin == null || senhaLogin.isEmpty()) {
                        json.addProperty("operacao", 2);
                        json.addProperty("status", "SENHA VAZIA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.validarEmail(emailLogin)) {
                        json.addProperty("operacao", 2);
                        json.addProperty("status", "EMAIL INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (senhaLogin.length() < 5 || senhaLogin.length() > 10) {
                        json.addProperty("operacao", 2);
                        json.addProperty("status", "SENHA DEVE SER DE 5 À 10 CARACTERES");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        Usuario user = new Usuario().realizarLogin(conexao, emailLogin, senhaLogin);
                        if (user == null) {
                            json.addProperty("operacao", 2);
                            json.addProperty("status", "USUARIO NAO ENCONTRADO");
                            String jsonString = gson.toJson(json);
                            saida.println(jsonString);
                            chat.setTextEnviado(jsonString);
                            clientSocket.close();
                            return;
                        } else {
                            Criar_ServerSocket_Page listaUsuarios = Criar_ServerSocket_Page.instancia;
                            List<String> usuarios = Usuario.usuariosLogados(conexao);
                            listaUsuarios.setjTextArea3(usuarios);
                            json.addProperty("operacao", 2);
                            json.addProperty("status", "OK");
                            json.addProperty("token", user.getToken());
                            json.addProperty("nome", user.getNome());
                            json.addProperty("id", user.getId());
                            String jsonString = gson.toJson(json);
                            saida.println(jsonString);
                            chat.setTextEnviado(jsonString);
                        }
                        conexaoBancoDados.fecharConexao();
                    }
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 2);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    // converte o objeto em uma string JSON
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Atualização de cadastro
                case 3:
                    try {
                    int id = -1;
                    String token = jsonObject.get("token").getAsString();
                    String nome = jsonObject.get("nome").getAsString();
                    String email = jsonObject.get("email").getAsString();
                    String senha = jsonObject.get("senha").getAsString();
                    try {
                        id = jsonObject.get("id").getAsInt();
                    } catch (NumberFormatException e) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "FORMATO INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }
                    if (nome == null || nome.isEmpty()) {
                        // adiciona as propriedades operacao e status ao objeto
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "NOME VAZIO");
                        // converte o objeto em uma string JSON
                        String jsonString = gson.toJson(json);
                        // envia a string JSON para o cliente
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (email == null || email.isEmpty()) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "EMAIL VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (senha == null || senha.isEmpty()) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "SENHA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, id, token)) {
                        Usuario.realizarLogout(conexao, id, token);
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "SESSAO INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    } else if (!Usuario.validaNome(nome)) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "NOME DEVE SER NO MINIMO 3 A 50 CARACTERES");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.validarEmail(email)) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "EMAIL INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.descriptografar(senha)) {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "SENHA INVALIDA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (Usuario.alterarDados(conexao, id, nome, email, senha)) {
                        Usuario.realizarLogout(conexao, id, token);
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "OK");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    } else {
                        json.addProperty("operacao", 3);
                        json.addProperty("status", "E-MAIL JA EXISTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 3);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS A");
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Listagem de incidentes
                case 4:
                    try {

                    String data = jsonObject.get("data").getAsString();
                    String estado = jsonObject.get("estado").getAsString();
                    String cidade = jsonObject.get("cidade").getAsString();

                    if (data.isEmpty()) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "DATA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (estado.isEmpty()) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "ESTADO VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (cidade.isEmpty()) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "CIDADE VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.validarData(data)) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "DATA INVALIDA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (estado.length() != 2) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "ENVIAR APENAS A SIGLA DO ESTADO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputEstado(estado)) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "ESTADO FORA DO PADRAO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputSemNumero(cidade)) {
                        json.addProperty("operacao", 4);
                        json.addProperty("status", "CIDADE FORA DO PADRAO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        String jsonString = Incidente.buscarIncidentes(conexao, data, estado, cidade);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 7);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    // converte o objeto em uma string JSON
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                }

                break;

                // Listagem de incidentes reportado pelo cliente
                case 5:
                    try {

                    int id = -1;
                    String token = jsonObject.get("token").getAsString();
                    try {
                        id = jsonObject.get("id").getAsInt();
                    } catch (NumberFormatException e) {
                        json.addProperty("operacao", 5);
                        json.addProperty("status", "FORMATO INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }

                    if (token.isEmpty() || id == -1) {
                        json.addProperty("operacao", 5);
                        json.addProperty("status", "USUARIO NAO LOGADO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, id, token)) {
                        json.addProperty("operacao", 5);
                        json.addProperty("status", "SESSAO INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }else {
                        String jsonString = Incidente.buscarIncidentesUsuario(conexao, id);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 5);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    // converte o objeto em uma string JSON
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Remover incidente reportado pelo cliente
                case 6:
                    try {
                    int idUsuario = -1;
                    int idIncidente = -1;
                    String token = jsonObject.get("token").getAsString();

                    try {
                        idUsuario = jsonObject.get("id").getAsInt();
                        idIncidente = jsonObject.get("id_incidente").getAsInt();
                    } catch (NumberFormatException e) {
                        json.addProperty("operacao", 6);
                        json.addProperty("status", "FORMATO INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }

                    if (token.isEmpty() || idUsuario == -1) {
                        json.addProperty("operacao", 6);
                        json.addProperty("status", "USUARIO NAO LOGADO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (idIncidente == -1) {
                        json.addProperty("operacao", 6);
                        json.addProperty("status", "INCIDENTE INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, idUsuario, token)) {
                        json.addProperty("operacao", 6);
                        json.addProperty("status", "SESSAO INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                    } else {
                        String jsonString = Incidente.removerIncidente(conexao, idIncidente);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 6);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    String jsonString = gson.toJson(json);
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Reportar incidente
                case 7:
                    try {

                    String data = jsonObject.get("data").getAsString();
                    String hora = jsonObject.get("hora").getAsString();
                    String estado = jsonObject.get("estado").getAsString();
                    String cidade = jsonObject.get("cidade").getAsString();
                    String bairro = jsonObject.get("bairro").getAsString();
                    String rua = jsonObject.get("rua").getAsString();
                    String token = jsonObject.get("token").getAsString();

                    int tipo_incidente = -1;
                    int id = -1;

                    try {
                        tipo_incidente = jsonObject.get("tipo_incidente").getAsInt();
                        id = jsonObject.get("id").getAsInt();
                    } catch (NumberFormatException e) {
                        resposta = "status: \"FORMATO INVALIDO\"";
                        saida.println(resposta);
                        chat.setTextEnviado(resposta);
                        clientSocket.close();
                        return;
                    }

                    if (data.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "DATA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (hora.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "HORA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (estado.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "ESTADO VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (cidade.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "CIDADE VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (bairro.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "BAIRRO VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (rua.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "RUA VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (token.isEmpty()) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "TOKEN VAZIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.validarData(data)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "DATA INVALIDA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.validarHorario(hora)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "HORA INVALIDA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputEstado(estado)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "ESTADO FORA DO PADRAO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (estado.length() != 2) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "ENVIAR APENAS A SIGLA DO ESTADO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputSemNumero(cidade)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "CIDADE FORA DO PADRAO ");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputSemNumero(bairro)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "BAIRRO FORA DO PADRAO ");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.verificarInputComNumero(rua)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "RUA FORA DO PADRAO ");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, id, token)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "SESSAO INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Incidente.inserirIncidente(conexao, data, hora, estado, cidade, bairro, rua, token, tipo_incidente, id)) {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "NAO FOI POSSIVEL ADICIONAR O INCIDENTE,");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        json.addProperty("operacao", 7);
                        json.addProperty("status", "OK");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    }

                    conexaoBancoDados.fecharConexao();

                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 7);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    // converte o objeto em uma string JSON
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Remover Cadastro de usuário
                case 8:
                    try {
                    int idUsuario = -1;
                    String senha = jsonObject.get("senha").getAsString();
                    String token = jsonObject.get("token").getAsString();

                    try {
                        idUsuario = jsonObject.get("id").getAsInt();
                    } catch (NumberFormatException e) {
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "FORMATO INVALIDO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }
                    if (token.isEmpty() || idUsuario == -1) {
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "USUARIO NAO LOGADO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, idUsuario, token)) {
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "SESSAO INEXISTENTE");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                    } else if (!Usuario.verificarSenha(conexao, idUsuario, senha)) {
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "SENHA INCORRETA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                    } else if (Usuario.excluirUsuario(conexao, idUsuario)) {
                        Usuario.realizarLogout(conexao, idUsuario, token);
                        Criar_ServerSocket_Page listaUsuarios = Criar_ServerSocket_Page.instancia;
                        List<String> usuarios = Usuario.usuariosLogados(conexao);
                        listaUsuarios.setjTextArea3(usuarios);
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "OK");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        json.addProperty("operacao", 8);
                        json.addProperty("status", "NAO FOI POSSIVEL REMOVER USUARIO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 8);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    String jsonString = gson.toJson(json);
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }

                break;

                // Logout
                case 9:
                    try {
                    int id = 11;
                    String token = jsonObject.get("token").getAsString();
                    try {
                        id = jsonObject.get("id").getAsInt();
                    } catch (NumberFormatException e) {
                        resposta = "status: \"FORMATO INVALIDO\"";
                        saida.println(resposta);
                        chat.setTextEnviado(resposta);
                        clientSocket.close();
                        return;
                    }
                    if (id == 0) {
                        json.addProperty("operacao", 9);
                        json.addProperty("status", "ID NULO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (token == null || token.isEmpty()) {
                        json.addProperty("operacao", 9);
                        json.addProperty("status", "TOKEN NULO");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.verificarSessao(conexao, id, token)) {
                        json.addProperty("operacao", 9);
                        json.addProperty("status", "SESSAO NAO ENCONTRADA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else if (!Usuario.realizarLogout(conexao, id, token)) {
                        json.addProperty("operacao", 9);
                        json.addProperty("status", "SESSAO NAO ENCONTRADA");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                        clientSocket.close();
                        return;
                    } else {
                        Criar_ServerSocket_Page listaUsuarios = Criar_ServerSocket_Page.instancia;
                        List<String> usuarios = Usuario.usuariosLogados(conexao);
                        listaUsuarios.setjTextArea3(usuarios);
                        json.addProperty("operacao", 9);
                        json.addProperty("status", "OK");
                        String jsonString = gson.toJson(json);
                        saida.println(jsonString);
                        chat.setTextEnviado(jsonString);
                    }
                    conexaoBancoDados.fecharConexao();
                } catch (NullPointerException | UnsupportedOperationException e) {
                    json.addProperty("operacao", 9);
                    json.addProperty("status", "CAMPOS OBRIGATORIOS NAO FORAM ENVIADOS");
                    String jsonString = gson.toJson(json);
                    // envia a string JSON para o cliente
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
                }
                break;

                default:
                    json.addProperty("status", "OPERACAO NAO ENCONTRADA");
                    String jsonString = gson.toJson(json);
                    saida.println(jsonString);
                    chat.setTextEnviado(jsonString);
                    clientSocket.close();
                    return;
            }

            //Fecha tudo
            entrada.close();
            saida.close();
            clientSocket.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão com o servidor", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro com o Banco de Dados", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }
}
