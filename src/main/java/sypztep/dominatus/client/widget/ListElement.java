package sypztep.dominatus.client.widget;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a text element in a list.
 * Can have an optional icon and supports variable substitution.
 */
public record ListElement(Text text, Identifier icon, boolean isHeader) {

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
}
