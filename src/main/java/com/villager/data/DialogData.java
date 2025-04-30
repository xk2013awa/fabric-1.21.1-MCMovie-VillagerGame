package com.villager.data;

public class DialogData {
    public String topic;
    public Choice[] choices;

    public static class Choice {
        public String text;
        public int friendshipChange;
    }
}