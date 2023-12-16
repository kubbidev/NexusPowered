package com.kubbidev.nexuspowered.paper.inventory.builder.item;

import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Item builder for {@link Material#WRITTEN_BOOK} and {@link Material#WRITTEN_BOOK} only
 */
public class BookBuilder extends BaseItemBuilder<BookBuilder> {

    private static final EnumSet<Material> BOOKS = EnumSet.of(Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);

    BookBuilder(ItemStack itemStack) {
        super(itemStack);
        if (!BOOKS.contains(itemStack.getType())) {
            throw new InventoryException("BookBuilder requires the material to be a WRITABLE_BOOK/WRITTEN_BOOK!");
        }
    }

    /**
     * Sets the author of the book. Removes author when given null.
     *
     * @param author the author to set
     * @return {@link BookBuilder}
     */
    public BookBuilder author(Component author) {
        BookMeta bookMeta = (BookMeta) getMeta();

        if (author == null) {
            bookMeta.setAuthor(null);
            setMeta(bookMeta);
            return this;
        }

        bookMeta.author(author);
        setMeta(bookMeta);
        return this;
    }

    /**
     * Sets the generation of the book. Removes generation when given null.
     *
     * @param generation the generation to set
     * @return {@link BookBuilder}
     */
    public BookBuilder generation(BookMeta.Generation generation) {
        BookMeta bookMeta = (BookMeta) getMeta();

        bookMeta.setGeneration(generation);
        setMeta(bookMeta);
        return this;
    }

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with
     * 256 characters per page.
     *
     * @param pages list of pages
     * @return {@link BookBuilder}
     */
    public BookBuilder page(Component... pages) {
        return page(Arrays.asList(pages));
    }

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with
     * 256 characters per page.
     *
     * @param pages list of pages
     * @return {@link BookBuilder}
     */
    public BookBuilder page(List<Component> pages) {
        BookMeta bookMeta = (BookMeta) getMeta();

        for (Component page : pages) {
            bookMeta.addPages(page);
        }

        setMeta(bookMeta);
        return this;
    }

    /**
     * Sets the specified page in the book. Pages of the book must be
     * contiguous.
     * <p>
     * The data can be up to 256 characters in length, additional characters
     * are truncated.
     * <p>
     * Pages are 1-indexed.
     *
     * @param page the page number to set, in range [1, {@link BookMeta#getPageCount()}]
     * @param data the data to set for that page
     * @return {@link BookBuilder}
     */
    public BookBuilder page(int page, Component data) {
        BookMeta bookMeta = (BookMeta) getMeta();

        bookMeta.page(page, data);
        setMeta(bookMeta);
        return this;
    }

    /**
     * Sets the title of the book.
     * <p>
     * Limited to 32 characters. Removes title when given null.
     *
     * @param title the title to set
     * @return {@link BookBuilder}
     */
    public BookBuilder title(Component title) {
        BookMeta bookMeta = (BookMeta) getMeta();

        if (title == null) {
            bookMeta.setTitle(null);
            setMeta(bookMeta);
            return this;
        }

        bookMeta.title(title);
        setMeta(bookMeta);
        return this;
    }
}
