import java.util.Comparator;
import java.util.Map;

/**
 * Critério A - Valor Final do Pedido (crescente).
 * Desempate 1: Volume Total de Itens (quantProdutos).
 * Desempate 2: Código Identificador do primeiro item do pedido.
 */
public class ComparadorCriterioA implements Comparator<Pedido> {
    private final Map<Pedido, Double> cacheValor;
    private final Map<Pedido, Integer> cacheVolume;

    public ComparadorCriterioA(Map<Pedido, Double> cacheValor, Map<Pedido, Integer> cacheVolume) {
        this.cacheValor = cacheValor;
        this.cacheVolume = cacheVolume;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        //Sua lógica de comparação aqui
        int c1 = Double.compare(
                PedidoMetricas.valorFinal(p1, cacheValor),
                PedidoMetricas.valorFinal(p2, cacheValor));
        if (c1 != 0) return c1;

        int c2 = Integer.compare(
                PedidoMetricas.volumeTotal(p1, cacheVolume),
                PedidoMetricas.volumeTotal(p2, cacheVolume));
        if (c2 != 0) return c2;

        return Integer.compare(
                PedidoMetricas.codigoPrimeiroItem(p1),
                PedidoMetricas.codigoPrimeiroItem(p2));
    }
}
