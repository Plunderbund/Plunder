package com.plunder.plunder.domain.catalog;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.domain.models.TvShowDetails;
import java.util.List;
import rx.Observable;

public interface CatalogManager {
  Observable<List<Movie>> popularMovies();

  Observable<List<Movie>> popularMovies(@IntRange(from = 1) int page);

  Observable<List<Movie>> popularMovies(@NonNull Genre genre);

  Observable<List<Movie>> popularMovies(@NonNull Genre genre, @IntRange(from = 1) int page);

  Observable<List<Movie>> searchMovies(@NonNull String query);

  Observable<List<Movie>> searchMovies(@NonNull String query, @IntRange(from = 1) int page);

  Observable<List<Movie>> similarMovies(@NonNull Movie movie);

  Observable<List<Movie>> similarMovies(@NonNull Movie movie, @IntRange(from = 1) int page);

  Observable<List<TvShow>> popularTvShows();

  Observable<List<TvShow>> popularTvShows(@IntRange(from = 1) int page);

  Observable<List<TvShow>> popularTvShows(@NonNull Genre genre);

  Observable<List<TvShow>> popularTvShows(@NonNull Genre genre, @IntRange(from = 1) int page);

  Observable<List<TvShow>> searchTvShows(@NonNull String query);

  Observable<List<TvShow>> searchTvShows(@NonNull String query, @IntRange(from = 1) int page);

  Observable<TvShowDetails> tvShowDetails(@NonNull TvShow tvShow);

  Observable<List<TvEpisode>> tvSeasonEpisodes(@NonNull TvShow tvShow, @NonNull TvSeason tvSeason);

  Observable<List<Genre>> genres();
}
