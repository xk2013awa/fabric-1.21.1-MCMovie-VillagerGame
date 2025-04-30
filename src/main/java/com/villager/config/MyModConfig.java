package com.villager.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "villager")
public class MyModConfig implements ConfigData {
    public String proxyAddress = "127.0.0.1:7890";  // 例子："127.0.0.1:7890"

    public static MyModConfig get() {
        return AutoConfig.getConfigHolder(MyModConfig.class).getConfig();
    }

    public static void register() {
        AutoConfig.register(MyModConfig.class, Toml4jConfigSerializer::new);
    }
}
