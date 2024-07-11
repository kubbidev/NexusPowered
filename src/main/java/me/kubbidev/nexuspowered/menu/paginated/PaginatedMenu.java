package me.kubbidev.nexuspowered.menu.paginated;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.menu.Menu;
import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.menu.pattern.MenuPattern;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Extension of {@link Menu} which automatically paginates {@link Item}s.
 */
@NotNullByDefault
public class PaginatedMenu extends Menu {

    private final MenuPattern pattern;
    private final List<Integer> contentSlots;

    private final int followingPageSlot;
    private final int previousPageSlot;
    private final Function<PaginatedInfo, ItemStack> followingPageItem;
    private final Function<PaginatedInfo, ItemStack> previousPageItem;

    private List<Item> content;
    private int page = 1;

    public PaginatedMenu(Function<PaginatedMenu, List<Item>> content, Player player, PaginatedMenuBuilder model) {
        super(player, model.getLines(), model.getTitle());
        this.content = ImmutableList.copyOf(content.apply(this));
        this.contentSlots = ImmutableList.copyOf(model.getContentSlots());

        this.followingPageSlot = model.getFollowingPageSlot();
        this.previousPageSlot = model.getPreviousPageSlot();

        this.followingPageItem = model.getFollowingPageItem();
        this.previousPageItem = model.getPreviousPageItem();
        this.pattern = model.getPattern();
    }

    public void updateContent(List<Item> content) {
        this.content = ImmutableList.copyOf(content);
    }

    @Override
    public void redraw() {
        this.pattern.apply(this);
        List<Integer> slots = new ArrayList<>(this.contentSlots);

        // calculate pages and pagination info
        List<List<Item>> pages = Lists.partition(this.content, slots.size());
        PaginatedInfo info = new PaginatedInfo(this.page, pages.size());
        normalizePage(info.size());

        // clear old items if not the first draw
        if (!isFirstDraw()) {
            slots.forEach(this::removeItem);
        }
        // draw items for the current page
        List<Item> currentPage = pages.isEmpty() ? new ArrayList<>() : pages.get(this.page - 1);
        for (Item item : currentPage) {
            setItem(slots.removeFirst(), item);
        }
        // draw pagination controls
        drawPageItems(info);
    }

    private void normalizePage(int maxPages) {
        if (this.page < 1) {
            this.page = 1;
        } else if (this.page > maxPages) {
            this.page = Math.max(1, maxPages);
        }
    }

    private void drawPageItems(PaginatedInfo info) {
        setItem(this.previousPageSlot, ItemStackBuilder.of(this.previousPageItem.apply(info))
                .build(() -> {
                    if (this.page > 1) {
                        this.page--;
                        redraw();
                    }
                }));

        setItem(this.followingPageSlot, ItemStackBuilder.of(this.followingPageItem.apply(info))
                .build(() -> {
                    if (this.page < info.size()) {
                        this.page++;
                        redraw();
                    }
                }));
    }
}