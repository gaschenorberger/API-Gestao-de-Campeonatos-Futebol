package br.com.campeonato.controller;

import br.com.campeonato.dto.partida.PartidaAtualizacaoRequest;
import br.com.campeonato.dto.partida.PartidaCadastroRequest;
import br.com.campeonato.dto.partida.PartidaPlacarRequest;
import br.com.campeonato.dto.partida.PartidaResponse;
import br.com.campeonato.dto.partida.PartidaStatusRequest;
import br.com.campeonato.entity.StatusPartida;
import br.com.campeonato.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @GetMapping
    public List<PartidaResponse> listar(
            @RequestParam(required = false) StatusPartida status,
            @RequestParam(required = false) Long timeId
    ) {
        return partidaService.listar(status, timeId);
    }

    @GetMapping("/{id}")
    public PartidaResponse buscarPorId(@PathVariable Long id) {
        return partidaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaResponse cadastrar(@Valid @RequestBody PartidaCadastroRequest request) {
        return partidaService.cadastrar(request);
    }

    @PatchMapping("/{id}")
    public PartidaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PartidaAtualizacaoRequest request
    ) {
        return partidaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        partidaService.excluir(id);
    }

    @PatchMapping("/{id}/status")
    public PartidaResponse atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody PartidaStatusRequest request
    ) {
        return partidaService.atualizarStatus(id, request);
    }

    @PatchMapping("/{id}/placar")
    public PartidaResponse atualizarPlacar(
            @PathVariable Long id,
            @Valid @RequestBody PartidaPlacarRequest request
    ) {
        return partidaService.atualizarPlacar(id, request);
    }
}