import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Vetor de pedidos cadastrados */
    static Pedido[] pedidosCadastrados;
    
    /** Vetor de pedidos ordenados pela data do pedido */
    static Pedido[] pedidosOrdenadosPorData;

    /** Vetor de pedidos ordenados pelo valor final (crescente) — usado para busca binária em localizarPedidosPremium */
    static Pedido[] pedidosOrdenadosPorValor;
    
    /** Quantidade de pedidos cadastrados atualmente no vetor */
    static int quantPedidos = 0;
    
    static IOrdenator<Pedido> ordenador;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de pedidos. Arquivo-texto no formato
     * N  (quantidade de pedidos) <br/>
     * dataDoPedido;formaDePagamento;descrições dos produtos do pedido <br/>
     * Deve haver uma linha para cada um dos pedidos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os pedidos carregados, ou vazio em caso de problemas de leitura.
     */
    static Pedido[] lerPedidos(String nomeArquivoDados) {
    	
    	Pedido[] pedidosCadastrados;
    	Scanner arquivo = null;
    	int numPedidos;
    	String linha;
    	Pedido pedido;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numPedidos = Integer.parseInt(arquivo.nextLine());
    		pedidosCadastrados = new Pedido[numPedidos];
    		
    		for (int i = 0; i < numPedidos; i++) {
    			linha = arquivo.nextLine();
    			pedido = criarPedido(linha);
    			pedidosCadastrados[i] = pedido;
    		}
    		quantPedidos = numPedidos;
    		
    	} catch (IOException excecaoArquivo) {
    		pedidosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return pedidosCadastrados;
    }
    
    private static Pedido criarPedido(String dados) {

    	String[] dadosPedido;
    	DateTimeFormatter formatoData;
    	LocalDate dataDoPedido;
    	int formaDePagamento;
    	Pedido pedido;
    	Produto produto;

    	dadosPedido = dados.split(";");

    	formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	dataDoPedido = LocalDate.parse(dadosPedido[0], formatoData);

    	formaDePagamento = Integer.parseInt(dadosPedido[1]);

    	pedido = new Pedido(dataDoPedido, formaDePagamento);

    	for (int i = 2; i < dadosPedido.length; i++) {
    		String campo = dadosPedido[i];
    		String nomeProduto;
    		int quantidade;

    		int separador = campo.lastIndexOf(':');
    		if (separador >= 0) {
    			nomeProduto = campo.substring(0, separador);
    			try {
    				quantidade = Integer.parseInt(campo.substring(separador + 1));
    			} catch (NumberFormatException e) {
    				nomeProduto = campo;
    				quantidade = 1;
    			}
    		} else {
    			nomeProduto = campo;
    			quantidade = 1;
    		}

    		produto = pesquisarProduto(nomeProduto);
    		pedido.incluirProduto(produto, quantidade);
    	}
    	return pedido;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto passado como parâmetro para esse método. 
     *  A busca não é sensível ao caso.
     *  @param pesquisado Nome do produto a ser pesquisado no vetor de produtos cadastrados. 
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto pesquisarProduto(String pesquisado) {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(pesquisado)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        if (!localizado) {
        	return null;
        } else {
        	return(produto);
        }     
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Ordenar pedidos");
        System.out.println("2 - Embaralhar pedidos");
        System.out.println("3 - Listar todos os pedidos");
        System.out.println("4 - Localizar pedidos premium (por valor de corte)");
        System.out.println("0 - Finalizar");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    
    /**
     * Localiza e exibe todos os pedidos cujo valor final seja maior ou igual a um valor de corte
     * informado pelo usuário.
     *
     * Estratégia otimizada: o vetor pedidosOrdenadosPorValor é mantido ordenado de forma crescente
     * pelo valor final (ComparadorCriterioA). Uma busca binária identifica o primeiro índice cujo
     * valorFinal() >= valorCorte em O(log n), evitando varredura completa do vetor. Os pedidos
     * elegíveis — todos a partir desse índice — são impressos em O(k), onde k é o número de
     * resultados encontrados, sem recalcular valores abaixo do corte.
     */
    static void localizarPedidosPremium() {
        if (quantPedidos == 0) {
            System.out.println("Não há pedidos cadastrados.");
            return;
        }

        System.out.print("Informe o valor de corte (R$): ");
        double corte = teclado.nextDouble();

        // Estratégia otimizada:
        // 1) ordena por valor final (merge O(n log n))
        // 2) pré-calcula valores uma única vez
        // 3) busca binária do primeiro >= corte (O(log n))
        Map<Pedido, Double> cacheValorFinal = new IdentityHashMap<>();
        Comparator<Pedido> porValorFinal = Comparator
                .comparingDouble((Pedido p) -> PedidoMetricas.valorFinal(p, cacheValorFinal))
                .thenComparingInt(PedidoMetricas::volumeTotal)
                .thenComparingInt(PedidoMetricas::codigoPrimeiroItem);

        mergeSort(pedidosCadastrados, quantPedidos, porValorFinal);

        double[] valores = new double[quantPedidos];
        for (int i = 0; i < quantPedidos; i++) {
            valores[i] = PedidoMetricas.valorFinal(pedidosCadastrados[i], cacheValorFinal);
        }

        int idx = lowerBound(valores, quantPedidos, corte);
        if (idx == quantPedidos) {
            System.out.println("Nenhum pedido premium encontrado.");
            return;
        }

        System.out.println("\n=== Pedidos Premium ===");
        for (int i = idx; i < quantPedidos; i++) {
            Pedido p = pedidosCadastrados[i];
            System.out.println(p);        
            p.imprimirRecibo();           
            System.out.println("--------------------------------");
        }
    }

    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção"); 
        System.out.println("3 - Seleção"); 
        System.out.println("4 - Mergesort"); 
        System.out.println("5 - Heapsort"); 
        System.out.println("0 - Finalizar");
       
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static int exibirMenuComparadores() {
        cabecalho();
        System.out.println("1 - Critério A: Valor Final do Pedido");
        System.out.println("2 - Critério B: Volume Total de Itens");
        System.out.println("3 - Critério C: Índice de Economia (Decrescente)");

        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    /** Ordena o vetor de pedidos cadastrados empregando um método de ordenação selecionado pelo
     *  usuário, dentre os seguintes: bolha, seleção, inserção, mergesort e heapsort.
     *  O usuário também escolhe um critério de ordenação: A (Valor Final), B (Volume de Itens)
     *  ou C (Índice de Economia, decrescente).
     *  O método interage com o usuário por meio de menus e aplica
     *  a ordenação escolhida. Ao final, exibe o tempo total gasto no processo de ordenação, em ms.
    */
   
    static void ordenarPedidos() {
        if (quantPedidos <= 1) {
            System.out.println("Não há pedidos suficientes para ordenar.");
            return;
        }

        Comparator<Pedido> comparador = null;
        int criterio = lerInteiroMenu("""
                === Critério de ordenação ===
                1) Critério A - Valor Final (desempate: Volume, 1º Item)
                2) Critério B - Volume Total (desempate: Data, Código)
                3) Critério C - Índice de Economia (DEC) (desempate: Valor Final, Código)
                Escolha: """, 1, 3);

        Map<Pedido, Double> cacheValorFinal = new IdentityHashMap<>();
        Map<Pedido, Integer> cacheVolume = new IdentityHashMap<>();
        Map<Pedido, Double> cacheEconomia = new IdentityHashMap<>();

        switch (criterio) {
            case 1 -> comparador = new ComparadorCriterioA(cacheValorFinal, cacheVolume);
            case 2 -> comparador = new ComparadorCriterioB(cacheVolume);
            case 3 -> comparador = new ComparadorCriterioC(cacheEconomia, cacheValorFinal);
        }

        int algoritmo = lerInteiroMenu("""
                === Algoritmo de ordenação ===
                1) Bolha
                2) Seleção
                3) Inserção
                4) MergeSort
                5) HeapSort
                Escolha: """, 1, 5);

        long ini = System.nanoTime();
        switch (algoritmo) {
            case 1 -> bubbleSort(pedidosCadastrados, quantPedidos, comparador);
            case 2 -> selectionSort(pedidosCadastrados, quantPedidos, comparador);
            case 3 -> insertionSort(pedidosCadastrados, quantPedidos, comparador);
            case 4 -> mergeSort(pedidosCadastrados, quantPedidos, comparador);
            case 5 -> heapSort(pedidosCadastrados, quantPedidos, comparador);
        }
        long fim = System.nanoTime();

        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("Tempo de ordenação: %.3f ms%n", ms);
    }

    private static void bubbleSort(Pedido[] v, int n, Comparator<Pedido> c) {
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (c.compare(v[j], v[j + 1]) > 0) {
                    Pedido tmp = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = tmp;
                    trocou = true;
                }
            }
            if (!trocou) break;
        }
    }

    private static void selectionSort(Pedido[] v, int n, Comparator<Pedido> c) {
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (c.compare(v[j], v[min]) < 0) min = j;
            }
            if (min != i) {
                Pedido tmp = v[i];
                v[i] = v[min];
                v[min] = tmp;
            }
        }
    }

    private static void insertionSort(Pedido[] v, int n, Comparator<Pedido> c) {
        for (int i = 1; i < n; i++) {
            Pedido chave = v[i];
            int j = i - 1;
            while (j >= 0 && c.compare(v[j], chave) > 0) {
                v[j + 1] = v[j];
                j--;
            }
            v[j + 1] = chave;
        }
    }

    private static void mergeSort(Pedido[] v, int n, Comparator<Pedido> c) {
        Pedido[] aux = new Pedido[n];
        mergeSortRec(v, aux, 0, n - 1, c);
    }

     private static void mergeSortRec(Pedido[] v, Pedido[] aux, int l, int r, Comparator<Pedido> c) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        mergeSortRec(v, aux, l, m, c);
        mergeSortRec(v, aux, m + 1, r, c);
        merge(v, aux, l, m, r, c);
    }

    private static void merge(Pedido[] v, Pedido[] aux, int l, int m, int r, Comparator<Pedido> c) {
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            if (c.compare(v[i], v[j]) <= 0) aux[k++] = v[i++];
            else aux[k++] = v[j++];
        }
        while (i <= m) aux[k++] = v[i++];
        while (j <= r) aux[k++] = v[j++];
        for (int x = l; x <= r; x++) v[x] = aux[x];
    }

    private static void heapSort(Pedido[] v, int n, Comparator<Pedido> c) {
        for (int i = n / 2 - 1; i >= 0; i--) heapify(v, n, i, c);
        for (int end = n - 1; end > 0; end--) {
            Pedido tmp = v[0];
            v[0] = v[end];
            v[end] = tmp;
            heapify(v, end, 0, c);
        }
    }

    private static void heapify(Pedido[] v, int n, int i, Comparator<Pedido> c) {
        int maior = i;
        int e = 2 * i + 1, d = 2 * i + 2;

        if (e < n && c.compare(v[e], v[maior]) > 0) maior = e;
        if (d < n && c.compare(v[d], v[maior]) > 0) maior = d;

        if (maior != i) {
            Pedido tmp = v[i];
            v[i] = v[maior];
            v[maior] = tmp;
            heapify(v, n, maior, c);
        }
    }

    private static int lowerBound(double[] arr, int n, double alvo) {
        int l = 0, r = n;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (arr[m] >= alvo) r = m;
            else l = m + 1;
        }
        return l;
    }

    private static int lerInteiroMenu(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            int op = teclado.nextInt();
            if (op >= min && op <= max) return op;
            System.out.println("Opção inválida.");
        }
    }

    static void embaralharPedidos(){
        Collections.shuffle(Arrays.asList(pedidosCadastrados));
    }

    /** Lista todos os pedidos cadastrados, numerados, um por linha */
    static void listarTodosOsPedidos() {
    	
        cabecalho();
        System.out.println("\nPedidos cadastrados: ");
        for (int i = 0; i < quantPedidos; i++) {
        	System.out.println(String.format("%02d - %s\n", (i + 1), pedidosCadastrados[i].toString()));
        }
    }
    
    public static void main(String[] args) {
		
    	teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
    	nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
       
        String nomeArquivoPedidos = "pedidos.txt";
        pedidosCadastrados = lerPedidos(nomeArquivoPedidos);
        
        ComparadorPorData comparadorPorData = new ComparadorPorData();
        ordenador = new Heapsort<>();
        ordenador.setComparador(comparadorPorData);
        pedidosOrdenadosPorData = ordenador.ordenar(pedidosCadastrados);

        // Cria cópia independente ordenada por valorFinal() (crescente) para busca binária em localizarPedidosPremium
        pedidosOrdenadosPorValor = Arrays.copyOf(pedidosCadastrados, quantPedidos);
        IOrdenator<Pedido> ordenadorValor = new Heapsort<>();
        ordenadorValor.setComparador(new ComparadorCriterioA());
        pedidosOrdenadosPorValor = ordenadorValor.ordenar(pedidosOrdenadosPorValor);

        int opcao = -1;
      
        do{
        	opcao = menu();
            switch (opcao) {
                case 1 -> ordenarPedidos();
                case 2 -> embaralharPedidos();
                case 3 -> listarTodosOsPedidos();
                case 4 -> localizarPedidosPremium();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);       

        teclado.close();    
    }
}
