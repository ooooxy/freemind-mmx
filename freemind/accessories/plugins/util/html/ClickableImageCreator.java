/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/* $Id: ClickableImageCreator.java,v 1.1.2.1.12.4 2006-10-10 18:51:52 christianfoltin Exp $ */

package accessories.plugins.util.html;

import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MapView;

/** */
public class ClickableImageCreator {

    public static class AreaHolder {
        // <area shape="rect" href="#id47808" alt="Import/Export of parts of a
        // map" title="Import/Export of parts of a map" coords="" />
        // <area shape="rect" href="#id47708" alt="Screenshots cross-red coming
        // soon." title="Screenshots cross-red coming soon."
        // coords="699,143,835,168" />
        String shape = "rect";

        String href;

        String alt;

        String title;

        Rectangle coordinates = new Rectangle();

    }

    Vector area = new Vector();


    private final MindMapNode root;

    private final ModeController modeController;


    private Rectangle innerBounds;


    private final String regExpLinkReplacement;

    /**
     * @param regExpLinkReplacement if for example the link abc must be replaced with FMabcFM,
     * then this string has to be FM$1FM.
     */
    public ClickableImageCreator(MindMapNode root,
            ModeController modeController, String regExpLinkReplacement) {
        super();
        this.root = root;
        this.regExpLinkReplacement = regExpLinkReplacement;
		MapView view = modeController.getView();
		if (view != null) {
			innerBounds = view.getInnerBounds();
		} else {
			// test case: give any bounds:
			innerBounds = new Rectangle(0,0,100,100);
		}
		this.modeController = modeController;
        createArea();
    }

    public String generateHtml() {
        StringBuffer htmlArea = new StringBuffer();
        for (Iterator i = area.iterator(); i.hasNext();) {
            AreaHolder holder = (AreaHolder) i.next();
            htmlArea.append("<area shape=\"" + holder.shape + "\" href=\"#"
                    + holder.href.replaceFirst("^(.*)$", regExpLinkReplacement) + "\" alt=\""
                    + StringEscapeUtils.escapeHtml(holder.alt) + "\" title=\""
                    + StringEscapeUtils.escapeHtml(holder.title)
                    + "\" coords=\"" + holder.coordinates.x + ","
                    + holder.coordinates.y + ","
                    + (holder.coordinates.width + holder.coordinates.x) + ","
                    + +(holder.coordinates.height + holder.coordinates.y)
                    + "\" />");
        }
        return htmlArea.toString();
    }

    private void createArea() {
        createArea(root);
    }

    /**
     */
    private void createArea(MindMapNode node) {
        if (node != null && node.getViewer() != null) {
            AreaHolder holder = new AreaHolder();
            holder.title = node.getShortText(modeController);
            holder.alt = node.getShortText(modeController);
            holder.href = node.getObjectId(modeController);
            holder.coordinates.x = (int) (node.getViewer().getX()-innerBounds.getMinX());
            holder.coordinates.y = (int) (node.getViewer().getY()-innerBounds.getMinY());
            holder.coordinates.width = node.getViewer().getWidth();
            holder.coordinates.height = node.getViewer().getHeight();
            area.add(holder);
            for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
                MindMapNode child = (MindMapNode) i.next();
                createArea(child);
            }
        }
    }

}

