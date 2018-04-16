import java.io.*;
import java.net.*;
import java.util.*;
public class ChatServer {
	
	ServerSocket ss = null;
	boolean started = false;
	List<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) {
		System.out.println("starting");
		new ChatServer().start();
		
	}
	
	//启动服务器
	public void start(){
		try {
			ss = new ServerSocket(8888);
			started = true;
		}catch (IOException e){
			e.printStackTrace();
		}
		try {

			while(started){
				Socket s = ss.accept();
				Client c = new Client(s);
System.out.println("a client connected!");
                new Thread(c).start();
                clients.add(c);
                //dis.close();
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } 
	}

	//接收客户端连接线程
	class Client implements Runnable{

		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnect = false;

		//接收和发送客户端消息
		public Client(Socket s){
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnect = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//发送客户端消息
		public void send(String str){
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("对方退出了，我从list里面去掉了");
			}
		}

		//接收客户端消息线程
		public void run() {
			try{
             while(bConnect){
	                String str = dis.readUTF();
System.out.println(str);               
                    for(int i=0; i<clients.size(); i++){
                    	Client c = clients.get(i);
                    	c.send(str);
                    }
             }
			}catch (EOFException e) {
				System.out.println("client close");
			}catch (IOException e){
				e.printStackTrace();
			} finally{
					try{
						if(dis != null) dis.close();
						if(dos != null) dos.close();
						if(s != null) {
							s.close();
							s = null;
						}
					}catch (IOException e){
						e.printStackTrace();
					}
			}
		}
		
	}
}
