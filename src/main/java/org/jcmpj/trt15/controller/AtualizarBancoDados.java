package org.jcmpj.trt15.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcmpj.trt15.resources.Global;
import org.jcmpj.trt15.view.Inicio;

public class AtualizarBancoDados implements Runnable {

    String REGEX;
    private final ArrayList<String> dataList;
    private final ArrayList<String> dataDupl;
    private Connection conn;
    private final String nome;
    private final Inicio ini;
    
    /**
     *
     * @param nome
     * @param ini
     */
    public AtualizarBancoDados(String nome, Inicio ini) {
        REGEX = "(\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4})";
        dataList = new ArrayList<>();
        dataDupl = new ArrayList<>();
        this.nome = nome;
        this.ini = ini;
    }

    @Override
    public void run() {
        String numProcesso;
        String reclamante;
        String reclamada;
        String aux;
        String[] auxRR;
        String[] dados = new String[3];

        /**
         * Expressão regular usada para separar os dados Nº Processo, nome
         * Reclamante, nome Reclamada que estão juntos em uma mesma linha
         */
        Pattern p = Pattern.compile(REGEX);

        /**
         * Obtem o dataList a partir de um arquivo
         */
        readerFile();
        // int count = 1;
        ini.setStatus("Gravando dados no Banco...");
        for (String linha : dataList) {
            Matcher m = p.matcher(linha);
            if (m.find()) {
                numProcesso = m.group();

                if (!dataDupl.contains(numProcesso)) {
                    dataDupl.add(numProcesso);
                } else {
                    System.out.println(numProcesso);
                }

                if (linha.indexOf("Audi") > 0) {
                    aux = linha.substring(m.end(), linha.indexOf("Audi")).trim();
                } else {
                    aux = linha.substring(m.end()).trim();
                }
                auxRR = aux.split("[Xx]");
                if (auxRR.length == 2) {
                    reclamante = auxRR[0].trim();
                    reclamada = auxRR[1].trim();
                    dados[0] = numProcesso;
                    dados[1] = reclamante;
                    dados[2] = reclamada;

                    InsertRecord(dados);
                } else {
                    
                }
            }
        }
        //valDuplicados();
        System.out.println("N° total sem duplicatas: " + dataDupl.size());
        System.out.printf("N° de duplicatas: %d \n", dataList.size() - dataDupl.size());
    }

    public void valDuplicados() {
        for (String s : dataList) {
            if (!dataDupl.contains(s)) {
                dataDupl.add(s);
            }
        }
    }

    public void readerFile() {

        File dataFile = new File(Global.getPathDataFile());
        try {
            try ( FileReader fr = new FileReader(dataFile);  BufferedReader br = new BufferedReader(fr)) {
                while (br.ready()) {
                    String linha = br.readLine();
                    dataList.add(linha);
                }
            }
        } catch (IOException ex) {
            String message = ex.getMessage();
            System.err.println(message);
        }
    }

    private Connection ConnDb() {
        try {
            String url = "jdbc:sqlite:" + Global.getPathLaudoDbFile();
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                return conn;
            } else {
                return conn;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }
        return null;
    }

    /**
     *
     * @param args
     */
    private void InsertRecord(String[] args) {
        conn = ConnDb();
        String sql = "INSERT INTO dataLaudo(numProcesso, nomeReclamante, nomeReclamada) VALUES(?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, args[0]);
            pstmt.setString(2, args[1]);
            pstmt.setString(3, args[2]);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            //System.err.println(e.getMessage());
        }
    }
}
