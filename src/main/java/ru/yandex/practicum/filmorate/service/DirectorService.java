package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository repository;

    public List<Director> getAllDirectors() {
        return repository.findAll();
    }

    public Director getDirectorById(@Positive int id) {
        Optional<Director> directorById = repository.findById(id);
        if (directorById.isEmpty()) {
            throw new NotFoundException("There is no director with id=" + id);
        } else {
            return directorById.get();
        }
    }

    public Director addDirector(@Valid Director director) {
        return repository.addDirector(director);
    }

    public void addFilmDirector(Director director, Film film) {
        repository.addFilmDirector(director, film);
    }

    public void deleteDirector(int id) {
        repository.deleteDirector(id);
    }

    public Director updateDirector(@Valid Director director) {
        return repository.updateDirector(director);
    }
}
