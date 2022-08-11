package consumerpetshop.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import consumerpetshop.dto.BalancoMensalDTO;
import consumerpetshop.dto.PedidoDTOConsumer;
import consumerpetshop.entity.BalancoMensalEntity;
import consumerpetshop.exception.EntidadeNaoEncontradaException;
import consumerpetshop.repository.BalancoMensalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalancoMensalService {
    private final BalancoMensalRepository balancoMensalRepository;
    private final SequencesMongoService sequencesMongoService;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;


    public BalancoMensalDTO getBalancoMesAtual() throws EntidadeNaoEncontradaException {
        LocalDate localDate = LocalDate.now();
        return entityToDto(findBalancoByMesAndAno(localDate.getMonthValue(), localDate.getYear()));
    }

    public BalancoMensalDTO getBalancoByMesAndAno(Integer mes, Integer ano) throws EntidadeNaoEncontradaException {
        return entityToDto(findBalancoByMesAndAno(mes, ano));
    }
    public BalancoMensalEntity findBalancoByMesAndAno(Integer mes, Integer ano) throws EntidadeNaoEncontradaException {
        return balancoMensalRepository.findBalancoMensalEntitiesByMesAndAno(mes, ano)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Não existem dados disponíveis para a data informada"));
    }

    public void atualizarBalanco(PedidoDTOConsumer pedidoDTOConsumer) {
        Optional<BalancoMensalEntity> balancoMensalEntity = balancoMensalRepository.findBalancoMensalEntitiesByMesAndAno(
                pedidoDTOConsumer.getDataEHora().getMonthValue(), pedidoDTOConsumer.getDataEHora().getYear());

        if (balancoMensalEntity.isPresent()) {
            BalancoMensalEntity balancoMensalEntityUpdate = balancoMensalEntity.get();
            balancoMensalEntityUpdate.setLucroBruto(balancoMensalEntityUpdate.getLucroBruto() + pedidoDTOConsumer.getValor());
            mongoTemplate.save(balancoMensalEntityUpdate, "balanco_mensal");
        } else {
            BalancoMensalEntity balancoMensalEntityNew = new BalancoMensalEntity();
            balancoMensalEntityNew.setAno(pedidoDTOConsumer.getDataEHora().getYear());
            balancoMensalEntityNew.setMes(pedidoDTOConsumer.getDataEHora().getMonthValue());
            balancoMensalEntityNew.setLucroBruto(pedidoDTOConsumer.getValor());
            balancoMensalEntityNew.setIdBalancoMensal(sequencesMongoService.getIdByEntidade("balanco_mensal"));
            balancoMensalRepository.save(balancoMensalEntityNew);
        }
    }

    public BalancoMensalDTO entityToDto(BalancoMensalEntity balancoMensalEntity) {
        return objectMapper.convertValue(balancoMensalEntity, BalancoMensalDTO.class);
    }
}
