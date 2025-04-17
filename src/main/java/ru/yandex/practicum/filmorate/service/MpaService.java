package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaRepository repository;

    public List<Mpa> getAllMpa() {
        return repository.findAll();
    }

    public Mpa getMpaById(@Positive int id) {
        Optional<Mpa> mpaById = repository.findById(id);
        if (mpaById.isEmpty()) {
            throw new NotFoundException("There is no mpa with id=" + id);
        } else {
            return mpaById.get();
        }
    }

}
