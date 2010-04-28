package evopaint.commands;

import evopaint.Configuration;
import evopaint.gui.ConfigurationDialog;

public class ShowConfigurationDialogCommand extends AbstractCommand {

	private final Configuration config;
	public ShowConfigurationDialogCommand(Configuration config){
		this.config = config;
		
	}
	@Override
	public void execute() {
		ConfigurationDialog configurationDialog = new ConfigurationDialog(config);
		configurationDialog.setModal(true);
		configurationDialog.setVisible(true);
	}

}
