package com.motherlodeprofit;

import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

@Slf4j
@Singleton
public class MotherlodeProfitSession
{


    @Getter(AccessLevel.PACKAGE)
    private int nuggetsCount;

    @Getter(AccessLevel.PACKAGE)
    private int coalCount;

    @Getter(AccessLevel.PACKAGE)
    private int goldCount;

    @Getter(AccessLevel.PACKAGE)
    private int mithrilCount;

    @Getter(AccessLevel.PACKAGE)
    private int adamantiteCount;

    @Getter(AccessLevel.PACKAGE)
    private int runiteCount;

    @Getter(AccessLevel.PACKAGE)
    private int nuggetsProfit;

    @Getter(AccessLevel.PACKAGE)
    private int coalProfit;

    @Getter(AccessLevel.PACKAGE)
    private int goldProfit;

    @Getter(AccessLevel.PACKAGE)
    private int mithrilProfit;

    @Getter(AccessLevel.PACKAGE)
    private int adamantiteProfit;

    @Getter(AccessLevel.PACKAGE)
    private int runiteProfit;

    @Inject
    private ItemManager itemManager;

    public int getTotalProfit()
    {
        return coalProfit + goldProfit + mithrilProfit + adamantiteProfit + runiteProfit;
    }

    public void updateOreFound(int item, int count)
    {
        switch (item)
        {
            case ItemID.GOLDEN_NUGGET:
                nuggetsCount += count;
                break;
            case ItemID.COAL:
                coalCount += count;
                coalProfit += count * itemManager.getItemPrice(ItemID.COAL);
                break;
            case ItemID.GOLD_ORE:
                goldCount += count;
                goldProfit += count * itemManager.getItemPrice(ItemID.GOLD_ORE);
                break;
            case ItemID.MITHRIL_ORE:
                mithrilCount += count;
                mithrilProfit += count * itemManager.getItemPrice(ItemID.MITHRIL_ORE);
                break;
            case ItemID.ADAMANTITE_ORE:
                adamantiteCount += count;
                adamantiteProfit += count * itemManager.getItemPrice(ItemID.ADAMANTITE_ORE);
                break;
            case ItemID.RUNITE_ORE:
                runiteCount += count;
                runiteProfit += count * itemManager.getItemPrice(ItemID.RUNITE_ORE);
                break;
            default:
                log.debug("Invalid ore specified. The quantity and profit will not be updated.");

        }
    }
}
