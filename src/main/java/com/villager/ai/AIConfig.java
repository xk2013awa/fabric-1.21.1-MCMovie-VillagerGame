package com.villager.ai;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "villager")
public class AIConfig implements ConfigData {
    public String apiKey = "";
    public String baseUrl = "https://api.openai.com/v1";
    public String proxy = "";
    public String model = "gpt-3.5-turbo";
    public double temperature = 0.8;
}
