package mask_of_loki.coretweaks.mixin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.util.NumberRange;

@Mixin(EntitySelectorReader.class)
public class EntitySelectorReaderMixin {
	private NumberRange.IntRange mct_lightLevel;
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	private void onConstructed(CallbackInfo ci) {
		this.mct_lightLevel = NumberRange.IntRange.ANY;
	}
	
	public void mct_setLightLevel(NumberRange.IntRange range) {
		this.mct_lightLevel = range;
	}
	
	public NumberRange.IntRange mct_getLightLevel() {
		return this.mct_lightLevel;
	}
	
	@Inject(method = "build", at = @At("RETURN"))
	private void onBuild(CallbackInfoReturnable<EntitySelector> cir) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = EntitySelector.class.getMethod("mct_setLightLevel", NumberRange.IntRange.class);
		method.invoke(cir.getReturnValue(), this.mct_lightLevel);
	}
}
