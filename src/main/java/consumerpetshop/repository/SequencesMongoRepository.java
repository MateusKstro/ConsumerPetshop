package consumerpetshop.repository;


import consumerpetshop.entity.SequencesMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SequencesMongoRepository extends MongoRepository<SequencesMongoEntity, Integer> {

    Optional<SequencesMongoEntity> findSequencesMongoEntitiesByEntidade(String entidade);
}
