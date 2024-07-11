package me.kubbidev.nexuspowered.menu.paginated;

import com.google.common.collect.ImmutableList;
import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.menu.pattern.MenuPattern;
import me.kubbidev.nexuspowered.menu.pattern.PatternMapping;
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
    public static final int DEFAULT_FOLLOWING_PAGE_SLOT = new MenuPattern()
            .shapeEmpty(5)
            .shape("000000010")
            .getShapedIndexes().getFirst();

    public static final int DEFAULT_PREVIOUS_PAGE_SLOT = new MenuPattern()
            .shapeEmpty(5)
            .shape("010000000")
            .getShapedIndexes().getFirst();

    public static final List<Integer> DEFAULT_CONTENT_SLOTS = new MenuPattern()
            .shape("011111110")
            .shape("011111110")
            .shape("011111110")
            .shape("011111110")
            .shape("011111110")
            .getShapedIndexesImmutable();

    public static final MenuPattern DEFAULT_PATTERN = new MenuPattern(PatternMapping.of(Material.LIGHT_BLUE_STAINED_GLASS))
            .shape("100000001")
            .shape("100000001")
            .shape("100000001")
            .shape("100000001")
            .shape("100000001")
            .shape("100000001");

    public static final Function<PaginatedInfo, ItemStack> DEFAULT_FOLLOWING_PAGE_ITEM = page -> ItemStackBuilder.of(Material.ARROW)
            .name(Component.text()
                    .color(NamedTextColor.AQUA)
                    .append(Component.text("--").decorate(TextDecoration.STRIKETHROUGH))
                    .append(Component.text(">"))
                    .build())
            .lore(Component.text("Switch to the following page.", NamedTextColor.WHITE))
            .lore(Component.empty())
            .lore(Component.empty())
            .lore(Component.text()
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Currently viewing page "))
                    .append(Component.text(page.current(), NamedTextColor.AQUA))
                    .append(Component.text('/'))
                    .append(Component.text(page.size(), NamedTextColor.AQUA))
                    .build())
            .build();

    public static final Function<PaginatedInfo, ItemStack> DEFAULT_PREVIOUS_PAGE_ITEM = page -> ItemStackBuilder.of(Material.ARROW)
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
                    .append(Component.text(page.current(), NamedTextColor.AQUA))
                    .append(Component.text('/'))
                    .append(Component.text(page.size(), NamedTextColor.AQUA))
                    .build())
            .build();

    public static PaginatedMenuBuilder create() {
        return new PaginatedMenuBuilder();
    }

    private int lines = DEFAULT_LINES;
    private Component title;
    private List<Integer> contentSlots = DEFAULT_CONTENT_SLOTS;

    private int followingPageSlot
            = DEFAULT_FOLLOWING_PAGE_SLOT;

    private int previousPageSlot
            = DEFAULT_PREVIOUS_PAGE_SLOT;

    private MenuPattern pattern = DEFAULT_PATTERN;
    private Function<PaginatedInfo, ItemStack> followingPageItem
            = DEFAULT_FOLLOWING_PAGE_ITEM;

    private Function<PaginatedInfo, ItemStack> previousPageItem
            = DEFAULT_PREVIOUS_PAGE_ITEM;

    private PaginatedMenuBuilder() {
    }

    public PaginatedMenuBuilder lines(int lines) {
        this.lines = lines;
        return this;
    }

    public PaginatedMenuBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public PaginatedMenuBuilder contentSlots(List<Integer> contentSlots) {
        this.contentSlots = ImmutableList.copyOf(contentSlots);
        return this;
    }

    public PaginatedMenuBuilder followingPageSlot(int followingPageSlot) {
        this.followingPageSlot = followingPageSlot;
        return this;
    }

    public PaginatedMenuBuilder previousPageSlot(int previousPageSlot) {
        this.previousPageSlot = previousPageSlot;
        return this;
    }

    public PaginatedMenuBuilder pattern(MenuPattern pattern) {
        this.pattern = Objects.requireNonNull(pattern, "pattern");
        return this;
    }

    public PaginatedMenuBuilder followingPageItem(Function<PaginatedInfo, ItemStack> followingPageItem) {
        this.followingPageItem = Objects.requireNonNull(followingPageItem, "followingPageItem");
        return this;
    }

    public PaginatedMenuBuilder previousPageItem(Function<PaginatedInfo, ItemStack> previousPageItem) {
        this.previousPageItem = Objects.requireNonNull(previousPageItem, "previousPageItem");
        return this;
    }

    public int getLines() {
        return this.lines;
    }

    public Component getTitle() {
        return this.title;
    }

    public List<Integer> getContentSlots() {
        return this.contentSlots;
    }

    public int getFollowingPageSlot() {
        return this.followingPageSlot;
    }

    public int getPreviousPageSlot() {
        return this.previousPageSlot;
    }

    public MenuPattern getPattern() {
        return this.pattern;
    }

    public Function<PaginatedInfo, ItemStack> getFollowingPageItem() {
        return this.followingPageItem;
    }

    public Function<PaginatedInfo, ItemStack> getPreviousPageItem() {
        return this.previousPageItem;
    }

    public PaginatedMenu build(Player player, Function<PaginatedMenu, List<Item>> content) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(this.title, "title");
        Objects.requireNonNull(this.contentSlots, "contentSlots");
        Objects.requireNonNull(this.pattern, "pattern");
        Objects.requireNonNull(this.followingPageItem, "followingPageItem");
        Objects.requireNonNull(this.previousPageItem, "previousPageItem");
        return new PaginatedMenu(content, player, this);
    }
}