package br.com.campeonato.repository;

import br.com.campeonato.entity.Partida;
import br.com.campeonato.entity.StatusPartida;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartidaRepository extends JpaRepository<Partida, Long>,
        JpaSpecificationExecutor<Partida> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Partida p where p.id = :id")
    Optional<Partida> buscarParaAtualizar(@Param("id") Long id);

    boolean existsByMandanteIdOrVisitanteId(Long mandanteId, Long visitanteId);

    List<Partida> findAllByCampeonatoIdAndStatus(Long campeonatoId, StatusPartida status);
}