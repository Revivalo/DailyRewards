package dev.revivalo.dailyrewards.data;

import java.util.Map;

public interface FindOneCallback {
    void onQueryDone(Map<String, Object> time);
}