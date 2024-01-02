package Main.Java.GUI;

import Main.Java.Block;
import Main.Java.NoobChain;
import Main.Java.Wallet.Transaction;
import javafx.scene.control.Alert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author pmpedrolima@gmail.com
 */
public class BlockchainGUI extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlockchainGUI gui = new BlockchainGUI();
            gui.setVisible(true);
        });
    }

    public BlockchainGUI() {
        setTitle("Blockchain GUI");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton showBlockchainButton = new JButton("Mostrar Blockchain");
        showBlockchainButton.addActionListener(e -> displayBlockchain());

        JButton showWalletBalancesButton = new JButton("Mostrar Saldos de Carteiras");
        showWalletBalancesButton.addActionListener(e -> displayWalletBalances());

        JButton performTransactionButton = new JButton("Realizar Transação de Exemplo");
        performTransactionButton.addActionListener(e -> performExampleTransaction());

        JButton mineBlockButton = new JButton("Minerar Bloco");
        mineBlockButton.addActionListener(e -> mineBlock());

        JButton lastBlockInfoButton = new JButton("Info Último Bloco");
        lastBlockInfoButton.addActionListener(e -> displayLastBlockInfo());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        panel.add(showBlockchainButton);
        panel.add(showWalletBalancesButton);
        panel.add(performTransactionButton);
        panel.add(mineBlockButton);
        panel.add(lastBlockInfoButton);

        add(panel);
    }

    private void displayBlockchain() {
        // Implemente a lógica para exibir o blockchain aqui
        JOptionPane.showMessageDialog(this, "Mostrar Blockchain");
    }

    private void displayWalletBalances() {
        // Implemente a lógica para exibir os saldos de carteiras aqui
        JOptionPane.showMessageDialog(this, "Mostrar Saldos de Carteiras");
    }

    private void mineBlock() {
        if (NoobChain.blockchain.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Blockchain está vazia. Não é possível minerar um bloco.");
            return;
        }

        Block newBlock = new Block(NoobChain.blockchain.get(NoobChain.blockchain.size() - 1).hash);
        newBlock.addTransaction(NoobChain.walletA.sendFunds(NoobChain.walletB.publicKey, 10f));
        NoobChain.addBlock(newBlock);

        JOptionPane.showMessageDialog(this, "Bloco minerado com sucesso!\nHash do Bloco: " + newBlock.hash);
    }

    // Método para realizar uma transação de exemplo
    private void performExampleTransaction() {
        if (NoobChain.walletA == null || NoobChain.walletB == null) {
            JOptionPane.showMessageDialog(this, "Carteiras não estão inicializadas. Não é possível realizar a transação de exemplo.");
            return;
        }

        Transaction transaction = NoobChain.walletA.sendFunds(NoobChain.walletB.publicKey, 10f);
        if (transaction != null) {
            JOptionPane.showMessageDialog(this, "Transação realizada com sucesso!\nID da Transação: " + transaction.transactionId);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao realizar a transação. Verifique os saldos e tente novamente.", "Erro na Transação", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para exibir informações sobre o último bloco minerado
    private void displayLastBlockInfo() {
        if (NoobChain.blockchain.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Blockchain está vazia. Não há blocos para exibir informações.");
            return;
        }

        Block lastBlock = NoobChain.blockchain.get(NoobChain.blockchain.size() - 1);

        JOptionPane.showMessageDialog(this,
                "Hash do Bloco: " + lastBlock.hash + "\nNúmero de Transações: " + lastBlock.transactions.size(),
                "Informações do Último Bloco Minerado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
