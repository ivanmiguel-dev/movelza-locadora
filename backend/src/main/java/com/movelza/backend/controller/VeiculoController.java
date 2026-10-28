
package com.movelza.backend.controller;

import com.movelza.backend.model.Veiculo;
import com.movelza.backend.repository.VeiculoRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:63342",
        "http://127.0.0.1:63342"
})
public class VeiculoController {

    private final VeiculoRepository veiculoRepository;

    public VeiculoController(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @GetMapping
    public List<Veiculo> listarVeiculos() {
        return veiculoRepository.findAll();
    }

    @GetMapping("/total")
    public long contarVeiculos() {
        return veiculoRepository.count();
    }
}
