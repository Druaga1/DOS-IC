import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
	File file = null;
	public FileManager(){
		
	}
	public void openFile(String path){
		file = new File(path);
	}
	public void closeFile(){
		file = null;
	}
	public String[] readFile(String path){
		file = new File(path);
		return file.read();
	}
	public String[] readFile(){
		return file.read();
	}
	public void writeFile(String path,String[] data,boolean overwrite){
		file = new File(path);
		for (int i=0; i<data.length;i++){
			String line = data[i];
			file.write(line, overwrite);
		}
	}
	
	public void writeFile(String[] data){
		for (int i=0; i<data.length;i++){
			String line = data[i];
			file.write(line, false);
		}
	}
	
	public void writeFile(String data){
		file.write(data, true);
	}

	public void writeFile(String dir, String data,boolean overwrite){
		file.write(data, !overwrite);
	}
	public void writeFile(String dir, String data){
		openFile(dir);
		file.write(data,true);
	}
	
	public void writeFile(String data,boolean overwrite){
		file.write(data, !overwrite);
	}
	public void writeFile(String[] data,boolean overwrite){
		for (int i=0; i<data.length;i++){
			String line = data[i];
			file.write(line, !overwrite);
		}
	}
	
	public void writeLine(String data){
		file.write(data, false);
	}
	public void testDir(String dir){
		this.writeFile(dir+"/temp", "TESTDIR",true);
	}
	
	public java.io.File[] listDirectory(String dir){
		try {
			System.out.println("Listing dir : "+dir);
			java.io.File folder = new java.io.File(dir);
			java.io.File[] listOfFiles = folder.listFiles();
			String[] files = new String[listOfFiles.length];
			for(int i=0; i<listOfFiles.length; i++){
				files[i] = listOfFiles[i].getName();
				System.out.println(files[i]);
			}
			return listOfFiles;
		} catch (Exception e) {
			System.out.println("failed");
			e.printStackTrace();
			return null;
		}
	}
	public String[] getFilesInDirectory(String dir){
		try {
			System.out.println("Listing dir : "+dir);
			java.io.File folder = new java.io.File(dir);
			java.io.File[] listOfFiles = folder.listFiles();
			String[] files = new String[listOfFiles.length];
			for(int i=0; i<listOfFiles.length; i++){
				files[i] = listOfFiles[i].getName();
				System.out.println(files[i]);
			}
			return files;
		} catch (Exception e) {
			System.out.println("failed");
			e.printStackTrace();
			return null;
		}
	}
	public void treeDirectory(String dir){
		try {
			
			for(Object file:Files.walk(Paths.get(dir)).filter(Files::isRegularFile).toArray()){
				System.out.println(file);
			}
		} catch (IOException e) {
		}
	}
	
	
}
