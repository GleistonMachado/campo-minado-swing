package dev.minesweeper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Campo {

	private final int linha;
	private final int coluna;
	private boolean aberto;
	private boolean minado;
	private boolean marcado;

	private List<Campo> campo = new ArrayList<Campo>();
	private Set<CampoObservador> observadores = new HashSet<>();

	Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}

	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}

	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(observador -> observador.ocorreuEvento(this, evento));
	}

	boolean adicionarVizinho(Campo campoVizinho) {
		boolean linhaDiferente = linha != campoVizinho.linha;
		boolean colunaDiferente = coluna != campoVizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;

		int deltaLinha = Math.abs(linha - campoVizinho.linha);
		int deltaColuna = Math.abs(coluna - campoVizinho.coluna);
		int deltaFinal = deltaColuna + deltaLinha;

		if (deltaFinal == 1 && !diagonal) {
			campo.add(campoVizinho);
			return true;

		} else if (deltaFinal == 2 && diagonal) {
			campo.add(campoVizinho);
			return true;

		} else {
			return false;
		}

	}

	public void alternarMarcacao() {
		if (!aberto) {
			marcado = !marcado;

			if (marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}

	public boolean abrirCampo() {

		if (!aberto && !marcado) {

			if (minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}

			setAberto(true);

			if (vizinhacaSegura()) {
				campo.forEach(vizinho -> vizinho.abrirCampo());
			}

			return true;

		} else {

			return false;
		}

	}

	// Verifica se nenhum campo esta minado
	public boolean vizinhacaSegura() {
		return campo.stream().noneMatch(vizinho -> vizinho.minado);
	}

	void minar() {
		minado = true;
	}

	public boolean isMinado() {
		return minado;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public boolean isAberto() {
		return aberto;
	}

	void setAberto(boolean aberto) {
		this.aberto = aberto;

		if (aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	public boolean isFechado() {
		return !isAberto();
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}

	public int quantidadeDeVizinhosMinados() {
		return (int) campo.stream().filter(vizinho -> vizinho.minado).count();
	}

	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}

}
