package gui;

import io.dialogues.Import.ImportDialogue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.api.FxRobot;
import view.MitoBenchWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;


/**
 * Created by peltzer on 21/12/2016.
 * Basic GUI Testing is now implemented almost :-)
 */

@RunWith(MockitoJUnitRunner.Silent.class)
public class GUITests extends FxRobot implements GUITestValidator {

    @Mock
    private ImportDialogue importDialogue = mock(ImportDialogue.class);


    @BeforeClass
    public static void setupSpec() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        registerPrimaryStage();
    }

    @Before
    public void setUp() throws Exception {

        setUpFileDialogue(importDialogue, getResource(GUITestFiles.project_file).toFile());
        setupApplication(MitoBenchWindow.class);

    }

    @Test
    public void testWalkThrough() {
        GUITestSteps steps = new GUITestSteps(this);
        steps.part1BasicStuff();
        steps.part2MenuInteraction();
        steps.part3AboutDialogueTests();
        steps.part4TreeViewTests();
        steps.part5TestImportDialogue();
    }


    /**
     * Testing file dialogues
     */

     private void setUpFileDialogue(final ImportDialogue importDialogue, final File file){
         when(importDialogue.start()).thenReturn(file);

     }


    /**
     * step("Export the selection", () -> {
     * robot.clickOn("#buttonExport");
     * validator.validateExportFile();
     * });
     */

    @Override
    public void validateSavedSession(String name) {

    }

    @Override
    public void validateExportFile() {

    }

    private String readFile(final String input) throws Exception {
        URL url = getClass().getResource(input);
        Path file = Paths.get(url.toURI());
        try {
            return new String(Files.readAllBytes(file), "UTF-8");
        } catch (IOException e) {
            throw new IOException(String.format("Unable to read file %s", file), e);
        }
    }


    private Path getResource(final String file) throws Exception {
        URL url = getClass().getResource("/" + file);

        if (url == null) {
            throw new FileNotFoundException(String.format("Unable to load %s", file));
        } else {
            return Paths.get(url.toURI());
        }
    }





}
