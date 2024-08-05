package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("""
            select u from User u left join u.roles roles left join u.languages languages
            where (u.contractType.id in ?1 or ?1 is null) and
                  (roles.id in ?2 or ?2 is null) and
                  (languages.id in ?3 or ?3 is null)""")
    List<User> findAllByContractAndRoleAndLanguage(@Nullable Collection<Long> contract,
                                                   @Nullable Collection<Long> role,
                                                   @Nullable Collection<Long> language,
                                                   Sort sort);

    @Query("""
            select u from User u left join u.roles roles left join u.languages languages
            where upper(u.firstname) like upper(concat('%', ?1, '%')) or
                  upper(u.surname) like upper(concat('%', ?1, '%')) or
                  upper(u.email) like upper(concat('%', ?1, '%')) or
                  upper(u.contractType.name) like upper(concat('%', ?1, '%')) or
                  upper(u.residence.city) like upper(concat('%', ?1, '%')) or
                  upper(u.workAddress.city) like upper(concat('%', ?1, '%')) or
                  upper(roles.name) like upper(concat('%', ?1, '%')) or
                  upper(languages.name) like upper(concat('%', ?1, '%'))""")
    List<User> FindAllBySearch(String search);

    @Query("""
        select u from User u
        where upper(u.firstname) in ?1 and upper(u.surname) in ?1""")
    List<User> findAllBySearchName(List<String> search);
}