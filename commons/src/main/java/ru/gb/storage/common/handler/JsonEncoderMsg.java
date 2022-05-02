package ru.gb.storage.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import ru.gb.storage.common.message.Message;

import java.util.List;

public class JsonEncoderMsg extends MessageToMessageEncoder<Message> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, List<Object> listOut) throws Exception {
        System.out.println("Исходящее сообщение: " + msg);
        String outMsg = mapper.writeValueAsString(msg);
        System.out.println("преобразовать сообщение в: " + outMsg);
        listOut.add(outMsg);
    }

}
