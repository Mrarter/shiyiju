package com.shiyiju.modules.artwork.vo;

import java.math.BigDecimal;

public class ArtworkPriceRuleVO {

    private BigDecimal onlineDaysWeight;
    private BigDecimal followCountWeight;
    private BigDecimal artistFloatMin;
    private BigDecimal artistFloatMax;
    private BigDecimal platformMaxGrowthRate;
    private BigDecimal manualAdjustRate;

    public BigDecimal getOnlineDaysWeight() { return onlineDaysWeight; }
    public void setOnlineDaysWeight(BigDecimal onlineDaysWeight) { this.onlineDaysWeight = onlineDaysWeight; }
    public BigDecimal getFollowCountWeight() { return followCountWeight; }
    public void setFollowCountWeight(BigDecimal followCountWeight) { this.followCountWeight = followCountWeight; }
    public BigDecimal getArtistFloatMin() { return artistFloatMin; }
    public void setArtistFloatMin(BigDecimal artistFloatMin) { this.artistFloatMin = artistFloatMin; }
    public BigDecimal getArtistFloatMax() { return artistFloatMax; }
    public void setArtistFloatMax(BigDecimal artistFloatMax) { this.artistFloatMax = artistFloatMax; }
    public BigDecimal getPlatformMaxGrowthRate() { return platformMaxGrowthRate; }
    public void setPlatformMaxGrowthRate(BigDecimal platformMaxGrowthRate) { this.platformMaxGrowthRate = platformMaxGrowthRate; }
    public BigDecimal getManualAdjustRate() { return manualAdjustRate; }
    public void setManualAdjustRate(BigDecimal manualAdjustRate) { this.manualAdjustRate = manualAdjustRate; }
}
