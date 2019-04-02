package receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import model.Alice;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Receiver, implements thread-based multicast receiver
 * Can be manually configured through another thread through reference to the object (example not provided).
 */
public class Receiver extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

  private final MulticastSocket socket;
  private final Map<String, InetAddress> groups;

  /**
   * Constructor for Receiver object
   * Initializes groups map and socket itself
   * @param mcPort
   * @throws IOException
   */
  public Receiver(int mcPort) throws IOException {
    groups = new HashMap<>();
    socket = new MulticastSocket(mcPort);
    logger.info("Multicast Receiver running at: {}", socket.getLocalSocketAddress());
  }

  /**
   * Method adds a new group to listen through socket
   * Such system is expected to instantiated and controlled from main thread, so it can be manually configured
   * @param mcIP
   */
  public synchronized void addListenGroup(String mcIP) {
    try { // todo: can be added multicast ip checker
      InetAddress address = InetAddress.getByName(mcIP);
      socket.joinGroup(address);
      groups.put(mcIP, address);
      logger.info("Added listen group: {}", mcIP);
    } catch (IOException e) {
      logger.error("Unable to add listen group: {}", mcIP);
    }
  }

  /**
   * Blocking method that receives a packet, transforms into Alice object and logs about it
   */
  private void receive() {
    try {
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      socket.receive(packet);

      Alice alice = new Alice(packet.getData(), packet.getOffset(), packet.getLength());
      logger.info("Received: {}", alice.toString());
    } catch (SocketException e) {
      logger.error("Socket is closed");
    } catch (IOException e) {
      logger.error("Error during packet receiving {}", e);
    }
  }

  @Override
  public void run() {
    while (!socket.isClosed()) {
      logger.trace("Waiting for a  multicast message...");
      receive();
    }
  }

  /**
   * Method removes all listen groups and closes socket
   */
  public synchronized void stopListen() {
    try {
      for (InetAddress address: groups.values())
        socket.leaveGroup(address);
    } catch (IOException e) {
      logger.error("Error during all groups leave");
    }

    socket.close();
    logger.info("Stopped listening and closed socket");
  }

  /**
   * Method remove `group` from listening addresses.
   * Such system is expected to instantiated and controlled from main thread, so it can be manually configured
   * @param group
   */
  public synchronized void leaveListenGroup(String group) {
    try {
      InetAddress address = groups.remove(group);
      socket.leaveGroup(address);
      logger.info("Left {} group", group);
    } catch (Exception e) {
      logger.error("Error during group leave [{}]", group); // IOException and KeyException
    }
  }

  /**
   * Example for receiver instance
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String log4jConfPath = "C:\\cygwin64\\home\\evger\\JavaProjects\\multicast-main\\src\\main\\resources\\logger.properties";
    PropertyConfigurator.configure(log4jConfPath);

    Receiver receiver = new Receiver(20000);
    receiver.addListenGroup("230.1.1.1");
    receiver.start();

    Thread.sleep(15000);
    receiver.stopListen();
  }
}
