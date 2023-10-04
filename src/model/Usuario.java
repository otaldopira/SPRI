/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 *
 * @author Eric
 */
public class Usuario {

    private int operacao;
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String token;
    private static Usuario instancia;

    public Usuario(String nome, String email, String senha, int operacao) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.operacao = operacao;
    }

    public Usuario(int id, String nome, String email, String token) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.token = token;
    }

    public Usuario(int id, String nome, String token) {
        this.id = id;
        this.nome = nome;
        this.token = token;
    }

    public Usuario() {
    }

    public static Usuario getInstancia() {
        return instancia;
    }

    public static void setInstancia(Usuario usuario) {
        instancia = usuario;
    }

    public int getOperacao() {
        return operacao;
    }

    public void setOperacao(int operacao) {
        this.operacao = operacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static String cifraDeCesar(String texto) {
        char[] chars = texto.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ((int) chars[i] + 2);
        }
        return new String(chars);
    }

    public static boolean descriptografar(String texto) {
        char[] chars = texto.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ((int) chars[i] - 2);
        }
        if (validarSenha(new String(chars)) == true) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean validarEmail(String email) {
        // define a expressão regular para validar o email
        //Pattern padrao = Pattern.compile("^[a-zA-Z0-9._%+-]{3,50}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$");
        Pattern padrao = Pattern.compile("^[A-Za-z0-9._-]{3,50}@[A-Za-z0-9.-]{3,10}$");
        // verifica se o email corresponde à expressão regular
        return padrao.matcher(email).matches();
    }

    public static boolean validaNome(String nome) {
        if (nome.length() >= 3 && nome.length() <= 50) {
            return true;
        }

        return false;
    }

    public static boolean validarSenha(String senha) {
        String pattern = "^[a-zA-Z0-9]{5,10}$";
        return senha.matches(pattern);
    }

    public static String gerarTokenSessao() {

        final int TOKEN_TAM = 20;
        String token = "";
        Random r = new Random();

        for (int i = 0; i < TOKEN_TAM; i++) {
            int n = r.nextInt(62);
            char c;
            if (n < 10) {
                c = (char) ('0' + n);
            } else if (n < 36) {
                c = (char) ('A' + n - 10);
            } else {
                c = (char) ('a' + n - 36);
            }
            token += c;
        }
        return token;

    }

    public static void salvarTokenSessao(Connection conexao, int idUsuario, String token) {

        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO sessao (token, usuario_id) VALUES (?, ?);";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, token);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erro ao salvar token de sessão: " + ex.getMessage());
        } finally {
            // Fechar recursos
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    public static List<String> usuariosLogados(Connection conexao) {
        List<String> list = new ArrayList<String>();
        String sql = "SELECT nome FROM usuario, sessao WHERE usuario.id = sessao.usuario_id";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    list.add(nome);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao realizar login: " + ex.getMessage());
        }

        return list;
    }

    public static Usuario realizarLogin(Connection conexao, String email, String senha) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, senha);
            rs = ps.executeQuery();
            if (rs.next()) {

                String token = gerarTokenSessao();
                salvarTokenSessao(conexao, rs.getInt("id"), token);

                // Cria a resposta de login com os dados do usuário
                Usuario user = new Usuario(rs.getInt("id"), rs.getString("nome"), token);

                return user;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao realizar login: " + ex.getMessage());

        } finally {
            // Fechar recursos
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
        }
        return null;

    }

    public static boolean verificarSessao(Connection conexao, int idUsuario, String token) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String sql = "SELECT COUNT(*) FROM sessao WHERE usuario_id = ? AND token = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, token);

            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao realizar login: " + ex.getMessage());

        } finally {
            // Fechar recursos
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
        }
        return false;

    }

    public static boolean realizarLogout(Connection conexao, int idUsuario, String token) throws SQLException {
        try {

            String sql = "DELETE FROM sessao WHERE usuario_id = ? AND token = ?";
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, token);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir sessao " + e.getMessage());
        }
        return false;
    }

    public static boolean alterarDados(Connection conexao, int id, String nome, String email, String senha) {
        try {
            // Primeiro, recupere os dados atuais do usuário
            Usuario usuarioAtual = recuperarUsuarioPorId(conexao, id);

            // Verifique se o email permanece o mesmo
            if (usuarioAtual.getEmail().equals(email)) {
                String sql = "UPDATE usuario SET nome = ?, senha = ? WHERE id = ?";
                int rowsAffected;
                try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                    ps.setString(1, nome);
                    ps.setString(2, senha);
                    ps.setInt(3, id);
                    rowsAffected = ps.executeUpdate();
                }

                if (rowsAffected > 0) {
                    System.out.println("Dados alterados com sucesso.");
                    return true;
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                    return false;
                }
            } else {
                // O email está sendo alterado, verifique se já existe no banco de dados
                if (verificarEmailExistente(conexao, email)) {
                    System.out.println("O e-mail já existe no banco de dados. Nenhum dado foi alterado.");
                    return false;
                }

                // O email não existe, atualize os dados
                String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id = ?";
                int rowsAffected;
                try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                    ps.setString(1, nome);
                    ps.setString(2, email);
                    ps.setString(3, senha);
                    ps.setInt(4, id);
                    rowsAffected = ps.executeUpdate();
                }

                if (rowsAffected > 0) {
                    System.out.println("Dados alterados com sucesso.");
                    return true;
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao alterar dados");
        }

        return false;
    }

    public static boolean verificarEmailExistente(Connection conexao, String email) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT(*) FROM usuario WHERE UPPER(email) = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, email.toUpperCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao verificar email existente: ");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar ResultSet: ");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement:");
                }
            }
        }
        return false;
    }

    public static void inserirUsuario(Connection conexao, String nome, String email, String senha) {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, senha);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erro ao inserir usuário: ");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    private static Usuario recuperarUsuarioPorId(Connection conexao, int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String email = rs.getString("email");
                    String senha = rs.getString("senha");
                    return new Usuario(usuarioId, nome, email, senha);
                }
            }
        }

        return null;
    }

    public static boolean verificarSenha(Connection conexao, int id, String senha) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM usuario WHERE id = ? AND senha = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, senha);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao realizar login: " + ex.getMessage());

        } finally {
            // Fechar recursos
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
        }
        return false;
    }

    public static boolean excluirUsuario(Connection conexao, int idUsuario) throws SQLException {
        try {
            String sql = "DELETE FROM usuario WHERE id = ?";
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir sessao " + e.getMessage());
        }
        return false;
    }
}
