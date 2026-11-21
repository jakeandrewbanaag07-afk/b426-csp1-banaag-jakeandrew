
package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Artist;
import com.joysistvi.recordingapp.service.ArtistService;

import java.util.List;
import java.util.Scanner;

public class ArtistView {

    private final ArtistService artistService;
    private final Scanner scanner;

    public ArtistView(ArtistService artistService, Scanner scanner) {
        this.artistService = artistService;
        this.scanner = scanner;
    }

    public void displayAllArtists() {

        List<Artist> artists = artistService.getAllActiveArtists();

        System.out.println("\n========== ACTIVE ARTISTS ==========");

        if (artists.isEmpty()) {
            System.out.println("No active artists found.");
            return;
        }

        System.out.printf("%-10s %-25s%n", "ID", "ARTIST NAME");

        for (Artist artist : artists) {
            System.out.printf("%-10d %-25s%n",
                    artist.getId(),
                    artist.getName());
        }
    }

    public void promptCreateArtist() {

        try {
            System.out.print("Enter artist name: ");
            String name = scanner.nextLine();

            artistService.createArtist(name);

            System.out.println("Artist created successfully.");

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    public void promptUpdateArtist() {

        try {
            System.out.print("Enter artist ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter new artist name: ");
            String name = scanner.nextLine();

            artistService.updateArtist(id, name);

            System.out.println("Artist updated successfully.");

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    public void promptDeleteArtist() {

        try {
            System.out.print("Enter artist ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            artistService.deleteArtist(id);

            System.out.println("Artist deleted successfully.");

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    public void promptArchiveArtist() {

        try {
            System.out.print("Enter artist ID to archive: ");
            int id = Integer.parseInt(scanner.nextLine());

            artistService.archiveArtist(id);

            System.out.println("Artist archived successfully.");

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    public void promptRestoreArtist() {

        try {
            // Get archived artists first.
            List<Artist> artists = artistService.getAllArchivedArtists();

            System.out.println("\n========== ARCHIVED ARTISTS ==========");

            // Stop if there are no archived artists.
            if (artists.isEmpty()) {
                System.out.println("No archived artists available.");
                return;
            }

            // Display archived artists.
            System.out.printf("%-10s %-25s%n", "ID", "ARTIST NAME");

            for (Artist artist : artists) {
                System.out.printf("%-10d %-25s%n",
                        artist.getId(),
                        artist.getName());
            }

            // Ask for the ID only if archived artists exist.
            System.out.print("Enter artist ID to restore: ");
            int id = Integer.parseInt(scanner.nextLine());

            artistService.restoreArtist(id);

            System.out.println("Artist restored successfully.");

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    public void promptSearchArtists() {

        try {
            System.out.print("Enter artist name to search: ");
            String keyword = scanner.nextLine();

            List<Artist> artists = artistService.searchArtists(keyword);

            System.out.println("\n========== SEARCH RESULTS ==========");

            if (artists.isEmpty()) {
                System.out.println("No matching artists found.");
                return;
            }

            for (Artist artist : artists) {
                System.out.println(
                        "ID: " + artist.getId()
                                + " | Name: " + artist.getName()
                );
            }

        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }
}
