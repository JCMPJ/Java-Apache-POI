package org.jcmpj.trt15.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcmpj.trt15.model.DBManager;

/**
 *
 * @author jcmpj
 */
public class Global {

    private static String pathAux;
    private static String pathDocumemtos;
    private static String pathDataFile;
    private static String pathLaudoDbFile;

    public static void Startmain() {

        StringBuilder sb = new StringBuilder();

        sb.append(System.getProperty("user.home"));
        sb.append(System.getProperty("file.separator"));
        if (File.separator.equalsIgnoreCase("/")){
            sb.append("Documentos");
        } else {
            sb.append("Documents");
        }
        sb.append(System.getProperty("file.separator"));
        pathDocumemtos = new String(sb); // pathDocumemtos + "Laudos" + File.separator + nomeDoArquivoLaudo
        sb.append("Laudos-TRT");
        sb.append(File.separator);
        pathAux = new String(sb);
        File strPath = new File(pathAux);
        String pathToDataFile = pathAux + "dataFile.txt";
        pathDataFile = pathToDataFile;
        String pathToLaudo = pathAux + "laudo.db";
        pathLaudoDbFile = pathToLaudo;

        File dataFile = new File(pathToDataFile);
        File laudoFile = new File(pathToLaudo);

        if (!Files.exists(Paths.get(pathAux))) {
            strPath.mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!laudoFile.exists()) {
            //laudoFile.mkdirs();
            if (DBManager.createNewDatabase()) {
                DBManager.createNewTable();
            }
        }
    }
    
    public static String getPathAux() {
        Startmain();
        return pathAux;
    }
    
    public static String getPathDoc() {
        Startmain();
        return pathDocumemtos;
    }
    
    public static String getPathDataFile() {
        Startmain();
        return pathDataFile;
    }

    public static String getPathLaudoDbFile() {
        Startmain();
        return pathLaudoDbFile;
    }
    
    public static String getUserName() {
        LoadResources loadResources = new LoadResources();
        Properties prop = loadResources.loadPropertiesFile("per.properties");
        //prop.forEach((k, v) -> System.out.println(k + ":" + v));
        System.out.println("per.username:: " + prop.getProperty("per.username"));
        return prop.getProperty("per.username");
    }
    
    public static String getPassword() {
        LoadResources loadResources = new LoadResources();
        Properties prop = loadResources.loadPropertiesFile("per.properties");
        //prop.forEach((k, v) -> System.out.println(k + ":" + v));
        System.out.println("per.password:: " + prop.getProperty("per.password"));
        return prop.getProperty("per.password");
    }
}
