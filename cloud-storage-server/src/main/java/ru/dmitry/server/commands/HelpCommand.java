package ru.dmitry.server.commands;

import io.netty.channel.ChannelHandlerContext;

public class HelpCommand implements Command {

    @Override
    public void action(ChannelHandlerContext chc) {
        chc.channel().writeAndFlush("Список команд :\n");
        chc.channel().writeAndFlush("/help - список команд\n");
        chc.channel().writeAndFlush("/ls - список файлов\n");
        chc.channel().writeAndFlush("/cd (name) - перейти в папку\n");
        chc.channel().writeAndFlush("/cd.. - вернуться назад\n");
        chc.channel().writeAndFlush("/touch (name.txt) создать текстовый файл с именем\n");
        chc.channel().writeAndFlush("/mkdir (name) создать директорию\n");
        chc.channel().writeAndFlush("/rm (name) удалить папку или файл по имени\n");
        chc.channel().writeAndFlush("/copy (name.txh, target name.txt) скопировать файл\n");
        chc.channel().writeAndFlush("/cat (name.txt) - вывести в консоль содержимое файла\n");
    }
}
