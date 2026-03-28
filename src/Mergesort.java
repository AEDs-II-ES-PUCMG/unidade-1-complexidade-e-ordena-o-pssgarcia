import java.util.Arrays;

public class Mergesort<T extends Comparable<T>> implements IOrdenador<T> {
	private int comparacoes;
	private int movimentacoes;
	private double tempoOrdenacao;
	private double inicio;

	private double nanoToMilli = 1.0 / 1_000_000;

	@Override
	public int getComparacoes() {
		return comparacoes;
	}

	@Override
	public int getMovimentacoes() {
		return movimentacoes;
	}

	@Override
	public double getTempoOrdenacao() {
		return tempoOrdenacao;
	}

	private void iniciar() {
		this.comparacoes = 0;
		this.movimentacoes = 0;
		this.inicio = System.nanoTime();
	}

	private void terminar() {
		this.tempoOrdenacao = (System.nanoTime() - this.inicio) * nanoToMilli;
	}

	@Override
	public T[] ordenar(T[] dados) {
		T[] dadosOrdenados = Arrays.copyOf(dados, dados.length);
		iniciar();
		if (dadosOrdenados.length > 1) {
			mergesort(dadosOrdenados, 0, dadosOrdenados.length - 1);
		}
		terminar();
		return dadosOrdenados;
	}

	private void mergesort(T[] array, int esq, int dir) {
		if (esq < dir) {
			int meio = (esq + dir) / 2;
			mergesort(array, esq, meio);
			mergesort(array, meio + 1, dir);
			intercalar(array, esq, meio, dir);
		}
	}

	private void intercalar(T[] array, int esq, int meio, int dir) {
		T[] a1 = Arrays.copyOfRange(array, esq, meio + 1);
		T[] a2 = Arrays.copyOfRange(array, meio + 1, dir + 1);

		movimentacoes += a1.length + a2.length;

		int i = 0;
		int j = 0;
		int k = esq;

		while (i < a1.length && j < a2.length) {
			comparacoes++;
			if (a1[i].compareTo(a2[j]) <= 0) {
				array[k++] = a1[i++];
			} else {
				array[k++] = a2[j++];
			}
			movimentacoes++;
		}

		while (i < a1.length) {
			array[k++] = a1[i++];
			movimentacoes++;
		}

		while (j < a2.length) {
			array[k++] = a2[j++];
			movimentacoes++;
		}
	}
}