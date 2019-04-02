package receiver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver {

  public Receiver() {

  }

  public static void main(String[] args) throws Exception {
    int mcPort = 12345;
    String mcIPStr = "230.1.1.1";
    MulticastSocket mcSocket = new MulticastSocket(mcPort);
    InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);

    System.out.println("Multicast Receiver running at:" + mcSocket.getLocalSocketAddress());
    mcSocket.joinGroup(mcIPAddress);

    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

    System.out.println("Waiting for a  multicast message...");
    mcSocket.receive(packet);
    String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
    System.out.println("[Multicast  Receiver] Received:" + msg);

    mcSocket.leaveGroup(mcIPAddress);
    mcSocket.close();
  }
}
