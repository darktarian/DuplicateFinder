/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 *
 * @author darktarian
 */
public class Duplicate {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException, UnsupportedEncodingException, URISyntaxException {
		// TODO code application logic here
		try {
			javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(DuplicateFinder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		DuplicateFinder df = new DuplicateFinder();
		df.setVisible(true);
	}
	
}
