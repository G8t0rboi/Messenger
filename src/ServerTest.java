import javax.swing.JFrame;

public class ServerTest {

    public static void main(String[] args) {
        Server boi = new Server();
        boi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boi.startRunning();
    }


}
