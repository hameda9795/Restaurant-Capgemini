package com.example.Restaurant.controller;


import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;

@Controller("/gallery")
public class GalleryController {

    // Controller for the gallery page
    public String gallery(Model model) {
        return "gallery";
    }


}
