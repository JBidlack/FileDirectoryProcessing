
/* John Bidlack
 * CMSC 412 6380
 * 2/19/22
 * 
 */
package fileDirectoryProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;


// class to handle the desired actions while keeping information private
public class Processing {
	private String dir = "";
	private File directory;
	private int count = 0;

// constructor
	public Processing() {
		Menu();
	}
	
// method to display a menu and feed the user input into the method which directs the choice
	public void Menu() {
		int selection;
		do {
		Scanner sel = new Scanner(System.in);
		
		System.out.println("0 - Exit ");
		System.out.println("1 - Select directory");
		System.out.println("2 - List directory content (first level)");
		System.out.println("3 - List directory content (all levels)");
		System.out.println("4 - Delete file");
		System.out.println("5 - Display file (Hexidecimal view)");
		System.out.println("6 - Encrypt file (XOR with Password)");
		System.out.println("7 - Decrypt file (XOR with Password)");
		System.out.println("");
		System.out.println("Please make a selection: ");
		
		selection = sel.nextInt();
		
		Selection(selection);
		} while (selection !=0);
	}

// method to handle the users selection
	public void Selection(int select) {

		switch(select) {
			case 0:
				System.out.println("Thank you for using our system!");
				System.exit(0);
			case 1:
				try {
					selectDir();
					System.out.println("");
				} catch (FileNotFoundException e) {
					e.getMessage();
					System.out.println("No such directory found!");
					System.out.println("");
				}
				break;
			case 2:
				if(chosenDirectory(directory)) {
					firstLvl(directory);
					System.out.println("");
					break;
				} 
				else {
					System.out.println("No directory chosen!");
					break;
				}
										
			case 3:
				if(chosenDirectory(directory)) {
					allLvl(directory);
					System.out.println("");

				}
				break;

			case 4:
				try {
					if(chosenDirectory(directory)) {
						deleteFile(directory);
						System.out.println("");
					}

				} catch (FileNotFoundException e) {
					e.getMessage();
					System.out.println("File not found!");
				}
				break;
			case 5:
				try {
					if(chosenDirectory(directory)) {
						hexadecimal(directory);
						System.out.println("");
					}
				} catch (IOException e) {
					System.out.println("File not found!");
				}
				break;

			case 6:
				
				if(chosenDirectory(directory)) {
					try {
						encrypt(directory);
					} catch (FileNotFoundException e) {
						e.getMessage();
						System.out.println("File not found!");
					}
					System.out.println("");
				}
				break;
			case 7:
				if(chosenDirectory(directory)) {
					try {
						decrypt(directory);
					} catch (FileNotFoundException e) {
						e.getMessage();
						System.out.println("File not found!");
					}
					System.out.println("");
				}
				break;
				
			default:
				System.out.println("Invalid selection");
				break;
		}
	}
	
// method to set the working directory. If the absolute path is not 
// found, a file not found exception is thrown.
	private void selectDir() throws FileNotFoundException {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please enter the absolute path to your directory: ");
		dir = sc.next();
		directory = new File(dir);
		if(!directory.exists()) {
			throw new FileNotFoundException();
		}
		else {
			System.out.println("Directory successfully set.");
		}

	}
	
// method to show the first level directory contents sorted as files first, then subdirectories
	private void firstLvl(File file) {
		File [] files = file.listFiles();
		
		Arrays.sort(files, (a, b) -> Boolean.compare(a.isDirectory(), b.isDirectory()));
		
		for (File f : files) {
			System.out.println(f.getName());
		}
	}
	
// method to display the directory contents, then recursively check the contents of all subdirectories
// Spacing was added to make it clear what level each file was found in
	private void allLvl(File file) {
		File [] files = file.listFiles();

		Arrays.sort(files, (a, b) -> Boolean.compare(a.isDirectory(), b.isDirectory()));
		

		for (File f: files) {
			for (int i = 0; i<count; i++) {
				System.out.print("   ");
			}
			System.out.println(f.getName());
			
			if (f.isDirectory()) {
				count++;
				allLvl(f);
				count = 0;
			}
		}
	}
	
// method to delete chosen files. If no file is found, file not found exception is thrown
	private void deleteFile(File file) throws FileNotFoundException{
		Scanner sc = new Scanner (System.in);
		File [] files = file.listFiles();
		int found = 0;
		
		System.out.println("Please enter the file name you'd like to delete from this directory");
		String fileName = sc.nextLine();
		
		for(File f: files) {
			if(fileName.equals(f.getName())) {
				found++;
				f.delete();
				System.out.println("File deleted successfully");
			}
		}
		if (found == 0) {
			throw new FileNotFoundException();
			
		}
	}
	
//method to convert the bytes of a file to hexidecimal with offset
	public void hexadecimal(File file) throws IOException {
		Scanner sc = new Scanner (System.in);
		File [] files = file.listFiles();
		int found = 0;

		System.out.println("Please enter the file name you'd like to convert:");
		String fileName = sc.next();
		
		for(File f: files) {
			if(fileName.equals(f.getName())) {
				found++;

				//file converted to an array of bytes, then uses a string formatter to convert the bytes 
				//to hexadecimal, then every 16 bytes start a new line with an offset calculated by line
				//and row
				try {
					byte[] fileContent = Files.readAllBytes(f.toPath());
					StringBuilder sb = new StringBuilder();
					
                    for (int i = 0; i < fileContent.length; i++) {
                        if (i == 0) {
                            System.out.println("00000000");
                        }
                        if (i % 16 == 0) {
                            System.out.printf("\n%08X", i);
                        }
                        System.out.printf("%02X ", fileContent[i]);
                    }
				} catch (IOException e) {
					System.out.println("An error occured");
				}
			}
		}
		if (found ==0) {
			throw new FileNotFoundException();
		}
	}
	
//method to encrypt a file byte by byte using XOR
	private void encrypt(File file) throws FileNotFoundException {
		Scanner sc = new Scanner (System.in);
		File [] files = file.listFiles();
		
		System.out.println("Please enter a password (Up to 256 characters)");
		String pw = sc.next();
		System.out.println("");
		System.out.println("Please enter the name of the file you would like to encrypt");
		String fileName = sc.next();
		String filePath;
		int found = 0;
		
		for(File f: files) {
			if(fileName.equals(f.getName())) {
				found++;
				try {
					filePath = f.getPath();
					byte[] fileContent = Files.readAllBytes(f.toPath());
					byte[] newFile = new byte[fileContent.length];
					StringBuilder sb = new StringBuilder();
					int index = 0;
					for(int i=0; i<fileContent.length; i++) {	
						if(index == pw.length()) {
							index = 0;
						}
						newFile[i] = (byte)(fileContent[i]^pw.charAt(index));
						index++;
					}
					FileOutputStream output = new FileOutputStream(filePath);
					output.write(newFile);
					output.close();
					System.out.println("Encryption succesfully executed");
				}
				catch (IOException e) {
					System.out.println("An error occured");
				}
			}
		}
		if (found ==0) {
			throw new FileNotFoundException();
		}
	}
	
	//method to decrypt a file byte by byte using XOR and writing the data to a new file
	private void decrypt(File file) throws FileNotFoundException {
		Scanner sc = new Scanner (System.in);
		File [] files = file.listFiles();
		
		System.out.println("Please enter a password (Up to 256 characters)");
		String pw = sc.next();
		System.out.println("");
		System.out.println("Please enter the name of the file you would like to encrypt");
		String fileName = sc.next();
		String filePath;
		int found = 0;
		
		for(File f: files) {
			if(fileName.equals(f.getName())) {
				found++;
				filePath = f.getPath();
				try {
					byte[] fileContent = Files.readAllBytes(f.toPath());
					byte[] newFile = new byte[fileContent.length];
					StringBuilder sb = new StringBuilder();
					int index = 0;
					for(int i=0; i<fileContent.length; i++) {	
						if(index == pw.length()) {
							index = 0;
						}
						newFile[i] = (byte)(fileContent[i]^pw.charAt(index));
						index++;
					}
					System.out.println("Please enter the destination file name (including extension):");
					String destination = sc.next();
					FileOutputStream output = new FileOutputStream(file +"\\"+ destination);
					output.write(newFile);
					output.close();
					System.out.println("Decrypted file succesfully created");
				}
				catch (IOException e) {
					System.out.println("An error occured");
				}
			}
		}
		if (found == 0) {
			throw new FileNotFoundException();
		}
	}
	
// method to tell if the initial directory has already been chosen
	private boolean chosenDirectory(File file) {
		if (file.exists()) {
			return true;
		}
		else {
			System.out.println("The directory has not been chosen!");
			return false;
		}
	}
}