package dev.minesweeper.view;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dev.minesweeper.model.Tabuleiro;

@SuppressWarnings("serial")
public class PainelTabuleiro extends JPanel {

	public PainelTabuleiro(Tabuleiro tabuleiro) {

		setLayout(new GridLayout(tabuleiro.getQuantidadeLinhas(), tabuleiro.getQuantidadeColunas()));

		tabuleiro.paraCadaCampo(campo -> add(new BotaoJogo(campo)));

		tabuleiro.registrarObservador(e -> {
			SwingUtilities.invokeLater(() -> {
				if (e.isGanhou()) {
					JOptionPane.showMessageDialog(this, "Parabéns, você ganhou!!! :)");
				} else {
					JOptionPane.showMessageDialog(this, "Você perdeu :(");
				}

				tabuleiro.reiniciarJogo();
			});
		});
	}
}
