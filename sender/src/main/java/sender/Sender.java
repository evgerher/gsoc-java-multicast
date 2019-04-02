package sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import model.Alice;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Sender, implements thread-based multicast sender object
 * Currently sends randomly generated Alice objects
 *
 */
public class Sender extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Sender.class);

  private final DatagramSocket udpSocket;
  private final InetAddress address;
  private final int port;
  private boolean closed;

  /**
   * Constructor, initalizes udp socket and sets address with port
   * @param address
   * @param port
   * @throws IOException if any
   */
  public Sender(String address, int port) throws IOException {
    udpSocket = new DatagramSocket();
    this.address = InetAddress.getByName(address);
    this.port = port;
    closed = false;
  }

  /**
   * Method sends an Alice object to the predefined address:port
   * @param alice
   */
  private void sendPacket(Alice alice) {
    logger.info("Send packet to {}:{} with content: {}", address.getHostName(), port, alice);
    byte[] bytes = alice.getBytes();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
    packet.setAddress(address);
    packet.setPort(port);
    try {
      udpSocket.send(packet);
    } catch (IOException e) {
      logger.error("Error during message sending: {}", alice);
    }
  }

  /**
   * Main logic for the thread
   * Each 5 seconds generates & sends a random Alice object until it is stopped by some external force
   */
  @Override
  public void run() {
    try {
      while (!closed) {
        Alice alice = Alice.generate();

        if (!udpSocket.isClosed())
          sendPacket(alice);
        Thread.sleep(5000);
      }
    } catch (InterruptedException e) {
      logger.error("Thread interrupted");
    }
  }

  /**
   * Method ends loop and closes udp socket
   */
  public void close() {
    closed = true;
    udpSocket.close();
  }

  /**
   * Example of Sender instance
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    int mcPort = 20000;
    String mcIPStr = "230.1.1.1";

    Sender sender = new Sender(mcIPStr, mcPort);
    sender.start();
    Thread.sleep(20000);
    sender.close();
  }
}
