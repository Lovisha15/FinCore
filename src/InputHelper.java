
import java.util.Scanner;

    public class InputHelper {

        public static int readInt(Scanner sc) {

            while (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.next();
            }

            return sc.nextInt();
        }

        public static long readLong(Scanner sc) {

            while (!sc.hasNextLong()) {
                System.out.println("Please enter a valid account number.");
                sc.next();
            }

            return sc.nextLong();
        }

        public static double readDouble(Scanner sc) {

            while (!sc.hasNextDouble()) {
                System.out.println("Please enter a valid amount.");
                sc.next();
            }

            return sc.nextDouble();
        }

        public static String readString(Scanner sc) {

            while (!sc.hasNext()) {
                System.out.println("Please enter something.");
                sc.next();
            }

            return sc.next();
        }
    }


