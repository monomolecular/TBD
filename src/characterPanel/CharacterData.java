package characterPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

//import static characterPanel.CharacterPanel.glyphArray;
import static groovy.json.StringEscapeUtils.escapeJava;

// ToDo: Convert characters back to strings.
// I may want to use a String rather than a char anyways, since one _glyph_ can be made of many code points (think
// about radicals and combining marks). A String is a fairly obvious way to package a sequence of code points, since
// that's what they are. Storing options: '\u00A7', same as any other character or use the numeric value (0x00A7), or
// use a source encoding where you can insert U+00A7 directly into the source code.


public class CharacterData {
    // The following three variables were in the original CharacterData class which came with ASCIIPanel.
    // ...and they don't seem to be used.
    public char character;
    public Color foregroundColor;
    public Color backgroundColor;

    // Basic test main() to check things are working.
    public static void main(String[] argv) throws Exception {

        BufferedImage test1 = createFontGlyph("\u263a", "DejaVu Sans Mono", 15, 0, true);    // 9786 = ☺ (Smiley FAce)
        BufferedImage test2 = createFontGlyph("\u00a5", "DejaVu Sans Mono", 15, 0, true);    // 165 = ¥ (yen Symbol)
    }

    /**
     * Create a glyph from a text string, passing in...
     * @param hex - Hex for the unicode character, e.g. \u235f = ⍟ (circled star), \u00a5 = ¥ (yen Symbol)
     * @param fontName - The name of the font, e.g. DejaVu Sans Mono
     * @param fontSize - The font size. Size 18 ends up being 11 x 22, size 34 is 20 x 40
     * @param debug - If debug=true then print some useful info to the commandline and save the glyph as a PNG file.
     */
    public static BufferedImage createFontGlyph(String hex, String fontName, int fontSize,
                                                int fontPadding, boolean debug) {
        /**
         * Best text->image info I found...
         * http://stackoverflow.com/questions/18800717/convert-text-content-to-image
         *
         * DejaVu LGC Sans Mono is a good looking "complete" Unicode, Mono-spaced font.
         * http://dejavu-fonts.org/
         * http://www.fonts2u.com/search.html?q=dejavu+sans+mono
         */
        Font font = new Font(fontName, Font.PLAIN, fontSize);

        /**
         * Because font metrics is based on a graphics context, we need to create a small, temporary image so we can
         * ascertain the width and height of the final image.
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);

        /**
         * The FontMetrics class defines a font metrics object, which encapsulates information about the rendering of a
         * particular font on a particular screen.
         */
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(hex);
        int height = fm.getHeight();

        // Pad the image a bit...
        Dimension dimension = new Dimension(width + (2 * fontPadding), height + (2 * fontPadding));
        int paddedWidth = (int)dimension.getWidth();
        int paddedHeight = (int)dimension.getHeight();

        g2d.dispose();

        // Find the code point for the character.
        int codePoint = hex.codePointAt(0);

        /**
         * Create a 2nd new image, this time to really hold the glyph.
         * TYPE_INT_ARGB represents an image with 8-bit RGBA color components packed into integer pixels.
         */
        img = new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();

        // Alpha interpolation blending algorithms are chosen with a preference for precision and
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        // Rendering is done with antialiasing.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Perform the color conversion calculations with the highest accuracy and visual quality.
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        // Dither when rendering geometry, if needed.
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        // Character glyphs are positioned with sub-pixel accuracy.
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // Eliminate blocky edges when scaling, subtle discontinuities along the horizontal/vertical edges may remain.
        // Interpolate color from 9 nearby samples when scaling.
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // Rendering algorithms are chosen with a preference for output quality.
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // Stroke normalization control hint value -- geometry should be left unmodified and rendered with sub-pixel accuracy.
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();

        // Fill the padded image with black.
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,paddedWidth,paddedHeight);

        // Swap the foreground and background text colors.
        HashMap<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
        font = font.deriveFont(map);
        g2d.setFont(font);

        // Write out the glyph string into the center of the image.
        g2d.drawString(hex, fontPadding, fm.getAscent() + fontPadding);

        g2d.dispose();

        if (debug) {
            Instant timestamp = Instant.now();
            Date date = Date.from(timestamp);
            Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");

            System.out.println("createUnicodeGlyph...");
            System.out.println(Character.getName(codePoint) + " (Category: " + Character.getType(codePoint) + ")");
            System.out.println("Font Name: " + font.getFontName());
            System.out.println("Character: " + hex + " (Hex: " + escapeJava(hex) +
                    ", CodePoint: " + codePoint + ")");
            System.out.println("Font Size: " + fontSize + ", Font Width: " + paddedWidth + ", " +
                    "Font Height: " + paddedHeight + " (pad: " + fontPadding + ")");
            System.out.println("File saved... " + codePoint + "-" + Character.getName(codePoint)
                    + formatter.format(date) + ".png");
            System.out.println();

            try {
                ImageIO.write(img, "png", new File(fontName + "-" + codePoint + "-" + Character.getName(codePoint) + "-"
                        + formatter.format(date) + ".png"));
            } catch (IOException eio) {
                eio.printStackTrace();
            }
        }

        return img;
    }

    //********************************************************************************************
    // HELPER FUNCTIONS - These are all overloaded versions of the primary createTextGlyph method.
    //********************************************************************************************
    public static BufferedImage createFontGlyph(String hex, String fontName, int fontSize,
                                                int fontPadding) {
        // Default the debug parameter to false by overloading.
        return createFontGlyph(hex, fontName, fontSize, fontPadding, false);
    }

    public static BufferedImage createFontGlyph(int codePoint, String fontName, int fontSize,
                                                int fontPadding) {
        // Default the debug parameter to false by overloading.
        String hex = Integer.toHexString(codePoint);
        return createFontGlyph(hex, fontName, fontSize, fontPadding, false);
    }

    public static BufferedImage createFontGlyph(int codePoint, String fontName, int fontSize,
                                                int fontPadding, boolean debug) {
        // Convert the code point to a hex representation for use by the main method.
        String hex = new String(Character.toChars(codePoint));
        return createFontGlyph(hex, fontName, fontSize, fontPadding, debug);
    }

    /**
     * LEGACY METHOD - This came with the original ASCII Panel implementation.
     * I'm trying to ditch it for an as needed load of individual unicode characters, converting from text directly to
     * character glyphs.
     *
     * This one loads the glyphs from an image of the CP437 character set.
     */
    /*private static BufferedImage[] loadCP437(int startChar, int endChar, String spriteFile) {
        BufferedImage glyphSprite = null;
        int charWidth = CharacterPanel.charWidth = 9;
        int charHeight = CharacterPanel.charHeight = 16;

        try {
            glyphSprite = ImageIO.read(CharacterPanel.class.getResource(spriteFile));
        } catch (IOException e) {
            System.err.println("loadCP437(): " + e.getMessage());
        }


        // CP437 is 32 characters wide and 8 characters high. startChar (0), endChar (255)
        for (int i = startChar; i < endChar; i++) {
            int sx = (i % 32) * charWidth + 8;
            int sy = (i / 32) * charHeight + 8;
            String hex = Integer.toHexString(i);

            glyphArray[i] = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
            glyphArray[i].getGraphics().drawImage(glyphSprite, 0, 0, charWidth, charHeight, sx, sy,
                    sx + charWidth, sy + charHeight, null);

            // Write BufferedImage to file
            try {
                ImageIO.write(glyphArray[i], "png", new File(i + "-" + hex + "-cp437.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return glyphArray;
    }*/
    //********************************************************************************************

}