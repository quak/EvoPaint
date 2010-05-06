package evopaint.commands;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import evopaint.Configuration;
import evopaint.Selection;
import evopaint.gui.util.IOverlay;
import evopaint.pixel.Pixel;
import evopaint.pixel.PixelColor;
import evopaint.pixel.rulebased.Rule;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.util.mapping.AbsoluteCoordinate;

public class CopySelectionCommand extends AbstractCommand {

	private final Configuration config;
	private SelectionCopyOverlay overlay;
	private Point location;
	private boolean dragging = false;
	private Rectangle rect;
    private RuleBasedPixel[][] pixels; 
	
	public CopySelectionCommand(Configuration config) {
		this.config = config;
	}
	
	public void setLocation(Point p) {
		location = p;
	}

    public void copyCurrentSelection() {
        Selection activeSelection = config.mainFrame.getShowcase().getActiveSelection();
        rect = activeSelection.getRectangle();
        pixels = new RuleBasedPixel[rect.width][rect.height];
        overlay = new SelectionCopyOverlay(rect.width, rect.height);
        for(int x = 0; x < rect.width; x++) {
            for (int y = 0; y < rect.height; y++) {
                Pixel pixel = config.world.get(rect.x + x, rect.y + y);
                if (pixel == null) continue;
                if (pixel instanceof RuleBasedPixel) {
                    pixels[x][y] = (RuleBasedPixel)pixel;
                }
                overlay.setRGB(x, y, pixel.getPixelColor().getInteger());
            }
        }
    }

    public void startDragging() {
        config.mainFrame.getShowcase().subscribe(overlay);
    }
    public void stopDragging() {
        config.mainFrame.getShowcase().unsubscribe(overlay);
    }
    public void submitPaste() {
        stopDragging();

        for(int x = 0; x < overlay.getWidth(); x++) {
            for (int y = 0; y < overlay.getHeight(); y++) {
                if (!pixelExistsInSource(x,y)) continue;
                RuleBasedPixel pixel = config.world.get(location.x + x, location.y + y);
                if (pixel != null) {
                    pixel.getPixelColor().setInteger(overlay.getRGB(x, y));
                    if (pixelExistsInSource(x, y))
                        pixel.setRules(pixels[x][y].getRules());
                }
                else{
                    PixelColor color = new PixelColor(overlay.getRGB(x, y));
                    List<Rule> rules = new ArrayList<Rule>();
                    if (pixelExistsInSource(x, y)) rules = pixels[x][y].getRules();
                    RuleBasedPixel pix = new RuleBasedPixel(color, new AbsoluteCoordinate(location.x +x , location.y + y, config.world), config.startingEnergy, rules);
                    config.world.set(pix);
                }
            }
        }
        config.mainFrame.setActiveTool(null);
    }

    private boolean pixelExistsInSource(int x, int y) {
        return pixels[x][y] != null;
    }

    @Override
	public void execute() {
        if (config.mainFrame.getShowcase().getActiveSelection() == null) return;
        config.mainFrame.setActiveTool(CopySelectionCommand.class);
        copyCurrentSelection();
    }
	
		
	private class SelectionCopyOverlay extends BufferedImage implements IOverlay {

		public SelectionCopyOverlay(int width, int height) {
			super(width, height, BufferedImage.TYPE_INT_RGB);
		}

		@Override
		public void paint(Graphics2D g2) {
			g2.drawImage(this, location.x, location.y, config.mainFrame.getShowcase());
		}
	}
}
