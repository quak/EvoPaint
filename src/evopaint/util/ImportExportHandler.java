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

package evopaint.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import evopaint.Configuration;
import java.io.File;
import java.util.regex.Pattern;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class ImportExportHandler {

    private XStream xStream = new XStream(new DomDriver());

    public Object importFromString(String importString) {
        return importFromString(importString, null);
    }

    public Object importFromString(String importString, File file) {
        int version;
        String xml;

        String [] versionAndRest =  importString.split("\n", 2);
        if (Pattern.matches("\\A\\d+\\Z", versionAndRest[0])) {
            version = Integer.parseInt(versionAndRest[0]);
            xml = versionAndRest[1];
        } else {
            version = 0;
            xml = importString;
        }

        if (version > Configuration.PROTOCOL_VERSION) {
            ExceptionHandler.handle(new Exception(), true, "<p>The rule set I am trying to import is too new for me. Please update me to the newest Version!</p>");
        }
        
        xml = update(version, xml);

        Object ret = null;
        try {
            ret = xStream.fromXML(xml);
        } catch (XStreamException ex) {
            if (file != null) {
                ExceptionHandler.handle(ex, false, "<p>I could not parse the file \"" + file.getAbsolutePath() + "\".</p><p>This either means this file is corrupted in some way or some internals have changed and we messed up the import (in that case a bug report would be appreciated). Please have a look at the message below and try to fix your rule set if you can. They are stored in XML format, so any text editor will do. Except Notepad.exe, because it sucks and will display your rule set without line breaks</p><p>If you cannot fix it, you can always delete the file in question and recreate the rule set using the rule set manager.</p>");
            } else {
                ExceptionHandler.handle(ex, false, "<p>I could not parse the XML of the rule set you pasted there.</p><p>This could have happened due to multiple causes. First check your XML, ie. check if you selected correctly before copying. If it looks right this error means that some internals have changed and we have messed up the import (in that case a bug report would be appreciated). The below message might help you to try and fix your rule set.</p>");
            }
        }

        if (ret != null &&
                file != null &&
                version < Configuration.PROTOCOL_VERSION) {
            Configuration.FILE_HANDLER.writeToFile(ret, file);
        }

        return ret;
    }

    public String exportToString(Object exportObject) {
        try {
            return Configuration.PROTOCOL_VERSION + "\n" + xStream.toXML(exportObject);
        } catch (XStreamException ex) {
            ExceptionHandler.handle(ex, true);
        }

        return null;
    }

    private String update(int version, String xml) {

        switch (version) {
            case 0:
                // not versioned yet, nothing to do but add the version in the end
            case 1:
                // nothing to do yet
            case 2:
                // just do demonstrate the priciple, we use fall-through to
                // incrementally update from version to version
        }

        return xml;
    }

}
