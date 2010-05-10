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
        final Configuration oldConfig = config;
        int runlevel = oldConfig.runLevel;
        oldConfig.runLevel = Configuration.RUNLEVEL_STOP;
        Thread t = new Thread() {
            @Override
            public void run() {
                Selection activeSelection = oldConfig.mainFrame.getShowcase().getActiveSelection();
                if (activeSelection == null) return;

                EvoPaint evoPaint = new EvoPaint();
                Configuration newConfig = evoPaint.getConfiguration();
                newConfig.setDimension(oldConfig.getDimension());
                newConfig.backgroundColor = oldConfig.backgroundColor;
                newConfig.startingEnergy = oldConfig.startingEnergy;
                newConfig.mutationRate = oldConfig.mutationRate;
                newConfig.operationMode = oldConfig.operationMode;
                newConfig.world.resetPendingOperations();
                newConfig.world = new World(newConfig);

                Rectangle rectangle = activeSelection.getRectangle();
                newConfig.setDimension(rectangle.getSize());
                for(int x = 0; x < activeSelection.getRectangle().x; x++) {
                    for(int y = 0; y < activeSelection.getRectangle().y; y++) {
                        RuleBasedPixel ruleBasedPixel = oldConfig.world.get(x + activeSelection.getRectangle().x, y + activeSelection.getRectangle().y);
                        if (ruleBasedPixel == null) continue;
                        newConfig.world.set(new RuleBasedPixel(ruleBasedPixel.getPixelColor(), new AbsoluteCoordinate(x, y, newConfig.world), ruleBasedPixel.getEnergy(), ruleBasedPixel.getRules()));
                    }
                }

                newConfig.runLevel = Configuration.RUNLEVEL_RUNNING;
                evoPaint.work();
            }
        };
        t.start();

        oldConfig.runLevel = runlevel;
    }
}
