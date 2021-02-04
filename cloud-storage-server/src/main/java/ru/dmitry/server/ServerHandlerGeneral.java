package ru.dmitry.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.dmitry.server.commands.Command;
import ru.dmitry.server.commands.CommandParser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ServerHandlerGeneral extends SimpleChannelInboundHandler<String> {

    private static final List<Channel> channels = new ArrayList<>();
    private int newClientIndex = 1;
    private String clientName;
    private Path clientMainDirInServer = null;
    private Path currentPath = null;
    private String commandDetails = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился" + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент №" + newClientIndex;
        newClientIndex ++;
        Path directoryServer = Path.of("DirectoryServer");
        if (Files.notExists(directoryServer)) {
            Files.createDirectory(directoryServer);
        }
        clientMainDirInServer = directoryServer.resolve(clientName);
        currentPath = clientMainDirInServer;
        ctx.channel().writeAndFlush("Введите '/help' для просмотра списка команд\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Получено сообщение от  " + clientName + " : " + s);

        Command command = CommandParser.parse(s);
        command.action(channelHandlerContext);

        if (s.startsWith("/help")) {

            // TODO: 11.01.2021 можно добавить команду clear
        } else if (s.startsWith("/ls")) {
            showResult(channelHandlerContext, currentPath);
        } else if (s.startsWith("/cd")) {
            Path requestedPathOrFile = getCommandDetails(s);
            showResult(channelHandlerContext, requestedPathOrFile);
            currentPath = requestedPathOrFile;
            // TODO: 11.01.2021  добавить проверку запрашиваемого пути
        } else if (s.startsWith("/cd..")) {

            // TODO: 13.01.2021 не работает

            String[] strings = currentPath.toString().split("/");
            currentPath = clientMainDirInServer;
            for (int i=0; i < strings.length-1; i++) {
                currentPath = Paths.get(currentPath.toString(), strings[i]);
                currentPath = currentPath;
            }
//            showResult(channelHandlerContext, currentPath);
        }else if (s.startsWith("/touch")) {
            Path requestedPathOrFile = getCommandDetails(s);
            Files.createFile(requestedPathOrFile);
            showResult(channelHandlerContext, currentPath);
        } else if (s.startsWith("/mkdir")) {
            Path requestedPathOrFile = getCommandDetails(s);
            Files.createDirectory(requestedPathOrFile);
            showResult(channelHandlerContext, currentPath);
        } else if (s.startsWith("/rm")) {
            Path requestedPathOrFile = getCommandDetails(s);
            Files.delete(requestedPathOrFile);
            showResult(channelHandlerContext, currentPath);
        } else if (s.startsWith("/copy")) {
            String[] strings = s.split(" ", 3);
            commandDetails = strings[1];
            String targetFolder = strings[2];
            Path fileFrom = Paths.get(currentPath.toString(), commandDetails);
            Path fileTo = Paths.get(currentPath.toString(),targetFolder);
            Files.copy(fileFrom, fileTo, StandardCopyOption.REPLACE_EXISTING);
            showResult(channelHandlerContext, currentPath);
        } else if (s.startsWith("/cat")) {
            Path requestedPathOrFile = getCommandDetails(s);
            List<String> fileString = Files.readAllLines(requestedPathOrFile, StandardCharsets.UTF_8);
                channelHandlerContext.channel().writeAndFlush("ReadFile:\n");
            for (String str: fileString) {
                channelHandlerContext.channel().writeAndFlush(str + "\n");
            }
            // TODO: 12.01.2021 Добавить флаг /fileRead/ чтобы покузывать содержимое файла в новом окне
        } else {
            channelHandlerContext.channel().writeAndFlush("некорректная команда\n");
        }
    }

    public void showResult(ChannelHandlerContext chc, Path needPath) {
        chc.channel().writeAndFlush(needPath.toString() + ":\n");
        chc.channel().writeAndFlush(String.join(" ", new File(needPath.toString()).list()) + "\n");
    }

    public Path getCommandDetails(String messageFromClient) {
        String[] strings = messageFromClient.split(" ", 2);
        commandDetails = strings[1];
        Path path = Paths.get(currentPath.toString(), commandDetails);
        return path;
    }

    public Path getCurrentPath() {
        return currentPath;
    }
}
