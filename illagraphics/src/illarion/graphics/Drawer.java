/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

import java.nio.FloatBuffer;

/**
 * The drawer is used to draw a few primitive shapes that are used in the client
 * by using the current implementation of the graphic render engine.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface Drawer {
    /**
     * The size of the border that is added in case a rectangle with rounded
     * edges shall be drawn.
     */
    int ROUNDED_BORDER_WIDTH = 6;

    /**
     * Draw a simple dot at a specified location on the screen.
     * 
     * @param x x coordinate of the dot on the screen
     * @param y y coordinate of the dot on the screen
     * @param size size of the dot on the screen
     * @param color color of the dot on the screen
     */
    void drawDot(int x, int y, float size, SpriteColor color);

    /**
     * Draw a line from one point to another with a specified width.
     * 
     * @param x1 the x coordinate of the first point of the line
     * @param y1 the y coordinate of the first point of the line
     * @param x2 the x coordinate of the second point of the line
     * @param y2 the y coordinate of the second point of the line
     * @param width the width of the line
     * @param color the color of the line
     */
    void drawLine(int x1, int y1, int x2, int y2, float width,
        SpriteColor color);

    /**
     * Draw a quadrangle that is filled with the color set with the parameter.
     * This function offers the possibility to create a irregular shaped
     * quadrangle since all 4 dots can be set separately.
     * 
     * @param x1 the x coordinate of the first corner of the quadrangle
     * @param y1 the y coordinate of the first corner of the quadrangle
     * @param x2 the x coordinate of the second corner of the quadrangle
     * @param y2 the y coordinate of the second corner of the quadrangle
     * @param x3 the x coordinate of the third corner of the quadrangle
     * @param y3 the y coordinate of the third corner of the quadrangle
     * @param x4 the x coordinate of the fourth corner of the quadrangle
     * @param y4 the y coordinate of the fourth corner of the quadrangle
     * @param color color the color the quadrangle is filled with
     */
    void drawQuadrangle(int x1, int y1, int x2, int y2, int x3, int y3,
        int x4, int y4, SpriteColor color);

    /**
     * Draw a quadrangle frame. This function offers the possibility to create a
     * irregular shaped quadrangle since all 4 dots can be set separately.
     * 
     * @param x1 the x coordinate of the first corner of the quadrangle
     * @param y1 the y coordinate of the first corner of the quadrangle
     * @param x2 the x coordinate of the second corner of the quadrangle
     * @param y2 the y coordinate of the second corner of the quadrangle
     * @param x3 the x coordinate of the third corner of the quadrangle
     * @param y3 the y coordinate of the third corner of the quadrangle
     * @param x4 the x coordinate of the fourth corner of the quadrangle
     * @param y4 the y coordinate of the fourth corner of the quadrangle
     * @param color color the color the quadrangle line is drawn with
     */
    void drawQuadrangleFrame(int x1, int y1, int x2, int y2, int x3, int y3,
        int x4, int y4, SpriteColor color);

    /**
     * Draw a rectangle that is filled with a color.
     * 
     * @param x1 x coordinate of the first corner of the rectangle
     * @param y1 y coordinate of the first corner of the rectangle
     * @param x2 x coordinate of the second corner of the rectangle
     * @param y2 y coordinate of the second corner of the rectangle
     * @param color the color the rectangle is filled with
     */
    void drawRectangle(int x1, int y1, int x2, int y2, SpriteColor color);

    /**
     * Draw a rectangle frame, so just the border of the rectangle that is
     * transparent inside.
     * 
     * @param x1 x coordinate of the first corner of the rectangle
     * @param y1 y coordinate of the first corner of the rectangle
     * @param x2 x coordinate of the second corner of the rectangle
     * @param y2 y coordinate of the second corner of the rectangle
     * @param width the width of the line
     * @param color the color of the rectangle border line
     */
    void drawRectangleFrame(int x1, int y1, int x2, int y2, float width,
        SpriteColor color);

    /**
     * Draw a rounded rectangle. The size that is set by the parameters is the
     * inner rectangle that is not influenced by the rounded corners. The border
     * for the rounded edges is added to the size of the rectangle. So the
     * resulting object is slightly bigger then the size set here.
     * 
     * @param x1 x coordinate of the first corner of the rectangle
     * @param y1 y coordinate of the first corner of the rectangle
     * @param x2 x coordinate of the second corner of the rectangle
     * @param y2 y coordinate of the second corner of the rectangle
     * @param color the color the rectangle is filled with
     */
    void drawRoundedRectangle(int x1, int y1, int x2, int y2, SpriteColor color);

    /**
     * Draw a frame of the rounded rectangle that is drawn with
     * {@link #drawRoundedRectangle(int, int, int, int, SpriteColor)} on the
     * screen. This can be used to draw a thick frame around that rectangle.
     * 
     * @param x1 x coordinate of the first corner of the rectangle
     * @param y1 y coordinate of the first corner of the rectangle
     * @param x2 x coordinate of the second corner of the rectangle
     * @param y2 y coordinate of the second corner of the rectangle
     * @param width the width of the line
     * @param color the color the rectangle is filled with
     */
    void drawRoundedRectangleFrame(int x1, int y1, int x2, int y2,
        float width, SpriteColor color);

    /**
     * Draw a triangle that is filled with the color that is set as paremeter.
     * 
     * @param x1 x coordinate of the first corner of the triangle
     * @param y1 y coordinate of the first corner of the triangle
     * @param x2 x coordinate of the second corner of the triangle
     * @param y2 y coordinate of the second corner of the triangle
     * @param x3 x coordinate of the third corner of the triangle
     * @param y3 y coordinate of the third corner of the triangle
     * @param color color the color the triangle is filled with
     */
    void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3,
        SpriteColor color);

    /**
     * Draw a stripe of triangles using the coordinates from a buffer. The first
     * triangles builds up with the first 3 coordinates. The second triangle is
     * build with the coordinates 2, 3 and 4. The fourth with 3, 4 and 5.
     * 
     * @param coords the float buffer containing the coordinates
     * @param color the color that is used to draw the shape
     */
    void drawTriangles(FloatBuffer coords, SpriteColor color);
}
