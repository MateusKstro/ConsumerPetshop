package consumerpetshop.repository;


import consumerpetshop.entity.PedidoMensalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PedidosMensalRepository extends MongoRepository<PedidoMensalEntity, Integer> {

    Optional<PedidoMensalEntity> findPedidosByMesAndAno(Integer mes, Integer ano);
}
