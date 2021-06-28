package rmc.mixins.astral_sorcery_guard.inject;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rmc.libs.event_factory.EventFactory;
import rmc.libs.tile_ownership.TileOwnership;
import rmc.mixins.astral_sorcery_guard.AstralSorceryGuard;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = TileLens.class)
public abstract class TileLensMixin {

    @Redirect(method = "Lhellfirepvp/astralsorcery/common/tile/TileLens;doColorEffects()V",
              remap = false,
              at = @At(value = "INVOKE",
                       target = "Lhellfirepvp/astralsorcery/common/item/lens/LensColorType;blockInBeam(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lhellfirepvp/astralsorcery/common/util/PartialEffectExecutor;)V"))
    private void guardBlockInBeam(LensColorType colorType, World world, BlockPos pos, BlockState state, PartialEffectExecutor executor) {
        if (EventFactory.testBlockBreak(EventFactory.convertFake(world, TileOwnership.loadOwner(((TileLens)(Object) this).getTileData())), world, pos, AstralSorceryGuard.BEAM_FAKE)) {
            colorType.blockInBeam(world, pos, state, executor);
        }
    }

    @Inject(method = "Lhellfirepvp/astralsorcery/common/tile/TileLens;doColorEffects()V",
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE_ASSIGN",
                     target = "Lhellfirepvp/astralsorcery/common/util/RaytraceAssist;collectedEntities(Lnet/minecraft/world/World;)Ljava/util/List;"))
    private void guardEntitiesInBeam(CallbackInfo mixin, World world, float effectMultiplier, List<BlockPos> linked, Vector3 thisVec, Iterator<BlockPos> var5, BlockPos linkedTo, PartialEffectExecutor exec, Vector3 to, RaytraceAssist rta, List<Entity> found) {
        Iterator<Entity> it = found.iterator();
        while (it.hasNext()) {
            if (!EventFactory.testEntityInteract(EventFactory.convertFake(world, TileOwnership.loadOwner(((TileLens)(Object) this).getTileData())), world, it.next(), AstralSorceryGuard.BEAM_FAKE)) {
                found.clear();
                break;
            }
        }
    }

}