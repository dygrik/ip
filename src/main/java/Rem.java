public class Rem {
    private static final String SEPARATOR =
            "____________________________________________________________";

    public static void main(String[] args) {
        String banner = " ____                      \n"
                + "|  _ \\    ___    _ __ ___  \n"
                + "| |_) |  / _ \\  | '_ ` _ \\ \n"
                + "|  _ <  |  __/  | | | | | |\n"
                + "|_| \\_\\  \\___|  |_| |_| |_|\n";

        // Greets user and exits immediately (Level-0)
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello! I'm Rem!");
        System.out.println("No more sleeping. Need help?");
        System.out.println(SEPARATOR);
        System.out.println("[Yawn] Need more sleep. Time for bed...");
        System.out.println(SEPARATOR);
    }
}
