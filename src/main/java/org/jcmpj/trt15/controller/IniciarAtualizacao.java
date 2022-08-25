/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcmpj.trt15.controller;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcmpj.trt15.resources.Global;

/**
 *
 * @author jcmpj
 */
public class IniciarAtualizacao {

    private final ArrayList<String> listaDados;
    private boolean ultimaPagina;
    private String REGEX;
    private final ArrayList<String> dataList;
    private final ArrayList<String> dataDupl;
    private Connection conn;

    public IniciarAtualizacao() {
        REGEX = "(\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4})";
        dataList = new ArrayList<>();
        dataDupl = new ArrayList<>();

        listaDados = new ArrayList<>();
        ultimaPagina = true;
    }

    public boolean atualizar() {
        update_dataFile();
        update_db();
        return true;
    }

    private void update_dataFile() {
        try ( Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate("https://pje.trt15.jus.br/primeirograu/login.seam");
            // System.out.println(page.title());

            page.fill("xpath=//*[@id=\"username\"]", "45938024900");
            page.fill("xpath=//*[@id=\"password\"]", "Borba1905@");
            page.locator("xpath=//*[@id=\"btnEntrar\"]").click();
            //page.locator("/html/body/pje-root/mat-sidenav-container/mat-sidenav-content/div/pje-cabecalho/div/mat-toolbar/section[2]/pje-cabecalho-perfil/div/button[1]").click();
            tempo(1);
            /* Botão trocar orgão julgador ou papel */
            page.locator("xpath=/html/body/pje-root/mat-sidenav-container/mat-sidenav-content/div/pje-cabecalho/div/mat-toolbar/section[2]/pje-cabecalho-perfil/div/button[1]").click();
            tempo(1);
            /* Botão promover para Perito */
            page.locator("xpath=//button[contains(text(), 'Perito')]").click();
            tempo(1);
            /* Botão Aguardando Laudo */
            page.locator("xpath=/html/body/pje-root/mat-sidenav-container/mat-sidenav-content/div/div/div/pje-home-perito/div/div/ng-component/div[2]/pje-painel-item[3]/div/mat-card").click();
            tempo(1);
            /**
             * Captura da lista de processos em uma certa página
             */
            int pag = 0;
            while (ultimaPagina) {
                try {
                    /**
                     * Seleciona as céluas que contém os dados de interesse do
                     * projeto
                     */
                    pag++;
                    //ini.setStatus("Lendo página: " + pag);
                    Locator rows = (Locator) page.locator("//pje-celula-dados-basicos-processo");

                    /**
                     * Constroi um ArrayList(listaDados) que será usado para
                     * gerar um arquivo .txt e posterior atualização do banco de
                     * dados
                     */
                    int count = rows.count();
                    for (int i = 0; i < count; i++) {
                        listaDados.add(rows.nth(i).textContent());
                        // System.out.println("Linha: " + i + " :: " + rows.nth(i).textContent() + "\n");
                    }
                } catch (ExceptionInInitializerError ex) {
                    System.err.println(ex.getMessage());
                }
                tempo(1);
                /* Botão próxima página */
                page.locator("//button[@aria-label=\"Próximo\"][not (@disabled)]").click();
                tempo(1);
                if (!page.isVisible("//button[@aria-label=\"Próximo\"][not (@disabled)]")) {
                    ultimaPagina = false;
                }
            }
            write(listaDados);
            //tempo(5);
        }
    }
    
    private void update_db() {
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
        // ini.setStatus("Gravando dados no Banco...");
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

    private static void tempo(int t) {
        int temp = t * 1000;
        try {
            Thread.sleep(temp);
        } catch (InterruptedException ex) {
            Logger.getLogger(Playwright.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void write(ArrayList<String> linha) {

        File dataFile = new File(Global.getPathDataFile());

        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }

            //construtor que recebe também como argumento se o conteúdo será acrescentado
            //ao invés de ser substituído (append)
            FileWriter fw = new FileWriter(dataFile, false);
            //construtor recebe como argumento o objeto do tipo FileWriter
            BufferedWriter bw = new BufferedWriter(fw);
            for (String l : linha) {
                //escreve o conteúdo no arquivo
                bw.write(l);
                //quebra de linha
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException ex) {
            String message = ex.getMessage();
            System.err.println(message);
        }
    }
    
    private void readerFile() {

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
    /*
    
    AcessarWebTRT15 thread1 = new AcessarWebTRT15("Arquivo Texto", this);
        AtualizarBancoDados th = new AtualizarBancoDados("Atualiza Banco", this);        
        
        Thread t1 = new Thread(thread1);
        Thread t2 = new Thread(thread2);
        
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
        t2.start();
        try {
            t2.join();
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
     */
}
