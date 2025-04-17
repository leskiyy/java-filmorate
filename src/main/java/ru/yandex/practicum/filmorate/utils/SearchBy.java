package ru.yandex.practicum.filmorate.utils;

import lombok.Getter;

@Getter
public enum SearchBy {
    DIRECTOR("""
            SELECT * FROM FILMS WHERE FILM_ID IN (
            SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID IN (
            SELECT DIRECTOR_ID FROM DIRECTORS WHERE NAME ILIKE ?))"""),
    TITLE("SELECT * FROM FILMS WHERE NAME ILIKE ?");

    private final String query;

    SearchBy(String query) {
        this.query = query;
    }
}
