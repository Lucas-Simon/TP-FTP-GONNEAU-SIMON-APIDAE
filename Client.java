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


public class Client {

	public static void main(String[] args){
		String ip;
		int port;
		String user = "";
		String password = "";
		String command = "";
		String request;
		BufferedWriter out = null;
		BufferedInputStream bis = null;
		OutputStream output = null;

		Scanner sc = new Scanner(System.in);
		try {

			System.out.println("IP serveur : localhost");
			ip = sc.nextLine();
			System.out.println("Port : 1234");
			port = Integer.parseInt(sc.nextLine());

			Socket sock = new Socket(ip, port);
			BufferedInputStream in = new BufferedInputStream(sock.getInputStream());

			String response = "";
			byte[] b = new byte[1024];
			int read = in.read(b);
			response = new String(b, 0, read);
			System.out.println(response);

			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			do{
				System.out.println("Commande : ");
				command = sc.nextLine();
				command = command.toLowerCase();
				request =  command + "\r\n";
				out.write(request);
				out.flush();
				read = in.read(b);
				try {
					response = new String(b, 0, read);

					if(command.substring(0,5).equals("retr ")) {
						byte[] resbytes = response.getBytes();
						if(!response.contains("File not found"))
							Files.write(Paths.get(command.substring(5, command.length())), response.getBytes());
						System.out.println(response);
					}
					else if(command.substring(0,5).equals("stor ")){
						String tmpSrc = command.substring(5,command.length());

						System.out.println("start transfer");

						byte[] bytearray = new byte[1024];
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(new File(tmpSrc));
							output= sock.getOutputStream();
							bis = new BufferedInputStream(fis);
							int readLength;
							while ((readLength = bis.read(bytearray)) > 0) {
								output.write(bytearray, 0, readLength);
							}

						}
						catch(Exception ex ){

							ex.printStackTrace();
						}
						System.out.println("end transfer");
					}
					else
						System.out.println(response);
				}catch (StringIndexOutOfBoundsException e){}

			}while(!command.equals("quit"));
			sock.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
