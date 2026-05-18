package com.joplayx.client.mixin;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantMenu.class)
public interface MerchantScreenHandlerAccessor {

    @Accessor("offers")
    MerchantOffers getOffers();
}
