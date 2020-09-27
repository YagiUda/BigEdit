
import BEServerBeanPkg.BEServerBeanRemote;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Author Name:   Cameron Yule

 * Big Edit V3.0
 * 
 */
public class BigEditClient implements ClientInterface{

    public BigEditClient() {
        
    }


	
String display;
String whiteBoard;
String str;

BufferedReader bufferedReader;
PrintWriter writer;
StringBuffer stringBuffer;
FileReader fileReader;

BEServerBeanRemote server;
InputStream in;
OutputStream out;


String currentFile;
String workDir = (System.getProperty("user.home")+"\\Documents\\NetBeansProjects\\ClientFiles");
String currentDir = workDir;
File tempFile = new File(workDir+"\\"+"temp.txt");
    

    
    @EJB
    private static BEServerBeanRemote bEServerBean;
   
     
   /**
     *iterate method simply goes over a String array and provides its contents to System out.
     */    
private void iterate(String[] input) {
		for(int i=0; i<=input.length-1;i++)
		{
			System.out.println(input[i]);
		}
	}

   /**
     *arrayzer  method converts any String into a String Array
     */
private String[] arrayzer(String input){
    String[] data;
    data = server.readFile(input, server.lineCount(input));
    return data;
}
   /**
     *getBoard method returns the client's whiteBoard contents.
     */
private String getBoard(){
    return whiteBoard;
}

   /**
     *collate method converts a String array into a normal String.
     */
private String collate(String[] input) {

    display="";

    for(int i=0; i<=input.length-1;i++)
	{
		//if we are at the end of the file, we dont want a blank new line
        	if (i==input.length-1) {
		display+=input[i];
		return display;
		}
		//keep adding to the display, return the display
		display+=input[i]+"\n";
			
		}
		return display;
}
    
  
    public static void main(String[] args) throws IOException {
        BigEditClient client = new BigEditClient();
        
 
        String[] receivedData;
       
        
        //DIRECTORY LISTING TEST
        client.connectToServer();
        client.iterate(client.getDirectoryListing("dir"));
        
        //READING DOCUMENT ON SERVER TEST
        receivedData = client.readFSDocument("test.txt");
        
        //OPEN THE FILE LOCALLY NOW...
        client.openFSDocument(client.collate(receivedData));
        
        //SIMULATING SAVING SOME CHANGES MADE TO THE FILE LOCALLY
        client.openFSDocument("This is the new string that will be taken from BigEdit's TextArea and saved locally...");
        
     
        //LOAD LOCAL WORKING FILE FOR TRANSMISSION BACK TO SERVER
        //temp.txt is what we were working on after receiving from the server
        //it is loaded onto our local whiteBoard using openFile method
        client.openFile("temp.txt");
        
        //SAVE THE DOCUMENT ON THE SERVER, THE WHITE BOARD
        //IS TURNED INTO A STRING ARRAY USING ARRAYZER
        
        client.writeFSDocument(client.arrayzer(client.getBoard()));
  
        //DISCONNECT FROM SERVER USING @Remove ON THE SERVER SIDE
        client.disconnectFromServer();
        
    }
    
    /**
     *
     * @param fileName
     * @throws IOException
     * openFile method will a use whatever file is specified by the parameter filename to read the file and copy it into
     * a string on the local whiteBoard for use by another method.
     */
    public void openFile(String fileName) throws IOException {
	
		str="";
		whiteBoard="";
		 try {
			 currentFile=currentDir+"\\"+fileName;
			 fileReader = new FileReader(currentFile);
			 bufferedReader = new BufferedReader(fileReader);
			 stringBuffer = new StringBuffer();
				
				while ((str = bufferedReader.readLine()) != null) {
					stringBuffer.append(str);
					stringBuffer.append("\n");
				}
				fileReader.close();
				System.out.println("\n\nContents of file:");
				System.out.println(stringBuffer.toString());
				str = stringBuffer.toString();
				
				whiteBoard = str;
				
		 } catch (FileNotFoundException e){
			 e.printStackTrace();
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	}  
    
    /**
     *connectToServer creates the context for the remote interface to be used by the client.
     */
    @Override
    public void connectToServer()  {
        Context ctx;
        try {
            ctx = new InitialContext(System.getProperties());
                           server = 
                   (BEServerBeanRemote) ctx.lookup("BEServerBeanPkg.BEServerBeanRemote");
        } catch (NamingException ex) {
            Logger.getLogger(BigEditClient.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        //At this point session is being confirmed, calling a method on the enterprise server
        System.out.println(server.startup());
    }
   /**
     *disconnectFromServer creates the ends the session.
     */
    @Override
    public void disconnectFromServer() {
        System.out.println("Disconnecting from server...");
        server.shutdown();
    }

   /**
     *getDirectoryListing will be provide directory contents of the specified directory on the server.
     */
    @Override
    public String[] getDirectoryListing(String dirname) {
        server.listDirectory(dirname);
        int lines = server.lineCount(server.getWhiteBoard());
        return server.readFile(server.getWhiteBoard(), lines);
    }

   /**
     *openFSDocument method will place a string into a local file for editing purposes.
     */
    @Override
    public void openFSDocument(String opened) {

	try {
		writer = new PrintWriter(tempFile);
		writer.println(opened);
		writer.close();
		System.out.println("\n"+tempFile+" locally saved for editing or transferring...");
		} catch (FileNotFoundException e1) {
			
		e1.printStackTrace();
	}

    }
   /**
     *this command will clear the BigEdit TextArea once it is used with the program.
     */
    @Override
    public void closeFSDocument() {
       //will call a method in bigEdit to clear the textArea currently being edited. 
    }

    
   /**
     *readFSdocument turns a regular string into a String array to provide increased data integrity while
     * being transferred between the client and server.
     */
    @Override
    public String[] readFSDocument(String opened) {
        server.openFile(opened);
        int lines = server.lineCount(server.getWhiteBoard());
        return server.readFile(server.getWhiteBoard(), lines);
        
        
    }
   
    
   /**
     *writeFSDocument will send the String array that readFSDocument has generated and pass it to the server.
     * the writeFile method will then be called on the server will be called to save the data locally on the server.
     */
    @Override
    public void writeFSDocument(String[] fileBuffer) {

        //sending data as a string array to server, converting it (using collate method) to regular String
        //converting String[] array to regular String
        //Saving that with the writeFile method.
        server.writeFile(server.collate(server.receivedData(fileBuffer)));
        
        
    }
    
}
