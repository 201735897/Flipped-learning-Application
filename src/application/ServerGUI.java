package application;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	// the stop and start buttons
	private JButton stopStart;
	// the notice add button
	private JButton noticeAdd;
	// JTextArea for the chat room and the events
	private JTextArea chat, event, notice;
	// The port number
	private JTextField tPortNumber;
	private JTextField noticeContent;
	// my server
	private Server server;
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGUI(int port) {
		super("Chat Server");
		server = null;
		// in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();
		JPanel south = new JPanel();
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("" + port,5);
		north.add(tPortNumber);
		south.add(new JLabel("Notice: "));
		
		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		
		noticeAdd = new JButton("Add");
		noticeAdd.addActionListener(this);
		noticeAdd.setEnabled(false);
		
		noticeContent = new JTextField("",22);
		noticeContent.addActionListener(this);
		noticeContent.setEditable(false);
		
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		south.add(noticeContent);
		south.add(noticeAdd);
		//south.add(fileDelete);
		add(south, BorderLayout.SOUTH);
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(3,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		notice = new JTextArea(80, 80);
		notice.setEditable(false);
		appendFile("Notice.\n");
		center.add(new JScrollPane(notice));
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(500, 600);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(event.getText().length() - 1);
	}
	void appendFile(String str) {
		notice.append(str);
		notice.setCaretPosition(notice.getText().length() - 1);
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		Object o = e.getSource();
		if(o==stopStart)
		{
			if(server != null) {
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			noticeContent.setEditable(false);
			noticeAdd.setEnabled(false);
			return;
			}
	      	// OK start the server	
			int port;
			try {
				port = Integer.parseInt(tPortNumber.getText().trim());
			}
			catch(Exception er) {
				appendEvent("Invalid port number");
				return;
			}
			noticeContent.setEditable(true);
			noticeAdd.setEnabled(true);
			// create a new Server
			server = new Server(port, this);
			// and start it as a thread
			new ServerRunning().start();
			stopStart.setText("Stop");
			tPortNumber.setEditable(false);
		}
		if(o==noticeAdd)
		{
			server.getNl().add(noticeContent.getText());
			appendFile(noticeContent.getText()+"\n");
		}
	}
	
	// entry point to start the Server
	public static void main(String[] arg) {
		// start server default port 1500
		new ServerGUI(1500);
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {
		public void run() {
			server.start();         // should execute until if fails
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
	}
}
