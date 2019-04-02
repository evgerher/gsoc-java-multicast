package model;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Class Alice, represents class instance of random length with a string and its hash
 */
public class Alice {

  private final String message;
  private final Integer hash;
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Alice.class);
  private static final Random random = new Random();

  public Alice(String message) {
    this.message = message;
    this.hash = message.hashCode();
  }

  /**
   * Method converts Alice instance into byte array
   * @return
   */
  public byte[] getBytes() {
      byte[] bytes = message.getBytes();
      ByteBuffer bf = ByteBuffer.allocate(4 + bytes.length);
      bf.putInt(hash);
      bf.put(bytes);

      return bf.array();
  }

  /**
   * Alice constructor from udp datagram
   * @param bytes
   * @param offset
   * @param length
   */
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

  /**
   * Generate random Alice object (string of length [5;20])
   * @return
   */
  public static Alice generate() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = random.nextInt(15) + 5; // [5, 20]
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + (int)
          (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    String generatedString = buffer.toString();

    return new Alice(generatedString);
  }
}
