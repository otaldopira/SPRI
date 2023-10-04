/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Eric
 */
public class Incidente {

    private String data;
    private String hora;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private int tipo;
    private int id;

    public Incidente(String data, String hora, String estado, String cidade, String bairro, String rua, int tipo, int id) {
        this.data = data;
        this.hora = hora;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.tipo = tipo;
        this.id = id;
    }

    public static boolean inserirIncidente(Connection conexao, String data, String hora, String estado, String cidade,
            String bairro, String rua, String token, int tipo_incidente, int id) {
        String sql = "INSERT INTO incidentes (data, hora, estado, cidade, bairro, rua, tipo_de_incidente, id_usuario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, data);
            ps.setString(2, hora);
            ps.setString(3, estado);
            ps.setString(4, cidade);
            ps.setString(5, bairro);
            ps.setString(6, rua);
            ps.setInt(7, tipo_incidente);
            ps.setInt(8, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Inserção do incidente realizada com sucesso.");
                return true;
            } else {
                System.out.println("Nenhum registro foi inserido.");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao inserir o incidente: " + ex.getMessage());
            return false;
        }
    }

    public static String buscarIncidentes(Connection conexao, String data, String estado, String cidade) {
        System.out.println("data: " + data);
        System.out.println("estado: " + estado);
        System.out.println("cidade: " + cidade);
        List<Incidente> incidentes = new ArrayList<>();

        try {
            String sql = "SELECT data, hora, estado, cidade, bairro, rua, tipo_de_incidente, id_incidente FROM incidentes WHERE data = ? AND estado = ? AND cidade = ? ORDER BY hora DESC;";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, data);
            ps.setString(2, estado);
            ps.setString(3, cidade);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String incidenteData = rs.getString("data");
                String hora = rs.getString("hora");
                String incidenteEstado = rs.getString("estado");
                String incidenteCidade = rs.getString("cidade");
                String bairro = rs.getString("bairro");
                String rua = rs.getString("rua");
                int tipoIncidente = rs.getInt("tipo_de_incidente");
                int idIncidente = rs.getInt("id_incidente");

                System.out.println(incidenteCidade);

                Incidente incidente = new Incidente(incidenteData, hora, incidenteEstado, incidenteCidade, bairro, rua, tipoIncidente, idIncidente);
                System.out.println(incidente);
                incidentes.add(incidente);
            }

            ps.close();
            rs.close();
        } catch (SQLException ex) {
            JSONObject respostaJson = new JSONObject();
            respostaJson.put("operacao", 4);
            respostaJson.put("status", "ERRO AO BUSCAR INCIDENTES");
            return respostaJson.toJSONString();
        }

        return formatarResposta(incidentes, 4);

    }

    public static String buscarIncidentesUsuario(Connection conexao, int id) {

        List<Incidente> incidentes = new ArrayList<>();

        try {
            String sql = "SELECT data, hora, estado, cidade, bairro, rua, tipo_de_incidente, id_incidente FROM incidentes WHERE id_usuario = ? ORDER BY hora DESC";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String incidenteData = rs.getString("data");
                String hora = rs.getString("hora");
                String incidenteEstado = rs.getString("estado");
                String incidenteCidade = rs.getString("cidade");
                String bairro = rs.getString("bairro");
                String rua = rs.getString("rua");
                int tipoIncidente = rs.getInt("tipo_de_incidente");
                int idIncidente = rs.getInt("id_incidente");

                System.out.println(incidenteCidade);

                Incidente incidente = new Incidente(incidenteData, hora, incidenteEstado, incidenteCidade, bairro, rua, tipoIncidente, idIncidente);
                System.out.println(incidente);
                incidentes.add(incidente);
            }

            ps.close();
            rs.close();
        } catch (SQLException ex) {
            JSONObject respostaJson = new JSONObject();
            respostaJson.put("operacao", 5);
            respostaJson.put("status", "ERRO AO BUSCAR INCIDENTES");
            return respostaJson.toJSONString();
        }

        return formatarResposta(incidentes, 5);

    }

    public static String removerIncidente(Connection conexao, int idIncidente) {
        JSONObject respostaJson = new JSONObject();
        try {
            String sql = "DELETE FROM incidentes WHERE id_incidente = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, idIncidente);
            int linhasAfetadas = ps.executeUpdate();
            ps.close();
            if (linhasAfetadas > 0) {
                respostaJson.put("operacao", 6);
                respostaJson.put("status", "OK");
                return respostaJson.toJSONString();
            } else {
                respostaJson.put("operacao", 6);
                respostaJson.put("status", "INCIDENTE NAO ENCONTRADO");
                return respostaJson.toJSONString();
            }

        } catch (SQLException ex) {
            respostaJson.put("operacao", 6);
            respostaJson.put("status", "ERRO AO REMOVER INCIDENTE");
            return respostaJson.toJSONString();
        }
    }

    private static String formatarResposta(List<Incidente> incidentes, int operacao) {
        JSONObject respostaJson = new JSONObject();
        respostaJson.put("operacao", operacao);
        respostaJson.put("status", "OK");

        JSONArray incidentesJson = new JSONArray();
        for (Incidente incidente : incidentes) {
            JSONObject incidenteJson = new JSONObject();
            incidenteJson.put("data", incidente.data);
            incidenteJson.put("hora", incidente.hora);
            incidenteJson.put("estado", incidente.estado);
            incidenteJson.put("cidade", incidente.cidade);
            incidenteJson.put("bairro", incidente.bairro);
            incidenteJson.put("rua", incidente.rua);
            incidenteJson.put("tipo_incidente", incidente.tipo);
            incidenteJson.put("id_incidente", incidente.id);

            incidentesJson.add(incidenteJson);
        }

        respostaJson.put("incidentes", incidentesJson);
        return respostaJson.toJSONString();
    }

    public static String obterTextoTipoIncidente(int tipoIncidente) {
        switch (tipoIncidente) {
            case 1:
                return "ALAGAMENTO";
            case 2:
                return "DESLIZAMENTO";
            case 3:
                return "ACIDENTE DE CARRO";
            case 4:
                return "OBSTRUÇÃO DA VIA";
            case 5:
                return "FISSURA DA VIA";
            case 6:
                return "PISTA EM OBRAS";
            case 7:
                return "LENTIDÃO NA PISTA";
            case 8:
                return "ANIMAIS NA PISTA";
            case 9:
                return "NEVOEIRO";
            case 10:
                return "TROMBA D'ÁGUA";
            default:
                return "TIPO DESCONHECIDO";
        }
    }

    public static boolean validarData(String data) {
        // Regex para o formato yyyy-MM-dd
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        boolean formatoValido = Pattern.matches(regex, data);

        if (formatoValido) {
            try {
                // Tenta transofrmar String em data
                LocalDate.parse(data);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean validarHorario(String horario) {
        // Regex para o formato HH:mm
        String regex = "\\d{2}:\\d{2}";
        boolean formatoValido = Pattern.matches(regex, horario);

        if (formatoValido) {
            try {
                // Tenta transformar String em horário
                LocalTime.parse(horario);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static boolean verificarInputEstado(String texto) {
        Pattern padrao = Pattern.compile("^[A-Z\\\\s]{1,2}$");
        return padrao.matcher(texto).matches();
    }
    

    public static boolean verificarInputSemNumero(String texto) {
        System.out.println(texto);
        Pattern padrao = Pattern.compile("^[A-Z'\\s]{1,50}$");
        
        return padrao.matcher(texto).matches();
    }
    
    public static boolean verificarInputComNumero(String texto) {
        Pattern padrao = Pattern.compile("^[A-Z0-9'\\s]{1,50}$");
        return padrao.matcher(texto).matches();
    }

    public static String normalizarDados(String texto) {
        // Converter para maiúsculas
        texto = texto.toUpperCase();

        // Remover acentos
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Remover caracteres especiais
        texto = texto.replaceAll("[^\\p{ASCII}!]", "");

        // Limitar o tamanho máximo para 50 caracteres
        if (texto.length() > 50) {
            texto = texto.substring(0, 50);
        }

        return texto;
    }

}
