/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author darktarian
 */
public class FTools {

	/**
	 * Permet d'ouvrir via une commande shell un fichier dans le viewer approrié Necessite de lui passer le nom de
	 * l'appli en plsu du fichier -> comme un appel en terminal
	 *
	 * @param file
	 * @param appli
	 */
	public void openFileByLinux(File file, String appli) {
		if (Data.os.contains("linux")) {
			try {
				String tmp = file.getCanonicalPath();
				String[] cmd = {"/bin/sh", "-c", appli + " " + tmp};
				executeCmd(cmd);
			} catch (IOException ex) {
				//Data.log.log(1, "erreur openFileByLinux: " + ex.getMessage() + " cause:" + ex.getCause(), Tools.class.getName());
			}
		}
	}

	/**
	 * Pemret d'ouvrit n'importe quel fichers avec le viewer par dzefaut de Windows
	 *
	 * @param file File du fichier que l'on veut ouvrir ex : File file = new File("c:\tmp\couc.txt")
	 */
	public void openFileByWin(File file) {
		if (Data.os.contains("windows")) {
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException ex) {
				//Data.log.log(1, "erreur openFileByWin: " + ex.getMessage() + " cause:" + ex.getCause(), Tools.class.getName());
			}
		}
	}

	/**
	 * Execute une commande shell de linux. Aucun retour n'est prevue dans cette version
	 *
	 * @param cmd
	 */
	public void executeCmd(String[] cmd) {

		try {
			Process runtime = Runtime.getRuntime().exec(cmd);
			InputStream in = runtime.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			reader.close();
		} catch (IOException ex) {
			//Data.log.log(1, "erreur executeCmdn: " + ex.getMessage() + " cause:" + ex.getCause(), Tools.class.getName() + "_cmd");
		}

	}

	/**
	 * Renvoi une string qui donne l'os sur lequel s'execute iaapi
	 *
	 * @return String
	 */
	public static String dectectOS() {
		String os = "";
		os = System.getProperty("os.name").toLowerCase();
		if (os.contains("linux")) {
			os = "linux";
		} else {
			os = "windows";
		}

		return os;
	}

}
