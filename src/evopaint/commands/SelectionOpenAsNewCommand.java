package evopaint.commands;

import evopaint.*;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.util.mapping.AbsoluteCoordinate;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 09.05.2010
 * Time: 17:44:37
 * To change this template use File | Settings | File Templates.
 */
public class SelectionOpenAsNewCommand extends AbstractCommand {
    private Configuration config;

    public SelectionOpenAsNewCommand(Configuration config) {
        this.config = config;
    }

    @Override
    public void execute() {
        int runlevel = config.runLevel;
        config.runLevel = Configuration.RUNLEVEL_STOP;
        Selection activeSelection = config.mainFrame.getShowcase().getActiveSelection();
        if (activeSelection == null) return;

        EvoPaint evoPaint = new EvoPaint();
        Configuration configuration = evoPaint.getConfiguration();
        configuration.setDimension(config.getDimension());
        configuration.backgroundColor = config.backgroundColor;
        configuration.startingEnergy = config.startingEnergy;
        configuration.mutationRate = config.mutationRate;
        configuration.operationMode = config.operationMode;
        configuration.world.resetPendingOperations();
        configuration.world = new World(configuration);
        configuration.runLevel = Configuration.RUNLEVEL_RUNNING;

        Rectangle rectangle = activeSelection.getRectangle();
        configuration.setDimension(rectangle.getSize());
        for(int x = 0; x < activeSelection.getRectangle().x; x++) {
            for(int y = 0; y < activeSelection.getRectangle().y; y++) {
                RuleBasedPixel ruleBasedPixel = config.world.get(x + activeSelection.getRectangle().x, y + activeSelection.getRectangle().y);
                if (ruleBasedPixel == null) continue;
                configuration.world.set(new RuleBasedPixel(ruleBasedPixel.getPixelColor(), new AbsoluteCoordinate(x, y, configuration.world), ruleBasedPixel.getEnergy(), ruleBasedPixel.getRules()));
            }
        } 
        evoPaint.work();

        config.runLevel = runlevel;
    }
}
