package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Alice {

  private final String message;
  private final Integer hash;
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Alice.class);

  public Alice(String message) {
    this.message = message;
    this.hash = message.hashCode();
  }

  public byte[] getBytes() {
    // todo: IOException
    try (ByteArrayOutputStream bis = new ByteArrayOutputStream()) {
      bis.write(message.getBytes());
      bis.write(hash);
      bis.flush(); // todo: yes?

      return bis.toByteArray();
    } catch (IOException e) {
      logger.error("Unable to convert object into byte array {}", e);
      return new byte[0];
    }
  }

  public Alice(byte[] bytes) {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

    byte[] msg = new byte[bis.available() - 4];

    bis.read(msg, 0, bis.available() - 4);
    int msgHash = bis.read();

    String message = new String(msg);
    if (message.hashCode() == msgHash) {
      this.message = message;
      this.hash = msgHash;
    } else {
      logger.error("Message was delievered with losses, incompatible hashes");
      throw new RuntimeException("Packet with losses");
    }
  }
}
