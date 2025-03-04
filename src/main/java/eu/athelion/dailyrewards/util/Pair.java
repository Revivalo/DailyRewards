package eu.athelion.dailyrewards.util;

import lombok.Getter;

@Getter
public class Pair <First, Second> {

    private final First first;
    private final Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }
}
