import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Basic Calculator");
        while (true) {
            System.out.println();
            System.out.println("Choose operation:");
            System.out.println("1) Add");
            System.out.println("2) Subtract");
            System.out.println("3) Multiply");
            System.out.println("4) Divide");
            System.out.println("5) Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("5")) {
                System.out.println("Goodbye!");
                break;
            }

            if (!choice.matches("[1-4]")) {
                System.out.println("Invalid choice, try again.");
                continue;
            }

            double a = readDouble("Enter first number: ");
            double b = readDouble("Enter second number: ");

            try {
                double result = 0;
                switch (choice) {
                    case "1": result = Calculator.add(a, b); break;
                    case "2": result = Calculator.subtract(a, b); break;
                    case "3": result = Calculator.multiply(a, b); break;
                    case "4": result = Calculator.divide(a, b); break;
                }
                System.out.println("Result: " + result);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please try again.");
            }
        }
    }
}
