package net.k1ra.Teleport;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.SQLException;

public class Utils {
    public static String get_local_storage_dir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return System.getenv("APPDATA") + "/Teleport/";
        else if (os.contains("mac"))
            return System.getProperty("user.home") + "/Library/Application Support/Teleport/";
        else if (os.contains("nux"))
            return System.getProperty("user.home") + "/.Teleport/";
        else
            return System.getProperty("user.dir") + "/.Teleport/";
    }

    public static String get_sqlite_db() {
        return get_local_storage_dir()+"db.db";
    }

    public static void connect_db() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setPoolName("Teleport");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:"+get_sqlite_db());
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        Database.conn = new HikariDataSource(config).getConnection();
    }

    public static void handle_error(String trace) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(trace);
        alert.showAndWait();
    }

    public static String get_extension(File in) {
        return in.getAbsolutePath().split("\\.")[in.getAbsolutePath().split("\\.").length-1];
    }

    public static void copy_image(File in, int id) {
        copy_image(in, new File(get_local_storage_dir()+id+"."+get_extension(in)));
    }

    public static void copy_image(File in, File out) {
        try {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(in);
                os = new FileOutputStream(out);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                is.close();
                os.close();
            }
        } catch (Exception e) {
            handle_error(e.toString());
        }
    }
}
