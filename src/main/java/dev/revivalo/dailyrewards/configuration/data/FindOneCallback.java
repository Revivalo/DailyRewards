package dev.revivalo.dailyrewards.configuration.data;

import dev.revivalo.dailyrewards.managers.reward.RewardType;

import java.util.Map;

public interface FindOneCallback {
    void onQueryDone(Map<RewardType, Long> time);
}
