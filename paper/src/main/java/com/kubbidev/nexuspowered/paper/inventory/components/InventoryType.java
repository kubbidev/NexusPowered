package com.kubbidev.nexuspowered.paper.inventory.components;

public enum InventoryType {

    CHEST(org.bukkit.event.inventory.InventoryType.CHEST, 9),
    WORKBENCH(org.bukkit.event.inventory.InventoryType.WORKBENCH, 9),
    HOPPER(org.bukkit.event.inventory.InventoryType.HOPPER, 5),
    DISPENSER(org.bukkit.event.inventory.InventoryType.DISPENSER, 8),
    BREWING(org.bukkit.event.inventory.InventoryType.BREWING, 4);

    private final org.bukkit.event.inventory.InventoryType inventoryType;
    private final int limit;

    InventoryType(org.bukkit.event.inventory.InventoryType inventoryType, int limit) {
        this.inventoryType = inventoryType;
        this.limit = limit;
    }

    public org.bukkit.event.inventory.InventoryType getInventoryType() {
        return this.inventoryType;
    }

    public int getLimit() {
        return this.limit;
    }
}
