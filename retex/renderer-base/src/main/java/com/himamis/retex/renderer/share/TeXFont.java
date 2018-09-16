/* TeXFont.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

package com.himamis.retex.renderer.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.himamis.retex.renderer.share.character.Character;
import com.himamis.retex.renderer.share.exception.AlphabetRegistrationException;
import com.himamis.retex.renderer.share.exception.FontAlreadyLoadedException;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.exception.SymbolMappingNotFoundException;
import com.himamis.retex.renderer.share.exception.TextStyleMappingNotFoundException;
import com.himamis.retex.renderer.share.exception.XMLResourceParseException;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.share.platform.font.Font;

/**
 * The default implementation of the TeXFont-interface. All font information is
 * read from an xml-file.
 */
public class TeXFont {

	public static final int NO_FONT = -1;

	private static String[] defaultTextStyleMappings;

	/**
	 * No extension part for that kind (TOP,MID,REP or BOT)
	 */
	protected static final int NONE = -1;

	protected final static int NUMBERS = 0;
	protected final static int CAPITALS = 1;
	protected final static int SMALL = 2;
	protected final static int UNICODE = 3;

	// Number of font ids in a single font description file.
	// private static final int NUMBER_OF_FONT_IDS = 256;

	private static Map<String, CharFont[]> textStyleMappings;
	private static Map<String, CharFont> symbolMappings;
	public static ArrayList<FontInfo> fontInfo = new ArrayList<FontInfo>();
	private static Map<String, Double> parameters;
	private static Map<String, Number> generalSettings;

	// private static boolean magnificationEnable = true;

	protected static final int TOP = 0, MID = 1, REP = 2, BOT = 3;

	protected static final int WIDTH = 0, HEIGHT = 1, DEPTH = 2, IT = 3;

	public static List<Character.UnicodeBlock> loadedAlphabets = new ArrayList<Character.UnicodeBlock>();

	protected double factor = 1;

	public boolean isBold = false;
	public boolean isRoman = false;
	public boolean isSs = false;
	public boolean isTt = false;
	public boolean isIt = false;

	static {
		DefaultTeXFontParser parser = new DefaultTeXFontParser();
		// load LATIN block
		loadedAlphabets.add(Character.UnicodeBlock.of('a'));
		// fonts + font descriptions
		parser.parseFontDescriptions(fontInfo);
		// general font parameters
		parameters = parser.parseParameters();
		// text style mappings
		textStyleMappings = parser.parseTextStyleMappings();
		// default text style : style mappings
		defaultTextStyleMappings = parser.parseDefaultTextStyleMappings();
		// symbol mappings
		symbolMappings = parser.parseSymbolMappings();
		// general settings
		generalSettings = parser.parseGeneralSettings();
		generalSettings.put("textfactor", 1);

		// check if mufontid exists
		int muFontId = generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR)
				.intValue();
		if (muFontId < 0 || muFontId >= fontInfo.size()
				|| fontInfo.get(muFontId) == null) {
			throw new XMLResourceParseException(
					DefaultTeXFontParser.RESOURCE_NAME,
					DefaultTeXFontParser.GEN_SET_EL,
					DefaultTeXFontParser.MUFONTID_ATTR,
					"contains an unknown font id!");
		}
	}

	private final double size; // standard size

	public static Font getFont(int fontId) {
		FontInfo info = fontInfo.get(fontId);
		Font font = info.getFont();
		return font;
	}

	public TeXFont(double pointSize) {
		size = pointSize;
	}

	public TeXFont(double pointSize, boolean b, boolean rm, boolean ss,
			boolean tt, boolean it) {
		this(pointSize, 1, b, rm, ss, tt, it);
	}

	public TeXFont(double pointSize, double f, boolean b, boolean rm,
			boolean ss, boolean tt, boolean it) {
		size = pointSize;
		factor = f;
		isBold = b;
		isRoman = rm;
		isSs = ss;
		isTt = tt;
		isIt = it;
	}

	public TeXFont copy() {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public TeXFont deriveFont(double size) {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public TeXFont scaleFont(double factor) {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public double getScaleFactor() {
		return factor;
	}

	public double getAxisHeight(int style) {
		return getParameter("axisheight") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing1(int style) {
		return getParameter("bigopspacing1") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing2(int style) {
		return getParameter("bigopspacing2") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing3(int style) {
		return getParameter("bigopspacing3") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing4(int style) {
		return getParameter("bigopspacing4") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing5(int style) {
		return getParameter("bigopspacing5") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	private Char getChar(char c, CharFont[] cf, int style) {
		int kind, offset;
		if (c >= '0' && c <= '9') {
			kind = NUMBERS;
			offset = c - '0';
		} else if (c >= 'a' && c <= 'z') {
			kind = SMALL;
			offset = c - 'a';
		} else if (c >= 'A' && c <= 'Z') {
			kind = CAPITALS;
			offset = c - 'A';
		} else {
			kind = UNICODE;
			offset = c;
		}

		// if the mapping for the character's range, then use the default style
		if (cf[kind] == null) {
			return getDefaultChar(c, style);
		}
		return getChar(
				new CharFont((char) (cf[kind].c + offset), cf[kind].fontId),
				style);
	}

	public Char getChar(char c, int textStyle0, int style)
			throws TextStyleMappingNotFoundException {
		// XXX
		String textStyle = TextStyle.getStyle(textStyle0);

		return getChar(c, textStyle, style);
	}

	public Char getChar(char c, String textStyle, int style)
			throws TextStyleMappingNotFoundException {


		Object mapping = textStyleMappings.get(textStyle);
		if (mapping == null) {
			throw new TextStyleMappingNotFoundException(textStyle);
		}
		return getChar(c, (CharFont[]) mapping, style);
	}

	public Char getChar(CharFont cf0, int style) {
		double fsize = getSizeFactor(style);
		int id = isBold ? cf0.boldFontId : cf0.fontId;
		FontInfo info = fontInfo.get(id);
		CharFont cf = cf0;
		if (isBold && cf.fontId == cf.boldFontId) {
			id = info.getBoldId();
			info = fontInfo.get(id);
			cf = new CharFont(cf.c, id, style);
		}
		if (isRoman) {
			id = info.getRomanId();
			info = fontInfo.get(id);
			cf = new CharFont(cf.c, id, style);
		}
		if (isSs) {
			id = info.getSsId();
			info = fontInfo.get(id);
			cf = new CharFont(cf.c, id, style);
		}
		if (isTt) {
			id = info.getTtId();
			info = fontInfo.get(id);
			cf = new CharFont(cf.c, id, style);
		}
		if (isIt) {
			id = info.getItId();
			info = fontInfo.get(id);
			cf = new CharFont(cf.c, id, style);
		}
		Font font = info.getFont();
		return new Char(cf.c, font, id, getMetrics(cf, factor * fsize));
	}

	public Char getChar(String symbolName, int style)
			throws SymbolMappingNotFoundException {
		Object obj = symbolMappings.get(symbolName);
		if (obj == null) {// no symbol mapping found!
			// for (Entry<String, CharFont> e : symbolMappings.entrySet()) {
			// System.out.println(e.getKey() + " , " + e.getValue());
			// }
			throw new SymbolMappingNotFoundException(symbolName);
		}
		return getChar((CharFont) obj, style);
	}

	public Char getDefaultChar(char c, int style) {
		// these default text style mappings will always exist,
		// because it's checked during parsing
		if (c >= '0' && c <= '9') {
			return getChar(c, defaultTextStyleMappings[NUMBERS], style);
		} else if (c >= 'a' && c <= 'z') {
			return getChar(c, defaultTextStyleMappings[SMALL], style);
		} else {
			return getChar(c, defaultTextStyleMappings[CAPITALS], style);
		}
	}

	public double getDefaultRuleThickness(int style) {
		return getParameter("defaultrulethickness") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getDenom1(int style) {
		return getParameter("denom1") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getDenom2(int style) {
		return getParameter("denom2") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public Extension getExtension(Char c, int style) {
		Font f = c.getFont();
		int fc = c.getFontCode();
		double s = getSizeFactor(style);

		// construct Char for every part
		FontInfo info = fontInfo.get(fc);
		int[] ext = info.getExtension(c.getChar());
		Char[] parts = new Char[ext.length];
		for (int i = 0; i < ext.length; i++) {
			if (ext[i] == NONE) {
				parts[i] = null;
			} else {
				parts[i] = new Char((char) ext[i], f, fc,
						getMetrics(new CharFont((char) ext[i], fc), s));
			}
		}

		return new Extension(parts[TOP], parts[MID], parts[REP], parts[BOT]);
	}

	public double getKern(CharFont left, CharFont right, int style) {
		if (left.fontId == right.fontId) {
			FontInfo info = fontInfo.get(left.fontId);
			return info.getKern(left.c, right.c,
					getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
		}
		return 0;
	}

	public CharFont getLigature(CharFont left, CharFont right) {
		if (left.fontId == right.fontId) {
			FontInfo info = fontInfo.get(left.fontId);
			return info.getLigature(left.c, right.c);
		}
		return null;
	}

	private static Metrics getMetrics(CharFont cf, double size) {
		FontInfo info = fontInfo.get(cf.fontId);
		double[] m = info.getMetrics(cf.c);
		if (m == null) {
			return new Metrics(1, 1, 0, 0, size * TeXFormula.PIXELS_PER_POINT,
					size);
		}
		return new Metrics(m[WIDTH], m[HEIGHT], m[DEPTH], m[IT],
				size * TeXFormula.PIXELS_PER_POINT, size);
	}

	public int getMuFontId() {
		return generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR)
				.intValue();
	}

	public Char getNextLarger(Char c, int style) {
		FontInfo info = fontInfo.get(c.getFontCode());
		CharFont ch = info.getNextLarger(c.getChar());
		FontInfo newInfo = fontInfo.get(ch.fontId);
		return new Char(ch.c, newInfo.getFont(), ch.fontId,
				getMetrics(ch, getSizeFactor(style)));
	}

	public double getNum1(int style) {
		return getParameter("num1") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getNum2(int style) {
		return getParameter("num2") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getNum3(int style) {
		return getParameter("num3") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getQuad(int style, int fontCode) {
		FontInfo info = fontInfo.get(fontCode);
		return info.getQuad(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getQuad(int style) {
		return getQuad(style, getMuFontId());
	}

	public double getSize() {
		return size;
	}

	public double getSkew(CharFont cf, int style) {
		FontInfo info = fontInfo.get(cf.fontId);
		char skew = info.getSkewChar();
		if (skew == -1) {
			return 0;
		}
		return getKern(cf, new CharFont(skew, cf.fontId), style);
	}

	public double getSpace(int style) {
		int spaceFontId = generalSettings
				.get(DefaultTeXFontParser.SPACEFONTID_ATTR)
				.intValue();
		FontInfo info = fontInfo.get(spaceFontId);
		return info
				.getSpace(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getSub1(int style) {
		return getParameter("sub1") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSub2(int style) {
		return getParameter("sub2") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSubDrop(int style) {
		return getParameter("subdrop") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup1(int style) {
		return getParameter("sup1") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup2(int style) {
		return getParameter("sup2") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup3(int style) {
		return getParameter("sup3") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getSupDrop(int style) {
		return getParameter("supdrop") * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getXHeight(int style, int fontCode) {
		FontInfo info = fontInfo.get(fontCode);
		return info
				.getXHeight(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getEM(int style) {
		return getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public boolean hasNextLarger(Char c) {
		FontInfo info = fontInfo.get(c.getFontCode());
		return (info.getNextLarger(c.getChar()) != null);
	}

	public void setBold(boolean bold) {
		isBold = bold;
	}

	public boolean getBold() {
		return isBold;
	}

	public void setRoman(boolean rm) {
		isRoman = rm;
	}

	public boolean getRoman() {
		return isRoman;
	}

	public void setTt(boolean tt) {
		isTt = tt;
	}

	public boolean getTt() {
		return isTt;
	}

	public void setIt(boolean it) {
		isIt = it;
	}

	public boolean getIt() {
		return isIt;
	}

	public void setSs(boolean ss) {
		isSs = ss;
	}

	public boolean getSs() {
		return isSs;
	}

	public boolean hasSpace(int font) {
		FontInfo info = fontInfo.get(font);
		return info.hasSpace();
	}

	public boolean isExtensionChar(Char c) {
		FontInfo info = fontInfo.get(c.getFontCode());
		return info.getExtension(c.getChar()) != null;
	}

	// public static void setMathSizes(double ds, double ts, double ss, double
	// sss) {
	// if (magnificationEnable) {
	// generalSettings.put("scriptfactor", Math.abs(ss / ds));
	// generalSettings.put("scriptscriptfactor", Math.abs(sss / ds));
	// generalSettings.put("textfactor", Math.abs(ts / ds));
	// TeXIcon.defaultSize = Math.abs(ds);
	// }
	// }

	// public static void setMagnification(double mag) {
	// if (magnificationEnable) {
	// TeXIcon.magFactor = mag / 1000f;
	// }
	// }

	// public static void enableMagnification(boolean b) {
	// magnificationEnable = b;
	// }

	private static double getParameter(String parameterName) {
		Object param = parameters.get(parameterName);
		if (param == null) {
			return 0;
		}
		return ((Double) param).doubleValue();
	}

	public static double getSizeFactor(int style) {
		if (style < TeXConstants.STYLE_TEXT) {
			return 1;
		} else if (style < TeXConstants.STYLE_SCRIPT) {
			return generalSettings.get("textfactor").doubleValue();
		} else if (style < TeXConstants.STYLE_SCRIPT_SCRIPT) {
			return generalSettings.get("scriptfactor").doubleValue();
		} else {
			return generalSettings.get("scriptscriptfactor").doubleValue();
		}
	}

	public double getMHeight(int style) {
		return getFontInfo(TextStyle.getDefault(TextStyle.CAPITALS).getFontId())
				.getHeight('M') * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public FontInfo getFontInfo(final int i) {
		// XXX
		// return Configuration.get().getFontInfo(i);
		return TeXFont.fontInfo.get(i);
	}

}
