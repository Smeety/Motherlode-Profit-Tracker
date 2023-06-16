/*
 * Copyright (c) 2018, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Lars <lars.oernlo@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.motherlodeprofit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.inject.Provides;

import java.util.Arrays;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Motherlode Profit Tracker",
        description = "Show profit collected at MLM",
        tags = {"pay", "dirt", "mining", "mlm", "skilling", "overlay"},
        enabledByDefault = true
)
public class MotherlodeProfitPlugin extends Plugin
{
    private static final Set<Integer> MOTHERLODE_MAP_REGIONS = ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
    private static final Set<Integer> MLM_ORE_TYPES = ImmutableSet.of(ItemID.RUNITE_ORE, ItemID.ADAMANTITE_ORE,
            ItemID.MITHRIL_ORE, ItemID.GOLD_ORE, ItemID.COAL, ItemID.GOLDEN_NUGGET);

    private static final int SACK_LARGE_SIZE = 162;
    private static final int SACK_SIZE = 81;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private MotherlodeProfitOverlay motherlodeProfitOverlay;

    @Inject
    private MotherlodeProfitConfig config;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Getter(AccessLevel.PACKAGE)
    private boolean inMlm;

    @Getter(AccessLevel.PACKAGE)
    private int curSackSize;
    @Getter(AccessLevel.PACKAGE)
    private int maxSackSize;
    @Getter(AccessLevel.PACKAGE)
    private Integer depositsLeft;

    @Inject
    private MotherlodeProfitSession session;
    private boolean shouldUpdateOres;
    private Multiset<Integer> inventorySnapshot;


    @Provides
    MotherlodeProfitConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MotherlodeProfitConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(motherlodeProfitOverlay);

        inMlm = checkInMlm();

        if (inMlm)
        {
            clientThread.invokeLater(this::refreshSackValues);
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(motherlodeProfitOverlay);

    }
    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (inMlm)
        {
            int lastSackValue = curSackSize;
            refreshSackValues();
            shouldUpdateOres = curSackSize < lastSackValue;
            if (shouldUpdateOres)
            {
                // Take a snapshot of the inventory before the new ore is added.
                ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
                if (itemContainer != null)
                {
                    inventorySnapshot = HashMultiset.create();
                    Arrays.stream(itemContainer.getItems())
                            .filter(item -> MLM_ORE_TYPES.contains(item.getId()))
                            .forEach(item -> inventorySnapshot.add(item.getId(), item.getQuantity()));
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING)
        {
            inMlm = checkInMlm();
        }
        else if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            // Prevent code from running while logged out.
            inMlm = false;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        final ItemContainer container = event.getItemContainer();

        if (!inMlm || !shouldUpdateOres || inventorySnapshot == null || container != client.getItemContainer(InventoryID.INVENTORY))
        {
            return;
        }

        // Build set of current inventory
        Multiset<Integer> current = HashMultiset.create();
        Arrays.stream(container.getItems())
                .filter(item -> MLM_ORE_TYPES.contains(item.getId()))
                .forEach(item -> current.add(item.getId(), item.getQuantity()));

        // Take the difference
        Multiset<Integer> delta = Multisets.difference(current, inventorySnapshot);

        // Update the session
        delta.forEachEntry(session::updateOreFound);
        inventorySnapshot = null;
        shouldUpdateOres = false;
    }


    private boolean checkInMlm()
    {
        GameState gameState = client.getGameState();
        if (gameState != GameState.LOGGED_IN
                && gameState != GameState.LOADING)
        {
            return false;
        }

        int[] currentMapRegions = client.getMapRegions();

        // Verify that all regions exist in MOTHERLODE_MAP_REGIONS
        for (int region : currentMapRegions)
        {
            if (!MOTHERLODE_MAP_REGIONS.contains(region))
            {
                return false;
            }
        }

        return true;
    }

    private void refreshSackValues()
    {
        curSackSize = client.getVarbitValue(Varbits.SACK_NUMBER);
        boolean sackUpgraded = client.getVarbitValue(Varbits.SACK_UPGRADED) == 1;
        maxSackSize = sackUpgraded ? SACK_LARGE_SIZE : SACK_SIZE;
    }
}