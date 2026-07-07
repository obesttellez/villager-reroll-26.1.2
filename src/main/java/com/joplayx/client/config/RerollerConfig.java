package com.joplayx.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joplayx.VillagerReroll;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class RerollerConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("villager-reroll.json");

    private static Config instance = new Config();

    public static Config get() { return instance; }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(reader, Config.class);
                if (instance == null) instance = new Config();
                VillagerReroll.LOGGER.info("[Reroller] Config loaded.");
            } catch (IOException e) {
                VillagerReroll.LOGGER.error("[Reroller] Failed to load config.", e);
                instance = new Config();
            }
        } else {
            instance = new Config();
            save();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(instance, writer);
            VillagerReroll.LOGGER.info("[Reroller] Config saved.");
        } catch (IOException e) {
            VillagerReroll.LOGGER.error("[Reroller] Failed to save config.", e);
        }
    }

    public static class Config {
        // Comma-separated list of namespaced enchantment IDs, e.g.
        // "minecraft:mending, minecraft:sharpness, minecraft:efficiency"
        // The reroller stops as soon as ANY of these is offered.
        public String targetEnchantments = "";
        public int minLevel = 1;
        public int maxEmeraldCost = 64;

        // Lectern position stored as separate ints so YACL can edit them directly
        // Integer.MIN_VALUE means "not set"
        public int lecternX = Integer.MIN_VALUE;
        public int lecternY = Integer.MIN_VALUE;
        public int lecternZ = Integer.MIN_VALUE;

        // Timing
        public int closeDelayTicks = 20;
        public int retryDelayTicks = 40;

        // Display
        public boolean hudEnabled = true;

        /**
         * Parses {@link #targetEnchantments} into a clean list of enchantment IDs,
         * trimming whitespace and dropping empty entries. Returns an empty list
         * if nothing is configured.
         */
        public List<String> targetEnchantmentList() {
            if (targetEnchantments == null || targetEnchantments.isBlank()) {
                return List.of();
            }
            return Arrays.stream(targetEnchantments.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        /**
         * Returns the lectern BlockPos, or null if not set.
         */
        public BlockPos lecternPos() {
            if (lecternX == Integer.MIN_VALUE) return null;
            return new BlockPos(lecternX, lecternY, lecternZ);
        }

        /**
         * Returns true if the lectern position has been set.
         */
        public boolean hasLecternPos() {
            return lecternX != Integer.MIN_VALUE;
        }

        /**
         * Sets the lectern position from a BlockPos.
         */
        public void setLecternPos(BlockPos pos) {
            lecternX = pos.getX();
            lecternY = pos.getY();
            lecternZ = pos.getZ();
        }

        /**
         * Clears the lectern position.
         */
        public void clearLecternPos() {
            lecternX = Integer.MIN_VALUE;
            lecternY = Integer.MIN_VALUE;
            lecternZ = Integer.MIN_VALUE;
        }

        /**
         * Returns a human-readable string of the lectern position.
         */
        public String lecternPosString() {
            if (!hasLecternPos()) return "Not set";
            return "X: " + lecternX + "  Y: " + lecternY + "  Z: " + lecternZ;
        }
    }
}
