package mask_of_loki.coretweaks.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mask_of_loki.coretweaks.CTTradeOffers;
import mask_of_loki.coretweaks.config.MainConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends AbstractTraderEntity {
	public WanderingTraderEntityMixin(EntityType<? extends AbstractTraderEntity> entityType_1, World world_1) {
		super(entityType_1, world_1);
	}
	
	@Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
	protected void onFillRecipes(CallbackInfo ci) {
		if (MainConfig.tweakTrades()) {
			CTTradeOffers tradeOffers = CTTradeOffers.Instance();
			if (tradeOffers.wandererTrades != null) {
				TraderOfferList offerList = this.getOffers();
				TradeOffers.Factory[] offers1 = tradeOffers.wandererTrades.get(1);
				TradeOffers.Factory[] offers2 = tradeOffers.wandererTrades.get(2);
				if (offers1 != null && offers2 != null) {
			        this.fillRecipesFromPool(offerList, offers1, 5);
			        int int_1 = this.random.nextInt(offers2.length);
			        TradeOffers.Factory offer = offers2[int_1];
			        TradeOffer tradeOffer_1 = offer.create(this, this.random);
			        if (tradeOffer_1 != null) {
			        	offerList.add(tradeOffer_1);
			        }
				}
			}
 			ci.cancel();
		}
	}
}
