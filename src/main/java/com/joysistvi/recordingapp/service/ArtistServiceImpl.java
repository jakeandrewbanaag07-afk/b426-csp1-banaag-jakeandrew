
package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.Artist;
import com.joysistvi.recordingapp.repository.ArtistRepository;

import java.util.List;

public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }



    private void validateId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Artist ID must be greater than zero."
            );
        }
    }

    private String validateName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Artist name cannot be empty."
            );
        }

        return name.trim();
    }

    private String validateKeyword(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        return keyword.trim();
    }



    @Override
    public List<Artist> getAllActiveArtists() {
        return artistRepository.findAllActive();
    }



    @Override
    public List<Artist> getAllArchivedArtists() {
        return artistRepository.findAllArchived();
    }



    @Override
    public void createArtist(String name) {

        name = validateName(name);

        Artist artist = new Artist(name);

        if (!artistRepository.save(artist)) {
            throw new IllegalStateException(
                    "Failed to create artist."
            );
        }
    }



    @Override
    public void updateArtist(int id, String name) {

        validateId(id);
        name = validateName(name);

        Artist artist = new Artist(id, name);

        if (!artistRepository.update(artist)) {
            throw new IllegalStateException(
                    "Artist not found or update failed."
            );
        }
    }


    @Override
    public void deleteArtist(int id) {

        validateId(id);

        if (!artistRepository.deleteById(id)) {
            throw new IllegalStateException(
                    "Artist not found or delete failed."
            );
        }
    }


    @Override
    public void archiveArtist(int id) {

        validateId(id);

        if (!artistRepository.archiveById(id)) {
            throw new IllegalStateException(
                    "Artist not found or already archived."
            );
        }
    }



    @Override
    public void restoreArtist(int id) {

        validateId(id);

        if (!artistRepository.restoreById(id)) {
            throw new IllegalStateException(
                    "Artist not found or not archived."
            );
        }
    }



    @Override
    public List<Artist> searchArtists(String keyword) {

        keyword = validateKeyword(keyword);

        return artistRepository.searchByName(keyword);
    }
}
