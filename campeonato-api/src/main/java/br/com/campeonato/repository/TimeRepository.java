package br.com.campeonato.repository;

import br.com.campeonato.entity.Time;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {

    List<Time> findAllByCampeonatoId(Long campeonatoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Time t where t.id in :ids order by t.id")
    List<Time> buscarParaAtualizar(@Param("ids") List<Long> ids);
}