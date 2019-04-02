package application;

import java.io.IOException;
import org.apache.log4j.PropertyConfigurator;
import receiver.Receiver;
import sender.Sender;

public class Executor {

  public static void main(String[] args) throws Exception {
    //-Dlog4j.configuration=file:/
    Sender sender = new Sender("221.1.1.1", 20000);
    Receiver receiver1 = new Receiver(20000);
    Receiver receiver2 = new Receiver(20000);

    receiver1.addListenGroup("221.1.1.1");
    receiver2.addListenGroup("221.1.1.1");


//    sender.setDaemon(true);
//    receiver1.setDaemon(true);
//    receiver2.setDaemon(true);

    sender.start();
    receiver1.start();
    receiver2.start();

    Thread.sleep(30);
    sender.close();
    receiver1.stopListen();
    receiver2.stopListen();

  }
}
