package miniProject;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ftpClient {

	public static void main(String argv[]) throws Exception {
		String username, password;
		byte[] buffer = new byte[1024];
		int bytesInBuffer;

		Socket clientSocket = new Socket("localhost", 6789);

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Username: ");
		username = inFromUser.readLine();
		System.out.print("Password: ");
		password = inFromUser.readLine();

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeInt(username.length());
		outToServer.write(username.getBytes(), 0, username.length());

		outToServer.writeInt(password.length());
		outToServer.write(password.getBytes(), 0, password.length());

		DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

		bytesInBuffer = inFromServer.readInt();
		inFromServer.read(buffer, 0, bytesInBuffer);
		String message = new String(buffer, 0, bytesInBuffer);
		if(message.equals("331 Username OK")) {
			
			try {
				File clientFile=null;
				System.out.println("The default client's directory's path for this program set to is C:\\Users\\prana");
				System.out.print("Is this OK for you (Y/N)? ");
				String ans=inFromUser.readLine();
				
				if(ans.equalsIgnoreCase("Y")) {
					clientFile=new File("C:\\Users\\prana");
				}
				else {
					System.out.println("Enter a valid client's directory's path as below or else you will get an error later");
					System.out.println("Example: C:\\Users\\prana\\Documents");
					String file=inFromUser.readLine();
					clientFile=new File(file);
				}
				
				bytesInBuffer = inFromServer.readInt();
				int userAuthenticator = inFromServer.read(buffer, 0, bytesInBuffer);
				String authenticate = new String(buffer, 0, userAuthenticator);

				while (!authenticate.equals("null")) {
					System.out.println("\nChoose one of the following: (Enter the number corresponding to your option)");
					System.out.println("\n1. Upload");
					System.out.println("2. Download");
					System.out.println("3. List of files in the current directory");
					System.out.println("4. Change Directory");
					System.out.println("5. Logout");

					String option = inFromUser.readLine();
					outToServer.writeInt(option.length());
					outToServer.write(option.getBytes(), 0, option.length());

					if (option.equals("1")) {
						System.out.print("Enter the filename you want to upload");
						String filename = inFromUser.readLine();
						FileInputStream fis = null;
						DataInputStream dis = null;

						outToServer.writeInt(filename.length());
						outToServer.write(filename.getBytes(), 0, filename.length());

						try {
							fis = new FileInputStream(clientFile+"\\"+ filename);
							dis = new DataInputStream(fis);
						} catch (Exception e) {
							System.out.println("File not found");
						}

						while((bytesInBuffer=dis.read(buffer))>0) {
						outToServer.writeInt(bytesInBuffer);
						outToServer.write(buffer, 0, bytesInBuffer);
						}
						outToServer.writeInt(0); // Indicates the end of line
						fis.close();
						dis.close();

						System.out.println("Upload successful....");
					}

					else if (option.equals("2")) {
						System.out.print("Enter the filename you want to download");
						String filename = inFromUser.readLine();
						FileOutputStream fos = null;
						DataOutputStream dos = null;

						outToServer.writeInt(filename.length());
						outToServer.write(filename.getBytes(), 0, filename.length());

						try {

							fos = new FileOutputStream(clientFile+"\\"+ filename);
							dos = new DataOutputStream(fos);

						} catch (Exception e) {
							System.out.println(e);
						}
						while((bytesInBuffer=inFromServer.readInt())>0){
							
							inFromServer.read(buffer, 0, bytesInBuffer);
							dos.write(buffer, 0, bytesInBuffer);
						}
						
						

						fos.close();
						dos.close();

						System.out.println("Download Successful...");

					} else if (option.equals("3")) {
						bytesInBuffer = inFromServer.readInt();
						inFromServer.read(buffer, 0, bytesInBuffer);
						String currentDir = new String(buffer, 0, bytesInBuffer);
						System.out.println("Current Directory of server is: " + currentDir);
						System.out.println("The files in directory are: ");

						bytesInBuffer = inFromServer.readInt();
						while (bytesInBuffer > 0) {

							inFromServer.read(buffer, 0, bytesInBuffer);
							String fileList = new String(buffer, 0, bytesInBuffer);
							System.out.println("\t"+fileList);
							bytesInBuffer = inFromServer.readInt();
						}

					} else if (option.equals("4")) {
						System.out.print("Enter the new directory path: ");
						String newDir = inFromUser.readLine();
						outToServer.writeInt(newDir.length());
						outToServer.write(newDir.getBytes(), 0, newDir.length());
					} else if (option.equals("5")) {
						authenticate = "null";
						outToServer.writeInt(authenticate.length());
						outToServer.write(authenticate.getBytes(), 0, authenticate.length());

						System.out.println("Logged out successfully...");
					}

					outToServer.writeInt(authenticate.length());
					outToServer.write(authenticate.getBytes(), 0, authenticate.length());

					bytesInBuffer = inFromServer.readInt();
					inFromServer.read(buffer, 0, bytesInBuffer);
					authenticate = new String(buffer, 0, bytesInBuffer);

				} // closing while loop

			} catch (Exception e) {
				System.out.println(e);
			}
		}
		else {
			System.out.println(message);
		}
		

		clientSocket.close();
	}
}