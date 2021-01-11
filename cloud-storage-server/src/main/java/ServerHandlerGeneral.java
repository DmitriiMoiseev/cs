import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerHandlerGeneral extends SimpleChannelInboundHandler<String> {

    private static final List<Channel> channels = new ArrayList<>();
    private int newClientIndex = 1;
    private String clientName;
    private Path clientMainDirInServer = null;
//    private Path clientMainDirInClient = null;
    private Path courrentPath = null;
    private String lastDir = null;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился" + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент №" + newClientIndex;
        newClientIndex ++;
        clientMainDirInServer = Files.createDirectory(Paths.get("/Users/DmitryMoiseev/cloud/cloud-storage-2/DirectoryServer","/", clientName));
//        clientMainDirInClient = Files.createDirectory(Paths.get("/Users/DmitryMoiseev/cloud/cloud-storage-2/DirectoryClient","/", clientName));
        courrentPath = clientMainDirInServer;
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
        if (s.startsWith("/help")) {
            channelHandlerContext.channel().writeAndFlush("Ссписок команд :\n");
            channelHandlerContext.channel().writeAndFlush("/help - список команд\n");
            channelHandlerContext.channel().writeAndFlush("/ls - список файлов\n");
            channelHandlerContext.channel().writeAndFlush("/cd (name) - перейти в папку\n");
            channelHandlerContext.channel().writeAndFlush("/cd.. - вернуться назад\n");
            channelHandlerContext.channel().writeAndFlush("/touch (name) создать текстовый файл с именем\n");
            channelHandlerContext.channel().writeAndFlush("/mkdir (name) создать директорию\n");
            channelHandlerContext.channel().writeAndFlush("/rm (name) удалить файл по имени\n");
            channelHandlerContext.channel().writeAndFlush("/copy (src, target) скопировать файл из одного пути в другой\n");
            channelHandlerContext.channel().writeAndFlush("/cat (name) - вывести в консоль содержимое файла\n");

            // TODO: 11.01.2021 можно добавить команду clear
        } else if (s.startsWith("/ls")) {
            channelHandlerContext.channel().writeAndFlush(courrentPath.toString() + ":\n");
            channelHandlerContext.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
            // TODO: 09.01.2021 Как отправить дерево
        } else if (s.startsWith("/cd")) {
            String[] strings = s.split(" ", 2);
            lastDir = strings[1];
            courrentPath = Paths.get(courrentPath.toString(), "/", lastDir.toString());
            channelHandlerContext.channel().writeAndFlush(courrentPath.toString() + ":\n");
            channelHandlerContext.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
            // TODO: 11.01.2021  добавить проверку запрашиваемого пути
        } else if (s.startsWith("/cd..")) {
            courrentPath = Paths.get(courrentPath.toString().split(lastDir, 2)[0]); // todo проверить работу метода
            channelHandlerContext.channel().writeAndFlush(courrentPath.toString() + ":\n");
            channelHandlerContext.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        }else if (s.startsWith("/touch")) {

        } else if (s.startsWith("/mkdir")) {
            String[] strings = s.split(" ", 2);
            lastDir = strings[1];
            Path newFolder = Paths.get(courrentPath.toString(), "/", lastDir.toString());
            Files.createDirectory(newFolder);
            channelHandlerContext.channel().writeAndFlush(courrentPath.toString() + ":\n");
            channelHandlerContext.channel().writeAndFlush(String.join(" ", new File(courrentPath.toString()).list()) + "\n");
        } else if (s.startsWith("/rm")) {

        } else if (s.startsWith("/copy")) {

        } else if (s.startsWith("/cat")) {

        } else {
            channelHandlerContext.channel().writeAndFlush("некорректная команда\n");
        }
    }
}
