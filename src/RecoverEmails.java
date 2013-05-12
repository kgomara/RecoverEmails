import java.io.*;
import net.n3.nanoxml.*;

public class RecoverEmails {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Finding Emails from Jameson Truth or Folklore");
		
		// Get the Backup Directory
		String backupDir = getBackupDir();
		System.out.println("backupDir = " + backupDir);
		
		// Get a list of all the Directories in backupDir - each is a device, could be one of ours
		File[] dirList = getBackedupDevices(backupDir);
		
		// Loop through the dirList for Backups containing "emailnames" and post to the spreadsheet
		int numEmailsSent = 0;
        for (File dir : dirList) {
    		File[] children = dir.listFiles();
    		for (File file : children) {
    			if (file.isDirectory()) {
    				// skip Directories, our files are at the root of the device backup
    			} else {
		        	if (fileContainsEmailnames("emailNames", file)) {
		            	System.out.println("File Name = " + file.getName());
		        		numEmailsSent += recoverEmailnamesFromBackup(file);
		        	}
    			}
	        }
        }

		// Finished!  Display the total number of Email found and posted to the spreadsheet
        System.out.printf("Processing complete. %d Emails found and sent to HQ%n", numEmailsSent);
	}
	
	private static String getBackupDir() {
		/* 
		 * path to Backup in Mac:
	 	 * ~/Library/Application\ Support/MobileSync/Backup/
		 *
		 * path to Backup for testing:
	 	 * /Users/kevinomara/Documents/Appiction/Jameson/Backup
		 *
		 * path to Backup in Windows XP:
		 * c:\Documents and Settings\(username)\Application Data\Apple Computer\MobileSync\Backup
		 *
		 * path to Backup in Windows Vista or 7:
	 	 * c:\Users\(username)\AppData\Roaming\Apple Computer\MobileSync\Backup\
		 */
		String userHome=System.getProperty("user.home");
		
		return userHome + "/Documents/Appiction/Jameson/Backup";
	}
	
	private static File[] getBackedupDevices(String backupDir) {
		File dir = new File(backupDir);
//		System.out.println("dir = " + dir);

		String[] children = dir.list();
//		System.out.println("children = " + children);
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        System.out.println("filename = " + filename);
		    }
		}

		// This filter does not return any files that start with `.'.
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".");
		    }
		};
		children = dir.list(filter);

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		return dir.listFiles(fileFilter);
	}
	
	private static Boolean fileContainsEmailnames(String searchText, File fileName) {

		StringBuilder sb = new StringBuilder();
	
		try {
			// Create the buffered input stream, from a file input stream
			BufferedInputStream bIn = new BufferedInputStream( new FileInputStream(fileName));
			
			int pos = 0;							// Holds the position of the last byte we have read
			int avl = bIn.available();				// Holds #of available bytes in our stream
		
			// Read as long as we have something
			while ( avl != 0 ) {
				byte[] buffer = new byte[avl];		// Holds the bytes which we read
			
				// Read from the file to the buffer starting from <pos>, <avl> bytes.
				bIn.read(buffer, pos, avl);
				pos += avl;							// Update the last read byte position
			
				// Create a new string from byte[] we read
				String strTemp = new String(buffer);
			
				// Append the string to the string builder
				sb.append(strTemp);
			
				// Get the next available set of bytes
				avl = bIn.available();
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	
		// Get the concatenated string from string builder
		String fileText = sb.toString();
			
		// If the index location in the file = -1 the string is not present
		return fileText.indexOf(searchText) != -1;
	}
	
	private static int recoverEmailnamesFromBackup(File file) {
		BinaryPListParser plist  = new BinaryPListParser();
		XMLElement xmlDoc;
		try {
			xmlDoc = plist.parse(file);
		} catch(IOException ex) {
			ex.printStackTrace();
			return 0;
		}
		System.out.println(xmlDoc);
		
		
		return 1;
	}
}
