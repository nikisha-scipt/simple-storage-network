package ru.gb.storage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.storage.common.handler.JsonDecoderFile;
import ru.gb.storage.common.handler.JsonDecoderMsg;
import ru.gb.storage.common.handler.JsonEncoderFile;
import ru.gb.storage.common.handler.JsonEncoderMsg;
import ru.gb.storage.common.message.AuthMessage;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;
import ru.gb.storage.server.jdbc.DataBase;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Server {

    private static Server instance = null;
    private final Logger LOG = Logger.getLogger(Server.class.getName());
    private final SimpleFormatter simpleFormatter = new SimpleFormatter();

    private Server() {

        try {
            FileHandler fileHandler = new FileHandler("Log.log");
            LOG.addHandler(fileHandler);
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startServer() throws InterruptedException  {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoderFile(),
                                    new JsonEncoderFile(),
                                    new SimpleChannelInboundHandler<Message>() {

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws  ClassNotFoundException {
                                            System.out.println("New active channel");
                                        }
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
                                            if (message instanceof AuthMessage) {
                                                AuthMessage msg = (AuthMessage) message;
                                                DataBase dataBase = DataBase.getInstance();
                                                dataBase.connect();
                                                if (dataBase.checkLogin(msg.getLogin())) {
                                                    TextMessage textMessage = new TextMessage();
                                                    textMessage.setText("auth");
                                                    channelHandlerContext.writeAndFlush(textMessage);
                                                    dataBase.disconnect();
                                                } else {
                                                    System.out.println("Авторизация не прошла");
                                                    TextMessage textMessage = new TextMessage();
                                                    textMessage.setText("Incorrect login or password");
                                                    channelHandlerContext.writeAndFlush(textMessage);
                                                }
                                            }
                                        }
                                    });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = server.bind("localhost",22333).sync().channel();
            System.out.println("Server started");
            channel.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
         return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = getInstance();
        server.startServer();
    }
}
