import java.io.*;
import java.util.Scanner;

public class Movie {

    // Seat class represents an individual seat in the theater
    static class Seat {
        String row;
        int column;
        boolean isReserved;

        // Constructor to initialize a seat
        public Seat(String row, int column) {
            this.row = row;
            this.column = column;
            this.isReserved = false;
        }
    }

    // Movie class represents a movie and its seating arrangement
    static class MovieReservationSystem {
        String title;
        Seat[][] seats;
        int seatPrice;

        // Constructor to initialize the movie with its title and price
        public MovieReservationSystem(String title, int price) {
            this.title = title;
            this.seatPrice = price;
            seats = new Seat[26][32]; // 26 rows and 32 columns
            for (int i = 0; i < 26; i++) {
                for (int j = 0; j < 32; j++) {
                    seats[i][j] = new Seat(Character.toString((char) ('A' + i)), j + 1);
                }
            }
        }

        // Load seat reservations from a file
        public void loadSeatsFromFile() {
            File file = new File(title + ".txt");
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        String row = parts[0];
                        int column = Integer.parseInt(parts[1]);
                        seats[row.charAt(0) - 'A'][column - 1].isReserved = true;
                    }
                } catch (IOException e) {
                    System.out.println("Error loading reserved seats for " + title);
                }
            }
        }

        // Save the current seat reservations to a file
        public void saveSeatsToFile() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(title + ".txt"))) {
                for (int i = 0; i < 26; i++) {
                    for (int j = 0; j < 32; j++) {
                        if (seats[i][j].isReserved) {
                            bw.write((char) ('A' + i) + "," + (j + 1) + "\n");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error saving reserved seats for " + title);
            }
        }

        // Reserve a range of seats
        public boolean reserveSeats(String row, int startColumn, int endColumn) {
            int rowIndex = row.charAt(0) - 'A'; // Convert row letter to index
            if (rowIndex < 0 || rowIndex >= 26 || startColumn < 1 || endColumn > 32 || startColumn > endColumn) {
                return false; // Invalid input
            }

            // Check if all seats in the range are available
            for (int col = startColumn - 1; col < endColumn; col++) {
                if (seats[rowIndex][col].isReserved) {
                    return false; // Seat already reserved
                }
            }

            // Reserve the seats
            for (int col = startColumn - 1; col < endColumn; col++) {
                seats[rowIndex][col].isReserved = true;
            }

            return true; // Successful reservation
        }

        // Display the seating chart
        public void displaySeats() {
            System.out.println("\nSeating Chart for " + title + ":");
            System.out.print("   ");
            for (int col = 1; col <= 32; col++) {
                System.out.printf("%2d ", col); // Print column numbers
            }
            System.out.println();

            // Print rows with seat status
            for (int i = 0; i < 26; i++) {
                System.out.printf("%2s ", (char) ('A' + i)); // Row labels
                for (int j = 0; j < 32; j++) {
                    System.out.print(seats[i][j].isReserved ? " X " : " O "); // Reserved/Available
                }
                System.out.println();
            }
        }
    }

    // Main method for the movie reservation system
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create a list of movies with IDs
        MovieReservationSystem movie1 = new MovieReservationSystem("Mufasa", 300);
        MovieReservationSystem movie2 = new MovieReservationSystem("Pushpa-2", 330);
        MovieReservationSystem movie3 = new MovieReservationSystem("Ganguva", 200);

        // Load reserved seats for each movie
        movie1.loadSeatsFromFile();
        movie2.loadSeatsFromFile();
        movie3.loadSeatsFromFile();

        // Movie selection
        while (true) {
            System.out.println("\nWelcome to the Movie Reservation System\n");
            System.out.println("Available Movies:");

            // Display movie choices
            System.out.println("1. Mufasa");
            System.out.println("2. Pushpa-2");
            System.out.println("3. Ganguva");
            System.out.println("0. Exit");

            System.out.print("\nEnter the movie number (1-3) to reserve tickets or 0 to exit: ");
            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Thank you for using the Movie Reservation System. Goodbye!");
                break; // Exit the application
            }

            MovieReservationSystem selectedMovie = null;
            if (choice == 1) {
                selectedMovie = movie1;
            } else if (choice == 2) {
                selectedMovie = movie2;
            } else if (choice == 3) {
                selectedMovie = movie3;
            } else {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            // Show seating chart for the selected movie
            selectedMovie.displaySeats();

            // Input user details
            System.out.print("Enter your name: ");
            scanner.nextLine(); // Consume the newline
            String name = scanner.nextLine();

            System.out.print("Enter the row (A-Z) for the seats: ");
            String row = scanner.next().toUpperCase();
            System.out.print("Enter the starting column (1-32): ");
            int startColumn = scanner.nextInt();
            System.out.print("Enter the ending column (1-32): ");
            int endColumn = scanner.nextInt();

            // Try to reserve the seats
            boolean success = selectedMovie.reserveSeats(row, startColumn, endColumn);
            if (success) {
                int numberOfSeats = endColumn - startColumn + 1;
                int totalAmount = numberOfSeats * selectedMovie.seatPrice;
                double gst = totalAmount * 0.18; // Calculate GST
                double finalAmount = totalAmount + gst; // Total including GST

                // Print confirmation details
                System.out.printf("Successfully reserved seats %s%d-%d for '%s'.%n", row, startColumn, endColumn, selectedMovie.title);
                System.out.printf("Total Amount: %d%n", totalAmount);
                System.out.printf("GST (18%%): %.2f%n", gst);
                System.out.printf("Final Amount (Including GST): %.2f%n", finalAmount);

                // Save the updated reservations to file
                selectedMovie.saveSeatsToFile();
            } else {
                System.out.printf("Reservation failed. Some or all seats in the range %s%d-%d are already reserved.%n", row, startColumn, endColumn);
            }
        }

        scanner.close(); // Close the scanner resource
    }
}
