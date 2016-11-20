package com.plunder.plunder.torrents;

import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import timber.log.Timber;

public class TorrentWebServer extends NanoHTTPD {
  private TorrentClient torrentClient;

  public TorrentWebServer(TorrentClient torrentClient) {
    super(9724);
    this.torrentClient = torrentClient;
  }

  public void start() throws IOException {
    start(Integer.MAX_VALUE, false);
  }

  public String getAddress() {
    return String.format(Locale.getDefault(), "http://localhost:%d/%s", getListeningPort(),
        torrentClient.getFile() != null ? torrentClient.getFile().getName() : "unknown.mp4");
  }

  @Override public Response serve(IHTTPSession session) {
    File file = torrentClient.getFile();

    if (file == null) {
      return newFixedLengthResponse(Response.Status.NOT_FOUND, "", "");
    }

    Map<String, String> header = session.getHeaders();
    String mime = getMimeTypeForFile(file.getAbsolutePath());

    try {
      Response res;
      String etag = Integer.toHexString((file.getAbsolutePath() + file.length()).hashCode());

      long startFrom = 0;
      long endAt = -1;
      String range = header.get("range");

      if (range != null) {
        if (range.startsWith("bytes=")) {
          range = range.substring("bytes=".length());
          int minus = range.indexOf('-');
          try {
            if (minus > 0) {
              startFrom = Long.parseLong(range.substring(0, minus));
              endAt = Long.parseLong(range.substring(minus + 1));
            }
          } catch (NumberFormatException ignored) {
          }
        }
      }

      // get if-range header. If present, it must match etag or else we
      // should ignore the range request
      String ifRange = header.get("if-range");
      boolean headerIfRangeMissingOrMatching = (ifRange == null || etag.equals(ifRange));

      String ifNoneMatch = header.get("if-none-match");
      boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*".equals(ifNoneMatch) || ifNoneMatch.equals(etag));

      // Change return code and add Content-Range header when skipping is
      // requested
      long fileLen = file.length();

      if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
        // range request that matches current etag
        // and the startFrom of the range is satisfiable
        if (headerIfNoneMatchPresentAndMatching) {
          // range request that matches current etag
          // and the startFrom of the range is satisfiable
          // would return range from file
          // respond with not-modified
          res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
          res.addHeader("ETag", etag);
        } else {
          if (endAt < 0) {
            endAt = fileLen - 1;
          }
          long newLen = endAt - startFrom + 1;
          if (newLen < 0) {
            newLen = 0;
          }

          torrentClient.setDownloadOffset(startFrom);

          FileInputStream fis = new FileInputStream(file);
          TorrentFileStream tis = new TorrentFileStream(torrentClient, fis);
          tis.skip(startFrom);

          res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, tis, newLen);
          res.addHeader("Accept-Ranges", "bytes");
          res.addHeader("Content-Length", "" + newLen);
          res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
          res.addHeader("ETag", etag);
        }
      } else {
        if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
          // return the size of the file
          // 4xx responses are not trumped by if-none-match
          res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
          res.addHeader("Content-Range", "bytes */" + fileLen);
          res.addHeader("ETag", etag);
        } else if (range == null && headerIfNoneMatchPresentAndMatching) {
          // full-file-fetch request
          // would return entire file
          // respond with not-modified
          res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
          res.addHeader("ETag", etag);
        } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
          // range request that doesn't match current etag
          // would return entire (different) file
          // respond with not-modified

          res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
          res.addHeader("ETag", etag);
        } else {
          torrentClient.setDownloadOffset(0);

          FileInputStream fis = new FileInputStream(file);
          TorrentFileStream tis = new TorrentFileStream(torrentClient, fis);

          res = newFixedLengthResponse(Response.Status.OK, mime, tis, (int) file.length());
          res.addHeader("Accept-Ranges", "bytes");
          res.addHeader("Content-Length", "" + fileLen);
          res.addHeader("ETag", etag);
        }
      }

      return res;
    } catch (IOException ioe) {
      return newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Forbidden");
    }
  }
}
