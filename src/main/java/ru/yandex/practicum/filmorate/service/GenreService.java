package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository repository;

    public List<Genre> getAllGenres() {
        return repository.findAll();
    }

    public Genre getGenreById(@Positive int id) {
        Optional<Genre> genreById = repository.findById(id);
        if (genreById.isEmpty()) {
            throw new NotFoundException("There is no genre with id=" + id);
        } else {
            return genreById.get();
        }
    }

}
