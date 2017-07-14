/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duplicate;

import java.util.Properties;

/**
 *
 * @author darktarian
 */
public class Data {
	
	public static String pathDirJAR ="";
	public static String os;
	public static Properties prop; 
	public static String navigateurs;
    public static String saveRepDefaut;
	public static String repTravailDefaut;
	public static String pathDir;// = System.getProperty("user.dir");
	public static String ignore_ext;
	public static String ignore_prefixe="";
	public static int NB_THREAD = 5;

	public Data() {
		prop = new Properties();
		pathDir = System.getProperty("user.dir");
	}
	
	
	
	
	
}
