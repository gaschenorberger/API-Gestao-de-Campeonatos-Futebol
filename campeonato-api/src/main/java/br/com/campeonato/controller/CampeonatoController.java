package br.com.campeonato.controller;

import br.com.campeonato.dto.campeonato.CampeonatoAtualizacaoRequest;
import br.com.campeonato.dto.campeonato.CampeonatoCadastroRequest;
import br.com.campeonato.dto.campeonato.CampeonatoResponse;
import br.com.campeonato.dto.campeonato.ClassificacaoResponse;
import br.com.campeonato.service.ClassificacaoService;
import br.com.campeonato.service.CampeonatoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campeonatos")
public class CampeonatoController {

    private final CampeonatoService campeonatoService;
    private final ClassificacaoService classificacaoService;


    public CampeonatoController(CampeonatoService campeonatoService, ClassificacaoService classificacaoService) {
        this.campeonatoService = campeonatoService;
        this.classificacaoService = classificacaoService;
    }

    @GetMapping
    public List<CampeonatoResponse> listar() {
        return campeonatoService.listar();
    }

    @GetMapping("/{id}")
    public CampeonatoResponse buscarPorId(@PathVariable Long id) {
        return campeonatoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampeonatoResponse cadastrar(@Valid @RequestBody CampeonatoCadastroRequest request) {
        return campeonatoService.cadastrar(request);
    }

    @PatchMapping("/{id}")
    public CampeonatoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CampeonatoAtualizacaoRequest request
    ) {
        return campeonatoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        campeonatoService.excluir(id);
    }

    @GetMapping("/{id}/classificacao")
    public List<ClassificacaoResponse> consultarClassificacao(@PathVariable Long id) {
        return classificacaoService.consultar(id);
    }
}