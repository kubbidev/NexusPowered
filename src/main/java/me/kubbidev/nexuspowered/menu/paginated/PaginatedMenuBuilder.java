package me.kubbidev.nexuspowered.menu.paginated;

import com.google.common.collect.ImmutableList;
import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.menu.scheme.MenuScheme;
import me.kubbidev.nexuspowered.menu.scheme.StandardSchemeMappings;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Specification class for a {@link PaginatedMenu}.
 */
@NotNullByDefault
public class PaginatedMenuBuilder {

    public static final int DEFAULT_LINES = 6;

    public static final int DEFAULT_NEXT_PAGE_SLOT = new MenuScheme()
            .maskEmpty(5)
            .mask("000000010")
            .getMaskedIndexes().getFirst();

    public static final int DEFAULT_PREVIOUS_PAGE_SLOT = new MenuScheme()
            .maskEmpty(5)
            .mask("010000000")
            .getMaskedIndexes().getFirst();

    public static final List<Integer> DEFAULT_ITEM_SLOTS = new MenuScheme()
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .getMaskedIndexesImmutable();

    public static final MenuScheme DEFAULT_SCHEME = new MenuScheme(StandardSchemeMappings.STAINED_GLASS)
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .scheme(3, 3)
            .scheme(3, 3)
            .scheme(3, 3)
            .scheme(3, 3)
            .scheme(3, 3)
            .scheme(3, 3);

    public static final Function<PageInfo, ItemStack> DEFAULT_NEXT_PAGE_ITEM = pageInfo -> ItemStackBuilder.of(Material.ARROW)
            .name(Component.text()
                    .color(NamedTextColor.AQUA)
                    .append(Component.text("--").decorate(TextDecoration.STRIKETHROUGH))
                    .append(Component.text(">"))
                    .build())
            .lore(Component.text("Switch to the next page.", NamedTextColor.WHITE))
            .lore(Component.empty())
            .lore(Component.empty())
            .lore(Component.text()
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Currently viewing page "))
                    .append(Component.text(pageInfo.getCurrent(), NamedTextColor.AQUA))
                    .append(Component.text('/'))
                    .append(Component.text(pageInfo.getSize(), NamedTextColor.AQUA))
                    .build())
            .build();

    public static final Function<PageInfo, ItemStack> DEFAULT_PREVIOUS_PAGE_ITEM = pageInfo -> ItemStackBuilder.of(Material.ARROW)
            .name(Component.text()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.AQUA)
                    .append(Component.text("<"))
                    .append(Component.text("--").decorate(TextDecoration.STRIKETHROUGH))
                    .build())
            .lore(Component.text("Switch to the previous page.", NamedTextColor.WHITE))
            .lore(Component.empty())
            .lore(Component.empty())
            .lore(Component.text()
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Currently viewing page "))
                    .append(Component.text(pageInfo.getCurrent(), NamedTextColor.AQUA))
                    .append(Component.text('/'))
                    .append(Component.text(pageInfo.getSize(), NamedTextColor.AQUA))
                    .build())
            .build();

    public static PaginatedMenuBuilder create() {
        return new PaginatedMenuBuilder();
    }

    private int lines;
    private Component title;
    private List<Integer> itemSlots;

    private int nextPageSlot;
    private int previousPageSlot;

    private MenuScheme scheme;
    private Function<PageInfo, ItemStack> nextPageItem;
    private Function<PageInfo, ItemStack> previousPageItem;

    private PaginatedMenuBuilder() {
        this.lines = DEFAULT_LINES;
        this.itemSlots = DEFAULT_ITEM_SLOTS;
        this.nextPageSlot = DEFAULT_NEXT_PAGE_SLOT;
        this.previousPageSlot = DEFAULT_PREVIOUS_PAGE_SLOT;
        this.scheme = DEFAULT_SCHEME;
        this.nextPageItem = DEFAULT_NEXT_PAGE_ITEM;
        this.previousPageItem = DEFAULT_PREVIOUS_PAGE_ITEM;
    }

    public PaginatedMenuBuilder copy() {
        PaginatedMenuBuilder copy = new PaginatedMenuBuilder();
        copy.lines = this.lines;
        copy.title = this.title;
        copy.itemSlots = this.itemSlots;
        copy.nextPageSlot = this.nextPageSlot;
        copy.previousPageSlot = this.previousPageSlot;
        copy.scheme = this.scheme.copy();
        copy.nextPageItem = this.nextPageItem;
        copy.previousPageItem = this.previousPageItem;
        return copy;
    }

    public PaginatedMenuBuilder lines(int lines) {
        this.lines = lines;
        return this;
    }

    public PaginatedMenuBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public PaginatedMenuBuilder itemSlots(List<Integer> itemSlots) {
        this.itemSlots = ImmutableList.copyOf(itemSlots);
        return this;
    }

    public PaginatedMenuBuilder nextPageSlot(int nextPageSlot) {
        this.nextPageSlot = nextPageSlot;
        return this;
    }

    public PaginatedMenuBuilder previousPageSlot(int previousPageSlot) {
        this.previousPageSlot = previousPageSlot;
        return this;
    }

    public PaginatedMenuBuilder scheme(MenuScheme scheme) {
        this.scheme = Objects.requireNonNull(scheme, "scheme");
        return this;
    }

    public PaginatedMenuBuilder nextPageItem(Function<PageInfo, ItemStack> nextPageItem) {
        this.nextPageItem = Objects.requireNonNull(nextPageItem, "nextPageItem");
        return this;
    }

    public PaginatedMenuBuilder previousPageItem(Function<PageInfo, ItemStack> previousPageItem) {
        this.previousPageItem = Objects.requireNonNull(previousPageItem, "previousPageItem");
        return this;
    }

    public int getLines() {
        return this.lines;
    }

    public Component getTitle() {
        return this.title;
    }

    public List<Integer> getItemSlots() {
        return this.itemSlots;
    }

    public int getNextPageSlot() {
        return this.nextPageSlot;
    }

    public int getPreviousPageSlot() {
        return this.previousPageSlot;
    }

    public MenuScheme getScheme() {
        return this.scheme;
    }

    public Function<PageInfo, ItemStack> getNextPageItem() {
        return this.nextPageItem;
    }

    public Function<PageInfo, ItemStack> getPreviousPageItem() {
        return this.previousPageItem;
    }

    public PaginatedMenu build(Player player, Function<PaginatedMenu, List<Item>> content) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(this.title, "title");
        Objects.requireNonNull(this.itemSlots, "itemSlots");
        Objects.requireNonNull(this.scheme, "scheme");
        Objects.requireNonNull(this.nextPageItem, "nextPageItem");
        Objects.requireNonNull(this.previousPageItem, "previousPageItem");

        return new PaginatedMenu(content, player, this);
    }
}