package web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;



public class Client {
    public String name = "Uninitialized";
    private String LNK = "localhost";
    private int PORT = 14514;
    private String charset = "UTF-8";
    private ByteBuffer sendbuffer = ByteBuffer.allocate(4096);
    private ByteBuffer readbuffer = ByteBuffer.allocate(4096);
    public SocketChannel sc;
    private ArrayList<RecvCallback> broadcast;
    public volatile byte shouldIClose = 0;

    public static void main(String[] args) {
        var cli = new Client();
        cli.init();
        cli.setRecvCallback((String recv) -> {
            System.out.println("Callback:" + recv);
        });


        new Thread(
                () -> {
                    cli.loop();
                }
        ).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MainThreadName:" + cli.name);
        cli.send("Problem A");
        cli.send("Problem B");
        cli.send("Problem C");
        try {
            Thread.sleep(1145141919810L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cli.stop();
        }
    }

    public void configure(String lnk, int port, String c) {
        LNK = lnk;
        PORT = port;
        charset = c;
    }

    public void setRecvCallback(RecvCallback target) {
        broadcast.add(target);
    }

    public void send(String s) {

        try {
            sendbuffer.clear();
            sendbuffer.put(("[" + name + "]" + s + "\r\n").getBytes(charset));
            sendbuffer.flip();
            sc.write(sendbuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rename(String newName) {
        send(newName);
        name = newName;
    }

    public void stop() {
        try {
            shouldIClose = 1;
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            broadcast = new ArrayList<>();
            sc = SocketChannel.open();
            SocketAddress address = new InetSocketAddress(LNK, PORT);
            sc.connect(address);
            sc.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void loop() {
        shouldIClose = 0;

        try {
            var sl = Selector.open();

            sc.register(sl, SelectionKey.OP_READ, ByteBuffer.allocate(4096));
            System.out.println("Client hajime at " + WebUtils.getFriendlyDatetime());
            var handler = new HandlerTemplate(4096);
            while (shouldIClose == 0) {
                sl.select();
                for (var ki = sl.selectedKeys().iterator(); ki.hasNext(); ki.remove()) {
                    var k = ki.next();
                    try {
                        if (k.isReadable()) {
                            var raw = handler.whenRead(k);

                            for (var recv : raw.split("\r\n")) {
                                System.out.println(recv);
                                // TODO: 反向路由
                                if (recv.startsWith("/set-name:")) {
                                    var sp = recv.split(":");
                                    System.out.println("SP:=>");
                                    for (var cur : sp)
                                        System.out.println("\t" + cur);
                                    name = sp[1];
                                } else {

                                }

                                for (var j : broadcast) { // 执行回调
                                    j.invoke(recv);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("有异常，可能是服务器崩了（");
                        e.printStackTrace();
                        shouldIClose = 1;
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
