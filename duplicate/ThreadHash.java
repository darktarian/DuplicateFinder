/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author darktarian
 */
public class ThreadHash extends Thread {

	JLabel scanLabel;
	BlockingQueue<File> blockingQueue;
	ConcurrentHashMap<String, Set<String>> findedfiles;
	BlockingQueue<String> cmdQueue;

	public ThreadHash(BlockingQueue<File> blockingQueueExt, JLabel scanLabel, ConcurrentHashMap<String, Set<String>> findedfilesExt,BlockingQueue<String> cmdQueueExt) {
		this.blockingQueue = blockingQueueExt;
		this.scanLabel = scanLabel;
		this.findedfiles = findedfilesExt;
		this.cmdQueue = cmdQueueExt;
	}

	private void calculHash(File input) throws NoSuchAlgorithmException, FileNotFoundException {
		StringBuilder sb = new StringBuilder("");
		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(input);

		try {

			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			fis.close();
		} catch (Exception e) {
			System.err.println("Erreur de lecture : " + input.getAbsoluteFile());
		}
		scanLabel.setText("Hash "+sb.toString());
		Set<String> tmp = findedfiles.get(sb.toString());
		if (tmp ==null) {
			tmp = new HashSet<>();
			Collections.synchronizedSet(tmp);
			findedfiles.put(sb.toString(), tmp);
		} 
		tmp.add(input.getAbsolutePath());		

	}

	@Override
	public void run() {
		boolean waitUntilEnd=true;
		
		try {
			while (true) {
				String cmd = null;
				if(waitUntilEnd)
					cmd = cmdQueue.poll();
				
				if (cmd == null) {
					File tmp = null;
					if(waitUntilEnd)
						tmp = blockingQueue.take();
					else{
						tmp = blockingQueue.poll();
						if(tmp == null){
							break;
						}
					}
					calculHash(tmp);
				}else{					
					switch(cmd){
						case "STOP":
							//System.err.println("STOPPPPPPPPPPPPPPPPPPPPPPPPPPPP!");
							waitUntilEnd=false;
							break;
					}
				}
			}

		} catch (NoSuchAlgorithmException | FileNotFoundException | InterruptedException ex) {
			Logger.getLogger(ThreadHash.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
