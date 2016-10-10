package com.plunder.plunder.domain.catalog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.domain.models.TvShowDetails;
import com.plunder.plunder.domain.models.Video;
import com.uwetrottmann.tmdb2.entities.GenreResults;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.TvResultsPage;
import com.uwetrottmann.tmdb2.entities.TvShowComplete;
import com.uwetrottmann.tmdb2.entities.Videos;
import java.util.ArrayList;
import java.util.List;

public final class TmdbMapper {
  @NonNull public static List<Movie> mapMovieResultsPage(MovieResultsPage resultsPage) {
    if (resultsPage == null) {
      return new ArrayList<>();
    }

    return Stream.of(resultsPage.results).map(TmdbMapper::mapMovie).collect(Collectors.toList());
  }

  @Nullable public static Movie mapMovie(com.uwetrottmann.tmdb2.entities.Movie value) {
    if (value == null) {
      return null;
    }

    return Movie.Builder()
        .id(value.id)
        .name(value.title)
        .tagline(value.tagline)
        .releaseDate(value.release_date)
        .overview(value.overview)
        .posterPath("http://image.tmdb.org/t/p/w342/" + value.poster_path)
        .largePosterPath("http://image.tmdb.org/t/p/w500/" + value.poster_path)
        .backdropPath("http://image.tmdb.org/t/p/w1280/" + value.backdrop_path)
        .imdbId(value.imdb_id)
        .build();
  }

  @NonNull public static List<TvShow> mapTvResultsPage(TvResultsPage resultsPage) {
    if (resultsPage == null) {
      return new ArrayList<>();
    }

    return Stream.of(resultsPage.results).map(TmdbMapper::mapTvShow).collect(Collectors.toList());
  }

  @Nullable public static TvShow mapTvShow(com.uwetrottmann.tmdb2.entities.TvShow value) {
    if (value == null) {
      return null;
    }

    return TvShow.Builder()
        .id(value.id)
        .name(value.name)
        .posterPath("http://image.tmdb.org/t/p/w342/" + value.poster_path)
        .largePosterPath("http://image.tmdb.org/t/p/w500/" + value.poster_path)
        .backdropPath("http://image.tmdb.org/t/p/w1280/" + value.backdrop_path)
        .build();
  }

  @Nullable public static TvShowDetails mapTvShowComplete(TvShowComplete value) {
    if (value == null) {
      return null;
    }

    return TvShowDetails.Builder()
        .id(value.id)
        .name(value.name)
        .posterPath("http://image.tmdb.org/t/p/w342/" + value.poster_path)
        .largePosterPath("http://image.tmdb.org/t/p/w500/" + value.poster_path)
        .backdropPath("http://image.tmdb.org/t/p/original/" + value.backdrop_path)
        .seasons(value.seasons == null ? new ArrayList<>() : Stream.of(value.seasons)
            .map(TmdbMapper::mapTvSeason)
            .sorted((a, b) -> Integer.compare(b.seasonNumber(), a.seasonNumber()))
            .collect(Collectors.toList()))
        .build();
  }

  @Nullable public static TvSeason mapTvSeason(com.uwetrottmann.tmdb2.entities.TvSeason value) {
    if (value == null) {
      return null;
    }

    return TvSeason.Builder()
        .id(value.id)
        .name(value.name)
        .seasonNumber(value.season_number)
        .posterPath("http://image.tmdb.org/t/p/w342/" + value.poster_path)
        .largePosterPath("http://image.tmdb.org/t/p/w500/" + value.poster_path)
        .episodes(value.episodes == null ? new ArrayList<>() : Stream.of(value.episodes)
            .map(TmdbMapper::mapTvEpisode)
            .sorted((a, b) -> Integer.compare(a.episodeNumber(), b.episodeNumber()))
            .collect(Collectors.toList()))
        .build();
  }

  @Nullable public static TvEpisode mapTvEpisode(com.uwetrottmann.tmdb2.entities.TvEpisode value) {
    if (value == null) {
      return null;
    }

    return TvEpisode.Builder()
        .id(value.id)
        .name(value.name)
        .overview(value.overview)
        .airDate(value.air_date)
        .seasonNumber(value.season_number)
        .episodeNumber(value.episode_number)
        .stillUrl("http://image.tmdb.org/t/p/w300/" + value.still_path)
        .build();
  }

  @Nullable public static Video mapVideo(Optional<Videos.Video> value) {
    if (value == null) {
      return null;
    }

    Videos.Video source = value.get();

    if (source == null) {
      return null;
    }

    return mapVideo(source);
  }

  @Nullable public static Video mapVideo(Videos.Video value) {
    if (value == null) {
      return null;
    }

    return Video.Builder()
        .id(value.id)
        .key(value.key)
        .name(value.name)
        .site(value.site)
        .size(value.size)
        .type(value.type)
        .build();
  }

  @NonNull
  public static List<Genre> combineGenreResults(GenreResults movieResults, GenreResults tvResults) {
    if (movieResults == null || tvResults == null) {
      return new ArrayList<>();
    }

    List<Genre> genres = new ArrayList<>();
    List<com.uwetrottmann.tmdb2.entities.Genre> matchedTvGenres = new ArrayList<>();

    for (com.uwetrottmann.tmdb2.entities.Genre movieResult : movieResults.genres) {
      com.uwetrottmann.tmdb2.entities.Genre tvMatch = null;

      for (com.uwetrottmann.tmdb2.entities.Genre tvResult : tvResults.genres) {
        if (!matchedTvGenres.contains(tvResult) && movieResult.name.equals(tvResult.name)) {
          tvMatch = tvResult;
          matchedTvGenres.add(tvMatch);
          break;
        }
      }

      genres.add(mapGenres(movieResult, tvMatch));
    }

    genres.addAll(Stream.of(tvResults.genres)
        .filterNot(matchedTvGenres::contains)
        .map(result -> mapGenres(null, result))
        .collect(Collectors.toList()));

    return Stream.of(genres)
        .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.name(), b.name()))
        .collect(Collectors.toList());
  }

  @Nullable public static Genre mapGenres(com.uwetrottmann.tmdb2.entities.Genre movieValue,
      com.uwetrottmann.tmdb2.entities.Genre tvValue) {
    if (movieValue == null && tvValue == null) {
      return null;
    }

    return Genre.Builder()
        .movieId(movieValue != null ? movieValue.id : null)
        .tvId(tvValue != null ? tvValue.id : null)
        .name(movieValue != null ? movieValue.name : tvValue != null ? tvValue.name : null)
        .build();
  }
}
