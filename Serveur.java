
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.net.ServerSocket;

class Serveur{

	public static void main (String args[]) throws Exception{
		ServerSocket sockserv=null;
		DataInputStream in ;
		DataOutputStream out;
		byte mess[];
		sockserv = new ServerSocket (1234);
		OutputStream output= null;
		BufferedInputStream bis = null;
		String response = "";
		int read;
		String saResponse = "";
		try {
			while (true){
				try {
					//Connection à la socket
					Socket sockcli = sockserv.accept();
					in = new DataInputStream (sockcli.getInputStream());
					out = new DataOutputStream (sockcli.getOutputStream());
					mess = new byte[80];
					out.write(mess);

					System.out.println("Connected");
					do{

						read = in.read(mess,0,80);
						if(read != -1){
							response = new String(mess, 0, read);
							saResponse = response;
						}
						//On vérifie la commande
						if(saResponse.substring(0,5).equals("retr ")){
							System.out.println("File "+response.substring(5,response.length()-2)+" wanted");

							String tmpSrc = "ftp/"+response.substring(5,response.length()-2);
							System.out.println("start transfer");

							byte[] bytearray = new byte[1024];
							FileInputStream fis = null;
							try {
								output= sockcli.getOutputStream();
								fis = new FileInputStream(new File(tmpSrc));
								bis = new BufferedInputStream(fis);

								int readLength = -1;
								while ((readLength = bis.read(bytearray)) > 0) {
									output.write(bytearray, 0, readLength);
								}

							}
							catch(FileNotFoundException ex ){
								out.write(("File not found").getBytes());
								out.flush();
							}
							catch(Exception ex ){
								ex.printStackTrace();
							}
							System.out.println("end transfer");
						}
						else if(saResponse.substring(0,5).equals("stor ")){
							byte[] resbytes = response.getBytes();
							Files.write(Paths.get("ftp/" + saResponse.substring(5, saResponse.length()-2)), response.getBytes());
							output= sockcli.getOutputStream();
							output.write(("File transfered").getBytes());

						}
						else if(saResponse.substring(0,5).equals("dele ")){
							System.out.println("Delete file "+response.substring(5,response.length()-2));
							try{
								File file = new File("./ftp/" + response.substring(5,response.length()-2));
								if(file.delete()){
									out.write((file.getName() + " deleted").getBytes());
								}else{
									out.write("File cannot be deleted".getBytes());
								}
							}catch(Exception e){e.printStackTrace();}
						}
						else if(saResponse.substring(0,4).equals("quit")){
							System.out.println("Le client est deconnecté.");
							break;
						}
						else{
							out.write("Commande inconnue".getBytes());
						}

					} while(!saResponse.contains("quit"));
					sockcli.close();
				}catch (IOException ex) {}
			}
		}finally {
			try {
				sockserv.close();
			} catch (IOException ex) {}
		}
	}

}
