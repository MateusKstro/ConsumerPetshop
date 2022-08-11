package consumerpetshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import consumerpetshop.dto.PedidoDTOConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final ObjectMapper objectMapper;

    private final BalancoMensalService balancoMensalService;

    private final PedidosMensalService pedidosMensalService;

    @KafkaListener(
            topics = "${kafka.balanco-topic}",
            groupId = "group1",
            containerFactory = "listenerContainerFactory",
            clientIdPrefix = "balanco-mensal")
    public void consumir(@Payload String mensagem,
                         @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                         @Header(KafkaHeaders.OFFSET) Long offset) throws JsonProcessingException {
        PedidoDTOConsumer pedido = objectMapper.readValue(mensagem, PedidoDTOConsumer.class);
        balancoMensalService.atualizarBalanco(pedido);
        pedidosMensalService.atualizarPedidos(pedido);
    }
}
