
package BEServerBeanPkg;

import javax.ejb.Remote;

/**
 * Author Name:   Cameron Yule

 * Big Edit V3.0
 * 
 */


@Remote
public interface BEServerBeanRemote {
   
   String collate(String[] input);
   String startup();
   void listDirectory(String dirName);
   void openFile(String fileName);
   void closeFile();
   String[] readFile(String arrayThis, int size);
   void writeFile(String input);
   void shutdown();
   String getWhiteBoard();
   int lineCount(String countThis);
   String[] receivedData(String[] rx);
}
