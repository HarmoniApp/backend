package org.harmoniapp.harmonidata.repositories;

import org.harmoniapp.harmonidata.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByIdAndIsActive(Long id, boolean active);

    Page<User> findAllByIsActive(boolean active, Pageable pageable);

    List<User> findAllByIsActive(boolean isActive);

    @Query("""
            select u from User u left join u.roles roles left join u.languages languages
            where ((u.contractType.id in ?1 or ?1 is null) and
                  (roles.id in ?2 or ?2 is null) and
                  (languages.id in ?3 or ?3 is null))
                  and u.isActive = ?4""")
    Page<User> findAllByContractAndRoleAndLanguageAndIsActive(@Nullable Collection<Long> contract,
                                                              @Nullable Collection<Long> role,
                                                              @Nullable Collection<Long> language,
                                                              boolean active,
                                                              Pageable pageable);

    @Query("""
            select u from User u left join u.roles roles left join u.languages languages
            where u.isActive = ?2 and
                  (
                  upper(u.firstname) like concat(?1, '%') or
                  upper(u.surname) like concat(?1, '%')
                  )""")
    List<User> FindAllBySearch(String search, boolean active);

    @Query("""
        select u from User u
        where u.isActive = ?2 and upper(u.firstname) in ?1 and upper(u.surname) in ?1""")
    List<User> findAllBySearchName(List<String> search, boolean active);

    @Query("""
        select u from User u left join u.roles roles
        where upper(roles.name) like upper('ADMIN') and u.isActive = true""")
    Page<User> findSupervisors(Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query("""
        select u from User u
        where u.isActive = true and u not in (
            select a.user from Absence a
            where a.status.name='approved' and (
                    (a.start <= ?1 and a.end >= ?1) or
                    (a.start >= ?1 and a.end >= ?2) or
                    (a.start <= ?2 and a.end >= ?2)
                    )
        )""")
    List<User> findAllActiveWithoutAbsenceInDateRange(LocalDate startDate, LocalDate endDate);

    @Query("select u from User u where u.residence.id = ?1 or u.workAddress.id = ?1")
    List<User> findByResidence_IdOrWorkAddress_Id(Long id);

    List<User> findByContractType_Id(Long id);

    List<User> findByLanguages_Id(Long ids);

    List<User> findByRoles_Id(Long ids);

    @Query("""
            select u from User u inner join u.roles roles
            where upper(roles.name) like upper('ADMIN') and u.isActive = true""")
    List<User> findAllActiveSupervisors();


    Set<User> findByIdInAndIsActiveTrue(Collection<Long> ids);
}