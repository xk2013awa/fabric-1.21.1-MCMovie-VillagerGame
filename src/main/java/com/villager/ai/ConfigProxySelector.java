package com.villager.ai;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ConfigProxySelector extends ProxySelector {
    private static final List<Proxy> NO_PROXY_LIST = List.of(Proxy.NO_PROXY);
    private final String proxyString;

    public ConfigProxySelector(String proxyString) {
        this.proxyString = proxyString;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException e) {
        // 可选：记录连接失败日志
    }

    @Override
    public synchronized List<Proxy> select(URI uri) {
        String scheme = uri.getScheme().toLowerCase();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            return getProxyFromConfig(proxyString.trim());
        } else {
            return NO_PROXY_LIST;
        }
    }

    private List<Proxy> getProxyFromConfig(String proxyAddress) {
        if (StringUtils.isBlank(proxyAddress)) return NO_PROXY_LIST;

        String[] split = proxyAddress.split(":", 2);
        if (split.length != 2) return NO_PROXY_LIST;

        String host = split[0];
        String portStr = split[1];

        if (!NumberUtils.isParsable(portStr)) return NO_PROXY_LIST;

        int port = Integer.parseInt(portStr);
        return List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
    }
}

