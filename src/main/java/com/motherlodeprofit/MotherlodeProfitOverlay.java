package com.motherlodeprofit;

import java.awt.*;
import java.text.DecimalFormat;
import javax.inject.Inject;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

public class MotherlodeProfitOverlay extends OverlayPanel
{
    private final MotherlodeProfitPlugin plugin;
    private final MotherlodeProfitSession motherlodeProfitSession;
    private final MotherlodeProfitConfig config;
    private final ItemManager itemManager;
    private long startTime;
    private long lastUpdateTime;
    public static String FormatIntegerWithCommas(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
    }

    @Inject
    MotherlodeProfitOverlay(MotherlodeProfitPlugin plugin, MotherlodeProfitSession motherlodeProfitSession, MotherlodeProfitConfig config, ItemManager itemManager)
    {
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.motherlodeProfitSession = motherlodeProfitSession;
        this.config = config;
        this.itemManager = itemManager;
        this.startTime = System.currentTimeMillis(); // Add this line to initialize startTime
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInMlm() || (!config.showQuantity() && !config.showProfit()))
        {
            return null;
        }

        MotherlodeProfitSession session = motherlodeProfitSession;

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
        int totalProfit = session.getTotalProfit();

        // Calculate profit per hour
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double hoursElapsed = elapsedTime / (1000.0 * 60 * 60);
        double profitPerHour = totalProfit / hoursElapsed;

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
                    .right((config.showQuantity() ? coalCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? QuantityFormatter.quantityToRSDecimalStack(coalProfit) + " GP" : ""))
                    .build());
        }
        if (goldProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Gold:")
                    .right((config.showQuantity() ? goldCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? QuantityFormatter.quantityToRSDecimalStack(goldProfit) + " GP" : ""))
                    .build());
        }
        if (mithrilProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mithril:")
                    .right((config.showQuantity() ? mithrilCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? QuantityFormatter.quantityToRSDecimalStack(mithrilProfit) + " GP" : ""))
                    .build());
        }
        if (adamantiteProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Adamantite:")
                    .right((config.showQuantity() ? adamantiteCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? QuantityFormatter.quantityToRSDecimalStack(adamantiteProfit) + " GP" : ""))
                    .build());
        }
        if (runiteProfit > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Runite:")
                    .right((config.showQuantity() ? runiteCount + (config.showProfit() ? " x " : "") : "") + (config.showProfit() ? QuantityFormatter.quantityToRSDecimalStack(runiteProfit) + " GP" : ""))
                    .build());
        }
            // Add blank line
            panelComponent.getChildren().add(LineComponent.builder().build());

        // Display profit per hour
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Profit/Hour:")
                .right(QuantityFormatter.quantityToRSDecimalStack((int)profitPerHour) + " GP")
                .build());

        if (totalProfit > 0)
            // Display total profit
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Profit:")
                    .right(FormatIntegerWithCommas(totalProfit) + " GP")
                    .build());

        return super.render(graphics);
    }
}
