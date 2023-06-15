package com.motherlodeprofit;

import java.awt.*;
import javax.inject.Inject;

import com.motherlodeprofit.MotherlodeProfitConfig;
import com.motherlodeprofit.MotherlodeProfitPlugin;
import com.motherlodeprofit.MotherlodeProfitSession;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class MotherlodeOreOverlay extends OverlayPanel
{
    private final MotherlodeProfitPlugin plugin;
    private final MotherlodeProfitSession motherlodeSession;
    private final MotherlodeProfitConfig config;
    private final ItemManager itemManager;

    @Inject
    MotherlodeOreOverlay(MotherlodeProfitPlugin plugin, MotherlodeProfitSession motherlodeSession, MotherlodeProfitConfig config, ItemManager itemManager)
    {
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.motherlodeSession = motherlodeSession;
        this.config = config;
        this.itemManager = itemManager;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInMlm() || (!config.showQuantity() && !config.showProfit()))
        {
            return null;
        }

        MotherlodeProfitSession session = motherlodeSession;

        int coalProfit = session.getCoalProfit();
        int goldProfit = session.getGoldProfit();
        int mithrilProfit = session.getMithrilProfit();
        int adamantiteProfit = session.getAdamantiteProfit();
        int runiteProfit = session.getRuniteProfit();

        int nuggetCount = session.getNuggetsCount();
        int coalCount = session.getCoalCount();
        int goldCount = session.getGoldCount();
        int mithrilCount = session.getMithrilCount();
        int adamantiteCount = session.getAdamantiteCount();
        int runiteCount = session.getRuniteCount();

        // Calculate total profit
        int totalProfit = coalProfit + goldProfit + mithrilProfit + adamantiteProfit + runiteProfit;

        // If no ores have been collected or both toggles are disabled, don't bother showing anything
        if (totalProfit == 0 || (!config.showQuantity() && !config.showProfit()))
        {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Ores profit:")
                .color(Color.GREEN)
                .build());

        panelComponent.setOrientation(ComponentOrientation.VERTICAL);

        if (config.showNuggets() && (config.showQuantity() && nuggetCount > 0))
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Nuggets:")
                    .right(config.showQuantity() ? String.valueOf(nuggetCount) : "")
                    .build());
        }

        if (coalProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Coal:")
                    .right((config.showQuantity() ? coalCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? coalProfit + "GP" : ""))
                    .build());
        }
        if (goldProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Gold:")
                    .right((config.showQuantity() ? goldCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? goldProfit + "GP" : ""))
                    .build());
        }
        if (mithrilProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mithril:")
                    .right((config.showQuantity() ? mithrilCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? mithrilProfit + "GP" : ""))
                    .build());
        }
        if (adamantiteProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Adamantite:")
                    .right((config.showQuantity() ? adamantiteCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? adamantiteProfit + "GP" : ""))
                    .build());
        }
        if (runiteProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Runite:")
                    .right((config.showQuantity() ? runiteCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? runiteProfit + "GP" : ""))
                    .build());
        }
            // Add blank line
            panelComponent.getChildren().add(LineComponent.builder().build());

        if (totalProfit > 0)
            // Display total profit
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Profit:")
                    .right(totalProfit + " GP")
                    .build());

        return super.render(graphics);
    }
}
