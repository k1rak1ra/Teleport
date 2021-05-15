package net.k1ra.Teleport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Database {
    public static Connection conn;
    public static Consumer<Exception> error_handler = Throwable::printStackTrace;

    static class Item {
        int id;
        String name;
        String command;
        boolean image;
        boolean exit;
        String color;

        public Item(int id, String name, String command, boolean image, boolean exit, String color) {
            this.id = id;
            this.name = name;
            this.command = command;
            this.image = image;
            this.exit = exit;
            this.color = color;
        }
    }

    public static void create_tables() {
        try {
            PreparedStatement smt = conn.prepareStatement("CREATE TABLE items (\n" +
                    "\tid INTEGER PRIMARY KEY,\n" +
                    "\tname TEXT NOT NULL,\n" +
                    "\tcommand TEXT NOT NULL,\n" +
                    "\timage INTEGER NOT NULL,\n" +
                    "\texit INTEGER NOT NULL,\n" +
                    "\tcolor TEXT NOT NULL\n" +
                    ");");
            smt.executeUpdate();
            smt.close();

            smt = conn.prepareStatement("INSERT INTO items (id, name, command, image, exit, color)  \n" +
                    "VALUES (0, \"\", \"\", 1, 1, \"\");");
            smt.executeUpdate();
            smt.close();

        } catch (Exception e) {
            error_handler.accept(e);
        }
    }

    public static int insert_connection(Item item) {
        try {
            PreparedStatement chk_smt = conn.prepareStatement("SELECT * FROM items ORDER BY id DESC");
            ResultSet rs = chk_smt.executeQuery();
            rs.next();
            item.id = rs.getInt("id");

            PreparedStatement smt = conn.prepareStatement("INSERT INTO items (id, name, command, image, exit, color)  \n" +
                    "VALUES (?, ?, ?, ?, ?, ?);");
            smt.setInt(1, ++item.id);
            smt.setString(2, item.name);
            smt.setString(3, item.command);
            smt.setInt(4, item.image?1:0);
            smt.setInt(5, item.exit?1:0);
            smt.setString(6, item.color);
            smt.executeUpdate();
            smt.close();

            return item.id;
        } catch (Exception e) {
            error_handler.accept(e);
        }
        return 0;
    }

    public static List<Item> get_connections() {
        List<Item> list = new ArrayList<>();

        try {
            PreparedStatement smt = conn.prepareStatement("SELECT * FROM items WHERE id > 0");
            ResultSet rs = smt.executeQuery();
            while (rs.next()) {
                list.add(new Item(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("command"),
                        rs.getInt("image")==1,
                        rs.getInt("exit")==1,
                        rs.getString("color")));
            }
        } catch (Exception e) {
            error_handler.accept(e);
        }

        return list;
    }

    public static void update_connection(Item item) {
        try {
            PreparedStatement smt = conn.prepareStatement("UPDATE items SET name = ?, command = ?, image = ?, exit = ?, color = ?  WHERE id = ?;");
            smt.setString(1, item.name);
            smt.setString(2, item.command);
            smt.setInt(3, item.image?1:0);
            smt.setInt(4, item.exit?1:0);
            smt.setString(5, item.color);
            smt.setInt(6, item.id);
            smt.executeUpdate();
            smt.close();
        } catch (Exception e) {
            error_handler.accept(e);
        }
    }

    public static void delete_connection(Item i) {
        try {
            PreparedStatement smt = conn.prepareStatement("DELETE FROM items WHERE id = ?;");
            smt.setInt(1, i.id);
            smt.executeUpdate();
            smt.close();

        } catch (Exception e) {
            error_handler.accept(e);
        }
    }
}
