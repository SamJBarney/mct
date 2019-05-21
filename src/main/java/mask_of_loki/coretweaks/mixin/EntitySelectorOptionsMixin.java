package mask_of_loki.coretweaks.mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import java.lang.reflect.Method;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mask_of_loki.coretweaks.helper.EntitySelectorReaderHelper;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.NumberRange;

@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {
	private static final SimpleCommandExceptionType MCT_INVALID_LIGHT_LEVEL = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.light.invalid", new Object[0]));
	@Shadow
	static void putOption(String string_1, EntitySelectorOptions.SelectorHandler entitySelectorOptions$SelectorHandler_1, Predicate<EntitySelectorReader> predicate_1, Component component_1) {}
	
	@Inject(method="register", at = @At(value="JUMP", ordinal = 0))
	private static void onRegister1(CallbackInfo ci) {
		putOption("light", reader -> {
			int cursor = reader.getReader().getCursor();
			NumberRange.IntRange range = NumberRange.IntRange.parse(reader.getReader());
			if ((range.getMin() == null || (Integer)range.getMin() > -1) && (range.getMax() == null || (Integer)range.getMax() > -1 && (Integer)range.getMax() < 16)) {
				EntitySelectorReaderHelper.setLightLevel(reader, range);
			} else {
				reader.getReader().setCursor(cursor);
               throw MCT_INVALID_LIGHT_LEVEL.createWithContext(reader.getReader());
			}
		}, reader -> {
			return EntitySelectorReaderHelper.getLightLevel(reader).isDummy();
		}, new TranslatableComponent("argument.entity.options.light.description", new Object[0]));
	}
}
