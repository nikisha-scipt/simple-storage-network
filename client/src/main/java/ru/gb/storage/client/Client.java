package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.stage.Stage;
import ru.gb.storage.common.handler.JsonDecoderMsg;
import ru.gb.storage.common.handler.JsonEncoderMsg;
import ru.gb.storage.common.message.AuthMessage;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Client {

    private static Client instance = null;
    private final Logger LOG = Logger.getLogger(Client.class.getName());
    private final SimpleFormatter simpleFormatter = new SimpleFormatter();
    private NioEventLoopGroup nioEventLoopGroup;
    private AuthMessage authMsg;
    private Bootstrap server;

    private Client() {
        try {
            FileHandler fileHandler = new FileHandler("Log.log");
            LOG.addHandler(fileHandler);
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient() throws IOException {
        Thread thread = new Thread(() -> {
            nioEventLoopGroup = new NioEventLoopGroup(1);
            try {
                this.server = new Bootstrap()
                        .group(nioEventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                        new LengthFieldPrepender(3),
                                        new StringDecoder(),
                                        new StringEncoder(),
                                        new JsonDecoderMsg(),
                                        new JsonEncoderMsg(),
                                        new SimpleChannelInboundHandler<Message>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, Message message) throws IOException {
                                                if (message instanceof TextMessage) {
                                                    TextMessage textMessage = (TextMessage) message;
                                                    System.out.println(textMessage.getText());
                                                    if (textMessage.getText().equals("auth")) {
                                                        System.out.println("Успешная авторизация");
                                                    }
                                                }
                                            }
                                        }
                                );
                            }
                        });
                System.out.println("Client started");
                Channel channel = server.connect("localhost", 22333).sync().channel();
                channel.writeAndFlush(authMsg);
                channel.closeFuture().sync();

            } catch (InterruptedException e) {
                LOG.info("Error " + e.getMessage());
            } finally {
                nioEventLoopGroup.shutdownGracefully();
            }
        });
        thread.start();
    }


    public void getAuth(AuthMessage message){
        authMsg = message;
    }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

}
