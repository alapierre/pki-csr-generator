package io.alapierre.pki.gui;

import io.alapierre.pki.gui.task.TaskBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.bouncycastle.pkcs.PKCSException;
import io.alapierre.rsa.RsaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @author Adrian Lapierre {@literal <al@alapierre.io>}
 * created 16.10.18
 */
public class InstallControler {

    public PasswordField pass;
    public TextField ceret;
    public TextField key;
    public ProgressIndicator progres;
    public Button installButton;

    private void generateKeyStore() throws IOException, PKCSException, CertificateException, KeyStoreException, NoSuchAlgorithmException {

        PrivateKey privateKey = RsaUtil.loadPrivateKey(
                new File(key.getText()),
                pass.getText().toCharArray());

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) fact.generateCertificate(new FileInputStream(ceret.getText()));

        String fileName = FileUtil.getBaseFileName(new File(ceret.getText()).getName());

        File outputDir = new File(key.getText()).getParentFile();
        File output = new File(outputDir, fileName + ".p12");

        RsaUtil.packToPKCS12(
                output,
                pass.getText().toCharArray(),
                pass.getText().toCharArray(),
                privateKey,
                new X509Certificate[]{cert}
        );
    }

    private void showSuccessDialog() {

        progres.setProgress(1);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generowanie keystore");
        alert.setHeaderText("Klucz oraz certyfikat zapisany w pliku keystore");
        alert.setContentText("Twój certyfikat jest zapisany w pliku .p12 \n" +
                "\nw tym samym katalogu, w którym klucz prywatny." +
                "\nHasło do keystore jest takie samo jak do klucza prywatnego.");
        alert.showAndWait();

        ceret.setText("");
        key.setText("");

        installButton.setVisible(true);
        progres.setVisible(false);

    }

    @FXML
    public void handleInstallButtonAction(ActionEvent actionEvent) {

        installButton.setVisible(false);
        progres.setVisible(true);

        TaskBuilder.backgroundJob(
                this::generateKeyStore,
                this::showSuccessDialog,
                ex -> {
                    progres.setVisible(false);
                    installButton.setDisable(false);
                    installButton.setVisible(true);
                });
    }

    public void handleCertButtonAction(ActionEvent actionEvent) {
        processFile(ceret);
    }

    public void handleKeyButtonAction(ActionEvent actionEvent) {
        processFile(key);
    }

    private void processFile(TextField field) {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(field.getScene().getWindow());

        if(file != null) {
            field.setText(file.getAbsolutePath());
        }

    }
}
