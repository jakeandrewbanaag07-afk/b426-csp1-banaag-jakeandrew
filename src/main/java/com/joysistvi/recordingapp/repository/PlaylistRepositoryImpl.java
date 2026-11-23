
package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Playlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final DbConnection dbConnection;

    public PlaylistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Playlist> findAllActive() {
        String sql = "SELECT p.id, p.name, p.user_id, u.username " +
                "FROM playlists p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.is_archived = 0 " +
                "ORDER BY p.id ASC";

        return getPlaylists(sql, null);
    }

    @Override
    public List<Playlist> findAllArchived() {
        String sql = "SELECT p.id, p.name, p.user_id, u.username " +
                "FROM playlists p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.is_archived = 1 " +
                "ORDER BY p.id ASC";

        return getPlaylists(sql, null);
    }

    @Override
    public List<Playlist> findByUserId(int userId) {
        String sql = "SELECT p.id, p.name, p.user_id, u.username " +
                "FROM playlists p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.is_archived = 0 AND p.user_id = ? " +
                "ORDER BY p.id ASC";

        return getPlaylists(sql, userId);
    }

    @Override
    public boolean save(Playlist playlist) {
        String sql = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setString(1, playlist.getName());
            prep.setInt(2, playlist.getUserId());

            return prep.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Repository Error (save): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Playlist playlist) {
        String sql = "UPDATE playlists SET name = ?, user_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setString(1, playlist.getName());
            prep.setInt(2, playlist.getUserId());
            prep.setInt(3, playlist.getId());

            return prep.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Repository Error (update): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM playlists WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setInt(1, id);

            return prep.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Repository Error (deleteById): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        return updateArchiveStatus(id, true);
    }

    @Override
    public boolean restoreById(int id) {
        return updateArchiveStatus(id, false);
    }

    @Override
    public List<Playlist> searchByName(String keyword) {
        String sql = "SELECT p.id, p.name, p.user_id, u.username " +
                "FROM playlists p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.is_archived = 0 AND p.name LIKE ? " +
                "ORDER BY p.id ASC";

        List<Playlist> playlists = new ArrayList<>();

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setString(1, "%" + keyword + "%");

            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapPlaylist(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Repository Error (searchByName): " + e.getMessage());
        }

        return playlists;
    }

    private List<Playlist> getPlaylists(String sql, Integer userId) {
        List<Playlist> playlists = new ArrayList<>();

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            if (userId != null) {
                prep.setInt(1, userId);
            }

            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapPlaylist(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Repository Error (getPlaylists): " + e.getMessage());
        }

        return playlists;
    }

    private boolean updateArchiveStatus(int id, boolean archived) {
        String sql = "UPDATE playlists SET is_archived = ? WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setBoolean(1, archived);
            prep.setInt(2, id);

            return prep.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Repository Error (updateArchiveStatus): " + e.getMessage());
            return false;
        }
    }

    private Playlist mapPlaylist(ResultSet rs) throws SQLException {
        return new Playlist(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("user_id"),
                rs.getString("username")
        );
    }
}
