package io.alapierre.pki.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import io.alapierre.rsa.KeyUsageEnum;
import io.alapierre.rsa.RsaUtil;
import io.alapierre.pki.gui.task.TaskBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;

import static io.alapierre.rsa.RsaUtil.generateCSR;
import static io.alapierre.rsa.RsaUtil.savePem;

/**
 * @author Adrian Lapierre {@literal <al@alapierre.io>}
 * created 19.09.18
 */
public class MainControler {

    private static int keySize = 2048;

    public TextField name;
    public TextField email;
    public PasswordField pass;
    public PasswordField passRep;
    public Button generate;

    public ProgressIndicator progres;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        if(!checkFields()) return;

        generate.setVisible(false);
        progres.setVisible(true);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(generate.getScene().getWindow());

        if(selectedDirectory != null) {


            TaskBuilder.backgroundJob(
                    () -> generateKeys(selectedDirectory),
                    () -> progres.setProgress(1),
                    ex -> {
                        progres.setVisible(false);
                        generate.setDisable(false);
                    });
        }
    }

    private boolean checkFields() {

        if(
            name.getText().isEmpty() ||
            email.getText().isEmpty() ||
            pass.getText().isEmpty() ||
            passRep.getText().isEmpty()
            ) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uzupełnij informacje");
            alert.setHeaderText(null);
            alert.setContentText("Wszystkie pola są wymagane!");

            alert.showAndWait();

            return false;
        }

        return true;
    }

    private void generateKeys(File selectedDirectory) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, OperatorCreationException {

        prepareKey(selectedDirectory, "sign", KeyUsageEnum.SIGN);
        System.out.println("Pierwszy gotowy");
        prepareKey(selectedDirectory, "encrypt", KeyUsageEnum.ENCYPR);
        System.out.println("Drugi gotowy");

    }

    private void prepareKey(File selectedDirectory, String prefix, KeyUsageEnum usage) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, OperatorCreationException {

        KeyPair keyPair = RsaUtil.generateKeyPair(keySize);

        PKCS10CertificationRequest csr = generateCSR(
                prepareSubject(),
                usage,
                keyPair);

        savePem(csr, new FileOutputStream(new File(selectedDirectory, prefix + "_" + FileUtil.toFileSystemSafeName(name.getText()) + ".csr")));
        savePem(keyPair.getPrivate(), pass.getText().toCharArray(), new FileOutputStream(new File(selectedDirectory, prefix + "_key.pem")));
        savePem(keyPair.getPublic(), new FileOutputStream(new File(selectedDirectory, prefix + "_public.pem")));
    }

    private String prepareSubject() {
        return String.format(
                "CN=%s, O=Example sp. z o.o., C=PL, emailAddress=%s, S=Mazowieckie, L=Warszawa",
                name.getText(),
                email.getText());
    }

    public void checkPassAction(KeyEvent actionEvent) {

        if(!pass.getText().equals(passRep.getText())) {
            if(!pass.getStyleClass().contains("error")) pass.getStyleClass().add("error");
            if(!passRep.getStyleClass().contains("error")) passRep.getStyleClass().add("error");
            generate.setDisable(true);
        } else {
            pass.getStyleClass().remove("error");
            passRep.getStyleClass().remove("error");
            generate.setDisable(false);
        }

    }

    public void handleInstallMenuAction(ActionEvent actionEvent) {
        try {
            loadAndShow("install.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private Stage getWindow() {
        return (Stage) pass.getScene().getWindow();
    }

    private void loadAndShow(String fxml) throws IOException {

        Stage stage = getWindow();

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource(fxml),
                        "problem z odczytem " + fxml
                ));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
