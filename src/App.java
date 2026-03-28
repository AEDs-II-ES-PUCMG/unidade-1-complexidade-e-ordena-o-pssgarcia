import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class App {
    static final int[] tamanhosTesteGrande =  { 31_250_000, 62_500_000, 125_000_000, 250_000_000, 500_000_000 };
    static final int[] tamanhosTesteMedio =   {     12_500,     25_000,      50_000,     100_000,     200_000 };
    static final int[] tamanhosTestePequeno = {          3,          6,          12,          24,          48 };
    static Random aleatorio = new Random();
    static long operacoes;
    static double nanoToMilli = 1.0/1_000_000;

    private static IOrdenador<Integer> selecionarOrdenador(int opcao) {
        switch (opcao) {
            case 1:
                return new BubbleSort<>();
            case 2:
                return new InsertionSort<>();
            case 3:
                return new Mergesort<>();
            default:
                return null;
        }
    }

    private static String nomeMetodo(int opcao) {
        switch (opcao) {
            case 1:
                return "Bolha";
            case 2:
                return "Inserção";
            case 3:
                return "MergeSort";
            default:
                return "Desconhecido";
        }
    }
    

    /**
     * Gerador de vetores aleatórios de tamanho pré-definido. 
     * @param tamanho Tamanho do vetor a ser criado.
     * @return Vetor com dados aleatórios, com valores entre 1 e (tamanho/2), desordenado.
     */
    static int[] gerarVetor(int tamanho){
        int[] vetor = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = aleatorio.nextInt(1, tamanho/2);
        }
        return vetor;        
    }

    /**
     * Gerador de vetores de objetos do tipo Integer aleatórios de tamanho pré-definido. 
     * @param tamanho Tamanho do vetor a ser criado.
     * @return Vetor de Objetos Integer com dados aleatórios, com valores entre 1 e (tamanho/2), desordenado.
     */
    static Integer[] gerarVetorObjetos(int tamanho) {
        Integer[] vetor = new Integer[tamanho];
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = aleatorio.nextInt(1, 10 * tamanho);
        }
        return vetor;
    }


    public static void main(String[] args) {
        int tam = 20;
        Integer[] vetor = gerarVetorObjetos(tam);
        System.out.println("Vetor original:");
        System.out.println(Arrays.toString(vetor));

        System.out.println("\nEscolha o método de ordenação:");
        System.out.println("1 - BubbleSort");
        System.out.println("2 - InsertionSort");
        System.out.println("3 - MergeSort");

        Scanner scanner = new Scanner(System.in);
        int opcao = scanner.nextInt();

        IOrdenador<Integer> ordenador = selecionarOrdenador(opcao);

        if (ordenador == null) {
            System.out.println("Opção inválida.");
            scanner.close();
            return;
        }

        Integer[] vetorOrdenado = ordenador.ordenar(vetor);

        System.out.println("\nVetor ordenado método " + nomeMetodo(opcao) + ":");
        System.out.println(Arrays.toString(vetorOrdenado));
        System.out.println("Comparações: " + ordenador.getComparacoes());
        System.out.println("Movimentações: " + ordenador.getMovimentacoes());
        System.out.println("Tempo de ordenação (ms): " + ordenador.getTempoOrdenacao());
        scanner.close();

    }
}
