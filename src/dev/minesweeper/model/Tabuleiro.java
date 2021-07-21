package dev.minesweeper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {

	private final int quantidadeLinhas;
	private final int quantidadeColunas;
	private final int quantidadeMinas;

	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();

	public Tabuleiro(int quantidadeLinhas, int quantidadeColunas, int quantidadeMinas) {
		this.quantidadeLinhas = quantidadeLinhas;
		this.quantidadeColunas = quantidadeColunas;
		this.quantidadeMinas = quantidadeMinas;

		gerarCampos();
		associarVizinhos();
		sortearMinas();
	}

	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}

	public void registrarObservador(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}

	private void notificarObservadores(Boolean resultado) {
		observadores.stream().forEach(observador -> observador.accept(new ResultadoEvento(resultado)));
	}

	public void abrir(int linha, int coluna) {
		campos.parallelStream().filter(campo -> campo.getLinha() == linha && campo.getColuna() == coluna).findFirst()
				.ifPresent(campo -> campo.abrirCampo());
	}

	public void alternarMarcacao(int linha, int coluna) {
		campos.parallelStream().filter(campo -> campo.getLinha() == linha && campo.getColuna() == coluna).findFirst()
				.ifPresent(campo -> campo.alternarMarcacao());
	}

	private void gerarCampos() {
		for (int linha = 0; linha < quantidadeLinhas; linha++) {
			for (int coluna = 0; coluna < quantidadeColunas; coluna++) {
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservador(this);
				campos.add(campo);
			}
		}
	}

	private void associarVizinhos() {
		for (Campo c1 : campos) {
			for (Campo c2 : campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}

	private void sortearMinas() {
		long minasArmardas = 0;

		Predicate<Campo> minado = campo -> campo.isMinado();

		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasArmardas = campos.stream().filter(minado).count();

		} while (minasArmardas < quantidadeMinas);
	}

	// Verifica se o o jogador ganhou a partida
	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(campo -> campo.objetivoAlcancado());
	}

	// Reinicia o jogo
	public void reiniciarJogo() {
		campos.stream().forEach(campo -> campo.reiniciar());
		sortearMinas();
	}

	public int getQuantidadeLinhas() {
		return quantidadeLinhas;
	}

	public int getQuantidadeColunas() {
		return quantidadeColunas;
	}

	@Override
	public void ocorreuEvento(Campo campo, CampoEvento evento) {
		if (evento == CampoEvento.EXPLODIR) {
			mostrarMinas();
			notificarObservadores(false);

		} else if (objetivoAlcancado()) {
			notificarObservadores(true);
		}

	}

	void mostrarMinas() {
		campos.stream().filter(campo -> campo.isMinado()).filter(campo -> !campo.isMarcado())
				.forEach(campo -> campo.setAberto(true));
	}

}
