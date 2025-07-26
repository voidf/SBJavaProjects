package web;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.regex.Pattern;

public class Server {
    private volatile byte shouldClose = 0; // 别让它加锁
    private int port = 14514;
    private LinkedHashMap<String, SocketChannel> sessionPool = new LinkedHashMap<>();

    private ArrayList<SocketChannel> onlines = new ArrayList<>();

    private ByteBuffer writebuf = ByteBuffer.allocate(4096);

    public static void main(String[] args) {
        var svr = new Server();
        svr.init();
    }



    public void init() {
        shouldClose = 0;
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) { // 自动关闭Channel
            ssc.socket().bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);

            Selector sl = Selector.open();
            ssc.register(sl, SelectionKey.OP_ACCEPT);

            System.out.println("Server hajime at " + WebUtils.getFriendlyDatetime());
            var handler = new HandlerTemplate(4096);

            while (shouldClose == 0) {
                sl.select();
                for (var ki = sl.selectedKeys().iterator(); ki.hasNext(); ki.remove()) {
                    var k = ki.next();
                    try {
                        if (k.isAcceptable()) {
                            var c = handler.whenAccept(k);
                            onlines.add(c);
                            System.out.println("有新连接连入");
                            System.out.println(c);

                            var na = getRandomString(8);
                            System.out.println(na);
                            injectMsg(c, "/set-name:" + na);
//                            broadcastMsg(na + "已连入服务器");
                            sessionPool.put(na, c);

                            if (onlines.size()>=2){
                                broadcastMsg("/ready");
                            }

                            System.out.println(sessionPool);
                        } else if (k.isReadable()) {
                            // TODO: 做路由
                            var c = (SocketChannel) k.channel();
                            var raw = handler.whenRead(k); // 可能会并发陷阱，读入多行
                            for (var recv : raw.split("\r\n")) {
                                System.out.println(recv);
                                var mat = Pattern.compile("^\\[(.*?)\\](.*?)$").matcher(recv);
                                if (mat.find()) { // 考虑扔掉不符要求的报文
                                    var clientName = mat.group(1);
                                    var realmsg = mat.group(2);
                                    System.out.println("Parsed ===> \n\tname:" + clientName + "\n\trealmsg:" + realmsg);

                                    if (realmsg.startsWith("#rename:")) {
                                        var newName = recv.split(":")[1];
                                        sessionPool.remove(clientName);
                                        sessionPool.put(newName, c);
                                    }
                                    else if (realmsg.startsWith("#life ")) {
                                        var life = recv.split(" ")[1];
                                        broadcastMsg(String.format("/healthchange %s %s", clientName, life));
                                    }
                                } else {
                                    System.out.println("不对劲的报文:" + raw);

                                    onlines.remove(c);

                                    for (var kk = sessionPool.entrySet().iterator();
                                         kk.hasNext(); ) {
                                        var tmp = kk.next();
                                        if (tmp.getValue() == c) {
                                            sessionPool.remove(tmp.getKey());
                                            System.out.println("Removed " + tmp.getKey());
                                            broadcastMsg("/leave "+tmp.getKey());
                                            break;
                                        }
                                    }

                                    System.out.println("在线列表" + sessionPool.keySet().toString());
                                }
                            }
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                        System.out.println("客户机离线");

                        var c = (SocketChannel) k.channel();
                        onlines.remove(c);

                        for (var kk = sessionPool.entrySet().iterator();
                             kk.hasNext(); ) {
                            var tmp = kk.next();
                            if (tmp.getValue() == c) {
                                sessionPool.remove(tmp.getKey());
                                System.out.println("Removed " + tmp.getKey());
                                broadcastMsg("/leave "+tmp.getKey());
                                break;
                            }
                        }

                        System.out.println("在线列表" + sessionPool.keySet().toString());
//                        ki.remove();
                    }
                }
//                System.out.println("Handled event");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String msg) {
        msg += "\r\n";
        writebuf.clear();
        try {
            writebuf.put(msg.getBytes("UTF-8"));

            for (var i : onlines) {
                writebuf.flip();
                i.write(writebuf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectMsg(SocketChannel s, String msg) {
        msg += "\r\n";
        writebuf.clear();
        try {
            writebuf.put(msg.getBytes("UTF-8"));
            writebuf.flip();
            s.write(writebuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomString(int length) {
        var strset = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789+=";
        var rd = new Random();
        var sb = new StringBuffer();
        for (var i = 0; i < length; i++) {
            sb.append(strset.charAt(rd.nextInt(strset.length())));
        }
        return sb.toString();
    }


    public void stop() {
        shouldClose = 1;
    }
}
