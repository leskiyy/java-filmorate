package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.DeletedUserException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.*;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    public void validateFilmDto(FilmDTO dto) {
        validateMpaById(dto.getMpa().getId());
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            validateGenreById(dto.getGenres().stream().mapToInt(Genre::getId).toArray());
        }
    }

    public void validateUserById(long... ids) {
        for (long id : ids) {
            if (!userRepository.existById(id)) throw new NotFoundException("There is no user with id=" + id);
        }
    }

    public void validateForFriends(long id) {
        if (userRepository.isUserDeleted(id)) throw new DeletedUserException("User with ID=" + id + " is deleted");
        validateUserById(id);
    }

    public void validateFilmById(long... ids) {
        for (long id : ids) {
            if (!filmRepository.existById(id)) throw new NotFoundException("There is no film with id=" + id);
        }
    }

    public void validateReviewById(long... ids) {
        for (long id : ids) {
            if (!reviewRepository.existById(id)) throw new NotFoundException("There is no review with id=" + id);
        }
    }

    public void validateMpaById(int... ids) {
        for (int id : ids) {
            if (!mpaRepository.existById(id)) throw new NotFoundException("There is no mpa with id=" + id);
        }
    }

    public void validateGenreById(int... ids) {
        for (int id : ids) {
            if (!genreRepository.existById(id)) {
                throw new NotFoundException("There is no genre with id=" + id);
            }
        }
    }

}
