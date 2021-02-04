package ru.dmitry.server.commands;

import io.netty.channel.ChannelHandlerContext;
import ru.dmitry.server.ServerHandlerGeneral;

import java.io.File;
import java.nio.file.Path;

public class LSCommand implements Command {

    @Override
    public void action(ChannelHandlerContext chc) {

        ServerHandlerGeneral
        chc.channel().writeAndFlush(needPath.toString() + ":\n");
        chc.channel().writeAndFlush(String.join(" ", new File(needPath.toString()).list()) + "\n");

    }
}
