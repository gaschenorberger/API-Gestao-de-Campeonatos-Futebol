package br.com.campeonato.service;

import br.com.campeonato.dto.jogador.JogadorAtualizacaoRequest;
import br.com.campeonato.dto.jogador.JogadorCadastroRequest;
import br.com.campeonato.dto.jogador.JogadorResponse;
import br.com.campeonato.entity.Jogador;
import br.com.campeonato.entity.Time;
import br.com.campeonato.repository.JogadorRepository;
import br.com.campeonato.repository.TimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JogadorService {

    private final JogadorRepository jogadorRepository;
    private final TimeRepository timeRepository;

    public JogadorService(JogadorRepository jogadorRepository, TimeRepository timeRepository) {
        this.jogadorRepository = jogadorRepository;
        this.timeRepository = timeRepository;
    }

    public List<JogadorResponse> listar(Long timeId) {
        List<Jogador> jogadores = timeId == null
                ? jogadorRepository.findAll()
                : jogadorRepository.findAllByTimeId(timeId);

        return jogadores.stream()
                .map(JogadorResponse::from)
                .toList();
    }

    public JogadorResponse buscarPorId(Long id) {
        return JogadorResponse.from(buscarJogador(id));
    }

    @Transactional
    public JogadorResponse cadastrar(JogadorCadastroRequest request) {
        Jogador jogador = new Jogador();
        jogador.setNome(request.nome().strip());
        jogador.setPosicao(request.posicao().strip());
        jogador.setNumeroCamisa(request.numeroCamisa());

        if (request.timeId() != null) {
            jogador.setTime(buscarTime(request.timeId()));
        }

        return JogadorResponse.from(jogadorRepository.save(jogador));
    }

    @Transactional
    public JogadorResponse atualizar(Long id, JogadorAtualizacaoRequest request) {
        if (request.nome() == null && request.posicao() == null
                && request.numeroCamisa() == null && request.timeId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe pelo menos um campo para atualizar."
            );
        }

        Jogador jogador = buscarJogador(id);

        if (request.nome() != null) {
            jogador.setNome(request.nome().strip());
        }

        if (request.posicao() != null) {
            jogador.setPosicao(request.posicao().strip());
        }

        if (request.numeroCamisa() != null) {
            jogador.setNumeroCamisa(request.numeroCamisa());
        }

        if (request.timeId() != null) {
            jogador.setTime(buscarTime(request.timeId()));
        }

        return JogadorResponse.from(jogador);
    }

    @Transactional
    public void excluir(Long id) {
        jogadorRepository.delete(buscarJogador(id));
    }

    private Jogador buscarJogador(Long id) {
        return jogadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Jogador " + id + " não encontrado."
                ));
    }

    private Time buscarTime(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Time " + id + " não encontrado."
                ));
    }
}