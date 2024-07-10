package me.kubbidev.nexuspowered.menu.paginated;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.menu.Menu;
import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.menu.scheme.MenuScheme;
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

    private final MenuScheme scheme;
    private final List<Integer> itemSlots;

    private final int nextPageSlot;
    private final int previousPageSlot;
    private final Function<PageInfo, ItemStack> nextPageItem;
    private final Function<PageInfo, ItemStack> previousPageItem;

    private List<Item> content;
    private int page = 1;

    public PaginatedMenu(Function<PaginatedMenu, List<Item>> content, Player player, PaginatedMenuBuilder model) {
        super(player, model.getLines(), model.getTitle());

        this.content = ImmutableList.copyOf(content.apply(this));
        this.itemSlots = ImmutableList.copyOf(model.getItemSlots());

        this.nextPageSlot = model.getNextPageSlot();
        this.previousPageSlot = model.getPreviousPageSlot();

        this.nextPageItem = model.getNextPageItem();
        this.previousPageItem = model.getPreviousPageItem();
        this.scheme = model.getScheme();
    }

    public void updateContent(List<Item> content) {
        this.content = ImmutableList.copyOf(content);
    }

    @Override
    public void redraw() {
        this.scheme.apply(this);
        List<Integer> slots = new ArrayList<>(this.itemSlots);

        // work out the items to display on this page
        List<List<Item>> pages = Lists.partition(this.content, slots.size());
        normalizePage(pages.size());

        List<Item> page = pages.isEmpty() ? new ArrayList<>() : pages.get(this.page - 1);
        drawPageItems(pages.size());

        // remove previous items
        if (!isFirstDraw()) {
            slots.forEach(this::removeItem);
        }

        // place the actual items
        for (Item item : page) {
            setItem(slots.removeFirst(), item);
        }
    }

    private void normalizePage(int maxPages) {
        if (this.page < 1) {
            this.page = 1;
        }
        if (this.page > maxPages) {
            this.page = Math.max(1, maxPages);
        }
    }

    private void drawPageItems(int maxPages) {
        setItem(this.previousPageSlot, ItemStackBuilder.of(this.previousPageItem.apply(PageInfo.create(this.page, maxPages)))
                .build(() -> {
                    if (this.page > 1) {
                        this.page--;
                        redraw();
                    }
                }));

        setItem(this.nextPageSlot, ItemStackBuilder.of(this.nextPageItem.apply(PageInfo.create(this.page, maxPages)))
                .build(() -> {
                    if (this.page < maxPages) {
                        this.page++;
                        redraw();
                    }
                }));
    }
}