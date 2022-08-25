package org.jcmpj.trt15.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcmpj.trt15.resources.Global;

public class DBManager {

    public static String url = "jdbc:sqlite:" + Global.getPathLaudoDbFile();

    //Cria conexões com o banco
    public static Connection getConnection() {
        try {

            Connection conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean createNewDatabase() {

        try ( Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    public static void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE \"dataLaudo\" (\n";
        sql += "\"id\"	INTEGER PRIMARY KEY AUTOINCREMENT,\n";
        sql += "\"numProcesso\"	VARCHAR(30) UNIQUE,\n";
        sql += "\"nomeReclamante\"	VARCHAR(100),\n";
        sql += "\"nomeReclamada\"	VARCHAR(100),\n";
        sql += "\"dataVistoria\"	VARCHAR(30),\n";
        sql += "\"horaVistoria\"	VARCHAR(30),\n";
        sql += "\"localVistoria\"	VARCHAR(200),\n";
        sql += "\"enderecoVistoria\"	VARCHAR(200),\n";
        sql += "\"periodoReclamado\"	VARCHAR(100),\n";
        sql += "\"funcaoExercida\"		VARCHAR(100),\n";
        sql += "\"acompanhantesReclamante\"	TEXT COLLATE NOCASE,\n";
        sql += "\"acompanhantesReclamada\"	TEXT COLLATE NOCASE,\n";
        sql += "\"atividadesReclamante\"	TEXT COLLATE NOCASE,\n";
        sql += "\"atividadesReclamada\"		TEXT COLLATE NOCASE,\n";
        sql += "\"quesitosReclamante\"		TEXT COLLATE NOCASE,\n";
        sql += "\"quesitosReclamada\"		TEXT COLLATE NOCASE,\n";
        sql += "\"descricaoLocalTrabalho\"	TEXT COLLATE NOCASE,\n";
        sql += "\"cidadeEmissao\"		VARCHAR(200),\n";
        sql += "\"dataEmissao\"	VARCHAR(30),\n";
        sql += "\"dataCriacao\"	INTEGER\n";
        sql += "  );";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ResultSet listAll() {
        String sql = "SELECT * FROM dataLaudo";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static ResultSet listById(String id) {

        String sql = "SELECT * FROM dataLaudo WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static ResultSet listByField(String campo, String valor) {

        String sql = "SELECT * FROM dataLaudo WHERE " + campo + " LIKE '" + valor + "%'";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param date format dd/MM/yyyy
     * @return date formatted yyyy-MM-dd
     */
    public static String sqLiteDate(String date) {
        String[] dma = date.split("/");
        String dia = dma[0];
        String mes = dma[1];
        String ano = dma[2];
        String amd = ano + "-" + mes + "-" + dia;

        return amd;
    }

    /**
     *
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static ResultSet listByDateField(String dateStart, String dateEnd) {
        String strStart = sqLiteDate(dateStart);
        String strEnd = sqLiteDate(dateEnd);
        String sql = "SELECT * FROM dataLaudo WHERE dataVistoria BETWEEN '" + strStart + "' AND '" + strEnd + "'";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Map<String, ?>> rsToList() throws SQLException {
        ResultSet rs = listAll();

        ResultSetMetaData md = rs.getMetaData();

        int columns = md.getColumnCount();

        List<Map<String, ?>> results = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();

            for (int i = 1; i <= columns; i++) {
                //row.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
                row.put(md.getColumnLabel(i), rs.getObject(i));
            }

            results.add(row);
        }
        return results;
    }

    public static List<Map<String, ?>> rsListFilter(String campo, String nome) throws SQLException {
        ResultSet rs = listByField(campo, nome);
        if (rs != null) {
            ResultSetMetaData md = rs.getMetaData();

            int columns = md.getColumnCount();

            List<Map<String, ?>> results = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= columns; i++) {
                    //row.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }

                results.add(row);
            }
            return results;
        }
        return null;
    }

    public static List<Map<String, ?>> rsListDateFilter(String dateStart, String dateEnd) throws SQLException {
        ResultSet rs = listByDateField(dateStart, dateEnd);
        if (rs != null) {
            ResultSetMetaData md = rs.getMetaData();

            int columns = md.getColumnCount();

            List<Map<String, ?>> results = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= columns; i++) {
                    //row.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }

                results.add(row);
            }
            return results;
        }
        return null;
    }

    /**
     *
     * @param campo
     * @param valor
     * @param id
     * @return
     */
    public static boolean updateByFieldAndValue(String campo, String valor, int id) {
        String sql = "UPDATE dataLaudo SET " + campo + " = ? WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, valor);
            pstmt.executeUpdate();

            //conn.commit(); //database in auto-commit mode
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param cod
     * @param campos
     * @param id
     * @return
     */
    public static boolean updateFields(int cod, Map<String, String> campos, int id) {
        String sql;
        String cap = "";
        System.out.println("Código Update: " + cod);
        try {
            Connection conn = DriverManager.getConnection(url);
            switch (cod) {
                case 1:
                    cap = "dataVistoria = '" + campos.get("dataVistoria") + "'";
                    break;
                case 2:
                    cap = "horaVistoria = '" + campos.get("horaVistoria") + "'";
                    break;
                case 3:
                    cap = "dataVistoria = '" + campos.get("dataVistoria") + "', horaVistoria = '" + campos.get("horaVistoria") + "'";                    
                    break;
                case 4:
                    cap = "localVistoria = '" + campos.get("localVistoria") + "'";
                    break;
                case 5:
                    cap = "dataVistoria = '" + campos.get("dataVistoria") + "', localVistoria = '" + campos.get("localVistoria") + "'";
                    break;
                case 6:
                    cap = "horaVistoria = " + "'" + campos.get("horaVistoria") + "', localVistoria = '" + campos.get("localVistoria") + "'";
                    break;
                case 7:
                    cap = "dataVistoria = '" + campos.get("dataVistoria") + "', horaVistoria = '" + campos.get("horaVistoria") + "', localVistoria = '" + campos.get("localVistoria") + "'";
                    break;
            }
            sql = "UPDATE dataLaudo SET " + cap + " WHERE id = " + id;
            System.out.println("SQL Update: " + sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            //conn.commit(); //database in auto-commit mode
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param txtReclamante
     * @param txtReclamada
     * @param id
     * @return
     */
    public static boolean updateActivities(String txtReclamante, String txtReclamada, String descLocalTrabalho, int id) {
        // claimant activities defendant(réu) activities
        String sql = "UPDATE dataLaudo SET atividadesReclamante = ?, atividadesReclamada = ?, descricaoLocalTrabalho = ? WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtReclamante);
            pstmt.setString(2, txtReclamada);
            pstmt.setString(3, descLocalTrabalho);

            pstmt.executeUpdate();

            //conn.commit(); //database in auto-commit mode
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static Map<String, String> getActivities(String id) {
        Map<String, String> txt = new HashMap<>();
        String sql = "SELECT atividadesReclamante, atividadesReclamada FROM dataLaudo WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                txt.put("atividadesReclamante", rs.getString("atividadesReclamante"));
                txt.put("atividadesReclamada", rs.getString("atividadesReclamada"));
                conn.close();
                return txt;
            } else {
                conn.close();
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static boolean updateSafeguard(String txtReclamante, String txtReclamada, int id) {
        // claimant activities defendant(réu) activities
        String sql = "UPDATE dataLaudo SET acompanhantesReclamante = ?, acompanhantesReclamada = ? WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtReclamante);
            pstmt.setString(2, txtReclamada);

            pstmt.executeUpdate();

            //conn.commit(); //database in auto-commit mode
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static Map<String, String> getSafeguard(String id) {
        Map<String, String> txt = new HashMap<>();
        String sql = "SELECT acompanhantesReclamante, acompanhantesReclamada FROM dataLaudo WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                txt.put("acompanhantesReclamante", rs.getString("acompanhantesReclamante"));
                txt.put("acompanhantesReclamada", rs.getString("acompanhantesReclamada"));
                conn.close();
                return txt;
            } else {
                conn.close();
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public static boolean updateQuesitos(String txtReclamante, String txtReclamada, int id) {
        // claimant activities defendant(réu) activities
        String sql = "UPDATE dataLaudo SET quesitosReclamante = ?, quesitosReclamada = ? WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtReclamante);
            pstmt.setString(2, txtReclamada);

            pstmt.executeUpdate();

            //conn.commit(); //database in auto-commit mode
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static Map<String, String> getQuesitos(String id) {
        Map<String, String> txt = new HashMap<>();
        String sql = "SELECT quesitosReclamante, quesitosReclamada FROM dataLaudo WHERE id = " + id;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                txt.put("quesitosReclamante", rs.getString("quesitosReclamante"));
                txt.put("quesitosReclamada", rs.getString("quesitosReclamada"));
                conn.close();
                return txt;
            } else {
                conn.close();
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void bdClose(Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
