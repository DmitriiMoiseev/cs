package ru.dmitry.server.commands;

import io.netty.channel.ChannelHandlerContext;

public interface Command {

    void action(ChannelHandlerContext chc);

}
