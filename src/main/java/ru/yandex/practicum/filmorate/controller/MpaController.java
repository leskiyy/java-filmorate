package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService service;

    @GetMapping
    public List<Mpa> getAllMpa() {
        List<Mpa> allMpa = service.getAllMpa();
        log.info("Successfully get all mpa");
        return allMpa;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        Mpa mpaById = service.getMpaById(id);
        log.info("Successfully get mpa with id={}", id);
        return mpaById;
    }

}
