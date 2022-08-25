package org.jcmpj.trt15.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jcmpj.trt15.model.DBManager;
import org.jcmpj.trt15.resources.Global;

/**
 *
 * @author jcmpj
 */
public class DocX {

    public static String id;
    static String numProcesso;
    static ResultSet rs;
    static String[] pathToDoc;

    public static void gerarDocumento() throws SQLException {
        FileInputStream fs = null;
        XWPFDocument doc = null;
        String text = null;
        Map<String, XWPFParagraph> listaAcompanhantes = new HashMap<>();

        Map<String, String> dl = PreencherForm.getLaudoIdNumProcesso();
        id = dl.get("id");

        rs = DBManager.listById(id);

        try {
            String pathToModel = System.getProperty("user.dir") + File.separator + "modelo_laudo.docx";
            // fs = new FileInputStream(getClass().getResource("/modelo.docx").getPath());
            fs = new FileInputStream(pathToModel);

        } catch (FileNotFoundException e) {

            System.out.print(e.getMessage());
        }

        try {
            doc = new XWPFDocument(fs);

        } catch (IOException ex) {
            System.out.print(ex.getMessage());

        }

        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                //List<XWPFRun> runs = paragraph.getRuns();
                if (run != null && run.getText(0) != null) {
                    text = run.getText(0);

                    if (text.contains("#processo")) { // <Processo/>
                        text = text.replace("#processo", rs.getString("numProcesso"));
                        run.setText(text, 0);
                        numProcesso = rs.getString("numProcesso");

                    } else if (text.contains("#reclamante")) {
                        text = text.replace("#reclamante", rs.getString("nomeReclamante"));
                        run.setText(text, 0);

                    } else if (text.contains("#reclamada")) {
                        text = text.replace("#reclamada", rs.getString("nomeReclamada"));
                        run.setText(text, 0);

                    } else if (text.matches("#?(dataEmissao)?")) {
                        if (text.equalsIgnoreCase("dataEmissao")){
                        text = text.replaceFirst("#?(dataEmissao)?", dataEmissao());
                        run.setText(text, 0);
                        }
                        else {
                            text = text.replace("#dataEmissao", dataEmissao());
                            run.setText(text, 0);
                            text = text.replace("#", "");
                            run.setText(text, 0);
                        }
                        
                    } else if (text.contains("#periodoReclamado")) {
                        if (rs.getString("periodoReclamado") == null) {
                            text = text.replaceFirst("#periodoReclamado", "");
                            run.setText(text, 0);
                        } else {
                            text = text.replaceFirst("#periodoReclamado", rs.getString("periodoReclamado"));
                            run.setText(text, 0);
                        }

                    } else if (text.matches("<funcaoExercida/>")) {
                        if (rs.getString("funcaoExercida") == null) {
                            text = text.replaceFirst("<funcaoExercida/>", "");
                            run.setText(text, 0);
                        } else {
                            text = text.replaceFirst("<funcaoExercida/>", rs.getString("funcaoExercida"));
                            run.setText(text, 0);
                        }

                    } else if (text.matches("#?(dataVistoria)?")) {
                        if (text.equalsIgnoreCase("dataVistoria")) {
                            if (rs.getString("dataVistoria") == null) {
                                text = text.replaceFirst("#?(dataVistoria)?", "");
                                run.setText(text, 0);

                            } else {
                                text = text.replaceFirst("dataVistoria", builFormDate(rs.getString("dataVistoria")));
                                run.setText(text, 0);
                                buildPathToDoc(rs.getString("dataVistoria"));
                            }
                        }

                    } else if (text.matches("#?(horaVistoria)?")) {
                        text = text.replaceFirst("#?(horaVistoria)?", rs.getString("horaVistoria"));
                        run.setText(text, 0);

                    } else if (text.matches("#?(localVistoria)?")) {
                        if (rs.getString("localVistoria") == null) {
                            text = text.replaceFirst("#?(localVistoria)?", "");
                            run.setText(text, 0);
                        } else {
                            text = text.replaceFirst("#?(localVistoria)?", rs.getString("localVistoria"));
                            run.setText(text, 0);
                        }

                    } else if (text.matches("#?(enderecoVistoria)?")) {
                        if (rs.getString("enderecoVistoria") == null) {
                            text = text.replaceFirst("#?(enderecoVistoria)?", "");
                            run.setText(text, 0);
                        } else {
                            text = text.replaceFirst("#?(enderecoVistoria)?", rs.getString("enderecoVistoria"));
                            run.setText(text, 0);
                        }
                    } else if (text.contains("<acompanhantesReclamante/>")) {
                        if (rs.getString("acompanhantesReclamante") == null) {
                            text = text.replace("<acompanhantesReclamante/>", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("acompanhantesReclamante", paragraph);
                            String[] acs = rs.getString("acompanhantesReclamante").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<acompanhantesReclamante/>", rs.getString("acompanhantesReclamante"));
                            //run.setText(text, 0);
                        }
                    } else if (text.contains("<acompanhantesReclamada/>")) {
                        if (rs.getString("acompanhantesReclamada") == null) {
                            text = text.replace("<acompanhantesReclamada/>", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("acompanhantesReclamada", paragraph);
                            String[] acs = rs.getString("acompanhantesReclamada").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<acompanhantesReclamada/>", rs.getString("acompanhantesReclamada"));
                            //run.setText(text, 0);                            
                        }
                    } else if (text.contains("<atividadesReclamante/>")) {
                        if (rs.getString("atividadesReclamante") == null) {
                            text = text.replace("<atividadesReclamante/>", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("atividadesReclamante", paragraph);
                            String[] acs = rs.getString("atividadesReclamante").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<atividadesReclamante/>", rs.getString("atividadesReclamante"));
                            //run.setText(text, 0);
                        }
                    } else if (text.contains("<atividadesReclamada/>")) {
                        if (rs.getString("atividadesReclamada") == null) {
                            text = text.replace("<atividadesReclamada/>", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("atividadesReclamada", paragraph);
                            String[] acs = rs.getString("atividadesReclamada").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<atividadesReclamada/>", rs.getString("atividadesReclamada"));
                            //run.setText(text, 0);
                        }
                    } else if (text.contains("<descricaoLocalTrabalho/>")) {
                        if (rs.getString("descricaoLocalTrabalho") == null) {
                            text = text.replace("<descricaoLocalTrabalho/>", "");
                            run.setText(text, 0);
                        } else {
                            text = text.replace("<descricaoLocalTrabalho/>", rs.getString("descricaoLocalTrabalho"));
                            run.setText(text, 0);
                        }
                    } else if (text.contains("<quesitosReclamante/>")) {
                        if (rs.getString("quesitosReclamante") == null) {
                            text = text.replace("<quesitosReclamante/>", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("quesitosReclamante", paragraph);
                            String[] acs = rs.getString("quesitosReclamante").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<atividadesReclamante/>", rs.getString("atividadesReclamante"));
                            //run.setText(text, 0);
                        }
                    } else if (text.contains("#quesitosReclamada")) {
                        if (rs.getString("quesitosReclamada") == null) {
                            text = text.replace("#quesitosReclamada", "");
                            run.setText(text, 0);
                        } else {
                            listaAcompanhantes.put("quesitosReclamada", paragraph);
                            String[] acs = rs.getString("quesitosReclamada").split("\n");
                            run.setText("", 0);
                            for (String a : acs) {
                                run.addTab();
                                run.setFontFamily("Arial");
                                run.setFontSize(12);
                                run.setText(a);
                                run.addBreak();
                            }
                            //text = text.replace("<atividadesReclamada/>", rs.getString("atividadesReclamada"));
                            //run.setText(text, 0);
                        }
                    }
                    // System.out.print(run + "\n");
                    // System.out.print(text);
                }
            }
        }

        String arquivoLaudo = pathToNewDoc();

        File dataFile = new File(arquivoLaudo); // C:\\User\\user\\Documents\\Laudos\\2022\\05\\31\\

        if (!Files.exists(Paths.get(arquivoLaudo))) {
            dataFile.mkdirs();
        }

        String strOut = arquivoLaudo + numProcesso + ".docx";
        try ( FileOutputStream out = new FileOutputStream(strOut)) {
            // pathDocumemtos + "Laudos" + File.separator + 'nomeDoArquivoLaudo';
            doc.write(out);
        } catch (Exception e) {
            System.out.print("Error ao abrir file outputstream");

        }
        try {
            String comando;
            if (File.separator.equals("/")) {
                comando = "libreoffice --writer " + strOut;
            } else {
                //comando = "\"c:\\Program Files (x86)\\microsoft Office\\office14\\winword.exe\" /t " + strOut;
                comando = locateWinword() + " /t " + strOut;
            }
            Process exec = Runtime.getRuntime().exec(comando);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        rs.close();
    }

    private static String dataEmissao() {
        String[] mes = {
            "",
            "janeiro",
            "fevereiro",
            "março",
            "abril",
            "maio",
            "junho",
            "julho",
            "agosto",
            "setembro",
            "outubro",
            "novembro",
            "dezembro"
        };
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        String[] dma = data.split("/");
        String dataEmissao = "Campinas, " + dma[0] + " de " + mes[Integer.parseInt(dma[1])] + " de " + dma[2];
        // System.out.println(dataEmissao);
        return dataEmissao;
    }

    private static String builFormDate(String data) {
        String dia, mes, ano;
        String[] amd = data.split("-");
        dia = amd[2];
        mes = amd[1];
        ano = amd[0];
        String dma = dia + "/" + mes + "/" + ano;

        return dma;
    }

    private static String[] buildPathToDoc(String data) {
        if (data == null || data.isEmpty()) {
            data = "1970-01-01";
        }
        pathToDoc = new String[3];
        String[] amd = data.split("-");
        pathToDoc[0] = amd[0];
        pathToDoc[1] = amd[1];
        pathToDoc[2] = amd[2];

        return pathToDoc;
    }

    private static String pathToNewDoc() throws SQLException {
        if (pathToDoc == null) {
            pathToDoc = buildPathToDoc(rs.getString("dataVistoria"));
        }
        StringBuilder path = new StringBuilder();
        path.append(Global.getPathDoc()); // C:\\User\\user\\Documents ou /home/user/Documentos
        path.append(File.separator);
        path.append("Laudos");          // C:\\User\\user\\Documents\\Laudos
        path.append(File.separator);
        path.append(pathToDoc[2]);      // C:\\User\\user\\Documents\\Laudos\\2022
        path.append(File.separator);
        path.append(pathToDoc[1]);      // C:\\User\\user\\Documents\\Laudos\\2022\\05
        path.append(File.separator);
        path.append(pathToDoc[0]);      // C:\\User\\user\\Documents\\Laudos\\2022\\05\\31        
        path.append(File.separator);
        // para verificar se o diretório pathDoc existe
        String pathToLaudo = new String(path); // C:\\User\\user\\Documents\\Laudos\\2022\\05\\31\\
        /**
         * String arquivoLaudo; if (File.separator.equalsIgnoreCase("/")){
         * arquivoLaudo = pathDoc + numProcesso + ".odt"; } else { arquivoLaudo
         * = pathDoc + numProcesso + ".docx"; }
         */
        return pathToLaudo;
    }

    private static String locateWinword() {
        String command = null;
        String[] path = {
            "C:\\Program Files (x86)\\Microsoft Office\\Office12\\winword.exe",
            "C:\\Program Files\\Microsoft Office\\Office12\\winword.exe",
            "C:\\Program Files (x86)\\Microsoft Office\\Office14\\winword.exe",
            "C:\\Program Files\\Microsoft Office\\Office14\\winword.exe",
            "C:\\Program Files (x86)\\Microsoft Office\\Office15\\winword.exe",
            "C:\\Program Files\\Microsoft Office\\Office15\\winword.exe",
            "C:\\Program Files (x86)\\Microsoft Office\\Office16\\winword.exe",
            "C:\\Program Files\\Microsoft Office\\Office16\\winword.exe",
            "C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\winword.exe",
            "C:\\Program Files\\Microsoft Office\\root\\Office16\\winword.exe"
        };

        for (int i = 0; i < path.length; i++) {
            File winwordexc = new File(path[i]);
            if (winwordexc.exists()) {
                command = path[i];
                break;
            }
            // System.out.println(path[i]);
            // System.out.println(command);
        }
        return command;
    }
}
