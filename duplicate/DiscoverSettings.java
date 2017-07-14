/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

/**
 *
 * @author fred
 */
public final class DiscoverSettings {

    /**
     *
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public DiscoverSettings() throws IOException, UnsupportedEncodingException, URISyntaxException {
        Data.pathDirJAR = getAppRootDir();
        boolean a = settingExists();
        if (a) {
            setSettings();
        }
    }

    /**
     * Retourne le resultat du test qui verifie l'existence du fichier de configuration de IAAPI
     *
     * @return boolean
     */
    public boolean settingExists() {

        //s'il n'existe pas :
        if (!new File(Data.pathDirJAR, "Duplicate.prop").exists()) {
            Data.os = FTools.dectectOS();
            String whereIam = Data.pathDirJAR;
            Data.prop.setProperty("OS", Data.os);
            Data.prop.setProperty("LOCALISATION", whereIam);            
            Data.prop.setProperty("SAVE_REP", System.getProperty("user.dir"));
            Data.prop.setProperty("WORK_REP", System.getProperty("user.dir"));
			Data.prop.setProperty("IGNORE_EXT", "sys,css,js,bat,so,dll");
			Data.prop.setProperty("IGNORE_PREFIXE", "$,.,..,");
            
            if (Data.os.equals("linux")) {
                Data.prop.setProperty("NAVIGATEUR", "iceweasel");
            } else {
                Data.prop.setProperty("NAVIGATEUR", "iexplorer");
            }

            try {
                FileWriter write = new FileWriter(new File(Data.pathDirJAR, "Duplicate.prop"));
                Data.prop.store(write, "Settings Généraux de l'application");
            } catch (IOException ex) {
                //Data.log.log(1, "Erreur dans l'ecriture du fichier de configuration", DiscoverSettings.class.getName());
            }
            return true;
        } else {
            return true;
        }

    }

    /**
     *
     * @throws IOException
     */
    public void setSettings() throws IOException {
        //Data.navigateurs = Data.prop.getProperty("Navigateur");
        Data.prop.load(new FileInputStream(new File(Data.pathDirJAR, "Duplicate.prop")));
        Data.os = Data.prop.getProperty("OS");
        Data.navigateurs = Data.prop.getProperty("NAVIGATEUR");
        Data.pathDir = Data.prop.getProperty("LOCALISATION");
        Data.saveRepDefaut = Data.prop.getProperty("SAVE_REP");
        Data.repTravailDefaut = Data.prop.getProperty("WORK_REP");
		Data.ignore_ext = Data.prop.getProperty("IGNORE_EXT");
		Data.ignore_prefixe = Data.prop.getProperty("IGNORE_PREFIXE");





    }

    /**
     * Permet d'obtenir le répertoire de lancement du JAR
     *
     * @return String
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public final String getAppRootDir() throws UnsupportedEncodingException, URISyntaxException {
        String jarDir = "";
        CodeSource source = duplicate.Duplicate.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(URLDecoder.decode(source.getLocation().toURI().getPath(), "UTF-8"));
        jarDir = jarFile.getParentFile().getPath();
        return jarDir;
    }
}
