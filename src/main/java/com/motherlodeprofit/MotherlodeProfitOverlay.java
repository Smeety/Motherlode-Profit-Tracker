package com.motherlodeprofit;

import java.awt.*;
import java.text.DecimalFormat;
import javax.inject.Inject;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
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
        setPriority(OverlayPriority.HIGH);
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

        if (coalProfit > 0) {
            String coalQuantityString = config.showQuantity() ? coalCount + (config.showProfit() ? " x " : "") : "";
            String coalProfitString = config.showProfit() ? (coalProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(coalProfit) : FormatIntegerWithCommas(coalProfit)) + " GP" : "";

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Coal:")
                    .right(coalQuantityString + coalProfitString)
                    .build());
        }

        if (goldProfit > 0) {
            String goldQuantityString = config.showQuantity() ? goldCount + (config.showProfit() ? " x " : "") : "";
            String goldProfitString = config.showProfit() ? (goldProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(goldProfit) : FormatIntegerWithCommas(goldProfit)) + " GP" : "";

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Gold:")
                    .right(goldQuantityString + goldProfitString)
                    .build());
        }

        if (mithrilProfit > 0) {
            String mithrilQuantityString = config.showQuantity() ? mithrilCount + (config.showProfit() ? " x " : "") : "";
            String mithrilProfitString = config.showProfit() ? (mithrilProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(mithrilProfit) : FormatIntegerWithCommas(mithrilProfit)) + " GP" : "";

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mithril:")
                    .right(mithrilQuantityString + mithrilProfitString)
                    .build());
        }

        if (adamantiteProfit > 0) {
            String adamantiteQuantityString = config.showQuantity() ? adamantiteCount + (config.showProfit() ? " x " : "") : "";
            String adamantiteProfitString = config.showProfit() ? (adamantiteProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(adamantiteProfit) : FormatIntegerWithCommas(adamantiteProfit)) + " GP" : "";

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Adamant:")
                    .right(adamantiteQuantityString + adamantiteProfitString)
                    .build());
        }

        if (runiteProfit > 0) {
            String runiteQuantityString = config.showQuantity() ? runiteCount + (config.showProfit() ? " x " : "") : "";
            String runiteProfitString = config.showProfit() ? (runiteProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(runiteProfit) : FormatIntegerWithCommas(runiteProfit)) + " GP" : "";

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Runite:")
                    .right(runiteQuantityString + runiteProfitString)
                    .build());
        }

            // Add blank line
            panelComponent.getChildren().add(LineComponent.builder().build());

        if (config.showProfitPerHour()) {
            // Display profit per hour
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("GP/H:")
                    .right(QuantityFormatter.quantityToRSDecimalStack((int) profitPerHour) + " GP")
                    .build());
        }

        if (totalProfit > 0)
            // Display total profit
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total:")
                    .right(FormatIntegerWithCommas(totalProfit) + " GP")
                    .build());

        return super.render(graphics);
    }
}
