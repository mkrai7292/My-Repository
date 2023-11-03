package com.thg.procurement.questionnaire.service.repository;

import com.thg.procurement.questionnaire.service.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JHipsterCouchbaseRepository<User, String> {
    default Optional<User> findOneByLogin(String login) {
        return findById(login);
    }

    default Page<User> findAllByActivatedIsTrue(Pageable pageable) {
        Page<User> page = findAllIdsByActivatedIsTrue(pageable);
        return new PageImpl<>(findAllById(toIds(page.getContent())), pageable, page.getTotalElements());
    }

    @Query(FIND_IDS_QUERY + " AND activated = true")
    Page<User> findAllIdsByActivatedIsTrue(Pageable pageable);
}
