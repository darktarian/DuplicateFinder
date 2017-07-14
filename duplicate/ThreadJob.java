/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author darktarian
 */
public class ThreadJob extends Thread {

	BlockingQueue<File> blockingQueue;
	BlockingQueue<String> cmdQueue;
	File depart;
	JLabel scanLabel;
	ConcurrentHashMap<String, Set<String>> findedfiles;
	JTree myTree;
	DefaultMutableTreeNode myRoot;
	DefaultMutableTreeNode hashNode;
	DefaultMutableTreeNode sub;

	public ThreadJob(File depart, JLabel scanLabel, JTree myTree,DefaultMutableTreeNode myRoot) {
		this.depart = depart;
		this.scanLabel = scanLabel;
		this.myTree = myTree;
		this.myRoot = myRoot;		
		blockingQueue = new LinkedBlockingQueue(1000);
		cmdQueue = new LinkedBlockingQueue<>();
		findedfiles = new ConcurrentHashMap<>();
	}

	private DefaultMutableTreeNode createNewNode(String input) {
		DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(input);
		return tmp;
	}

	public void run() {
		ThreadFinder find = new ThreadFinder(blockingQueue, depart, scanLabel, cmdQueue);
		ArrayList<ThreadHash> hashList = new ArrayList<>();

		for (int i = 0; i < Data.NB_THREAD; i++) {
			hashList.add(new ThreadHash(blockingQueue, scanLabel, findedfiles, cmdQueue));
		}

		find.start();

		for (ThreadHash hash : hashList) {
			hash.start();
		}

		try {
			for (ThreadHash hash : hashList) {
				hash.join();
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(DuplicateFinder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<String, Set<String>> entrySet : findedfiles.entrySet()) {
					String key = entrySet.getKey();
					Set<String> value = entrySet.getValue();
					//System.err.println(key);
					if (value.size() > 1) {
						
						hashNode = createNewNode("SHA1 : " +key+" - (Nb:"+value.size()+")" );
						
						for (String file : value) {
							sub = createNewNode(file);
							hashNode.add(sub);
						}
						myRoot.add(hashNode);
					}
				}
				myTree.setModel(new DefaultTreeModel(myRoot));
				
				myTree.updateUI();
			}
		});
		

	}

}
