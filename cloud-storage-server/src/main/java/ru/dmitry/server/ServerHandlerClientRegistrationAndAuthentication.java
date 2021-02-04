package ru.dmitry.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ServerHandlerClientRegistrationAndAuthentication extends SimpleChannelInboundHandler<String> {

    private String login;
    private String password;
    static String userName;
    public Path userDirectory;
    private Path serverDirectory = Paths.get("DirectoryServer");
    private static final Map<String, Path> usersBD = new HashMap<String, Path>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился" + ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        channelHandlerContext.channel().writeAndFlush("Введите Login");
        login = s;
        channelHandlerContext.channel().writeAndFlush("Введит Password");
        password = s;
        userName = login + password;
        boolean isRegisteredClient = usersBD.containsKey(userName);
        if (isRegisteredClient = true) {
            channelHandlerContext.fireChannelRead(usersBD.get(userName).toString());
        } else if (isRegisteredClient = false) {
            userDirectory = Paths.get(serverDirectory.toString(), userName);
            usersBD.put(userName, userDirectory);
            channelHandlerContext.fireChannelRead(usersBD.get(userName).toString());
        } else {
            channelHandlerContext.writeAndFlush("неправильно введен логин или пароль")
        }
        channelHandlerContext.fireChannelRead()
    }
}
