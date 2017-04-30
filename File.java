import java.io.*;

public class File {
	public String directory;
	public String name;
	public String[] contents = new String[0];
	public BufferedReader breader;
	public BufferedWriter bwriter;
	public FileReader reader;
	public FileWriter writer;
	
	public static String path;
	
	public File(String directory, String name){
		this.directory = directory;
		this.name = name;
		this.path = directory+"/"+name;
	}
	public File(String path){
		this.directory = "";
		for (int i=0; i<path.split("/").length-1;i++){
			this.directory+=path.split("/")[i]+"/";
		}
		this.name = path.split("/")[path.split("/").length-1];
		this.path = path;
	}
	public void write(String line,boolean overwrite){
		new java.io.File(directory).mkdirs();
		try{
			System.out.println("Attempting to write to file ["+path+"]...");
			writer = new FileWriter(path, overwrite);
			bwriter = new BufferedWriter(writer);
			if (overwrite == false){
				String[] result = read();
				for (int i=0; i<result.length; i++){
					if (result[i] != null){
						bwriter.write(result[i]);
						bwriter.newLine();
					}
				}
			}
			if(line != null){
				writer.flush();
				writer = new FileWriter(path, overwrite);
				bwriter = new BufferedWriter(writer);
				bwriter.write(line);
				bwriter.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed");
		}finally{
	        try {
		        bwriter.close();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void printContents(){
		String[] result = read();
		for (int i=0; i<contents.length; i++){
			System.out.println(contents[i]);
		}
		
	}
	
	public String[] read(){
		try{
			System.out.println("Attempting to read from file ["+path+"]...");
			FileReader reader = new FileReader(path);
			BufferedReader breader = new BufferedReader(reader);
			contents = new String[0];
			String line = "";
			while (line != null) {
				line = breader.readLine();
				//System.out.println(line);
				addContent(line);
	        }
			breader.close();
			reader.close();
			return contents;
		} catch (IOException e) {
			System.out.println("The system failed to read from the file ["+path+"]!");
		}
		return null;
	}
	
	public String read(int line){
		String[] result = read();
		return result[line];
	}
	public void addContent(String str){
		String[] temp_str = contents;
		contents = new String[contents.length+1];
		for (int i=0; i<temp_str.length; i++){
			contents[i]=temp_str[i];
		}
		try{
			str = str.replace("\t", "");
			str = str.replace("\n", "");
		}catch(Exception e){}
		contents[contents.length-1] = str;
	}
}
