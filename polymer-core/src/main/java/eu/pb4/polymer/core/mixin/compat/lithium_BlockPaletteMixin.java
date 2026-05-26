package eu.pb4.polymer.core.mixin.compat;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.impl.client.InternalClientRegistry;
import net.caffeinemc.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.IdMap;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.PacketContext;

@Pseudo
@Mixin(value = LithiumHashPalette.class, priority = 500, remap = false)
public class lithium_BlockPaletteMixin {
    @ModifyArg(method = {"write(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/core/IdMap;)V", "getSerializedSize(Lnet/minecraft/core/IdMap;)I" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMap;getId(Ljava/lang/Object;)I", remap = false), remap = false, require = 0)
    public Object polymer$getIdRedirect(Object object) {
        if (object instanceof BlockState blockState) {
            return PolymerBlockUtils.getPolymerBlockState(blockState, PacketContext.get());
        }
        return object;
    }

    @Environment(EnvType.CLIENT)
    @Redirect(method = "read(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/core/IdMap;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMap;byIdOrThrow(I)Ljava/lang/Object;", remap = false), remap = false, require = 0)
    private Object polymer$replaceState(IdMap<?> instance, int index) {
        return InternalClientRegistry.decodeRegistry(instance, index);
    }
}
