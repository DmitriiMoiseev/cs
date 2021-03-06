package ru.dmitry.client.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.dmitry.client.ui.ReadMessage;

public class ClientHandlerGeneral extends SimpleChannelInboundHandler<String> {
    ReadMessage readMessage;

    public ClientHandlerGeneral(ReadMessage readMessage) {
        this.readMessage = readMessage;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (readMessage != null) {
            readMessage.readMessage(s);
        }
    }
}
