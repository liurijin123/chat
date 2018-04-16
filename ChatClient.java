import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame{

    Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    private boolean bConnected = false;
    
	TextField tfTxt = new TextField();
    TextArea taContent = new TextArea();
    
	public static void main(String[] args) {
		new ChatClient().launchFrame();

	}
	//窗口
	public void launchFrame(){
		setLocation(400, 300);
		this.setSize(600, 600);
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter()  {

			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
			
		});
		tfTxt.addActionListener(new TFlistener());
		setVisible(true);
		connect();
		
		new Thread(new RecvThread()).start();
	}
	
	//连接
	public void connect(){
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println("connect");
			bConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    //断开连接
	public void disconnect(){
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//窗口监听
	private class TFlistener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			String str = tfTxt.getText().trim();
			tfTxt.setText("");
			try {
				dos.writeUTF(str);
				dos.flush();
				//dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	//接收消息线程
	private class RecvThread implements Runnable{

		@Override
		public void run() {			
				try {
					while(bConnected){
						String str = dis.readUTF();
						//System.out.println(str);
						taContent.setText(taContent.getText() + str + '\n');
					}
				} catch (SocketException e){
					System.out.println("退出了");
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		
	}
}
