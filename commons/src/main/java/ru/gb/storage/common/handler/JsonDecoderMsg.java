package ru.gb.storage.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.gb.storage.common.message.Message;

import java.util.List;

public class JsonDecoderMsg extends MessageToMessageDecoder<String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String message, List<Object> listOut) throws Exception {
        System.out.println("Входящее сообщение: " + message);
        Message msg = mapper.readValue(message, Message.class);
        System.out.println("преобразовать входящее сообщение в: " + msg);
        listOut.add(msg);
    }
}
