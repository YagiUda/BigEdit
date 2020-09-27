/**
 * Author Name:   Cameron Yule

 * Big Edit V3.0
 * 
 */

public interface ClientInterface {
	
	
	public void connectToServer();

	public void disconnectFromServer();

	public String[] getDirectoryListing(String dirname);

	public void openFSDocument(String opened);

	public void closeFSDocument();

	public String[] readFSDocument(String opened);

	public void writeFSDocument(String[] fileBuffer);

}
