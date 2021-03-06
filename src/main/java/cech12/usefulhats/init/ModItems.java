package cech12.usefulhats.init;

import cech12.usefulhats.item.*;
import cech12.usefulhats.UsefulHatsMod;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= UsefulHatsMod.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    private static final Item[] items = {
            new AquanautHelmetItem(),
            new ChoppingHatItem(),
            new HaloItem(),
            new LuckyHatItem(),
            new MiningHatItem(),
            new PostmanHatItem(),
            new StockingCapItem(),
            new StrawHatItem(),
            new WingHelmetItem()
    };

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : ModItems.items) {
            event.getRegistry().register(item);
        }
    }

    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerColors(ColorHandlerEvent.Item event) {
        ItemColors itemcolors = event.getItemColors();
        for (Item item : ModItems.items) {
            //if (item instanceof IDyeableArmorItem) {
            itemcolors.register((itemStack, layer) -> layer > 0 ? -1 : ((IDyeableArmorItem)itemStack.getItem()).getColor(itemStack), item);
            //}
        }
    }

    /**
     * Called at mod initialization.
     */
    public static void addEventListeners() {
        MinecraftForge.EVENT_BUS.addListener(ModItems::onBreakSpeedEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onBreakEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onEntityJoinWorldEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onItemFishedEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onLivingDropsEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onLivingEquipmentChangeEvent);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onLivingSetAttackTargetEvent);
    }

    /**
     * Called at mod initialization. (client side)
     */
    @OnlyIn(Dist.CLIENT)
    public static void addClientEventListeners() {
        MinecraftForge.EVENT_BUS.addListener(ModItems::onItemToolTipEvent);
    }

    private static void onBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        ItemStack headSlotItemStack = event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD);
        for (Item item : ModItems.items) {
            if (item instanceof IBreakSpeedChanger && headSlotItemStack.getItem() == item) {
                ((IBreakSpeedChanger) item).onBreakSpeedEvent(event, headSlotItemStack);
            }
        }
    }

    private static void onBreakEvent(BlockEvent.BreakEvent event) {
        ItemStack headSlotItemStack = event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD);
        for (Item item : ModItems.items) {
            if (item instanceof IBreakSpeedChanger && headSlotItemStack.getItem() == item) {
                ((IBreakSpeedChanger) item).onBreakEvent(event, headSlotItemStack);
            }
        }
    }

    private static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof MobEntity) {
            MobEntity entity = (MobEntity) event.getEntity();
            for (Item item : ModItems.items) {
                if (item instanceof IMobEntityChanger) {
                    ((IMobEntityChanger) item).onEntityJoinWorldEvent(entity, event);
                }
            }
        }
    }

    private static void onItemFishedEvent(ItemFishedEvent event) {
        ItemStack headSlotItemStack = event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD);
        for (Item item : ModItems.items) {
            if (item instanceof IItemFishedListener && headSlotItemStack.getItem() == item) {
                ((IItemFishedListener) item).onItemFishedListener(event, headSlotItemStack);
            }
        }
    }

    private static void onItemToolTipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        for (Item item : ModItems.items) {
            if (/*item instanceof AbstractHatItem &&*/ stack.getItem() == item) {
                ((AbstractHatItem) item).onItemToolTipEvent(stack, event.getToolTip());
            }
        }
    }

    private static void onLivingDropsEvent(LivingDropsEvent event) {
        if (event.getSource().getImmediateSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getImmediateSource();
            ItemStack headSlotItemStack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
            for (Item item : ModItems.items) {
                if (item instanceof ILivingDropsListener && item == headSlotItemStack.getItem()) {
                    ((ILivingDropsListener) item).onLivingDropsEvent(event, player, headSlotItemStack);
                }
            }
        }
    }

    private static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlotType.HEAD && event.getEntityLiving() instanceof PlayerEntity) {
            Item fromItem = event.getFrom().getItem();
            Item toItem = event.getFrom().getItem();
            for (Item item : ModItems.items) {
                if (item instanceof IEquipmentChangeListener && (fromItem == item || toItem == item)) {
                    ((IEquipmentChangeListener) item).onEquipmentChangeEvent(event);
                }
            }
        }
    }

    private static void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event) {
        if (event.getEntity() instanceof MobEntity && event.getTarget() instanceof PlayerEntity) {
            MobEntity mob = (MobEntity) event.getEntity();
            PlayerEntity player = (PlayerEntity) event.getTarget();
            Item headSlotItem = player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem();
            for (Item item : ModItems.items) {
                if (item instanceof IAttackTargetChanger && item == headSlotItem) {
                    ((IAttackTargetChanger) item).onLivingSetAttackTarget(mob, player);
                }
            }
        }
    }
}
