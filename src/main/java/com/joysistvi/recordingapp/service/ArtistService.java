
package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.Artist;
import java.util.List;

public interface ArtistService {

    List<Artist> getAllActiveArtists();

    List<Artist> getAllArchivedArtists();

    void createArtist(String name);

    void updateArtist(int id, String name);

    void deleteArtist(int id);

    void archiveArtist(int id);

    void restoreArtist(int id);

    List<Artist> searchArtists(String keyword);
}
