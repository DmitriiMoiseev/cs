import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
//    private Path clientMainDirInClient = null;
    private Path courrentPath = null;
    private String lastDirOrFile = null;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился" + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент №" + newClientIndex;
        newClientIndex ++;
        clientMainDirInServer = Files.createDirectory(Paths.get("DirectoryServer","/", clientName).toAbsolutePath());
//        clientMainDirInClient = Files.createDirectory(Paths.get("/Users/DmitryMoiseev/cloud/CloudStorage/DirectoryClient","/", clientName));
        courrentPath = clientMainDirInServer;
        ctx.channel().writeAndFlush("Введите '/help' для просмотра списка команд\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Получено сообщение от  " + clientName + " : " + msg);
        if (msg.startsWith("/help")) {
            ctx.channel().writeAndFlush("Ссписок команд :\n");
            ctx.channel().writeAndFlush("/help - список команд\n");
            ctx.channel().writeAndFlush("/ls - список файлов\n");
            ctx.channel().writeAndFlush("/cd (name) - перейти в папку\n");
            ctx.channel().writeAndFlush("/cd.. - вернуться назад\n");
            ctx.channel().writeAndFlush("/touch (name) создать текстовый файл с именем\n");
            ctx.channel().writeAndFlush("/mkdir (name) создать директорию\n");
            ctx.channel().writeAndFlush("/rm (name) удалить файл по имени\n");
            ctx.channel().writeAndFlush("/copy (src, target) скопировать файл из одного пути в другой\n");
            ctx.channel().writeAndFlush("/cat (name) - вывести в консоль содержимое файла\n");

            // TODO: 11.01.2021 можно добавить команду clear
        } else if (msg.startsWith("/ls")) {
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
            // TODO: 09.01.2021 Как отправить дерево
        } else if (msg.startsWith("/cd")) {
            String[] strings = msg.split(" ", 2);
            lastDirOrFile = strings[1];
            courrentPath = Paths.get(courrentPath.toString(), "/", lastDirOrFile.toString());
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
            // TODO: 11.01.2021  добавить проверку запрашиваемого пути
        } else if (msg.startsWith("/cd..")) {
            courrentPath = Paths.get(courrentPath.toString().split(lastDirOrFile, 2)[0]); // todo проверить работу метода
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        }else if (msg.startsWith("/touch")) {
            String[] strings = msg.split(" ", 2);
            lastDirOrFile = strings[1];
            Path newFile = Paths.get(courrentPath.toString(), lastDirOrFile, "txt");
            Files.createFile(newFile);
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        } else if (msg.startsWith("/mkdir")) {
            String[] strings = msg.split(" ", 2);
            lastDirOrFile = strings[1];
            Path newFolder = Paths.get(courrentPath.toString(), "/", lastDirOrFile.toString());
            Files.createDirectory(newFolder);
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        } else if (msg.startsWith("/rm")) {
            String[] strings = msg.split(" ", 2);
            lastDirOrFile = strings[1];
            Path folderForDelete = Paths.get(courrentPath.toString(), "/", lastDirOrFile.toString());
            Files.delete(folderForDelete);
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
            // TODO: 12.01.2021 Добавить предварительное копирование файла, для его восстановления.
            // TODO: 12.01.2021 Попробовать использовать get.Parent - чтобы определять путь файла 
        } else if (msg.startsWith("/copy")) {
            String[] strings = msg.split(" ", 3);
            lastDirOrFile = strings[1];
            String targetFolder = strings[2];
            Path fileFrom = Paths.get(lastDirOrFile).getParent();
            Path fileTo = Paths.get(courrentPath.toString(), targetFolder, "txt");
            Files.copy(fileFrom, fileTo, StandardCopyOption.REPLACE_EXISTING);
            courrentPath = Paths.get(courrentPath.toString(), targetFolder);
            ctx.channel().writeAndFlush(courrentPath.toString() + ":\n");
            ctx.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        } else if (msg.startsWith("/cat")) {
            String[] strings = msg.split(" ", 2);
            lastDirOrFile = strings[1];
            Path file = Paths.get(lastDirOrFile);
            List<String> fileString = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String s: fileString) {
                ctx.channel().writeAndFlush(s);
            }
            // TODO: 12.01.2021 Добавить флаг /fileRead/ чтобы покузывать содержимое файла в новом окне
        } else {
            ctx.channel().writeAndFlush("некорректная команда\n");
        }
    }
}
