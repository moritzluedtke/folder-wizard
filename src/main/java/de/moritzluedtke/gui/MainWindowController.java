package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWindowController {
	
	private static final Logger log = LogManager.getLogger();
	
	@FXML
	private JFXButton buttonCreate;
	
	@FXML
	private JFXButton buttonDelete;
	
	@FXML
	private JFXButton buttonCreateByStructure;
	
	@FXML
	public void clickedButtonCreate(ActionEvent actionEvent) {
		log.info("buttonCreate clicked");
	}
	
	@FXML
	public void clickedButtonDelete(ActionEvent actionEvent) {
		log.info("buttonDelete clicked");
	}
	
	@FXML
	public void clickedButtonCreateByStructure(ActionEvent actionEvent) {
		log.info("buttonCreateByStructure clicked");
	}
}
