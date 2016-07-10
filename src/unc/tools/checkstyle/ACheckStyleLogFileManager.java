package unc.tools.checkstyle;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ACheckStyleLogFileManager implements CheckStyleLogManager {
	protected  PrintWriter out = null;
	protected  BufferedWriter bufWriter;
	protected String logFileName;
	protected Map<String, Set<String>> fileNameToMessages = new HashMap();
	
	
	public  void newLog (String aFileName, int lineNo, int colNo, String key,
            Object... arg) {
		
	}
	void appendLine(String aLine) {
		out.println(aLine);
		out.flush();
	}
	
	 void maybeCreateOrLoadAppendableFile(String aFileName) {
		 if (out != null && bufWriter != null) {
			 return;
		 }
	        String aFullFileName = aFileName;
	        File aFile = new File(aFullFileName);
	        boolean aNewFile = !aFile.exists();
	        try {
	            bufWriter
	                    = Files.newBufferedWriter(
	                            Paths.get(aFullFileName),
	                            Charset.forName("UTF8"),
	                            StandardOpenOption.WRITE,
	                            StandardOpenOption.APPEND,
	                            StandardOpenOption.CREATE);
	            out = new PrintWriter(bufWriter, true);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            //Oh, no! Failed to create PrintWriter
	        }


	    }
	 
	 protected List<String> toTextLines(String aFileName) {
		 List<String> result = new ArrayList();
		 File aFile = new File(aFileName);
		 if (!aFile.exists())
			 return result;
		 try (BufferedReader br = new BufferedReader(new FileReader(aFileName))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       result.add(line);
			    }
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		 return result;
	 
	 }
}
