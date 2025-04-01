package sypztep.dominatus.client.widget;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a text element in a list.
 * Can have an optional icon and supports variable substitution.
 */
public class ListElement {
    private final Text text;
    private final Identifier icon;
    private final boolean isHeader;

    private ListElement(Text text, Identifier icon, boolean isHeader) {
        this.text = text;
        this.icon = icon;
        this.isHeader = isHeader;
    }

    /**
     * Create a text element.
     */
    public static ListElement text(Text text) {
        return new ListElement(text, null, false);
    }

    /**
     * Create a header element.
     */
    public static ListElement header(Text text) {
        return new ListElement(text, null, true);
    }

    /**
     * Create an element with an icon.
     */
    public static ListElement withIcon(Text text, Identifier icon) {
        return new ListElement(text, icon, false);
    }

    public Text text() {
        return text;
    }

    public Identifier icon() {
        return icon;
    }

    public boolean isHeader() {
        return isHeader;
    }
}