package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.FrustumProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerFrustumMixin implements FrustumProvider {

    @Unique
    private Frustum frustum;

    @Override
    public void particle_core_setFrustum(Frustum frustum) {
        this.frustum = frustum;
    }

    @WrapWithCondition(method = "renderParticles", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.buildGeometry (Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    private boolean particle_core_cullParticles(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v){
        //return frustum.isVisible(instance.getBoundingBox());
        return ((FrustumAccessor)frustum).getFrustumIntersection().testPoint(
                (float)(((ParticleAccessor)instance).getX() - ((FrustumAccessor)frustum).getX()),
                (float)(((ParticleAccessor)instance).getY() - ((FrustumAccessor)frustum).getY()),
                (float)(((ParticleAccessor)instance).getZ() - ((FrustumAccessor)frustum).getZ())
        );
    }
}