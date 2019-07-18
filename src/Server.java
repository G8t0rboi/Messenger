import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class Server extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket client;

    //constructor
    public Server() {

        super("Awesome Messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add( new JScrollPane(chatWindow) );
        setSize(300,150);
        setVisible(true);
    }

    //sets up and runs server
    public void startRunning() {
        try {
            server = new ServerSocket(6789, 100);
            for(;;) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }
                catch (EOFException eofException) {
                    showMessage("\n Server ended the connection! ");
                }
                finally {
                    closeStuff();
                }
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    //wait for connection, then display connection info
    private void waitForConnection() throws IOException {
        showMessage("Waiting for people to connect...");
        client = server.accept();
        showMessage(" Now connected to " +client.getInetAddress().getHostName());
    }

    //get  stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();
        input =  new ObjectInputStream(client.getInputStream());
        showMessage("\n Streams are now setup \n");
    }

    //during the chat conversation
    private void whileChatting() throws IOException {
        String message = "You are now connectred! Start chatting";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (String)input.readObject();
                showMessage("\n" + message);
            }
            catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n idk");
            }
        }while(!message.equals("CLIENT - END"));
    }

    //closes connections
    private void closeStuff() {
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //send  message to client
    private void sendMessage(String message) {
        try {
            output.writeObject("SERVER - " +message);
            output.flush();
            showMessage("\nServer - " + message);
        } catch (IOException ioException ) {
            chatWindow.append("\n ERROR: CANT SEND");
        }
    }

    //updates chatWindow
    private void showMessage(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.append(text);
            }
        });
    }

    //let the user type into userText
    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userText.setEditable(tof);
            }
        });
    }
}
