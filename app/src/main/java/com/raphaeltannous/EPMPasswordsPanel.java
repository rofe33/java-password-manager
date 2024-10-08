package com.raphaeltannous;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import net.miginfocom.swing.MigLayout;

/**
 * EPMPasswordsPanel
 */
public class EPMPasswordsPanel extends JPanel {
    private EPMFrame frame;
    protected PasswordManagerInterface db;

    private CustomTable passwordsTable;
    private DefaultTableModel passwordsTableModel;

    private boolean newActionListenerInProgress = false;
    private boolean editActionListenerInProgress = false;
    private boolean deleteActionListenerInProgress = false;
    private boolean copyWebsiteActionListenerInProgress = false;
    private boolean copyUsernameActionListenerInProgress = false;
    private boolean copyPasswordActionListenerInProgress = false;
    private boolean showOrUpdateTOTPMenuItemActionListenerInProgress = false;
    private boolean showOrUpdateBackupCodesMenuItemActionListenerInProgress = false;

    EPMPasswordsPanel(
        EPMFrame frame,
        Path databasePath,
        String databasePassword
    ) {
        this.frame = frame;

        db = new PasswordManagerSQLite(
            databasePath,
            databasePassword
        );

        initPanelComponents();

        if (db.getPasswordsCount() > 0) {
            passwordsTable.addRowSelectionInterval(0, 0);
        }

        enableTools();
    }

    private void enableTools() {
        frame.newPasswordMenuItem.setEnabled(true);
        enableToolsOnSelection();

        frame.newPasswordMenuItem.addActionListener(e -> newPasswordMenuItemActionListener());
        frame.deletePasswordMenuItem.addActionListener(e -> deletePasswordMenuItemActionListener());
        frame.editPasswordMenuItem.addActionListener(e -> editPasswordMenuItemActionListener());
        frame.copyWebsiteMenuItem.addActionListener(e -> copyWebsiteMenuItemActionListener());
        frame.copyUsernameMenuItem.addActionListener(e -> copyUsernameMenuItemActionListener());
        frame.copyPasswordMenuItem.addActionListener(e -> copyPasswordMenuItemActionListener());
        frame.showOrUpdateTOTPMenuItem.addActionListener(e -> showOrUpdateTOTPMenuItemActionListener());
        frame.showOrUpdateBackupCodesMenuItem.addActionListener(e -> showOrUpdateBackupCodesMenuItemActionListener());
    }

    private void enableToolsOnSelection() {
        boolean status = true;

        if (passwordsTable.getSelectedRow() == -1) {
            status = false;
        }

        frame.editPasswordMenuItem.setEnabled(status);
        frame.deletePasswordMenuItem.setEnabled(status);
        frame.copyWebsiteMenuItem.setEnabled(status);
        frame.copyUsernameMenuItem.setEnabled(status);
        frame.copyPasswordMenuItem.setEnabled(status);
        frame.showOrUpdateTOTPMenuItem.setEnabled(status);
        frame.showOrUpdateBackupCodesMenuItem.setEnabled(status);
    }

    private Object[][] databaseDataToTableData() {
        List<String[]> passwords = db.fetchPasswords();
        int passwordsSize = passwords.size();

        Object[][] convertedPasswords = new Object[passwordsSize][];

        for (int i = 0; i < passwordsSize; i++) {
            Object[] row = passwords.get(i);

            if (row[5].equals("1")) {
                row[5] = "Yes";
            } else {
                row[5] = "No";
            }

            convertedPasswords[i] = row;
        }

        return convertedPasswords;
    }

    private void initPanelComponents() {
        // Enabling closeMenuItem
        frame.closeMenuItem.setEnabled(true);

        JScrollPane scrollPane = new JScrollPane();
        passwordsTable = new CustomTable();

        JPopupMenu passwordPopupMenu = new JPopupMenu();
        JMenuItem newPasswordMenuItem = new JMenuItem();
        JMenuItem editPasswordMenuItem = new JMenuItem();
        JMenuItem deletePasswordMenuItem = new JMenuItem();
        JMenuItem copyWebsiteMenuItem = new JMenuItem();
        JMenuItem copyUsernameMenuItem = new JMenuItem();
        JMenuItem copyPasswordMenuItem = new JMenuItem();
        JMenuItem showOrUpdateTOTPMenuItem = new JMenuItem();
        JMenuItem showOrUpdateBackupCodesMenuItem = new JMenuItem();

        setLayout(new MigLayout("insets 0, fill"));

        // scrollPane
        {
            // table
            passwordsTableModel = getTableModel();

            passwordsTable.setModel(passwordsTableModel);

            passwordsTable.setAutoCreateRowSorter(true);

            passwordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


            scrollPane.setViewportView(passwordsTable);

            passwordsTable.setBorder(null);
            passwordsTable.getTableHeader().setBorder(null);
            passwordsTable.setFocusable(false);

            scrollPane.setBorder(null);
            scrollPane.setViewportBorder(null);
            scrollPane.getHorizontalScrollBar().setBorder(null);
            scrollPane.getVerticalScrollBar().setBorder(null);
        }

        // Hiding the Id Column, but it will be still accessible by the code.
        passwordsTable.removeColumn(
            passwordsTable.getColumnModel().getColumn(0)
        );

        add(scrollPane, "grow");

        // passwordPopupMenu
        {
            // newPasswordMenuItem
            newPasswordMenuItem.setText("Add Password");
            newPasswordMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/plus-square.svg"));
            newPasswordMenuItem.addActionListener(e -> newPasswordMenuItemActionListener());
            passwordPopupMenu.add(newPasswordMenuItem);

            // editPasswordMenuItem
            editPasswordMenuItem.setText("Edit Password");
            editPasswordMenuItem.addActionListener(e -> editPasswordMenuItemActionListener());
            editPasswordMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/pencil-square.svg"));
            passwordPopupMenu.add(editPasswordMenuItem);
            passwordPopupMenu.addSeparator();

            // deletePasswordMenuItem
            deletePasswordMenuItem.setText("Delete Password");
            deletePasswordMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/x-square.svg"));
            deletePasswordMenuItem.addActionListener(e -> deletePasswordMenuItemActionListener());
            passwordPopupMenu.add(deletePasswordMenuItem);
            passwordPopupMenu.addSeparator();

            // copyWebsiteMenuItem
            copyWebsiteMenuItem.setText("Copy Website");
            copyWebsiteMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/website-copy.svg"));
            copyWebsiteMenuItem.addActionListener(e -> copyWebsiteMenuItemActionListener());
            passwordPopupMenu.add(copyWebsiteMenuItem);

            // copyUsernameMenuItem
            copyUsernameMenuItem.setText("Copy Username");
            copyUsernameMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/user-copy.svg"));
            copyUsernameMenuItem.addActionListener(e -> copyUsernameMenuItemActionListener());
            passwordPopupMenu.add(copyUsernameMenuItem);

            // copyPasswordMenuItem
            copyPasswordMenuItem.setText("Copy Password");
            copyPasswordMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/key-copy.svg"));
            copyPasswordMenuItem.addActionListener(e -> copyPasswordMenuItemActionListener());
            passwordPopupMenu.add(copyPasswordMenuItem);
            passwordPopupMenu.addSeparator();

            // showOrUpdateTOTPMenuItem
            showOrUpdateTOTPMenuItem.setText("Show Or Update TOTP");
            showOrUpdateTOTPMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/clock-history.svg"));
            showOrUpdateTOTPMenuItem.addActionListener(e -> showOrUpdateTOTPMenuItemActionListener());
            passwordPopupMenu.add(showOrUpdateTOTPMenuItem);
            passwordPopupMenu.addSeparator();

            // showOrUpdateBackupCodesMenuItem
            showOrUpdateBackupCodesMenuItem.setText("Show Or Update Backup Codes");
            showOrUpdateBackupCodesMenuItem.setIcon(new FlatSVGIcon("com/raphaeltannous/icons/code-square.svg"));
            showOrUpdateBackupCodesMenuItem.addActionListener(e -> showOrUpdateBackupCodesMenuItemActionListener());
            passwordPopupMenu.add(showOrUpdateBackupCodesMenuItem);
        }

        passwordsTable.setComponentPopupMenu(passwordPopupMenu);

        // Mouse listener
        passwordsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Point point = event.getPoint();
                int currentRow = passwordsTable.rowAtPoint(point);
                passwordsTable.setRowSelectionInterval(currentRow, currentRow);

                enableTools();
            }
        });
    }

    private DefaultTableModel getTableModel() {
        return new DefaultTableModel(
            databaseDataToTableData(),
            new String[] {
                "Id", "Website", "Username/Email", "Passwords", "OTP", "Has Backup Code", "Note"
            }
        ) {
            Class<?>[] columnTypes = {
                String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            boolean[] columnEditable = {
                false, false, false, false, false, false, false
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
    }

    protected void updateTableModel() {
        passwordsTableModel = getTableModel();

        passwordsTable.setModel(passwordsTableModel);

        // Hiding the Id Column, but it will be still accessible by the code.
        passwordsTable.removeColumn(
            passwordsTable.getColumnModel().getColumn(0)
        );
    }

    private int getSelectedPasswordId() {
        int selectedRow = passwordsTable.getSelectedRow();
        return Integer.parseInt((String) passwordsTableModel.getValueAt(selectedRow, 0));
    }

    private void copyPasswordMenuItemActionListener() {
        if (copyPasswordActionListenerInProgress) {
            return;
        }

        copyPasswordActionListenerInProgress = true;

        int passwordId = getSelectedPasswordId();

        String password = db.fetchPassword(passwordId);
        EPMUtilities.copyToClipboard(password);

        SwingUtilities.invokeLater(() -> copyPasswordActionListenerInProgress = false);
    }

    private void copyUsernameMenuItemActionListener() {
        if (copyUsernameActionListenerInProgress) {
            return;
        }

        copyUsernameActionListenerInProgress = true;

        int passwordId = getSelectedPasswordId();

        String username = db.fetchUsername(passwordId);
        EPMUtilities.copyToClipboard(username);

        SwingUtilities.invokeLater(() -> copyUsernameActionListenerInProgress = false);
    }

    private void copyWebsiteMenuItemActionListener() {
        if (copyWebsiteActionListenerInProgress) {
            return;
        }

        copyWebsiteActionListenerInProgress = true;

        int passwordId = getSelectedPasswordId();

        String website = db.fetchWebsite(passwordId);
        EPMUtilities.copyToClipboard(website);

        SwingUtilities.invokeLater(() -> copyWebsiteActionListenerInProgress = false);
    }

    private void deletePasswordMenuItemActionListener() {
        if (deleteActionListenerInProgress) {
            return;
        }

        deleteActionListenerInProgress = true;

        int passwordId = getSelectedPasswordId();

        db.deletePassword(passwordId);

        updateTableModel();
        enableTools();

        SwingUtilities.invokeLater(() -> deleteActionListenerInProgress = false);
    }

    private void editPasswordMenuItemActionListener() {
        if (editActionListenerInProgress) {
            return;
        }

        editActionListenerInProgress = true;

        EPMEditPasswordDialog editPasswordDialog = new EPMEditPasswordDialog(
            this.frame,
            this,
            this.frame,
            getSelectedPasswordId()
        );

        editPasswordDialog.setVisible(true);
        updateTableModel();
        enableTools();

        SwingUtilities.invokeLater(() -> editActionListenerInProgress = false);
    }

    private void newPasswordMenuItemActionListener() {
        if (newActionListenerInProgress) {
            return;
        }

        newActionListenerInProgress = true;

        EPMNewPasswordDialog addPasswordDialog = new EPMNewPasswordDialog(
            this.frame,
            this,
            this.frame
        );

        addPasswordDialog.setVisible(true);
        updateTableModel();
        enableTools();

        SwingUtilities.invokeLater(() -> newActionListenerInProgress = false);
    }

    private void showOrUpdateBackupCodesMenuItemActionListener() {
        if (showOrUpdateBackupCodesMenuItemActionListenerInProgress) {
            return;
        }

        showOrUpdateBackupCodesMenuItemActionListenerInProgress = true;

        EPMBackupCodesDialog backupCodesDialog = new EPMBackupCodesDialog(
            this.frame,
            this,
            this.frame,
            getSelectedPasswordId()
        );

        backupCodesDialog.setVisible(true);
        updateTableModel();
        enableTools();

        SwingUtilities.invokeLater(() -> showOrUpdateBackupCodesMenuItemActionListenerInProgress = false);
    }

    private void showOrUpdateTOTPMenuItemActionListener() {
        if (showOrUpdateTOTPMenuItemActionListenerInProgress) {
            return;
        }

        showOrUpdateTOTPMenuItemActionListenerInProgress = true;

        EPMTOTPDialog totpDialog = new EPMTOTPDialog(
            this.frame,
            this,
            getSelectedPasswordId()
        );

        totpDialog.setVisible(true);
        updateTableModel();
        enableTools();

        SwingUtilities.invokeLater(() -> showOrUpdateTOTPMenuItemActionListenerInProgress = false);
    }
}

class CustomTable extends JTable {
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component cell = super.prepareRenderer(renderer, row, column);

        Color gray = new Color(242, 242, 242);
        Color white = Color.WHITE;

        if (!cell.getBackground().equals(getSelectionBackground())) {
            Color bg = (row % 2 == 0 ? gray : white);

            cell.setBackground(bg);

            bg = null;
        }

        return cell;
    }
}
