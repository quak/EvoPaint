/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * 
 *  This file is part of EvoPaint.
 * 
 *  EvoPaint is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EvoPaint.  If not, see <http://www.gnu.org/licenses/>.
 */

package evopaint.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class JAboutDialog extends JDialog {

    public JAboutDialog(Frame owner) {
        super(owner, "About EvoPaint", true);

        JTextPane messagePane = new JTextPane();
        messagePane.setContentType("text/html");
        messagePane.setEditable(false);
        messagePane.setBorder(new LineBorder(new JPanel().getBackground(), 10));
        messagePane.setAlignmentX(LEFT_ALIGNMENT);
        messagePane.setPreferredSize(new Dimension(800, 650));
        add(messagePane, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        JButton okButton = new JButton("I <3 U");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
         });
        controlPanel.add(okButton);
        controlPanel.setAlignmentX(LEFT_ALIGNMENT);
        add(controlPanel);

        messagePane.setText(
                "<html>" +
                "<body style='padding:10px;'>" +
                "<p style='text-align: center; color: GRAY; font-style: italic; margin: 0;'>" +
                "\"A godlike act - I create creation,<br />I experience it, then I disintegrate it.\"<br /><span style='font-size: smaller;'>(Absurd Minds - The Focus)</span>" +
                "</p>" +
                "<p style='margin: 0; margin-top: 10px;'>" +
                "EvoPaint has been developed for a software lab in applied computer science at the Universität Klagenfurt, Austria and was initally released in May 2010, consisting of roughly 13,000 lines of code not counting blank lines or comments." +
                "</p>" +
                "<h2 style='margin: 0; margin-top: 8px;'>Contact</h2>" +
                "<p style='margin: 0;'>" +
                "If you wish to talk about EvoPaint or have cool ideas for further development, if you wish to contribute or support in any way, feel free to contact Markus Echterhoff using tam@edu.uni-klu.ac.at" +
                "</p>" +
                "<h2 style='margin: 0; margin-top: 8px;'>License</h2>" +
                "<p style='margin: 0;'>" +
                "EvoPaint is free software as envisioned by Richard Stallman and shall stay free. Therefore EvoPaint is licensed under the GNU Public License version 3 or higher.<br />EvoPaint uses source files and libraries that are licensed differently, please see the corresponding licenses for more information." +
                "</p>" +
                "<h2 style='margin: 0; margin-top: 8px;'>Credits</h2>" +
                "<table><tr><td style='white-space: nowrap;'>Markus Echterhoff <tam@edu.uni-klu.ac.at></td><td>(vision, project management, core engine, user interface, rule set manager, user-guide texts)</td></tr>" +
                "<tr><td>Daniel Hölbling</td><td>(tool handling, selection management, miscellaneous code contributions)</td></tr>" +
                "<tr><td>Augustin Malle</td><td>(logo design, software requirements specifications, user-guide design)</td></tr></table>" +
                "<h2 style='margin: 0; margin-top: 8px;'>Thanks</h2>" +
                "<ul>" +
                "<li>To Ao.Univ.-Prof. Mag. Dr. Günther Fliedl for supervising this software lab and giving us the freedom this project needed to be developed.</li>" +
                "<li>To Severin Kacianka for fresh views on complex problems during numerous lunches and coffee breaks as well as some joyful playtesting." +
                "<li>To Markus Feuerstack for all the feedback and comments in countless testing sessions. A feature is polished once you don't notice it at all." +
                "<li>To Werner Randelshofer for sharing his image to avi converter, we use it to record videos.</li>" +
                "<li>To the XStream developers for sharing their library. We use XStream for the import and export of data.</li>" +
                "<li>To Daniel W. Dyer for sharing the uncommons-maths library we use for random number generation.</li>" +
                "<li>To the developers of the prefuse visualization toolkit for sharing their code. We made good use of their range slider class.</li>" +
                "<li>To the developers of The Gimp and the Oxygen icon set. Your icons look great in EvoPaint.</li>" +
                "<li>To the open source community as a whole, keep the spirit alive!</li>" +
                "</ul>" +
                "</body>" +
                "</html>"
                );
    }

}
