package com.nsc.kubernetes.demo.controller;

import com.nsc.kubernetes.demo.model.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class MovieApiDelegateImpl implements MovieApiDelegate {
    @Override
    public ResponseEntity<Movie> getMovieById(String username) {
        Movie movie = new Movie();
        movie.setMovieId("123");
        movie.setMovieName("Apthamitra");
        return ResponseEntity.ok(movie);
    }
}
