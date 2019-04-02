package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Alice {

  private final String message;
  private final Integer hash;
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Alice.class);

  public Alice(String message) {
    this.message = message;
    this.hash = message.hashCode();
  }

  public byte[] getBytes() {
      byte[] bytes = message.getBytes();
      ByteBuffer bf = ByteBuffer.allocate(4 + bytes.length);
      bf.putInt(hash);
      bf.put(bytes);

      return bf.array();
  }

  public Alice(byte[] bytes, int offset, int length) {
    ByteBuffer bf = ByteBuffer.wrap(bytes, offset, length);

    int msgHash = bf.getInt();
    byte[] msg = new byte[length - 4];
    bf.get(msg, 0, length - 4);

    String message = new String(msg);
    if (message.hashCode() == msgHash) {
      this.message = message;
      this.hash = msgHash;
    } else {
      logger.error("Message was delievered with losses, incompatible hashes");
      throw new RuntimeException("Packet with losses");
    }
  }

  @Override
  public String toString() {
    return message;
  }
}
