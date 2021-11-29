package db_init;

import db_setup.FlywayInitializer;
import org.jetbrains.annotations.NotNull;

public class Main {

    public static void main(@NotNull String[] args) {
        try {
            FlywayInitializer.initDb();
        } catch (Throwable e) {
            System.out.println(e);
        }
    }

}
