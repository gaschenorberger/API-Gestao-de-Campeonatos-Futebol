package br.com.campeonato.controller;

import br.com.campeonato.dto.jogador.JogadorAtualizacaoRequest;
import br.com.campeonato.dto.jogador.JogadorCadastroRequest;
import br.com.campeonato.dto.jogador.JogadorResponse;
import br.com.campeonato.service.JogadorService;
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
@RequestMapping("/jogadores")
public class JogadorController {

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }

    @GetMapping
    public List<JogadorResponse> listar(@RequestParam(required = false) Long timeId) {
        return jogadorService.listar(timeId);
    }

    @GetMapping("/{id}")
    public JogadorResponse buscarPorId(@PathVariable Long id) {
        return jogadorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JogadorResponse cadastrar(@Valid @RequestBody JogadorCadastroRequest request) {
        return jogadorService.cadastrar(request);
    }

    @PatchMapping("/{id}")
    public JogadorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody JogadorAtualizacaoRequest request
    ) {
        return jogadorService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        jogadorService.excluir(id);
    }
}