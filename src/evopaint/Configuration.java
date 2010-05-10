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

package evopaint;


import evopaint.gui.MainFrame;
import evopaint.interfaces.IRandomNumberGenerator;
import evopaint.pixel.Pixel;
import evopaint.pixel.rulebased.Action;
import evopaint.pixel.rulebased.Condition;
import evopaint.pixel.rulebased.actions.AssimilationAction;
import evopaint.pixel.rulebased.actions.CopyAction;
import evopaint.pixel.rulebased.actions.ChangeEnergyAction;
import evopaint.pixel.rulebased.actions.MoveAction;
import evopaint.pixel.rulebased.actions.SetColorAction;
import evopaint.pixel.rulebased.actions.PartnerProcreationAction;
import evopaint.pixel.rulebased.conditions.ColorLikenessColorCondition;
import evopaint.pixel.rulebased.conditions.ColorLikenessMyColorCondition;
import evopaint.pixel.rulebased.conditions.EnergyCondition;
import evopaint.pixel.rulebased.conditions.ExistenceCondition;
import evopaint.pixel.rulebased.targeting.Qualifier;
import evopaint.pixel.rulebased.targeting.qualifiers.ColorLikenessColorQualifier;
import evopaint.pixel.rulebased.targeting.qualifiers.ColorLikenessMyColorQualifier;
import evopaint.pixel.rulebased.targeting.qualifiers.ExistenceQualifier;
import evopaint.pixel.rulebased.targeting.qualifiers.EnergyQualifier;
import evopaint.util.FileHandler;
import evopaint.util.ImportExportHandler;
import evopaint.util.RandomNumberGeneratorWrapper;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.DefaultSeedGenerator;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;

/**
 * Holds every configuration variables and state of the program. Almost all
 * parts of EvoPaint are tied to this class. It also serves as a bridge between
 * GUI and core engine. There are a lot of public variables in this class. If
 * you need to ensure integrity, you might have to restrict access to getters
 * and setters. See dimension for an example.
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class Configuration {
    public static final String VERSION = "1.0";
    public static final int PROTOCOL_VERSION = 1;

    public static final int OPERATIONMODE_AGENT_SIMULATION = 0;
    public static final int OPERATIONMODE_CELLULAR_AUTOMATON = 1;

    public static final int RUNLEVEL_RUNNING = 2;
    public static final int RUNLEVEL_PAINTING_ONLY = 1;
    public static final int RUNLEVEL_STOP = 0;

    public final int pixelType = Pixel.RULESET;

    public static final String USER_GUIDE_URL = "http://github.com/unixtam/EvoPaint";
    public static final String CODE_DOWNLOAD_URL = "http://github.com/unixtam/EvoPaint";

    public static final List<Condition> AVAILABLE_CONDITIONS = new ArrayList<Condition>() {{
        add(new ExistenceCondition());
        add(new EnergyCondition());
        add(new ColorLikenessColorCondition());
        add(new ColorLikenessMyColorCondition());
    }};

    public static final List<Action> AVAILABLE_ACTIONS = new ArrayList<Action>() {{
        add(new ChangeEnergyAction());
        add(new CopyAction());
        add(new MoveAction());
        add(new AssimilationAction());
        add(new PartnerProcreationAction());
        add(new SetColorAction());
    }};

    public static final List<Qualifier> AVAILABLE_QUALIFIERS = new ArrayList<Qualifier>() {{
        add(new ExistenceQualifier());
        add(new EnergyQualifier());
        add(new ColorLikenessColorQualifier());
        add(new ColorLikenessMyColorQualifier());
    }};

    private static int INSTANCE_COUNTER = 0;
    public static FileHandler FILE_HANDLER = new FileHandler();
    public static ImportExportHandler IMPORT_EXPORT_HANDLER = new ImportExportHandler();

    public IRandomNumberGenerator rng;
    public World world;
    public Perception perception;
    public MainFrame mainFrame;
    public Brush brush;
    public Paint paint;
    public List<Action> usedActions; // because we do not want actions to be randomly created (energy consumption issue), we carry around a list of used actions that we can use during mutation

    public static boolean INITIALIZED = false;
    public static String DEFAULT_ENCODER_COMMAND_UNIX = "mencoder -quiet -nosound -ovc x264 -x264encopts qp=30:pass=1 INPUT_FILE -o OUTPUT_FILE";
    public static String DEFAULT_ENCODER_COMMAND_WINDOWS = "lib" + File.separator + "mencoder-31139 -quiet -nosound -ovc x264 -x264encopts qp=30:pass=1 INPUT_FILE -o OUTPUT_FILE";
    // BEGIN user configurable
    public int runLevel = Configuration.RUNLEVEL_RUNNING;
    public int operationMode = OPERATIONMODE_AGENT_SIMULATION;
    // BEGIN options dialog
    public static String ENCODER_COMMAND;
    public double mutationRate = 0.01; // NOTE: it seems the lowest double > 0 generated by the rng is 10^(-18): 0.000000000000000001
    public int fps = 30;
    public int fpsVideo = 30; // images will be created in fps intervals or less. this value defines the fps used for video recording. this is fixed. it can never be lower than fps (if it is, the lower value will be used), because images are generated every 1000/fps ms
    private Dimension dimension = new Dimension(300, 300);
    public int backgroundColor = 0;
    public int startingEnergy = 100;
    public int paintHistorySize = 7;
    // END options dialog
    // END user configurable

    public String saveFilePath = null;

    public static synchronized void decreaseInstanceCounter() {
        INSTANCE_COUNTER--;
        if (INSTANCE_COUNTER == 0) {
            System.exit(0);
        }
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean setDimension(Dimension dimension) {
        if (false == perception.setDimension(dimension)) { // perception-resize fails if recording a video
            return false;
        }
        world.setDimension(dimension);
        mainFrame.getShowcase().setImage(perception.getImage());
        this.dimension = dimension;
        return true;
    }

    private IRandomNumberGenerator createRNG() {
        // Random, SecureRandom, AESCounterRNG, CellularAutomatonRNG,
        // CMWC4096RNG, JavaRNG, MersenneTwisterRNG, XORShiftRNG

        // default seed size for cellularAutomatonRNG is 4 bytes;
        int seed_size_bytes = 4;

        // set fixed seed or null for generation
        byte [] seed = null;
       // byte [] seed = new byte [] { 1, 2, 3, 4 };

        // default seed generator checks some different approaches and will
        // always succeed
        if (seed == null) {
            SeedGenerator sg = DefaultSeedGenerator.getInstance();
            try {
                seed = sg.generateSeed(4);
            } catch (SeedException e) {
                // log.error("got seed exception from default seed generator. this should not have happened.");
                java.lang.System.exit(1);
            }
        }
        return new RandomNumberGeneratorWrapper(new CellularAutomatonRNG(seed));
    }

    public Configuration() {
        if (false == INITIALIZED) {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                ENCODER_COMMAND = DEFAULT_ENCODER_COMMAND_WINDOWS;
            } else {
                ENCODER_COMMAND = DEFAULT_ENCODER_COMMAND_UNIX;
            }
            INITIALIZED = true;
        }
        rng = createRNG();
        world = new World(this);
        perception = new Perception(this);
        brush = new Brush(this);
        paint = new Paint(this);
        usedActions = new ArrayList<Action>();
        mainFrame = new MainFrame(this);
        INSTANCE_COUNTER++;
    }
}
