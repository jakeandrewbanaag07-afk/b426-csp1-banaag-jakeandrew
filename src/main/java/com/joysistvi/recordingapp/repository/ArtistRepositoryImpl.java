
package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepositoryImpl implements ArtistRepository {

    private final DbConnection dbConnection;

    public ArtistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ========================================
    // FIND ALL ACTIVE ARTISTS
    // ========================================

    @Override
    public List<Artist> findAllActive() {
        return findArtists(0, null);
    }

    // ========================================
    // FIND ALL ARCHIVED ARTISTS
    // ========================================

    @Override
    public List<Artist> findAllArchived() {
        return findArtists(1, null);
    }

    // ========================================
    // SEARCH ARTISTS
    // ========================================

    @Override
    public List<Artist> searchByName(String keyword) {
        return findArtists(0, keyword);
    }

    // ========================================
    // SHARED SELECT METHOD
    // ========================================

    private List<Artist> findArtists(int archived, String keyword) {

        List<Artist> artists = new ArrayList<>();

        String sql = "SELECT id, name, is_archived FROM artists "
                + "WHERE is_archived = ?";

        if (keyword != null) {
            sql += " AND name LIKE ?";
        }

        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, archived);

            if (keyword != null) {
                statement.setString(2, "%" + keyword + "%");
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Artist artist = new Artist(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBoolean("is_archived")
                    );

                    artists.add(artist);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving artists.", e);
        }

        return artists;
    }

    // ========================================
    // CREATE ARTIST
    // ========================================

    @Override
    public boolean save(Artist artist) {

        String sql = "INSERT INTO artists (name, is_archived) "
                + "VALUES (?, 0)";

        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artist.getName());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating artist.", e);
        }
    }

    // ========================================
    // UPDATE ARTIST
    // ========================================

    @Override
    public boolean update(Artist artist) {

        String sql = "UPDATE artists SET name = ? "
                + "WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artist.getName());
            statement.setInt(2, artist.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating artist.", e);
        }
    }

    // ========================================
    // DELETE ARTIST
    // ========================================

    @Override
    public boolean deleteById(int id) {

        String sql = "DELETE FROM artists WHERE id = ?";

        return executeUpdate(sql, id);
    }

    // ========================================
    // ARCHIVE ARTIST
    // ========================================

    @Override
    public boolean archiveById(int id) {

        String sql = "UPDATE artists SET is_archived = 1 "
                + "WHERE id = ? AND is_archived = 0";

        return executeUpdate(sql, id);
    }

    // ========================================
    // RESTORE ARTIST
    // ========================================

    @Override
    public boolean restoreById(int id) {

        String sql = "UPDATE artists SET is_archived = 0 "
                + "WHERE id = ? AND is_archived = 1";

        return executeUpdate(sql, id);
    }

    // ========================================
    // SHARED UPDATE METHOD
    // ========================================

    private boolean executeUpdate(String sql, int id) {

        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed.", e);
        }
    }
}
