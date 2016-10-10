package com.plunder.plunder.domain.catalog;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.domain.models.TvShowDetails;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.AppendToDiscoverResponse;
import com.uwetrottmann.tmdb2.entities.GenreResults;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.TvResultsPage;
import com.uwetrottmann.tmdb2.entities.TvShowComplete;
import com.uwetrottmann.tmdb2.enumerations.SortBy;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import rx.Observable;

public class TmdbCatalogManager implements CatalogManager {
  private final Tmdb tmdbManager;

  public TmdbCatalogManager(@NonNull Tmdb tmdbManager) {
    Preconditions.checkNotNull(tmdbManager);
    this.tmdbManager = tmdbManager;
  }

  @Override public Observable<List<Movie>> popularMovies() {
    return popularMovies(1);
  }

  @Override public Observable<List<Movie>> popularMovies(@IntRange(from = 1) int page) {
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    return Observable.defer(() -> {
      Call<MovieResultsPage> call = tmdbManager.moviesService().popular(page, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapMovieResultsPage);
  }

  @Override public Observable<List<Movie>> popularMovies(@NonNull Genre genre) {
    return popularMovies(genre, 1);
  }

  @Override
  public Observable<List<Movie>> popularMovies(@NonNull Genre genre, @IntRange(from = 1) int page) {
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    if (genre.movieId() == null) {
      return Observable.empty();
    }

    return Observable.defer(() -> {
      Call<MovieResultsPage> call = tmdbManager.discoverService()
          .discoverMovie(false, true, null, page, null, null, null, null, null,
              SortBy.POPULARITY_DESC, null, null, null, null, null, null, null,
              new AppendToDiscoverResponse(genre.movieId()), null, null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapMovieResultsPage);
  }

  @Override public Observable<List<Movie>> searchMovies(@NonNull String query) {
    return searchMovies(query, 1);
  }

  @Override
  public Observable<List<Movie>> searchMovies(@NonNull String query, @IntRange(from = 1) int page) {
    Preconditions.checkNotNull(query);
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    return Observable.defer(() -> {
      Call<MovieResultsPage> call =
          tmdbManager.searchService().movie(query, page, null, null, null, null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapMovieResultsPage);
  }

  @Override public Observable<List<Movie>> similarMovies(@NonNull Movie movie) {
    return similarMovies(movie, 1);
  }

  @Override
  public Observable<List<Movie>> similarMovies(@NonNull Movie movie, @IntRange(from = 1) int page) {
    Preconditions.checkNotNull(movie);
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    return Observable.defer(() -> {
      Call<MovieResultsPage> call = tmdbManager.moviesService().similar(movie.id(), page, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapMovieResultsPage);
  }

  @Override public Observable<List<TvShow>> popularTvShows() {
    return popularTvShows(1);
  }

  @Override public Observable<List<TvShow>> popularTvShows(@IntRange(from = 1) int page) {
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    return Observable.defer(() -> {
      Call<TvResultsPage> call = tmdbManager.tvService().popular(page, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapTvResultsPage);
  }

  @Override public Observable<List<TvShow>> popularTvShows(@NonNull Genre genre) {
    return popularTvShows(genre, 1);
  }

  @Override public Observable<List<TvShow>> popularTvShows(@NonNull Genre genre,
      @IntRange(from = 1) int page) {
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    if (genre.tvId() == null) {
      return Observable.empty();
    }

    return Observable.defer(() -> {
      Call<TvResultsPage> call = tmdbManager.discoverService()
          .discoverTv(page, null, SortBy.POPULARITY_DESC, null, null, null,
              new AppendToDiscoverResponse(genre.tvId()), null, null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapTvResultsPage);
  }

  @Override public Observable<List<TvShow>> searchTvShows(@NonNull String query) {
    return searchTvShows(query, 1);
  }

  @Override public Observable<List<TvShow>> searchTvShows(@NonNull String query,
      @IntRange(from = 1) int page) {
    Preconditions.checkNotNull(query);
    Preconditions.checkArgument(page >= 1, "page must be at least 1");

    return Observable.defer(() -> {
      Call<TvResultsPage> call = tmdbManager.searchService().tv(query, page, null, null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapTvResultsPage);
  }

  @Override public Observable<TvShowDetails> tvShowDetails(@NonNull TvShow tvShow) {
    Preconditions.checkNotNull(tvShow);

    return Observable.defer(() -> {
      Call<TvShowComplete> call = tmdbManager.tvService().tv(tvShow.id(), null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(TmdbMapper::mapTvShowComplete);
  }

  @Override public Observable<List<TvEpisode>> tvSeasonEpisodes(@NonNull TvShow tvShow,
      @NonNull TvSeason tvSeason) {
    Preconditions.checkNotNull(tvShow);
    Preconditions.checkNotNull(tvSeason);

    return Observable.defer(() -> {
      Call<com.uwetrottmann.tmdb2.entities.TvSeason> call =
          tmdbManager.tvSeasonsService().season(tvShow.id(), tvSeason.seasonNumber(), null, null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).concatMapIterable(result -> result.episodes).map(TmdbMapper::mapTvEpisode).toList();
  }

  @Override public Observable<List<Genre>> genres() {
    return Observable.defer(() -> {
      Call<GenreResults> call = tmdbManager.genreService().movie(null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).flatMap(movieGenres -> {
      Call<GenreResults> call = tmdbManager.genreService().tv(null);

      try {
        return Observable.just(call.execute().body());
      } catch (IOException e) {
        return Observable.error(e);
      }
    }, Pair::new).map(pair -> TmdbMapper.combineGenreResults(pair.first, pair.second));
  }
}