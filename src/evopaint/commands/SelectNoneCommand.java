package evopaint.commands;

import evopaint.Configuration;
import evopaint.gui.Showcase;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 20:27:27
 * To change this template use File | Settings | File Templates.
 */
public class SelectNoneCommand extends AbstractCommand {
    private Configuration configuration;

    public SelectNoneCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void execute() {
        Showcase showcase = configuration.mainFrame.getShowcase();
        showcase.setActiveSelection(null);
    }
}
