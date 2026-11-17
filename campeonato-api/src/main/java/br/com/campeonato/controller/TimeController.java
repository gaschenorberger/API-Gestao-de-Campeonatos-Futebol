package br.com.campeonato.controller;

import br.com.campeonato.dto.time.TimeAtualizacaoRequest;
import br.com.campeonato.dto.time.TimeCadastroRequest;
import br.com.campeonato.dto.time.TimeResponse;
import br.com.campeonato.service.TimeService;
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
@RequestMapping("/times")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    public List<TimeResponse> listar(@RequestParam(required = false) Long campeonatoId) {
        return timeService.listar(campeonatoId);
    }

    @GetMapping("/{id}")
    public TimeResponse buscarPorId(@PathVariable Long id) {
        return timeService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeResponse cadastrar(@Valid @RequestBody TimeCadastroRequest request) {
        return timeService.cadastrar(request);
    }

    @PatchMapping("/{id}")
    public TimeResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TimeAtualizacaoRequest request
    ) {
        return timeService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        timeService.excluir(id);
    }
}