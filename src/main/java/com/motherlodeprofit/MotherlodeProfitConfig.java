/*
 * Copyright (c) 2018, Seth <Sethtroll3@gmail.com>
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

import net.runelite.client.config.*;

@ConfigGroup("motherlodeprofit")
public interface MotherlodeProfitConfig extends Config
{

    @ConfigSection(
            name = "Decimal Configuration",
            description = "Configuration for decimal formatting",
            position = 6
    )
    String decimalSection = "Decimal Configuration";

    @ConfigItem(
            keyName = "showQuantity",
            name = "Show Quantity",
            description = "Toggle to show the quantity of ores",
            position = 1
    )
    default boolean showQuantity()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showProfit",
            name = "Show Profit",
            description = "Toggle to show the profit from ores",
            position = 2
    )
    default boolean showProfit()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showNuggets",
            name = "Show Nuggets",
            description = "Configures whether to show nuggets",
            position = 4
    )
    default boolean showNuggets()
    {
        return true;
    }
    @ConfigItem(
            keyName = "showProfitPerHour",
            name = "Show Profit per Hour",
            description = "Toggle the display of profit per hour",
            position = 5
    )
    default boolean showProfitPerHour() {
        return true;
    }

    @ConfigItem(
            keyName = "useRSDecimalStack",
            name = "Use RS Decimal Stack",
            description = "Use RS Decimal Stack format for profit above 100,000 GP",
            position = 6,
            section = decimalSection
    )
    default boolean useRSDecimalStack()
    {
        return true;
    }
    @ConfigItem(
            keyName = "profitThreshold",
            name = "Profit Threshold",
            description = "The profit threshold to use RS decimal stack format",
            position = 7,
            section = decimalSection
    )
    @Range(min = 1)
    default int profitThreshold()
    {
        return 100000;
    }
}