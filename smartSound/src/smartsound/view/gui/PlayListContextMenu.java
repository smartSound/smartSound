/* 
 *	Copyright (C) 2012 André Becker
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import smartsound.settings.Global;
import smartsound.view.Action;


public class PlayListContextMenu extends JPopupMenu
{
	private JMenuItem saveItem;
    private JMenuItem loadItem;
    private PlayList list;
    protected GUIController controller;
	
    protected class DeleteAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent arg0)
        {
            list.removeSelectedEntries();
        }
        public DeleteAction()
        {
            super("Delete selected");
        }
    }

    protected class LoadAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if(result == 0)
                controller.getLoadAction().execute(chooser.getSelectedFile().getAbsolutePath());
        }
        
        public LoadAction()
        {
            super("Load");
        }
    }

    protected class ResetPluginAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent arg0)
        {
            try
            {
                Global.getInstance().removeProperty("plugin");
                JOptionPane.showMessageDialog(null, "Plugin successfully resetted. The next time you start smartSound you can choose a different plugin.");
            }
            catch(IOException e)
            {
                JOptionPane.showMessageDialog(null, "Error while resetting the plugin");
            }
        }

        public ResetPluginAction()
        {
            super("Reset plugin");
        }
    }

    protected class SaveAction extends AbstractAction
    {

        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(null);
            if(result == 0)
                controller.getSaveAction().execute(chooser.getSelectedFile().getAbsolutePath());
        }
        
        public SaveAction()
        {
            super("Save");
        }
    }


    public PlayListContextMenu(GUIController controller, PlayList list)
    {
        this.list = list;
        this.controller = controller;
        add(new JMenuItem(new DeleteAction()));
        addSeparator();
        add(new JMenuItem(new SaveAction()));
        add(new JMenuItem(new LoadAction()));
        addSeparator();
        add(new JMenuItem(new ResetPluginAction()));
    }

}
