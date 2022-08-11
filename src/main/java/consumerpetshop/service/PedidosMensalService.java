package consumerpetshop.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import consumerpetshop.dto.PedidoDTOConsumer;
import consumerpetshop.dto.PedidoMensalDto;
import consumerpetshop.entity.PedidoMensalEntity;
import consumerpetshop.exception.EntidadeNaoEncontradaException;
import consumerpetshop.repository.PedidosMensalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidosMensalService {

    private final PedidosMensalRepository pedidosMensalRepository;
    private final ObjectMapper objectMapper;
    private final SequencesMongoService sequencesMongoService;
    private final MongoTemplate mongoTemplate;


    public PedidoMensalDto getPedidoMesAtual() throws EntidadeNaoEncontradaException {
        LocalDate localDate = LocalDate.now();
        return entityToDto(findPedidoByMesAndAno(localDate.getMonthValue(), localDate.getYear()));
    }

    public PedidoMensalDto getPedidoByMesAndAno(Integer mes, Integer ano) throws EntidadeNaoEncontradaException {
        return entityToDto(findPedidoByMesAndAno(mes, ano));
    }

    public PedidoMensalEntity findPedidoByMesAndAno(Integer mes, Integer ano) throws EntidadeNaoEncontradaException {
        return pedidosMensalRepository.findPedidosByMesAndAno(mes, ano)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Não existem dados disponíveis para a data informada"));
    }

    public void atualizarPedidos(PedidoDTOConsumer pedidoDTOConsumer) {
        Optional<PedidoMensalEntity> pedidoMensalEntity = pedidosMensalRepository.findPedidosByMesAndAno(
                pedidoDTOConsumer.getDataEHora().getMonthValue(), pedidoDTOConsumer.getDataEHora().getYear());
        if (pedidoMensalEntity.isPresent()) {
            PedidoMensalEntity pedidoMensalEntityUpdate = pedidoMensalEntity.get();
            pedidoMensalEntityUpdate.setTotalPedido(pedidoMensalEntityUpdate.getTotalPedido() + 1);
            mongoTemplate.save(pedidoMensalEntityUpdate, "pedido_mensal");
        } else {
            PedidoMensalEntity pedidoMensalEntityNovo = new PedidoMensalEntity();
            pedidoMensalEntityNovo.setAno(pedidoDTOConsumer.getDataEHora().getYear());
            pedidoMensalEntityNovo.setMes(pedidoDTOConsumer.getDataEHora().getMonthValue());
            pedidoMensalEntityNovo.setTotalPedido(1);
            pedidoMensalEntityNovo.setIdPedidoMensal(sequencesMongoService.getIdByEntidade("pedido_mensal"));
            pedidosMensalRepository.save(pedidoMensalEntityNovo);
        }
    }

    public PedidoMensalDto entityToDto(PedidoMensalEntity pedidoMensalEntity) {
        return objectMapper.convertValue(pedidoMensalEntity, PedidoMensalDto.class);
    }
}
