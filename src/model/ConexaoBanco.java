/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Eric
 */
public class ConexaoBanco {

    private Connection conexao;

    public ConexaoBanco() {
        try {
            // registrando a classe JDBC do SQLite
            Class.forName("org.sqlite.JDBC");
            // criando a conexão com o banco de dados
            conexao = DriverManager.getConnection("jdbc:sqlite:meubanco.db");
            System.out.println("Conexão estabelecida com sucesso.");
            // criando objeto Statement para executar comandos SQL
            Statement statement = conexao.createStatement();
            // executando comandos SQL para criar tabelas
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS usuario ( id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR (50) NOT NULL, email VARCHAR (60) NOT NULL UNIQUE, senha VARCHAR NOT NULL );");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS sessao (token VARCHAR (20) PRIMARY KEY NOT NULL, usuario_id INTEGER CONSTRAINT fk_usuario_id REFERENCES usuario (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS incidentes (id_incidente INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT, hora TEXT, estado TEXT, cidade TEXT, bairro TEXT, rua TEXT, tipo_de_incidente TEXT, id_usuario INTEGER REFERENCES usuario (id) NOT NULL)");
            System.out.println("Tabelas criadas!");
        } catch (ClassNotFoundException e) {
            System.out.println("Classe JDBC do SQLite não encontrada.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }

    public Connection getConexao() {
        return conexao;
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexão fechada com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

}
