/*
 * Copyright (C) 2013 Connor Monahan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.cmastudios.permissions;

import java.sql.*;

import org.bukkit.OfflinePlayer;

/**
 *
 * @author Connor Monahan
 */
class PlayerGroupDatabase {

    static Group getGroup(Permissions plugin, OfflinePlayer player) throws SQLException {
        try (PreparedStatement stmt = plugin.getDatabaseConnection().prepareStatement(
            "SELECT group_name FROM playergroups WHERE player = ?")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return plugin.getGroup(result.getString("group_name"));
                } else {
                    return null;
                }
            }
        }
    }

    static void setGroup(Connection conn, OfflinePlayer player, Group group, Timestamp expirationDate) throws SQLException {
        final String stmtText;
        if (PlayerGroupDatabase.exists(conn, player)) {
            stmtText = "UPDATE playergroups SET group_name = ?, expiration_date = ? WHERE player = ?";
        } else {
            stmtText = "INSERT INTO playergroups (group_name, expiration_date, player) VALUES (?, ?, ?)";
        }
        try (PreparedStatement stmt = conn.prepareStatement(stmtText)) {
            stmt.setString(1, group.getName());
            stmt.setTimestamp(2, expirationDate);
            stmt.setString(3, player.getUniqueId().toString());
            stmt.executeUpdate();
        }
    }

    static boolean exists(Connection conn, OfflinePlayer player) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT group_name FROM playergroups WHERE player = ?")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }
        }
    }

    static Timestamp getExpirationDate(Connection conn, OfflinePlayer player) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT expiration_date FROM playergroups WHERE player = ?")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet result = stmt.executeQuery()) {
                if(result.next()) {
                    return result.getTimestamp("expiration_date");
                } else {
                    return null;
                }
            }
        }
    }
}
