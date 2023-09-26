package com.crazy.sql.plugin.entity;

import com.crazy.sql.plugin.base.BasePlugin;

public class PluginEntity {
    private String name;
    private BasePlugin plugin;

    public PluginEntity() {
    }

    public PluginEntity(String name, BasePlugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
