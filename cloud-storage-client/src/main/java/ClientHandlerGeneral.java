import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandlerGeneral extends SimpleChannelInboundHandler<String> {
    ReadMessage readMessage;

    public ClientHandlerGeneral(ReadMessage readMessage) {
        this.readMessage = readMessage;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        if (readMessage != null) {
            readMessage.readMessage(msg);
        }
    }
}
