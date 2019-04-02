package sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import model.Alice;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Sender.class);

  private final DatagramSocket udpSocket;
  private final InetAddress address;
  private final int port;
  private boolean closed;

  public Sender(String address, int port) throws IOException {
    udpSocket = new DatagramSocket();
    this.address = InetAddress.getByName(address);
    this.port = port;
    closed = false;
  }

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

  public void close() {
    closed = true;
    udpSocket.close();
  }

  public static void main(String[] args) throws Exception {
    String log4jConfPath = "C:\\cygwin64\\home\\evger\\JavaProjects\\multicast-main\\src\\main\\resources\\logger.properties";
    PropertyConfigurator.configure(log4jConfPath);

    int mcPort = 20000;
    String mcIPStr = "230.1.1.1";

    Sender sender = new Sender(mcIPStr, mcPort);
    sender.start();
    Thread.sleep(20000);
    sender.close();
  }
}
