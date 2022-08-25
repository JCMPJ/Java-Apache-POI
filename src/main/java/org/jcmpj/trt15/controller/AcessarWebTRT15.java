package org.jcmpj.trt15.controller;

/**
 *
 * @author jcmpj
 */
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
//import com.microsoft.playwright.*;
import com.microsoft.playwright.Playwright;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import org.jcmpj.trt15.resources.Global;
import org.jcmpj.trt15.view.Inicio;

public class AcessarWebTRT15 implements Runnable {

    private final ArrayList<String> listaDados;
    private boolean ultimaPagina;
    private final String nome;
    private final Inicio ini;
    /**
     * 
     * @param nome
     * @param ini 
     */
    public AcessarWebTRT15(String nome, Inicio ini) {
        listaDados = new ArrayList<>();
        ultimaPagina = true;
        this.nome = nome;
        this.ini = ini;
    }

    @Override
    public void run() {
        try ( Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://pje.trt15.jus.br/primeirograu/login.seam");
            // System.out.println(page.title());

            String username = Global.getUserName();
            String password = Global.getPassword();
            
            page.fill("xpath=//*[@id=\"username\"]", username);
            page.fill("xpath=//*[@id=\"password\"]", password);
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
                    ini.setStatus("Lendo página: " + pag);
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

    /**
     * Tempo t em segundos
     * @param t
     */
    private static void tempo(int t) {
        int temp = t * 1000;
        try {
            Thread.sleep(temp);
        } catch (InterruptedException ex) {
            Logger.getLogger(Playwright.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Gerar o arquivo texto que será usado posteriormente pela classe
     * AtualizarBancoDados
     * @param linha 
     */
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
}
