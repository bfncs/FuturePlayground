package us.byteb.app;

import java.util.concurrent.CompletableFuture;

import static java.text.MessageFormat.format;

public class FuturePlayground {
    private static final int DELAY_MIN = 1000;
    private static final int DELAY_MAY = 5000;

    public static void main(String[] args) {
        executeSerialExample();
        executeParallelExample();
    }

    private static void executeSerialExample() {
        System.out.println("### Start serial example");

        try {
            final String result = getFuture("SerialHello", getRandomDelay())
                    .thenCompose((helloResult) -> getFuture("SerialWorld", getRandomDelay())
                            .thenApplyAsync(worldResult -> format("{0} {1}", helloResult, worldResult))
                    )
                    .get();
            System.out.printf("Result %s%n", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("### End serial example");
    }

    private static void executeParallelExample() {
        System.out.println("### Start parallel example");
        CompletableFuture<String> helloFuture = getFuture("ParallelHello", getRandomDelay());
        CompletableFuture<String> worldFuture = getFuture("ParallelWorld", getRandomDelay());
        try {
            CompletableFuture.allOf(helloFuture, worldFuture)
                    .thenRunAsync(() -> {
                        try {
                            System.out.printf("Result: %s %s\n", helloFuture.get(), worldFuture.get());
                        } catch (Exception e) {
                            System.out.println("Unable to complete future");
                            e.printStackTrace();
                        }
                    })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("### End parallel example");
    }

    private static CompletableFuture<String> getFuture(final String result, long delay) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.printf("Resolving %s in %dms %n", result, delay);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.printf("Unable to resolve %s%n", result);
                return String.format("Not%s", result);
            }

            System.out.printf("Resolved %s%n", result);
            return result;
        });
    }

    private static long getRandomDelay() {
        return DELAY_MIN + (int) (Math.random() * ((DELAY_MAY - DELAY_MIN) + 1));
    }
}
