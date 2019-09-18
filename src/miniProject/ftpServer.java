package miniProject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ftpServer {

	public static void main(String argv[]) throws Exception {
		String username, password, message;
		byte[] buffer = new byte[1024];
		int bytesInBuffer;

		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {

			Socket connectionSocket = welcomeSocket.accept();

			DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			bytesInBuffer = inFromClient.readInt();
			inFromClient.read(buffer, 0, bytesInBuffer);
			username = new String(buffer, 0, bytesInBuffer);

			bytesInBuffer = inFromClient.readInt();
			inFromClient.read(buffer, 0, bytesInBuffer);
			password = new String(buffer, 0, bytesInBuffer);

			if (username.equals("pshrest2") && password.equals("olemiss")) {

				message = "331 Username OK";
				System.out.print("Client logged in with ");
				outToClient.writeInt(message.length());
				outToClient.write(message.getBytes(), 0, message.length());

				Random rand = new Random();
				String userAuthenticate = rand.nextInt(100) + "MPUA";
				System.out.println("authentication number: "+userAuthenticate);
				System.out.println();
				outToClient.writeInt(userAuthenticate.length());
				outToClient.write(userAuthenticate.getBytes(), 0, userAuthenticate.length());
				String authenticate = userAuthenticate;

				File file = new File("");

				while (authenticate.equals(userAuthenticate)) {

					bytesInBuffer = inFromClient.readInt();
					inFromClient.read(buffer, 0, bytesInBuffer);
					String option = new String(buffer, 0, bytesInBuffer);

					if (option.equals("1")) {
						System.out.println("\nOption 1 has been choosen");
						bytesInBuffer = inFromClient.readInt();
						inFromClient.read(buffer, 0, bytesInBuffer);
						String filename = new String(buffer, 0, bytesInBuffer);
						FileOutputStream fos = null;
						DataOutputStream dos = null;
						try {

							fos = new FileOutputStream(filename);
							dos = new DataOutputStream(fos);

							//bytesInBuffer = inFromClient.readInt();

							while((bytesInBuffer=inFromClient.readInt())>0){

							inFromClient.read(buffer, 0, bytesInBuffer);

							dos.write(buffer, 0, bytesInBuffer);
							}
							
							System.out.println("\nFile "+filename+" has been uploaded");

						} catch (Exception e) {
							System.out.println(e);
						}

						fos.close();
						dos.close();

					} else if (option.equals("2")) {
						System.out.println("\nOption 2 has been choosen");
						FileInputStream fis = null;
						DataInputStream dis = null;

						bytesInBuffer = inFromClient.readInt();
						inFromClient.read(buffer, 0, bytesInBuffer);
						String filename = new String(buffer, 0, bytesInBuffer);

						try {
							fis = new FileInputStream(filename);
							dis = new DataInputStream(fis);
						} catch (Exception e) {
							System.out.println("\nFile not found");
						}
						while((bytesInBuffer=dis.read(buffer))>0) {
											
							outToClient.writeInt(bytesInBuffer);
							outToClient.write(buffer, 0, bytesInBuffer);
						}
						outToClient.writeInt(0);
						
						fis.close();
						dis.close();
						
						System.out.println("\nClient downloaded file: "+filename);

					} else if (option.equals("3")) {
						System.out.println("\nOption 3 has been choosen");
						File newfile= new File(".");
						String currentDir = newfile.getAbsolutePath();

						outToClient.writeInt(currentDir.length());
						outToClient.write(currentDir.getBytes(), 0, currentDir.length());

						String[] fileList = newfile.list();
						for (String f : fileList) {
							outToClient.writeInt(f.length());
							outToClient.write(f.getBytes(), 0, f.length());
						}

						outToClient.writeInt(0);
						System.out.println("\nFiles of the curent directory has been listed");

					} else if (option.equals("4")) {
						
						//NEEDS REVIEW
						//Once the directory is changed and you try to list the 
						//files of the new directory, it won't print the contents 
						//of the new directory but prints the contents of the old one
						
						file=new File("");
						bytesInBuffer=inFromClient.readInt();
						inFromClient.read(buffer,0,bytesInBuffer);
						String dir=new String(buffer,0,bytesInBuffer);
						dir=file.getAbsolutePath()+"\\"+dir;
						System.out.println("\nOld directory's path: "+file.getAbsolutePath());
						System.setProperty("user.dir", dir);
						System.out.println("New directory's path: "+file.getAbsolutePath());
					} 
					
//					else if (option.equals("5")) {
//						userAuthenticate="null";
//						outToClient.writeInt(userAuthenticate.length());
//						outToClient.write(userAuthenticate.getBytes(),0,userAuthenticate.length());
//					}
					
					
					bytesInBuffer=inFromClient.readInt();
					inFromClient.read(buffer,0,bytesInBuffer);
					authenticate=new String(buffer,0,bytesInBuffer);
					
					outToClient.writeInt(authenticate.length());
					outToClient.write(authenticate.getBytes(), 0, authenticate.length());
					
					

				}//closing while loop
				System.out.println("\nClient "+ userAuthenticate+" has logged out...");
			}

			else {
				message = "Invalid username or password";
				outToClient.writeInt(message.length());
				outToClient.write(message.getBytes(), 0, message.length());
				System.out.println("\nInvalid client");
			}
			connectionSocket.close();
		}
	}

}
