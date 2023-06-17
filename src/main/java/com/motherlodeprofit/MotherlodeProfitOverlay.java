package com.motherlodeprofit;

import java.awt.*;
import java.text.DecimalFormat;
import javax.inject.Inject;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.util.QuantityFormatter;

public class MotherlodeProfitOverlay extends OverlayPanel {
    private final MotherlodeProfitPlugin plugin;
    private final MotherlodeProfitSession motherlodeProfitSession;
    private final MotherlodeProfitConfig config;
    private final ItemManager itemManager;
    private final long startTime;
    private int maxPanelWidth = ComponentConstants.STANDARD_WIDTH;

    @Inject
    MotherlodeProfitOverlay(
            MotherlodeProfitPlugin plugin,
            MotherlodeProfitSession motherlodeProfitSession,
            MotherlodeProfitConfig config,
            ItemManager itemManager
    ) {
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.motherlodeProfitSession = motherlodeProfitSession;
        this.config = config;
        this.itemManager = itemManager;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isInMlm()) {
            return null;
        }

        MotherlodeProfitSession session = motherlodeProfitSession;
        int totalProfit = session.getTotalProfit();
        int nuggetCount = session.getNuggetsCount();

        if (totalProfit == 0 && nuggetCount == 0) {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Motherlode Profit")
                .color(Color.GREEN)
                .build());

        panelComponent.setOrientation(ComponentOrientation.VERTICAL);

        if (config.showNuggets() && config.showQuantity() && nuggetCount > 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Nuggets:")
                    .right(String.valueOf(nuggetCount))
                    .build());
        }

        maxPanelWidth = ComponentConstants.STANDARD_WIDTH;

        addOreLine("Coal", session.getCoalCount(), session.getCoalProfit(), graphics);
        addOreLine("Gold", session.getGoldCount(), session.getGoldProfit(), graphics);
        addOreLine("Mithril", session.getMithrilCount(), session.getMithrilProfit(), graphics);
        addOreLine("Adamantite", session.getAdamantiteCount(), session.getAdamantiteProfit(), graphics);
        addOreLine("Runite", session.getRuniteCount(), session.getRuniteProfit(), graphics);

        if (config.showQuantity() || config.showProfit()) {
            panelComponent.getChildren().add(LineComponent.builder().build());
        }

        if (config.showProfitPerHour()) {
            double profitPerHour = calculateProfitPerHour(totalProfit);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("GP/H:")
                    .right(QuantityFormatter.quantityToRSDecimalStack((int) profitPerHour) + " GP")
                    .build());
        }

        if (totalProfit > 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total:")
                    .right(formatIntegerWithCommas(totalProfit) + " GP")
                    .build());
        }

        panelComponent.setPreferredSize(new Dimension(maxPanelWidth, 0));

        return super.render(graphics);
    }

    private void addOreLine(String oreName, int oreCount, int oreProfit, Graphics2D graphics) {
        if (oreProfit > 0 && (config.showQuantity() || config.showProfit())) {
            String quantityString = config.showQuantity() ? oreCount + (config.showProfit() ? " x " : "") : "";
            String profitString = config.showProfit() ? (oreProfit > config.profitThreshold() && config.useRSDecimalStack() ? QuantityFormatter.quantityToRSDecimalStack(oreProfit) : formatIntegerWithCommas(oreProfit)) + " GP" : "";

            final FontMetrics fontMetrics = graphics.getFontMetrics();
            int panelWidth = Math.max(ComponentConstants.STANDARD_WIDTH, fontMetrics.stringWidth(oreName + " " + quantityString + profitString) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);

            maxPanelWidth = Math.max(maxPanelWidth, panelWidth);

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(oreName + ":")
                    .right(quantityString + profitString)
                    .build());
        }
    }

    private double calculateProfitPerHour(int totalProfit) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double hoursElapsed = elapsedTime / (1000.0 * 60 * 60);
        return totalProfit / hoursElapsed;
    }

    private String formatIntegerWithCommas(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
    }
}
