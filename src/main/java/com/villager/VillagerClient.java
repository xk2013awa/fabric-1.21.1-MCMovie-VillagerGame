package com.villager;

import com.villager.config.MyModConfig;
import net.fabricmc.api.ClientModInitializer;

public abstract class VillagerClient implements ClientModInitializer {

    public static final String MOD_ID = "villager";

    @Override
    public void onInitializeClient() {
        MyModConfig.register(); // 注册 Cloth 配置界面

        // 注入全局代理选择器
//        ProxySelector.setDefault(new ConfigProxySelector(MyModConfig.get().proxyAddress));
    }
}