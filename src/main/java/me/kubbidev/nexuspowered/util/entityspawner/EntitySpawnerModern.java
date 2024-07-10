package me.kubbidev.nexuspowered.util.entityspawner;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;

enum EntitySpawnerModern implements EntitySpawner {
    INSTANCE;

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> entityClass, Consumer<? super T> beforeAdd) {
        return location.getWorld().spawn(location, entityClass, beforeAdd);
    }

}