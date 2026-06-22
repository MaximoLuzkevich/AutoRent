package com.AutoRent.Frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String inicio() {
        return "forward:/login.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "forward:/registro.html";
    }

    @GetMapping("/cliente")
    public String cliente() {
        return "forward:/cliente-inicio.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin-propietarios.html";
    }

    @GetMapping({"/propietario", "/propietario-panel"})
    public String propietario() {
        return "forward:/propietario-autos.html";
    }
}
