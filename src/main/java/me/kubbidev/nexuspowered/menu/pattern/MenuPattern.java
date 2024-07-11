package me.kubbidev.nexuspowered.menu.pattern;

import com.google.common.collect.ImmutableList;
import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.menu.Menu;
import me.kubbidev.nexuspowered.util.Text;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Helps to populate a menu with border items
 */
@NotNullByDefault
public class MenuPattern {
    private static final boolean[] EMPTY_SHAPE = new boolean[9];

    private final PatternMapping mapping;
    private final List<boolean[]> shapeRows = new ArrayList<>();

    public MenuPattern(@NotNull PatternMapping mapping) {
        this.mapping = mapping;
    }

    public MenuPattern() {
        this(PatternMapping.EMPTY);
    }

    public MenuPattern shape(String s) {
        char[] chars = Text.removeWhitespaces(s).toCharArray();
        if (chars.length != 9) {
            throw new IllegalArgumentException("invalid mask: " + s);
        }
        boolean[] ret = new boolean[9];
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case '1' -> ret[i] = true;
                case '0' -> ret[i] = false;
                default -> throw new IllegalArgumentException("invalid mask character: " + c);
            }
        }
        this.shapeRows.add(ret);
        return this;
    }

    public MenuPattern shapes(String... strings) {
        for (String s : strings) {
            shape(s);
        }
        return this;
    }

    public MenuPattern shapeEmpty(int lines) {
        for (int i = 0; i < lines; i++) {
            this.shapeRows.add(EMPTY_SHAPE);
        }
        return this;
    }

    public void apply(Menu menu) {
        forEachShapeIndex(i -> {
            Item item = this.mapping.get();
            if (item != null) {
                menu.setItem(i, item);
            }
        });
    }

    public List<Integer> getShapedIndexes() {
        List<Integer> result = new LinkedList<>();
        forEachShapeIndex(result::add);
        return result;
    }

    public ImmutableList<Integer> getShapedIndexesImmutable() {
        return ImmutableList.copyOf(getShapedIndexes());
    }

    private void forEachShapeIndex(Consumer<Integer> action) {
        int menuIndex = 0;
        for (boolean[] rowMasks : this.shapeRows) {
            for (boolean isMasked : rowMasks) {
                int index = menuIndex++;
                if (isMasked) action.accept(index);
            }
        }
    }

}