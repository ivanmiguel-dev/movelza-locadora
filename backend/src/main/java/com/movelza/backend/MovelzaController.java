
package com.movelza.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovelzaController {

    @GetMapping("/")
    public String inicio() {
        return "MOVELZA - Backend funcionando com sucesso!";
    }

    @GetMapping("/api/status")
    public String status() {
        return "API MOVELZA online!";
    }
}
