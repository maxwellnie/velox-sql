package com.crazy.sql.plugin;

import com.crazy.sql.plugin.base.BasePlugin;
import com.crazy.sql.plugin.entity.PluginEntity;

import java.util.*;
import java.util.stream.Collectors;

public class PluginManager {
    private static final Map<String,BasePlugin> basePluginMap= Collections.synchronizedMap(new HashMap<>(16));
    public static void register( PluginEntity pluginEntity){
        basePluginMap.put(pluginEntity.getName(), pluginEntity.getPlugin());
    }
    public static List<PluginEntity> getBasePlugins(){
        return basePluginMap.entrySet().stream().map((entry)->new PluginEntity(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
