package com.github.lunatrius.stackie.handler;

import com.github.lunatrius.stackie.reference.Reference;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StackingHandlerTick extends StackingHandler {
    public static final StackingHandlerTick INSTANCE = new StackingHandlerTick();

    private MinecraftServer server = null;
    private int ticks = -1;

    @SubscribeEvent
    public void tick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        if (--this.ticks < 0) {
            if (this.server != null && this.server.worlds != null) {
                processWorlds(this.server.worlds);
            }

            this.ticks = ConfigurationHandler.General.interval;
        }
    }

    public void setServer(final MinecraftServer server) {
        this.server = server;
    }

    private void processWorlds(final WorldServer[] worldServers) {
        for (final WorldServer world : worldServers) {
            final List<EntityItem> entityItems = new ArrayList<EntityItem>();
            final List<EntityXPOrb> entityExperienceOrbs = new ArrayList<EntityXPOrb>();

            for (final Entity entity : world.loadedEntityList) {
                if (entity.isDead) {
                    continue;
                }

                final Class<? extends Entity> clazz = entity.getClass();

                if (EntityItem.class.equals(clazz)) {
                    entityItems.add((EntityItem) entity);
                } else if (EntityXPOrb.class.equals(clazz)) {
                    entityExperienceOrbs.add((EntityXPOrb) entity);
                }
            }

            if (entityItems.size() >= 2 && entityItems.size() <= ConfigurationHandler.General.stackLimit) {
                try {
                    stackItems(Lists.reverse(entityItems));
                } catch (final Exception e) {
                    Reference.logger.error("Could not stack items!", e);
                }
            }

            if (entityExperienceOrbs.size() >= 2 && entityExperienceOrbs.size() <= ConfigurationHandler.General.stackLimit) {
                try {
                    stackExperience(Lists.reverse(entityExperienceOrbs));
                } catch (final Exception e) {
                    Reference.logger.error("Could not stack experience!", e);
                }
            }
        }
    }

    private void stackItems(final List<EntityItem> entityItems) {
        final ListIterator<EntityItem> iteratorL = entityItems.listIterator();
        while (iteratorL.hasNext()) {
            final EntityItem entityItemL = iteratorL.next();

            if (entityItemL.isDead) {
                continue;
            }

            final ListIterator<EntityItem> iteratorR = entityItems.listIterator(iteratorL.nextIndex());
            while (iteratorR.hasNext()) {
                final EntityItem entityItemR = iteratorR.next();

                if (entityItemR.isDead) {
                    continue;
                }

                if (!areEntitiesClose(entityItemL, entityItemR)) {
                    continue;
                }

                final boolean merged = stackItems(entityItemL, entityItemR);
                if (merged) {
                    entityItemR.setDead();
                }
            }
        }
    }

    private void stackExperience(final List<EntityXPOrb> entityOrbs) {
        final ListIterator<EntityXPOrb> iteratorL = entityOrbs.listIterator();
        while (iteratorL.hasNext()) {
            final EntityXPOrb entityOrbL = iteratorL.next();

            if (entityOrbL.isDead) {
                continue;
            }

            final ListIterator<EntityXPOrb> iteratorR = entityOrbs.listIterator(iteratorL.nextIndex());
            while (iteratorR.hasNext()) {
                final EntityXPOrb entityOrbR = iteratorR.next();

                if (entityOrbR.isDead) {
                    continue;
                }

                if (!areEntitiesClose(entityOrbL, entityOrbR)) {
                    continue;
                }

                final boolean merged = stackExperience(entityOrbL, entityOrbR);
                if (merged) {
                    entityOrbR.setDead();
                }
            }
        }
    }
}
