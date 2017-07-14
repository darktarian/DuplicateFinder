package duplicate;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class DuplicateFinder extends javax.swing.JFrame {
	File depart;
	DefaultMutableTreeNode myRoot;
	DefaultTreeModel myModel;
	DefaultMutableTreeNode hashNode;
	DefaultMutableTreeNode sub;

	public DuplicateFinder() throws IOException, UnsupportedEncodingException, URISyntaxException {
		initComponents();
		Data data = new Data();
		DiscoverSettings disc = new DiscoverSettings();
		JOptionPane info = new JOptionPane();
		scanLabel.setText("");

		myRoot = new DefaultMutableTreeNode("hash");
		myTree.setModel(myModel);
		myTree.setShowsRootHandles(true);
		myTree.setRootVisible(true);
		scanMenuItem.setVisible(false);
		cleanMenuItem.setVisible(false);
		toolsMenu.setVisible(false);

		JPopupMenu jm = new JPopupMenu();
		JMenu subM = new JMenu("Move");
		JMenuItem itemDel = new JMenuItem("Delete");
		itemDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					TreePath[] mesChemins = myTree.getSelectionModel().getSelectionPaths();
					StringBuilder lst = new StringBuilder();
					for (TreePath chemin : mesChemins) {
						lst.append(chemin.toString() + "\n");
					}
					int test = JOptionPane.showConfirmDialog(
							null,
							"Vous êtes sur le point d'effacer ce fichier:\n" + lst,
							"Confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
					);

					for (TreePath chemin : mesChemins) {
						Object b = chemin.getLastPathComponent();
						File tmp = new File(b.toString());
						if (test == JOptionPane.OK_OPTION) {
							if (tmp.isFile()) {
								tmp.delete();
								myTree.removeSelectionPath(chemin);
								DefaultTreeModel model = (DefaultTreeModel) myTree.getModel();
								model.removeNodeFromParent((MutableTreeNode) b);
							} else {
								JOptionPane.showMessageDialog(null, "Directory cant be deleted !");
							}
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "You need to Select something !");
				}
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		JMenuItem itemSubMOther = new JMenuItem("Move");
		itemSubMOther.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TreePath myPath = myTree.getAnchorSelectionPath();
				Object b = myPath.getLastPathComponent();
				File tmp = new File(b.toString());
				JFileChooser chooser = new JFileChooser();
				int c = chooser.showSaveDialog(chooser);
				if (c == JFileChooser.APPROVE_OPTION) {
					try {

						Files.move(tmp.toPath(), chooser.getSelectedFile().toPath(), StandardCopyOption.COPY_ATTRIBUTES);
					} catch (IOException ex) {
						Logger.getLogger(DuplicateFinder.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		JMenuItem itemInfo = new JMenuItem("Info");
		itemInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					//TreePath myPath = myTree.getAnchorSelectionPath();
					TreePath[] mesChemins = myTree.getSelectionModel().getSelectionPaths();
					StringBuilder lst = new StringBuilder();
					int tt = 0;
					for (TreePath chemin : mesChemins) {
						File tmp = new File(chemin.getLastPathComponent().toString());
						tt += tmp.length();
						lst.append(chemin.getLastPathComponent().toString()).append("\n");
					}

					//File tmp = new File(myPath.getLastPathComponent().toString());
					JOptionPane.showMessageDialog(null, "Taille total:" + adaptTaille((int) tt) + " \n" + lst);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "You need to Select something !");
				}
			}
		});

		JMenuItem itemOpenDir = new JMenuItem("OpenDir");
		itemOpenDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					TreePath myPath = myTree.getAnchorSelectionPath();
					Object b = myPath.getLastPathComponent();
					File tmp = new File(b.toString());
					String tmp2 = tmp.getParent();
					Desktop.getDesktop().open(new File(tmp2));
				} catch (IOException ex) {
					Logger.getLogger(DuplicateFinder.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		JMenuItem itemRename = new JMenuItem("Rename");
		itemRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				TreePath[] mesChemins = myTree.getSelectionModel().getSelectionPaths();
				//StringBuilder lst = new StringBuilder();
				int inc = 0;

				for (TreePath chemin : mesChemins) {
					Object b = chemin.getLastPathComponent();
					File tmp = new File(b.toString());
					if (mesChemins.length > 1) {
						String ext = tmp.getName().substring(tmp.getName().indexOf("."));
						String name = tmp.getName().substring(0, tmp.getName().indexOf("."));
						tmp.renameTo(new File(tmp.getParent(), name + "_(doublon)-" + inc + ext));
						inc++;
					} else {
						JFileChooser newFile = new JFileChooser(tmp.getParentFile());
						int bb = newFile.showSaveDialog(newFile);
						if (bb == JFileChooser.APPROVE_OPTION) {
							tmp.renameTo(newFile.getSelectedFile());
							break;
						}
					}

				}
			}
		});

		//////////////////////
		subM.add(itemSubMOther);
		//subM.add(itemSubMBin);
		///////////////////////
		jm.add(itemDel);
		jm.add(subM);
		//jm.add(itemMove);
		jm.addSeparator();
		jm.add(itemInfo);
		jm.add(itemOpenDir);
		jm.add(itemRename);

		myTree.setComponentPopupMenu(jm);
	}

	public String adaptTaille(int len) {
		StringBuilder sb = new StringBuilder();
		if (len <= 1024) {
			sb.append(Integer.toString(len)).append(" o");
		}
		if (len > 1024 && len < 1048576) {
			sb.append(Integer.toString(len / 1024)).append(" kb");
		}
		if (len > 1048576) {
			sb.append(Integer.toString(len / 1048576)).append(" Mb");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        myTree = new javax.swing.JTree();
        scanLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mainMenu = new javax.swing.JMenu();
        addDirMenuItem = new javax.swing.JMenuItem();
        scanMenuItem = new javax.swing.JMenuItem();
        cleanMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        expandMenuItem = new javax.swing.JMenuItem();
        collapseMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane2.setViewportView(myTree);

        scanLabel.setText("jLabel1");

        mainMenu.setText("Menu");

        addDirMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        addDirMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/diagona_pack_03_(2)_0016.png"))); // NOI18N
        addDirMenuItem.setText("Add Directory");
        addDirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDirMenuItemActionPerformed(evt);
            }
        });
        mainMenu.add(addDirMenuItem);

        scanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        scanMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/diagona_pack_26_(2)_0016.png"))); // NOI18N
        scanMenuItem.setText("Scan");
        scanMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanMenuItemActionPerformed(evt);
            }
        });
        mainMenu.add(scanMenuItem);

        cleanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        cleanMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/diagona_pack_29_(3)_0016.png"))); // NOI18N
        cleanMenuItem.setText("Clean");
        cleanMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanMenuItemActionPerformed(evt);
            }
        });
        mainMenu.add(cleanMenuItem);

        jMenuBar1.add(mainMenu);

        toolsMenu.setText("Tools");

        expandMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        expandMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/diagona_pack_38_(3)_0016.png"))); // NOI18N
        expandMenuItem.setText("Expand All");
        expandMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(expandMenuItem);

        collapseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        collapseMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/diagona_pack_39_(3)_0016.png"))); // NOI18N
        collapseMenuItem.setText("Collapse All");
        collapseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collapseMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(collapseMenuItem);

        jMenuBar1.add(toolsMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scanLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scanLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addDirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDirMenuItemActionPerformed
		// TODO add your handling code here:
		JFileChooser choose = new JFileChooser();
		choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int b = choose.showOpenDialog(choose);
		if (JFileChooser.APPROVE_OPTION == b) {
			depart = choose.getSelectedFile();
			myRoot = new DefaultMutableTreeNode(depart.getAbsolutePath());
			myModel = new DefaultTreeModel(myRoot);
			myTree.setModel(myModel);
			myTree.updateUI();
			scanMenuItem.setVisible(true);
			cleanMenuItem.setVisible(true);
		}
    }//GEN-LAST:event_addDirMenuItemActionPerformed

    private void scanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanMenuItemActionPerformed
		toolsMenu.setVisible(true);
		ThreadJob job = new ThreadJob(depart, scanLabel, myTree, myRoot);
		job.start();

    }//GEN-LAST:event_scanMenuItemActionPerformed

    private void expandMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandMenuItemActionPerformed
		// TODO add your handling code here:
		for (int i = 0; i < myTree.getRowCount(); i++) {
			myTree.expandRow(i);
			myTree.updateUI();
		}
    }//GEN-LAST:event_expandMenuItemActionPerformed

    private void collapseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collapseMenuItemActionPerformed
		// TODO add your handling code here:
		for (int i = 0; i < myTree.getRowCount(); i++) {
			myTree.collapseRow(i);
		}
    }//GEN-LAST:event_collapseMenuItemActionPerformed

    private void cleanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanMenuItemActionPerformed
		// TODO add your handling code here:
		depart = null;
		myTree.removeAll();
		myRoot.removeAllChildren();
		myTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		myTree.updateUI();
		scanMenuItem.setVisible(false);
		cleanMenuItem.setVisible(false);
		toolsMenu.setVisible(false);
		scanLabel.setText("");
    }//GEN-LAST:event_cleanMenuItemActionPerformed

	private DefaultMutableTreeNode createNewNode(String input) {
		DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(input);
		return tmp;
	}

	public static void main(String args[]) {


		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new DuplicateFinder().setVisible(true);
				} catch (IOException | URISyntaxException ex) {
					Logger.getLogger(DuplicateFinder.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addDirMenuItem;
    private javax.swing.JMenuItem cleanMenuItem;
    private javax.swing.JMenuItem collapseMenuItem;
    private javax.swing.JMenuItem expandMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenu mainMenu;
    private javax.swing.JTree myTree;
    private javax.swing.JLabel scanLabel;
    private javax.swing.JMenuItem scanMenuItem;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
}
