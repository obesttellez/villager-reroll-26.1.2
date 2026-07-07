package com.joplayx.client.util;

import com.joplayx.client.config.RerollerConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.List;

/**
 * Trade reading and evaluation utilities.
 *
 * Reads from OffersStore which is populated by MerchantOffersPacketMixin
 * when the server sends ClientboundMerchantOffersPacket.
 *
 * This approach confirmed from working mod (VillagerRoller.java line 695-696):
 *   if (!(event.packet instanceof ClientboundMerchantOffersPacket p)) return;
 *   mc.executeIfPossible(() -> triggerTradeCheck(p.getOffers()));
 */
public class TradeUtil {

    public record TradeResult(boolean found, String description) {}

    public static TradeResult checkTrades(RerollerConfig.Config cfg) {
        MerchantOffers offers = OffersStore.get();

        if (offers == null || offers.isEmpty()) {
            return new TradeResult(false, "No trades available");
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return new TradeResult(false, "No level");

        // Registry lookup — confirmed from working mod line 707
        var reg = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        List<String> targets = cfg.targetEnchantmentList();
        if (targets.isEmpty()) {
            return new TradeResult(false, "No target enchantments set");
        }

        String bestSeen = "Nothing";

        for (MerchantOffer offer : offers) {
            ItemStack result = offer.getResult();
            if (!result.is(Items.ENCHANTED_BOOK)) continue;

            // EnchantmentHelper.getEnchantmentsForCrafting — confirmed from working mod line 686
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(result).entrySet()) {
                Holder<Enchantment> enchHolder = entry.getKey();
                int level = entry.getIntValue();

                // Registry key string — confirmed from working mod line 708
                String enchantmentId = reg.getKey(enchHolder.value()).toString();

                // getBaseCostA() — confirmed from working mod line 731
                int cost = offer.getBaseCostA().getCount();

                String desc = enchantmentId + " " + toRoman(level) + " for " + cost + " emeralds";
                bestSeen = desc;

                boolean matchesAnyTarget = targets.stream()
                        .anyMatch(target -> target.equalsIgnoreCase(enchantmentId));

                if (matchesAnyTarget
                        && level >= cfg.minLevel
                        && cost <= cfg.maxEmeraldCost) {
                    return new TradeResult(true, desc);
                }
            }
        }

        return new TradeResult(false, bestSeen);
    }

    private static String toRoman(int level) {
        return switch (level) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III";
            case 4 -> "IV"; case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}
