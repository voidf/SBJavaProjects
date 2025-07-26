package web;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

interface Handler {
    SocketChannel whenAccept(SelectionKey k) throws IOException;

    String whenRead(SelectionKey k) throws IOException;
}

public class HandlerTemplate implements Handler {
    public int buf = 4096;
    public String charset = "UTF-8";

    public HandlerTemplate() {
    }

    public HandlerTemplate(int buf) {
        this(buf, "UTF-8");
    }

    public HandlerTemplate(String charset) {
        this(4096, charset);
    }

    public HandlerTemplate(int buf, String charset) {
        this.buf = buf;
        this.charset = charset;
    }

    @Override
    public SocketChannel whenAccept(SelectionKey k) throws IOException {
        SocketChannel sc =
                ((ServerSocketChannel) k.channel())
                        .accept();
        sc.configureBlocking(false);
        sc.register(
                k.selector(),
                SelectionKey.OP_READ,
                ByteBuffer.allocate(buf)
        );
        return sc;
    }

    @Override
    public String whenRead(SelectionKey k) throws IOException, SocketException {
        SocketChannel sc = (SocketChannel) k.channel();
        try {

            ByteBuffer bf = (ByteBuffer) k.attachment();
            String recv = "";

            if (sc.read(bf) == -1) {
                System.out.println("EOF，连接中断");
                sc.shutdownInput();
                sc.shutdownOutput();
                sc.close();
            } else {
                bf.flip(); // 读入指针置0
                recv = Charset.forName(charset).newDecoder().decode(bf).toString();
                bf.clear();
//                sc.register(k.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(buf));
            }
            return recv;
        } catch (SocketException e) {
            sc.close();
            throw new SocketException(e.toString());
        }


    }
}
