/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author darktarian
 */
public class ThreadFinder extends Thread {

	File dirStart;
	JLabel scanLabel;
	BlockingQueue<File> blockingQueue;
	BlockingQueue<String> cmdQueue;

	public ThreadFinder(BlockingQueue<File> blockingQueueExt, File dirStart, JLabel scanJLabel, BlockingQueue<String> cmdQueueExt) {
		this.dirStart = dirStart;
		this.scanLabel = scanJLabel;
		this.blockingQueue = blockingQueueExt;
		this.cmdQueue = cmdQueueExt;
	}

	private boolean checkFile(File inFile) {
		boolean test = true;
		boolean readable = inFile.canRead();

		if (inFile.getName().contains("System Volume Information")) {
			return false;
		}

		if (readable) {
			String[] ext = Data.prop.getProperty("IGNORE_EXT").split(",");
			String[] pre = Data.prop.getProperty("IGNORE_PREFIXE").split(",");
			String extFichier = inFile.getName().substring(inFile.getName().lastIndexOf(".") + 1);

			for (String extension : ext) {
				if (extension.equals(extFichier)) {
					return false;
				}
			}
			for (String prefixe : pre) {
				if (inFile.getName().startsWith(prefixe)) {
					return false;
				}
			}
		} else {
			test = false;
		}
		return test;
	}

	private void findFile(File dirStart) throws NoSuchAlgorithmException, IOException {
		File liste[] = dirStart.listFiles();
		for (File fichier : liste) {
			try {
				//if(!fichier.getName().startsWith("$") || fichier.getName().contains("System Volume Information") || fichier.getName().endsWith("sys")){
				if (checkFile(fichier)) {
					if (fichier.isDirectory() && fichier.canRead()) {
						findFile(fichier);
					} else {
						scanLabel.setText("Processing : " + fichier.getAbsolutePath());
						try {
							//System.err.println("fichier trouvé : "+fichier);
							blockingQueue.put(fichier);
						} catch (InterruptedException ex) {
							Logger.getLogger(ThreadFinder.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			} catch (NoSuchAlgorithmException | IOException e) {
				System.out.println("erreur " + fichier);
			}
		}
	}

	@Override
	public void run() {
		try {
			findFile(dirStart);
			for (int i = 0; i < Data.NB_THREAD; i++) {
				cmdQueue.add("STOP");
			}
		} catch (NoSuchAlgorithmException | IOException ex) {
			Logger.getLogger(ThreadFinder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
