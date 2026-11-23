package com.joysistvi.recordingapp;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.repository.*;
import com.joysistvi.recordingapp.service.*;
import com.joysistvi.recordingapp.view.*;

import java.util.Scanner;

// Entry Point
public class App {

    private final DbConnection dbConnection = new DbConnection();
    private final Scanner scanner = new Scanner(System.in);

    // Wiring (Repository -> Service -> View)
    private final ArtistRepository artistRepository = new ArtistRepositoryImpl(dbConnection);
    private final ArtistService artistService = new ArtistServiceImpl(artistRepository);
    private final ArtistView artistView = new ArtistView(artistService, scanner);

    private final AlbumRepository albumRepository = new AlbumRepositoryImpl(dbConnection);
    private final AlbumService albumService = new AlbumServiceImpl(albumRepository);
    private final AlbumView albumView = new AlbumView(albumService, scanner);

    private final UserRepository userRepository = new UserRepositoryImpl(dbConnection);
    private final UserService userService = new UserServiceImpl(userRepository);
    private final UserView userView = new UserView(userService, scanner);

    private final SongRepository songRepository = new SongRepositoryImpl(dbConnection);
    private final SongService songService = new SongServiceImpl(songRepository);
    private final SongView songView = new SongView(songService, scanner);

    private final PlaylistRepository playlistRepository = new PlaylistRepositoryImpl(dbConnection);
    private final PlaylistService playlistService = new PlaylistServiceImpl(playlistRepository);
    private final PlaylistView playlistView = new PlaylistView(playlistService, scanner);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        while (true) {
            User currentUser = showAuthMenuAndAuthenticate();
            if (currentUser == null) {
                System.out.println("\nExiting Recording Studio App. Goodbye!");
                break;
            }

            // Any logged-in user can enter the dashboard
            System.out.println("\nWelcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")!");
            dashboard();
            System.out.println("\nLogged out " + currentUser.getUsername() + ".");
        }
    }

    // Main menu shown after login. Stays open until the user chooses 0 (Back / Logout).
    private void dashboard() {
        while (true) {
            System.out.println("\n=== DASHBOARD ===");
            System.out.println("1. Song Management");
            System.out.println("2. Artist Management");
            System.out.println("3. Album Management");
            System.out.println("4. Users Management");
            System.out.println("5. Playlist Management");
            System.out.println("0. Back");

            int choice = promptForInt("Choice: ");

            switch (choice) {
                case 1 -> songView.handleMenu();
                case 2 -> artistView.handleMenu();
                case 3 -> albumView.handleMenu();
                case 4 -> userView.handleMenu();
                case 5 -> playlistView.handleMenu();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private User showAuthMenuAndAuthenticate() {
        while (true) {
            System.out.println("\n=== RECORDING STUDIO APP ===");
            System.out.println("1. Login");
            System.out.println("2. Register Account");
            System.out.println("0. Exit");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> {
                    User loggedInUser = userView.promptLogin();
                    if (loggedInUser != null) {
                        return loggedInUser;
                    }
                }
                case 2 -> userView.promptCreateUser();
                case 0 -> {
                    return null;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private int promptForInt(String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}


/*
===============================================================================
                             Build &  Wiring Order (Bottom-Up)
===============================================================================

    Model
    │
    ▼
    Repository
     │
     ▼
    Service
     │
     ▼
    View
     │
     ▼
    Main Application (App)

    /*
===============================================================================
                    DATA FLOW (Top-Down)
===============================================================================

REQUEST FLOW (Save / Insert / Update / Delete)

User/App
  ↓
View
  ↓
Service
  ↓
Repository (DAO)
  ↓
Database


RESPONSE FLOW (Retrieve / Search)

Database
  ↓
Repository (DAO)
  ↓
Service
  ↓
View
  ↓
User




Dependency Wiring:
Repository --> Service --> View --> main()

Example:
ArtistRepositoryImpl
        │
        ▼
ArtistServiceImpl
        │
        ▼
ArtistView
        │
        ▼
App.main()

Dependency Flow
View → Service → Repository → Database

Data Flow
User → View → Service → Repository → Database
Database → Repository → Service → View → User
 */
