package sypztep.dominatus.client.widget;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record ListElement(Text text, Identifier icon) {
    public ListElement(Text text) {
        this(text,null);
    }
}