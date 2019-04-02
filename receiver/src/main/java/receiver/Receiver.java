package receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import model.Alice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

  private final MulticastSocket socket;
  private final Map<String, InetAddress> groups;

  public Receiver(int mcPort) throws IOException {
//    int mcPort = 12345;

    groups = new HashMap<>();
    socket = new MulticastSocket(mcPort);

    logger.info("Multicast Receiver running at: {}", socket.getLocalSocketAddress());
  }

  public synchronized void addListenGroup(String mcIP) {
//    String mcIPStr = "230.1.1.1";
    try { // todo: can be added multicast ip checker
      InetAddress address = InetAddress.getByName(mcIP);
      socket.joinGroup(address);
      groups.put(mcIP, address);
      logger.info("Added listen group: {}", mcIP);
    } catch (IOException e) {
      logger.error("Unable to add listen group: {}", mcIP);
    }
  }

  private void receive() {
    try {
      DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
      socket.receive(packet);

      Alice alice = new Alice(packet.getData(), packet.getOffset(), packet.getLength());
      logger.info("Received: {}", alice.toString());
    } catch (IOException e) {
      logger.error("Error during packet receiving {}", e);
    }
  }

  @Override
  public void run() {
    while (!socket.isClosed()) {
      logger.debug("Waiting for a  multicast message...");
      receive();
    }
  }

  public synchronized void stopListen() {
    try {
      for (InetAddress address: groups.values())
        socket.leaveGroup(address);
    } catch (IOException e) {
      logger.error("Error during all groups leave");
    }

    socket.close();
  }

  public synchronized void leaveListenGroup(String mcIP) {
    try {
      InetAddress address = groups.remove(mcIP);
      socket.leaveGroup(address);
    } catch (Exception e) {
      logger.error("Error during group leave [{}]", mcIP); // IOException and KeyException
    }
  }
}
