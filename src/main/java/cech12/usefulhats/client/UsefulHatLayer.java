package cech12.usefulhats.client;

import cech12.usefulhats.item.AbstractHatItem;
import cech12.usefulhats.item.IUsefulHatModelOwner;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Colorable hat layer which adds the {@link UsefulHatModel} to rendering.
 *
 * Textures for these hats must lie in textures/models/usefulhats/
 * with names: HATNAME.png or HATNAME_overlay.png
 */
public class UsefulHatLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends BipedArmorLayer<T, M, A> {

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public UsefulHatLayer(IEntityRenderer<T, M> renderer) {
        super(renderer, (A) new UsefulHatModel<T>(0.5F), (A) new UsefulHatModel<T>(0.5F));
    }

    @Override
    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlotType slot, @Nullable String type) {
        //texture location is another for this model (only for hats)
        if (slot == EquipmentSlotType.HEAD) {
            String texture = stack.getItem().getRegistryName().getPath();
            String domain = stack.getItem().getRegistryName().getNamespace();
            String s1 = String.format("%s:textures/models/usefulhats/%s%s.png", domain, texture, type == null ? "" : String.format("_%s", type));
            ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
            if (resourcelocation == null) {
                resourcelocation = new ResourceLocation(s1);
                ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
            }
            return resourcelocation;
        }
        //to avoid errors in texture finding use super method
        return super.getArmorResource(entity, stack, slot, type);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack hatItemStack = entityIn.getItemStackFromSlot(EquipmentSlotType.HEAD);
        Item hatItem = hatItemStack.getItem();
        if (hatItem instanceof AbstractHatItem && hatItem instanceof IUsefulHatModelOwner) {
            //super method makes its job good.
            super.render(entityIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    protected void setModelSlotVisible(A model, EquipmentSlotType slotIn) {
        //disable all render models of biped model except the hat (because it is overridden with own model)
        this.setModelVisible(model);
        if (slotIn == EquipmentSlotType.HEAD && model instanceof UsefulHatModel) {
            model.bipedHead.showModel = true;
            model.bipedHeadwear.showModel = true;
        }
    }
}
