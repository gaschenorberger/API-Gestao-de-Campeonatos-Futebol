package br.com.campeonato.repository;

import br.com.campeonato.entity.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    List<Jogador> findAllByTimeId(Long timeId);
}