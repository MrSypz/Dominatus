package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that displays text information in paragraphs.
 */
public class InfoPanel extends ScrollablePanel {
    private final List<Text> paragraphs = new ArrayList<>();
    private final int textColor;

    public InfoPanel(int x, int y, int width, int height, Text title) {
        this(x, y, width, height, title, 0xFFFFFFFF);
    }

    @Override
    protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public InfoPanel(int x, int y, int width, int height, Text title, int textColor) {
        super(x, y, width, height, title);
        this.textColor = textColor;
    }

    /**
     * Add a paragraph of text to the panel.
     */
    public void addParagraph(Text text) {
        paragraphs.add(text);
        updateContentHeight();
    }

    /**
     * Clear all paragraphs.
     */
    public void clearParagraphs() {
        paragraphs.clear();
        updateContentHeight();
    }

    /**
     * Update the content height based on the text.
     */
    private void updateContentHeight() {
        int height = 0;
        int availableWidth = getContentWidth() - 10; // account for scrollbar

        for (Text paragraph : paragraphs) {
            height += textRenderer.wrapLines(paragraph, availableWidth).size() * textRenderer.fontHeight;
            height += padding; // space between paragraphs
        }

        setContentHeight(height);
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = getContentX();
        int y = getContentY();
        int width = getContentWidth() - 10; // account for scrollbar

        // Apply scrolling offset
        int yOffset = -(int) scrollAmount;

        // Render each paragraph
        for (Text paragraph : paragraphs) {
            List<OrderedText> lines = textRenderer.wrapLines(paragraph, width);

            for (OrderedText line : lines) {
                if (yOffset + y >= getContentY() && yOffset + y + textRenderer.fontHeight <= getContentY() + getContentHeight()) {
                    context.drawText(textRenderer, line, x, y + yOffset, textColor, true);
                }
                yOffset += textRenderer.fontHeight;
            }

            yOffset += padding; // Add space between paragraphs
        }
    }
}