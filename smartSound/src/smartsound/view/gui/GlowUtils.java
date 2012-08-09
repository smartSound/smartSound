/*
 *	Copyright (C) 2012 Markus Niedermann
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package smartsound.view.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * This is a simple class that provides methods to add or get a glow outside
 * opaque or partial transparent shapes in a BufferedImage.
 * 
 * @author Markus Niedermann
 * 
 */
public class GlowUtils {
	/**
	 * Adds a shadow outside opaque or partial transparent shapes. AntiAliasing
	 * is ON.
	 * 
	 * @param src
	 *            source image
	 * @param shadowColor
	 *            color of the shadow
	 * @param distance
	 *            distance between shadow and shape
	 * @param shadowRadius
	 *            blur radius for the shadow
	 * @param lift
	 *            true, if shape should be placed higher than in the original
	 *            shape, false if shadow should be placed lower
	 * @return
	 */
	public static BufferedImage addShadow(final BufferedImage src,
			final Color shadowColor, final int distance,
			final int shadowRadius, final boolean lift) {
		return addShadow(src, shadowColor, distance, shadowRadius, 0, true,
				lift);
	}

	/**
	 * Adds a shadow outside opaque or partial transparent shapes.
	 * 
	 * @param src
	 *            source image
	 * @param shadowColor
	 *            color of the shadow
	 * @param distance
	 *            distance between shadow and shape
	 * @param shadowRadius
	 *            blur radius for the shadow
	 * @param opaqueLimit
	 *            sets the opacity limit. range: 0-255
	 * 
	 * @param useAntiAliasing
	 *            true, if anti aliasing should be used.
	 * @param lift
	 *            true, if shape should be placed higher than in the original
	 *            shape, false if shadow should be placed lower
	 * @return
	 */
	public static BufferedImage addShadow(final BufferedImage src,
			final Color shadowColor, final int distance,
			final int shadowRadius, final int opaqueLimit,
			final boolean useAntiAliasing, final boolean lift) {

		BufferedImage ret = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		BufferedImage opaque = smaller(
				getOpaqueImage(src, shadowColor, opaqueLimit),
				shadowRadius / 2, opaqueLimit);
		BufferedImage border = getGlow(opaque, shadowColor, shadowRadius,
				opaqueLimit, useAntiAliasing);

		Graphics2D g2d = (Graphics2D) ret.getGraphics();
		int x1 = lift ? -distance : 0;
		int x0 = lift ? 0 : distance;
		int y1 = lift ? -distance : 0;
		int y0 = lift ? 0 : distance;

		g2d.drawImage(opaque, x0, y0, null);

		g2d.drawImage(border, x0, y0, null);
		g2d.drawImage(src, x1, y1, null);

		return ret;
	}

	/**
	 * Adds a Glow outside opaque or partial transparent shapes. AntiAliasing is
	 * ON
	 * 
	 * @param src
	 *            source image
	 * @param glowColor
	 *            color of the glow
	 * @param radius
	 *            radius of the glow
	 * @return new BufferedImage, containing glow and source image
	 */
	public static BufferedImage addGlow(final BufferedImage src,
			final Color glowColor, final int radius) {
		return addGlow(src, glowColor, radius, 0, true);
	}

	/**
	 * 
	 * Adds a Glow outside opaque or partial transparent shapes.
	 * 
	 * @param src
	 *            source image
	 * @param glowColor
	 *            color of the glow
	 * @param radius
	 *            radius of the glow
	 * @param opaqueLimit
	 *            sets the opacity limit. range: 0-255
	 * @param useAntiAliasing
	 *            true, if anti aliasing should be used.
	 * @return new BufferedImage, containing glow and source image
	 */
	public static BufferedImage addGlow(final BufferedImage src,
			final Color glowColor, final int radius, final int opaqueLimit,
			final boolean useAntiAliasing) {
		BufferedImage ret = getGlow(src, glowColor, radius, opaqueLimit,
				useAntiAliasing);
		Graphics2D g2d = (Graphics2D) ret.getGraphics();
		g2d.drawImage(src, 0, 0, null);
		return ret;
	}

	/**
	 * 
	 * Returns a Glow outside opaque or partial transparent shapes.
	 * 
	 * @param src
	 *            source image
	 * @param glowColor
	 *            color of the glow
	 * @param radius
	 *            radius of the glow
	 * @param opaqueLimit
	 *            sets the opacity limit. range: 0-255
	 * @param useAntiAliasing
	 *            true, if anti aliasing should be used.
	 * @return new BufferedImage, containing glow
	 */
	public static BufferedImage getGlow(final BufferedImage src,
			final Color glowColor, final int radius, final int opaqueLimit,
			final boolean useAntiAliasing) {
		BufferedImage ret = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		return getGlow(src, glowColor, radius, opaqueLimit, useAntiAliasing,
				ret);

	}

	private static BufferedImage getGlow(final BufferedImage src,
			final Color glowColor, final int radius, final int opaqueLimit,
			final boolean useAntiAliasing, final BufferedImage ret) {

		boolean[][] opaque = getOpaqueMask(src, opaqueLimit);
		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				if (((src.getRGB(x, y) >> 24) & 0xff) > opaqueLimit) {
					if (isBorder(x, y, src, opaqueLimit)) {
						addGlow(ret, x, y, radius, glowColor.getRGB(), opaque);
					}
				}

			}
		}

		if (useAntiAliasing) {
			setRenderHints(ret);
		}

		return ret;

	}

	private static BufferedImage smaller(final BufferedImage src,
			final int radius, final int opaqueLimit) {

		BufferedImage ret = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				if (((src.getRGB(x, y) >> 24) & 0xff) > opaqueLimit
						&& checkRadius(src, x, y, opaqueLimit, radius)) {
					ret.setRGB(x, y, src.getRGB(x, y));
				}

			}
		}
		return ret;
	}

	private static boolean checkRadius(final BufferedImage src, final int x,
			final int y, final int opaqueLimit, final int radius) {
		int minX = Math.max(x - radius, 0);
		int maxX = Math.min(src.getWidth(), x + radius);

		int minY = Math.max(y - radius, 0);
		int maxY = Math.min(src.getHeight(), y + radius);

		for (int curY = minY; curY < maxY; curY++) {
			for (int curX = minX; curX < maxX; curX++) {
				if (getDistance(curX, curY, x, y, radius) <= 1
						&& ((src.getRGB(curX, curY) >> 24) & 0xff) <= opaqueLimit) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean[][] getOpaqueMask(final BufferedImage src,
			final int opaqueLimit) {
		boolean[][] opaque = new boolean[src.getHeight()][src.getWidth()];
		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				if (((src.getRGB(x, y) >> 24) & 0xff) > opaqueLimit) {
					opaque[y][x] = true;
				}

			}
		}
		return opaque;
	}

	private static BufferedImage getOpaqueImage(final BufferedImage src,
			final Color col, final int opaqueLimit) {
		BufferedImage ret = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				if (((src.getRGB(x, y) >> 24) & 0xff) > opaqueLimit) {
					ret.setRGB(x, y, col.getRGB());
				}

			}
		}
		return ret;
	}

	private static boolean isBorder(final int x, final int y,
			final BufferedImage main, final int opaqueLimit) {
		if (isTransparent(x - 1, y - 1, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x, y - 1, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x + 1, y - 1, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x - 1, y, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x + 1, y, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x - 1, y + 1, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x, y + 1, main, opaqueLimit)) {
			return true;
		}
		if (isTransparent(x + 1, y + 1, main, opaqueLimit)) {
			return true;
		}
		return false;

	}

	private static boolean isTransparent(final int x, final int y,
			final BufferedImage main, final int opaqueLimit) {
		if (x >= 0 && x < main.getWidth() && y >= 0 && y < main.getHeight()) {
			if (((main.getRGB(x, y) >> 24) & 0xff) <= opaqueLimit) {
				return true;
			}
		}
		return false;
	}

	private static void addGlow(final BufferedImage glow, final int x,
			final int y, final int radius, final int color,
			final boolean[][] opaque) {
		int minX = Math.max(x - radius, 0);
		int maxX = Math.min(glow.getWidth(), x + radius);

		int minY = Math.max(y - radius, 0);
		int maxY = Math.min(glow.getHeight(), y + radius);

		for (int curY = minY; curY < maxY; curY++) {
			for (int curX = minX; curX < maxX; curX++) {
				int curColor = getColor(curX, curY, radius, x, y, color);
				if (!opaque[curY][curX]
						&& ((curColor >> 24) & 0xff) > ((glow
								.getRGB(curX, curY) >> 24) & 0xff)) {
					glow.setRGB(curX, curY, curColor);
				}
			}
		}
	}

	private static int getColor(final int curX, final int curY,
			final int radius, final int x, final int y, final int color) {
		double distance = getDistance(curX, curY, x, y, radius);
		if (distance > 1)
			return 0;
		int alpha = (color >> 24) & 0xff;

		return ((color & 0x00ffffff) + ((int) (alpha * (1.0 - distance)) << 24));

	}

	private static double getDistance(final int curX, final int curY,
			final int x, final int y, final int radius) {
		return Math.sqrt((curX - x) * (curX - x) + (curY - y)
				* (curY - y))
				/ radius;
	}

	private static void setRenderHints(final BufferedImage img) {
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

	}
}
