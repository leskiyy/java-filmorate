package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public List<Director> getAllDirectors() {
        List<Director> allDirectors = service.getAllDirectors();
        log.info("Successfully get all directors");
        return allDirectors;
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        Director directorById = service.getDirectorById(id);
        log.info("Successfully get director with id={}", id);
        return directorById;
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) {
        service.addDirector(director);
        log.info("Successfully add director");
        return director;
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        service.updateDirector(director);
        log.info("Successfully updated director");
        return director;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        service.deleteDirector(id);
        log.info("Successfully deleted director with id={}", id);
    }

}
