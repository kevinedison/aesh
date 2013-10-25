/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.terminal;

import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.util.ANSI;

import java.io.PrintStream;

/**
 * Value object that describe how a string should be displayed
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class TerminalString {

    private String characters;
    private Color backgroundColor;
    private Color textColor;
    private TerminalTextStyle style;
    private boolean ignoreRendering;

    public TerminalString(String chars, Color backgroundColor, Color textColor,
                          TerminalTextStyle style) {
        this.characters = chars;
        this.style = style;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public TerminalString(String chars, Color backgroundColor, Color textColor) {
        this(chars, backgroundColor, textColor, new TerminalTextStyle());
    }

    public TerminalString(String chars, TerminalTextStyle style) {
        this(chars, Color.DEFAULT_BG, Color.DEFAULT_TEXT, style);
    }

    public TerminalString(String chars) {
        this(chars, Color.DEFAULT_BG, Color.DEFAULT_TEXT, new TerminalTextStyle());
    }

    public TerminalString(String chars, boolean ignoreRendering) {
        this(chars, Color.DEFAULT_BG, Color.DEFAULT_TEXT, new TerminalTextStyle());
        this.ignoreRendering = ignoreRendering;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String chars) {
        this.characters = chars;
    }

    public boolean containSpaces() {
        return characters.indexOf(Parser.SPACE_CHAR) > 0;
    }

    public void switchSpacesToEscapedSpaces() {
       characters = Parser.switchSpacesToEscapedSpacesInWord(characters);
    }

    public TerminalTextStyle getStyle() {
        return style;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public int getANSILength() {
        if(ignoreRendering)
            return 0;
        else
            return ANSI.getStart().length() + 8 + ANSI.reset().length();
    }

    public TerminalString cloneRenderingAttributes(String chars) {
        if(ignoreRendering)
            return new TerminalString(chars, true);
        else
            return new TerminalString(chars, backgroundColor, textColor, style);
    }

    /**
     * style, text color, background color
     */
    public String toString(TerminalString prev) {
        if(ignoreRendering)
            return characters;
        if(equalsIgnoreCharacter(prev))
            return characters;
        else {
            StringBuilder builder = new StringBuilder();
            builder.append(ANSI.getStart());
            builder.append(style.getValueComparedToPrev(prev.getStyle()));
            if(this.getTextColor() != prev.getTextColor() || prev.getStyle().isInvert())
                builder.append(';').append(this.getTextColor().getValue());
            if(this.getBackgroundColor() != prev.getBackgroundColor() || prev.getStyle().isInvert())
                builder.append(';').append(this.getBackgroundColor().getValue());

            builder.append('m');
            builder.append(getCharacters());
            return builder.toString();
        }
    }

    @Override
    public String toString() {
        if(ignoreRendering)
            return characters;
        StringBuilder builder = new StringBuilder();
        builder.append(ANSI.getStart());
        builder.append(style.toString()).append(';');
        builder.append(this.getTextColor().getValue()).append(';');
        builder.append(this.getBackgroundColor().getValue());
        builder.append('m');
        builder.append(getCharacters());
        //reset it to plain
        builder.append(ANSI.reset());
        return builder.toString();
    }

    public void write(PrintStream out) {
        if(ignoreRendering) {
            out.print(characters);
        }
        else {
            out.print(ANSI.getStart());
            out.print(style.toString());
            out.print(';');
            out.print(this.getTextColor().getValue());
            out.print(';');
            out.print(this.getBackgroundColor().getValue());
            out.print('m');
            out.print(getCharacters());
        }
    }

    public boolean equalsIgnoreCharacter(TerminalString that) {
        if (style != that.style) return false;
        if (ignoreRendering != that.ignoreRendering) return false;
        if (backgroundColor != that.backgroundColor) return false;
        if (textColor != that.textColor) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TerminalString)) return false;

        TerminalString that = (TerminalString) o;

        if(ignoreRendering) {
            return characters.equals(that.characters);
        }

        if (backgroundColor != that.backgroundColor) return false;
        if (!characters.equals(that.characters)) return false;
        if (textColor != that.textColor) return false;
        if (style != that.style) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = characters.hashCode();
        result = 31 * result + backgroundColor.hashCode();
        result = 31 * result + textColor.hashCode();
        result = 31 * result + style.hashCode();
        return result;
    }

}