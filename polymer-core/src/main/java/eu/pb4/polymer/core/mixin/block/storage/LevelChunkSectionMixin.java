package eu.pb4.polymer.core.mixin.block.storage;

import eu.pb4.polymer.core.impl.interfaces.PolymerBlockPosStorage;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunkSection;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements PolymerBlockPosStorage {
    @Unique
    private @Nullable ShortSet polymer$blocks;
    @Unique
    private @Nullable ShortSet polymer$lights;
    @Unique
    private boolean polymer$requireLightUpdate;

    @Unique
    private ShortSet polymer$getOrCreateBlocks() {
        var blocks = this.polymer$blocks;
        if (blocks == null) {
            blocks = new ShortOpenHashSet();
            this.polymer$blocks = blocks;
        }
        return blocks;
    }

    @Unique
    private ShortSet polymer$getOrCreateLights() {
        var lights = this.polymer$lights;
        if (lights == null) {
            lights = new ShortOpenHashSet();
            this.polymer$lights = lights;
        }
        return lights;
    }

    @Override
    public @Nullable ShortSet polymer$getBackendSet() {
        return this.polymer$blocks;
    }

    @Override
    public Iterator<BlockPos.MutableBlockPos> polymer$iterator(SectionPos sectionPos) {
        var blocks = this.polymer$blocks;
        if (blocks == null) {
            return Collections.emptyIterator();
        }

        var blockPos = new BlockPos.MutableBlockPos();
        var iterator = blocks.iterator();

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public BlockPos.MutableBlockPos next() {
                var value = iterator.nextShort();

                return blockPos.set(sectionPos.relativeToBlockX(value), sectionPos.relativeToBlockY(value), sectionPos.relativeToBlockZ(value));
            }
        };
    }

    @Override
    public @Nullable Iterator<BlockPos.MutableBlockPos> polymer$iterator() {
        return null;
    }

    @Override
    public void polymer$setSynced(int x, int y, int z, boolean lightSource) {
        var i = PolymerBlockPosStorage.pack(x, y, z);
        this.polymer$getOrCreateBlocks().add(i);
        if (lightSource) {
            this.polymer$getOrCreateLights().add(i);
        }
    }

    @Override
    public void polymer$removeSynced(int x, int y, int z) {
        var i = PolymerBlockPosStorage.pack(x, y, z);
        var blocks = this.polymer$blocks;
        if (blocks != null) {
            blocks.remove(i);
        }

        var lights = this.polymer$lights;
        if (lights != null && lights.remove(i)) {
            this.polymer$requireLightUpdate = true;
        }
    }

    @Override
    public boolean polymer$isSynced(int x, int y, int z) {
        return this.polymer$blocks != null && this.polymer$blocks.contains(PolymerBlockPosStorage.pack(x, y, z));
    }

    @Override
    public boolean polymer$hasAny() {
        return this.polymer$blocks != null && !this.polymer$blocks.isEmpty();
    }

    @Override
    public boolean polymer$hasLights() {
        return this.polymer$lights != null && !this.polymer$lights.isEmpty();
    }

    @Override
    public boolean polymer$requireLights() {
        return this.polymer$requireLightUpdate || polymer$hasLights();
    }

    @Override
    public void polymer$setRequireLights(boolean value) {
        this.polymer$requireLightUpdate = value;
    }
}
