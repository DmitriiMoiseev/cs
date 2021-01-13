import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientNetty {
    private SocketChannel channel;

    private static final String HOST = "localhost";
    private static final int PORT = 8787;

    public ClientNetty(ReadMessage readMessage) {
        Thread t = new Thread() {
            @Override
            public void run() {
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    channel = ch;
                                    ch.pipeline()
                                            .addLast(new StringDecoder())
                                            .addLast(new StringEncoder())
                                            .addLast(new ClientHandlerGeneral(readMessage));
                                }
                            });
                    ChannelFuture f = b.connect(HOST, PORT).sync();
                    f.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }

}
