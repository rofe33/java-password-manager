package com.raphaeltannous;

import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import net.miginfocom.swing.MigLayout;

/**
 * EPMPasswordGenerator
 */
public class EPMPasswordGeneratorDialog extends JDialog {
    private JPanel dialogPane;
    private JPanel contentPanel;

    private JPasswordField remotePasswordField;
    private JPasswordField localPasswordField;

    private JButton okButton;
    private JButton cancelButton;
    private JButton generateButton;

    private JCheckBox useLowerCheckBox;
    private JCheckBox useUpperCheckBox;
    private JCheckBox useDigitsCheckBox;
    private JCheckBox usePunctuationCheckBox;

    private JSpinner passwordLengthSpinner;
    private JSpinner numberOfWhiteSpacesSpinner;

    private boolean generateButtonActionListenerInProgress = false;
    private boolean copyLocalPasswordButtonActionListenerInProgress = false;

    private Color[] success = new Color[]{new Color(0, 255, 0), new Color(200, 255, 200)};

    EPMPasswordGeneratorDialog(
        Window owner,
        JPasswordField remotePasswordField
    ) {
        super(owner);

        this.remotePasswordField = remotePasswordField;

        initDialogComponents();

        getRootPane().setDefaultButton(okButton);

        // By default show the password
        localPasswordField.setEchoChar((char)0);

        // Copy button
        JButton copyLocalPasswordButton = new JButton();
        copyLocalPasswordButton.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/key-copy-gray.svg"));
        copyLocalPasswordButton.addActionListener(e -> copyLocalPasswordButtonActionListener());
        copyLocalPasswordButton.setToolTipText("Copy Password");

        // Show reveal password button
        localPasswordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        // localPasswordToolBar
        JToolBar localPasswordToolBar = new JToolBar();
        localPasswordToolBar.addSeparator();
        localPasswordToolBar.add(copyLocalPasswordButton);
        localPasswordField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, localPasswordToolBar);

        // Generate a password by default
        generateButtonActionListener();
    }

    private void copyLocalPasswordButtonActionListener() {
        if (copyLocalPasswordButtonActionListenerInProgress) {
            return;
        }

        copyLocalPasswordButtonActionListenerInProgress = false;

        EPMUtilities.copyToClipboard(
            String.valueOf(localPasswordField.getPassword())
        );

        SwingUtilities.invokeLater(() -> copyLocalPasswordButtonActionListenerInProgress = false);
    }

    private void initDialogComponents() {
        dialogPane = new JPanel();
        contentPanel = new JPanel();

        JLabel localPasswordLabel = new JLabel();
        JLabel passwordLengthSpinnerLabel = new JLabel();
        JLabel numberOfWhiteSpacesSpinnerLabel = new JLabel();

        localPasswordField = new JPasswordField();

        useLowerCheckBox = new JCheckBox();
        useUpperCheckBox = new JCheckBox();
        useDigitsCheckBox = new JCheckBox();
        usePunctuationCheckBox = new JCheckBox();

        SpinnerNumberModel passwordLengthSpinnerModel = new SpinnerNumberModel(32, 1, 128, 1);
        passwordLengthSpinner = new JSpinner(passwordLengthSpinnerModel);
        passwordLengthSpinner.addChangeListener(e -> passwordLengthSpinnerChangeListener());

        numberOfWhiteSpacesSpinner = new JSpinner();
        numberOfWhiteSpacesSpinner.addChangeListener(e -> numberOfWhiteSpacesSpinnerChangeListener());

        // Update spinner model for numberOfWhiteSpacesSpinner
        updateNumberOfWhiteSpacesSpinnerModel();

        // Making the passwordLengthSpinner un-editable for invalid input.
        JFormattedTextField passwordLengthSpinnerText = ((JSpinner.NumberEditor) passwordLengthSpinner.getEditor()).getTextField();
        ((NumberFormatter) passwordLengthSpinnerText.getFormatter()).setAllowsInvalid(false);

        // Making the numberOfWhiteSpacesSpinner un-editable for invalid input.
        JFormattedTextField numberOfWhiteSpacesSpinnerText = ((JSpinner.NumberEditor) numberOfWhiteSpacesSpinner.getEditor()).getTextField();
        ((NumberFormatter) numberOfWhiteSpacesSpinnerText.getFormatter()).setAllowsInvalid(false);

        okButton = new JButton();
        cancelButton = new JButton();
        generateButton = new JButton();

        // this
        setTitle("Password Generator");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(777, 333);
        setModal(true);

        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout("insets 0, fill"));

        // dialogPane
        {
            dialogPane.setLayout(new MigLayout("insets 0, fill"));

            // contentPanel
            {
                contentPanel.setLayout(new MigLayout(
                    "insets 0, fill",
                    // Column Constraints
                    "[177!][177!]",
                    // Row Constraints
                    ""
                ));

                // localPasswordLabel
                localPasswordLabel.setText("Generated Password");
                localPasswordLabel.setLabelFor(localPasswordField);
                contentPanel.add(localPasswordLabel, "cell 0 0");

                // localPasswordField
                EPMUtilities.addChangeListener(localPasswordField, e -> localPasswordFieldActionListener());
                contentPanel.add(localPasswordField, "cell 0 1 2 1, growx");

                // useLowerCheckBox
                useLowerCheckBox.setText("Lowercase");
                useLowerCheckBox.setDisplayedMnemonicIndex(0);
                useLowerCheckBox.setMnemonic('L');
                useLowerCheckBox.setSelected(true);
                useLowerCheckBox.addActionListener(e -> checkCheckBoxesStatus());
                contentPanel.add(useLowerCheckBox, "cell 0 2, align left");

                // useUpperCheckBox
                useUpperCheckBox.setText("Uppercase");
                useUpperCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
                useUpperCheckBox.setDisplayedMnemonicIndex(0);
                useUpperCheckBox.setMnemonic('U');
                useUpperCheckBox.setSelected(true);
                useUpperCheckBox.addActionListener(e -> checkCheckBoxesStatus());
                contentPanel.add(useUpperCheckBox, "cell 1 2, align right");

                // useDigitsCheckBox
                useDigitsCheckBox.setText("Digits");
                useDigitsCheckBox.setDisplayedMnemonicIndex(0);
                useDigitsCheckBox.setMnemonic('D');
                useDigitsCheckBox.setSelected(true);
                useDigitsCheckBox.addActionListener(e -> checkCheckBoxesStatus());
                contentPanel.add(useDigitsCheckBox, "cell 0 3, align left");

                // usePunctuationCheckBox
                usePunctuationCheckBox.setText("Punctuation");
                usePunctuationCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
                usePunctuationCheckBox.setDisplayedMnemonicIndex(0);
                usePunctuationCheckBox.setMnemonic('P');
                usePunctuationCheckBox.setSelected(true);
                usePunctuationCheckBox.addActionListener(e -> checkCheckBoxesStatus());
                contentPanel.add(usePunctuationCheckBox, "cell 1 3, align right");

                // passwordLengthSpinnerLabel
                passwordLengthSpinnerLabel.setText("Length");
                passwordLengthSpinnerLabel.setDisplayedMnemonicIndex(0);
                passwordLengthSpinnerLabel.setDisplayedMnemonic('S');
                passwordLengthSpinnerLabel.setLabelFor(passwordLengthSpinner);
                contentPanel.add(passwordLengthSpinnerLabel, "cell 0 4, align center");

                // numberOfWhiteSpacesSpinnerLabel
                numberOfWhiteSpacesSpinnerLabel.setText("Number of Spaces");
                numberOfWhiteSpacesSpinnerLabel.setDisplayedMnemonicIndex(0);
                numberOfWhiteSpacesSpinnerLabel.setDisplayedMnemonic('N');
                numberOfWhiteSpacesSpinnerLabel.setLabelFor(numberOfWhiteSpacesSpinner);
                contentPanel.add(numberOfWhiteSpacesSpinnerLabel, "cell 1 4, align center");

                // passwordLengthSpinner
                contentPanel.add(passwordLengthSpinner, "cell 0 5, align center");

                // numberOfWhiteSpacesSpinner
                contentPanel.add(numberOfWhiteSpacesSpinner, "cell 1 5, align center");

                // generateButton
                generateButton.setText("Generate");
                generateButton.setMnemonic('G');
                generateButton.setDisplayedMnemonicIndex(0);
                generateButton.addActionListener(e -> generateButtonActionListener());
                contentPanel.add(generateButton, "cell 0 6 2 1, align center");

                // okButton
                okButton.setText("Ok");
                okButton.setMnemonic('O');
                okButton.setDisplayedMnemonicIndex(0);
                okButton.addActionListener(e -> okButtonActionListener());
                okButton.setEnabled(false);
                contentPanel.add(okButton, "cell 0 7, align center");

                // cancelButton
                cancelButton.setText("Cancel");
                cancelButton.setMnemonic('C');
                cancelButton.setDisplayedMnemonicIndex(0);
                cancelButton.addActionListener(e -> cancelButtonActionListener());
                contentPanel.add(cancelButton, "cell 1 7, align center");
            }
        }
        dialogPane.add(contentPanel, "align center");

        contentPane.add(dialogPane, "align center");
        setLocationRelativeTo(getOwner());
    }

    private void cancelButtonActionListener() {
        dispose();
    }

    private void okButtonActionListener() {
        if (Objects.nonNull(remotePasswordField)) {
            remotePasswordField.setText(
                String.valueOf(localPasswordField.getPassword())
            );
        }

        dispose();
    }

    private void checkCheckBoxesStatus() {
        JCheckBox[] checkBoxes = new JCheckBox[] {
            useLowerCheckBox,
            useUpperCheckBox,
            useDigitsCheckBox,
            usePunctuationCheckBox
        };

        int checkBoxesLength = checkBoxes.length;

        boolean[] checkBoxesStatus = new boolean[checkBoxesLength];

        for (int i = 0; i < checkBoxesLength; i++) {
            checkBoxesStatus[i] = checkBoxes[i].isSelected();
        }

        int count = countTrueCheckBoxes(checkBoxesStatus);

        if (count == 1) {
            for (int i = 0; i < checkBoxesLength; i++) {
                if (checkBoxesStatus[i]) {
                    checkBoxes[i].setEnabled(false);
                }
            }
        } else {
            for (JCheckBox checkBox: checkBoxes) {
                if (!checkBox.isEnabled()) {
                    checkBox.setEnabled(true);
                }
            }
        }

        generateButtonActionListener();
    }

    private int countTrueCheckBoxes(boolean[] checkBoxesStatus) {
        int count = 0;

        for (boolean status: checkBoxesStatus) {
            if (status) {
                count++;
            }
        }

        return count;
    }

    private void numberOfWhiteSpacesSpinnerChangeListener() {
        generateButtonActionListener();
    }

    private void generateButtonActionListener() {
        if (generateButtonActionListenerInProgress) {
            return;
        }

        generateButtonActionListenerInProgress = true;


        localPasswordField.setText(
            SecurePasswordGenerator.generatePassword(
                (int) passwordLengthSpinner.getValue(),
                (int) numberOfWhiteSpacesSpinner.getValue(),
                useLowerCheckBox.isSelected(),
                useUpperCheckBox.isSelected(),
                useDigitsCheckBox.isSelected(),
                usePunctuationCheckBox.isSelected()
            )
        );

        SwingUtilities.invokeLater(() -> generateButtonActionListenerInProgress = false);
    }

    private void updateNumberOfWhiteSpacesSpinnerModel() {
        int maxNumberOfWhiteSpaces = SecurePasswordGenerator.maxNumberOfWhitesSpaces(
            (int) passwordLengthSpinner.getValue()
        );

        SpinnerNumberModel newSpinnerModel = new SpinnerNumberModel(0, 0, maxNumberOfWhiteSpaces, 1);

        numberOfWhiteSpacesSpinner.setModel(newSpinnerModel);
    }

    private void passwordLengthSpinnerChangeListener() {
        generateButtonActionListener();
        updateNumberOfWhiteSpacesSpinnerModel();
    }

    private void localPasswordFieldActionListener() {
        boolean isEmpty = String.valueOf(localPasswordField.getPassword()).isEmpty();

        if (isEmpty) {
            localPasswordField.putClientProperty("JComponent.outline", "error");
        } else {
            localPasswordField.putClientProperty("JComponent.outline", this.success);
        }

        checkOkButtonStatus();
    }

    private void checkOkButtonStatus() {
        boolean  isLocalPasswordFieldEmpty = String.valueOf(localPasswordField.getPassword()).isEmpty();

        if (!isLocalPasswordFieldEmpty) {
            okButton.setEnabled(true);
            return;
        }

        okButton.setEnabled(false);
    }
}
